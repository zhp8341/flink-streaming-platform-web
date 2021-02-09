// CodeMirror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: https://codemirror.net/LICENSE

(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
    "use strict";

    CodeMirror.defineMode("sql", function (config, parserConfig) {
        var client = parserConfig.client || {},
            atoms = parserConfig.atoms || {"false": true, "true": true, "null": true},
            builtin = parserConfig.builtin || set(defaultBuiltin),
            keywords = parserConfig.keywords || set(sqlKeywords),
            operatorChars = parserConfig.operatorChars || /^[*+\-%<>!=&|~^\/]/,
            support = parserConfig.support || {},
            hooks = parserConfig.hooks || {},
            dateSQL = parserConfig.dateSQL || {"date": true, "time": true, "timestamp": true},
            backslashStringEscapes = parserConfig.backslashStringEscapes !== false,
            brackets = parserConfig.brackets || /^[\{}\(\)\[\]]/,
            punctuation = parserConfig.punctuation || /^[;.,:]/

        function tokenBase(stream, state) {
            var ch = stream.next();

            // call hooks from the mime type
            if (hooks[ch]) {
                var result = hooks[ch](stream, state);
                if (result !== false) return result;
            }

            if (support.hexNumber &&
                ((ch == "0" && stream.match(/^[xX][0-9a-fA-F]+/))
                    || (ch == "x" || ch == "X") && stream.match(/^'[0-9a-fA-F]+'/))) {
                // hex
                // ref: http://dev.mysql.com/doc/refman/5.5/en/hexadecimal-literals.html
                return "number";
            } else if (support.binaryNumber &&
                (((ch == "b" || ch == "B") && stream.match(/^'[01]+'/))
                    || (ch == "0" && stream.match(/^b[01]+/)))) {
                // bitstring
                // ref: http://dev.mysql.com/doc/refman/5.5/en/bit-field-literals.html
                return "number";
            } else if (ch.charCodeAt(0) > 47 && ch.charCodeAt(0) < 58) {
                // numbers
                // ref: http://dev.mysql.com/doc/refman/5.5/en/number-literals.html
                stream.match(/^[0-9]*(\.[0-9]+)?([eE][-+]?[0-9]+)?/);
                support.decimallessFloat && stream.match(/^\.(?!\.)/);
                return "number";
            } else if (ch == "?" && (stream.eatSpace() || stream.eol() || stream.eat(";"))) {
                // placeholders
                return "variable-3";
            } else if (ch == "'" || (ch == '"' && support.doubleQuote)) {
                // strings
                // ref: http://dev.mysql.com/doc/refman/5.5/en/string-literals.html
                state.tokenize = tokenLiteral(ch);
                return state.tokenize(stream, state);
            } else if ((((support.nCharCast && (ch == "n" || ch == "N"))
                || (support.charsetCast && ch == "_" && stream.match(/[a-z][a-z0-9]*/i)))
                && (stream.peek() == "'" || stream.peek() == '"'))) {
                // charset casting: _utf8'str', N'str', n'str'
                // ref: http://dev.mysql.com/doc/refman/5.5/en/string-literals.html
                return "keyword";
            } else if (support.escapeConstant && (ch == "e" || ch == "E")
                && (stream.peek() == "'" || (stream.peek() == '"' && support.doubleQuote))) {
                // escape constant: E'str', e'str'
                // ref: https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS-ESCAPE
                state.tokenize = function (stream, state) {
                    return (state.tokenize = tokenLiteral(stream.next(), true))(stream, state);
                }
                return "keyword";
            } else if (support.commentSlashSlash && ch == "/" && stream.eat("/")) {
                // 1-line comment
                stream.skipToEnd();
                return "comment";
            } else if ((support.commentHash && ch == "#")
                || (ch == "-" && stream.eat("-") && (!support.commentSpaceRequired || stream.eat(" ")))) {
                // 1-line comments
                // ref: https://kb.askmonty.org/en/comment-syntax/
                stream.skipToEnd();
                return "comment";
            } else if (ch == "/" && stream.eat("*")) {
                // multi-line comments
                // ref: https://kb.askmonty.org/en/comment-syntax/
                state.tokenize = tokenComment(1);
                return state.tokenize(stream, state);
            } else if (ch == ".") {
                // .1 for 0.1
                if (support.zerolessFloat && stream.match(/^(?:\d+(?:e[+-]?\d+)?)/i))
                    return "number";
                if (stream.match(/^\.+/))
                    return null
                // .table_name (ODBC)
                // // ref: http://dev.mysql.com/doc/refman/5.6/en/identifier-qualifiers.html
                if (support.ODBCdotTable && stream.match(/^[\w\d_$#]+/))
                    return "variable-2";
            } else if (operatorChars.test(ch)) {
                // operators
                stream.eatWhile(operatorChars);
                return "operator";
            } else if (brackets.test(ch)) {
                // brackets
                return "bracket";
            } else if (punctuation.test(ch)) {
                // punctuation
                stream.eatWhile(punctuation);
                return "punctuation";
            } else if (ch == '{' &&
                (stream.match(/^( )*(d|D|t|T|ts|TS)( )*'[^']*'( )*}/) || stream.match(/^( )*(d|D|t|T|ts|TS)( )*"[^"]*"( )*}/))) {
                // dates (weird ODBC syntax)
                // ref: http://dev.mysql.com/doc/refman/5.5/en/date-and-time-literals.html
                return "number";
            } else {
                stream.eatWhile(/^[_\w\d]/);
                var word = stream.current().toLowerCase();
                // dates (standard SQL syntax)
                // ref: http://dev.mysql.com/doc/refman/5.5/en/date-and-time-literals.html
                if (dateSQL.hasOwnProperty(word) && (stream.match(/^( )+'[^']*'/) || stream.match(/^( )+"[^"]*"/)))
                    return "number";
                if (atoms.hasOwnProperty(word)) return "atom";
                if (builtin.hasOwnProperty(word)) return "builtin";
                if (keywords.hasOwnProperty(word)) return "keyword";
                if (client.hasOwnProperty(word)) return "string-2";
                return null;
            }
        }

        // 'string', with char specified in quote escaped by '\'
        function tokenLiteral(quote, backslashEscapes) {
            return function (stream, state) {
                var escaped = false, ch;
                while ((ch = stream.next()) != null) {
                    if (ch == quote && !escaped) {
                        state.tokenize = tokenBase;
                        break;
                    }
                    escaped = (backslashStringEscapes || backslashEscapes) && !escaped && ch == "\\";
                }
                return "string";
            };
        }

        function tokenComment(depth) {
            return function (stream, state) {
                var m = stream.match(/^.*?(\/\*|\*\/)/)
                if (!m) stream.skipToEnd()
                else if (m[1] == "/*") state.tokenize = tokenComment(depth + 1)
                else if (depth > 1) state.tokenize = tokenComment(depth - 1)
                else state.tokenize = tokenBase
                return "comment"
            }
        }

        function pushContext(stream, state, type) {
            state.context = {
                prev: state.context,
                indent: stream.indentation(),
                col: stream.column(),
                type: type
            };
        }

        function popContext(state) {
            state.indent = state.context.indent;
            state.context = state.context.prev;
        }

        return {
            startState: function () {
                return {tokenize: tokenBase, context: null};
            },

            token: function (stream, state) {
                if (stream.sol()) {
                    if (state.context && state.context.align == null)
                        state.context.align = false;
                }
                if (state.tokenize == tokenBase && stream.eatSpace()) return null;

                var style = state.tokenize(stream, state);
                if (style == "comment") return style;

                if (state.context && state.context.align == null)
                    state.context.align = true;

                var tok = stream.current();
                if (tok == "(")
                    pushContext(stream, state, ")");
                else if (tok == "[")
                    pushContext(stream, state, "]");
                else if (state.context && state.context.type == tok)
                    popContext(state);
                return style;
            },

            indent: function (state, textAfter) {
                var cx = state.context;
                if (!cx) return CodeMirror.Pass;
                var closing = textAfter.charAt(0) == cx.type;
                if (cx.align) return cx.col + (closing ? 0 : 1);
                else return cx.indent + (closing ? 0 : config.indentUnit);
            },

            blockCommentStart: "/*",
            blockCommentEnd: "*/",
            lineComment: support.commentSlashSlash ? "//" : support.commentHash ? "#" : "--",
            closeBrackets: "()[]{}''\"\"``"
        };
    });

    // `identifier`
    function hookIdentifier(stream) {
        // MySQL/MariaDB identifiers
        // ref: http://dev.mysql.com/doc/refman/5.6/en/identifier-qualifiers.html
        var ch;
        while ((ch = stream.next()) != null) {
            if (ch == "`" && !stream.eat("`")) return "variable-2";
        }
        stream.backUp(stream.current().length - 1);
        return stream.eatWhile(/\w/) ? "variable-2" : null;
    }

    // "identifier"
    function hookIdentifierDoublequote(stream) {
        // Standard SQL /SQLite identifiers
        // ref: http://web.archive.org/web/20160813185132/http://savage.net.au/SQL/sql-99.bnf.html#delimited%20identifier
        // ref: http://sqlite.org/lang_keywords.html
        var ch;
        while ((ch = stream.next()) != null) {
            if (ch == "\"" && !stream.eat("\"")) return "variable-2";
        }
        stream.backUp(stream.current().length - 1);
        return stream.eatWhile(/\w/) ? "variable-2" : null;
    }

    // variable token
    function hookVar(stream) {
        // variables
        // @@prefix.varName @varName
        // varName can be quoted with ` or ' or "
        // ref: http://dev.mysql.com/doc/refman/5.5/en/user-variables.html
        if (stream.eat("@")) {
            stream.match(/^session\./);
            stream.match(/^local\./);
            stream.match(/^global\./);
        }

        if (stream.eat("'")) {
            stream.match(/^.*'/);
            return "variable-2";
        } else if (stream.eat('"')) {
            stream.match(/^.*"/);
            return "variable-2";
        } else if (stream.eat("`")) {
            stream.match(/^.*`/);
            return "variable-2";
        } else if (stream.match(/^[0-9a-zA-Z$\.\_]+/)) {
            return "variable-2";
        }
        return null;
    };

    // short client keyword token
    function hookClient(stream) {
        // \N means NULL
        // ref: http://dev.mysql.com/doc/refman/5.5/en/null-values.html
        if (stream.eat("N")) {
            return "atom";
        }
        // \g, etc
        // ref: http://dev.mysql.com/doc/refman/5.5/en/mysql-commands.html
        return stream.match(/^[a-zA-Z.#!?]/) ? "variable-2" : null;
    }

    // these keywords are used by all SQL dialects (however, a mode can still overwrite it)
    var sqlKeywords = "alter and as asc between by count create delete desc distinct drop from group having in insert into is join like not on or order select set table union update values where limit all ";

    // turn a space-separated list into an array
    function set(str) {
        var obj = {}, words = str.split(" ");
        for (var i = 0; i < words.length; ++i) obj[words[i]] = true;
        return obj;
    }

    var defaultBuiltin = "bool boolean bit blob enum long longblob longtext medium mediumblob mediumint mediumtext time timestamp tinyblob tinyint tinytext text bigint int int1 int2 int3 int4 int8 integer float float4 float8 double char varbinary varchar varcharacter precision real date datetime year unsigned signed decimal numeric"

    // A generic SQL Mode. It's not a standard, it just try to support what is generally supported
    CodeMirror.defineMIME("text/x-sql", {
        name: "sql",
        keywords: set(sqlKeywords + "begin"),
        builtin: set(defaultBuiltin),
        atoms: set("false true null unknown"),
        dateSQL: set("date time timestamp"),
        support: set("ODBCdotTable doubleQuote binaryNumber hexNumber")
    });

    CodeMirror.defineMIME("flink/x-fsql", {
        name: "sql",
        keywords: set("varchar partition timestamp primary key string int bigint double date boolean tinyint samllint float decimal varbinary create having or order like set left right join no not  limit all for null true false tumble_rowtime minute hour interval select from table with group by order and or date_format timestamp from_timestamp  function view insert  into where join having distinct all sum union all partition over as desc asc count substring type project minute tumble_start tumble_end hop_start hop_end function view  insert into "),
        //keywords: set(sqlKeywords + " varchar int bigint double date boolean tinyint smallint float decimal varbinary begin add after all alter analyze and anti archive array as asc at between bucket buckets by cache cascade case cast change clear cluster clustered codegen collection column columns comment commit compact compactions compute concatenate cost create cross cube current current_date current_timestamp database databases datata dbproperties defined delete delimited deny desc describe dfs directories distinct distribute drop else end escaped except exchange exists explain export extended external false fields fileformat first following for format formatted from full function functions global grant group grouping having if ignore import in index indexes inner inpath inputformat insert intersect interval into is items join keys last lateral lazy left like limit lines list load local location lock locks logical macro map minus msck natural no not null nulls of on optimize option options or order out outer outputformat over overwrite partition partitioned partitions percent preceding principals purge range recordreader recordwriter recover reduce refresh regexp rename repair replace reset restrict revoke right rlike role roles rollback rollup row rows schema schemas select semi separated serde serdeproperties set sets show skewed sort sorted start statistics stored stratify struct  tables tablesample tblproperties temp temporary terminated then to touch transaction transactions transform true truncate unarchive unbounded uncache union unlock unset use using values view when where window with"),
        builtin: set("VARCHAR INT BIGINT DOUBLE DATE BOOLEAN TINYINT SAMLLINT FLOAT DECIMAL VARBINARY TIMESTAMP"),
        atoms: set("false true null unknown"),
        dateSQL: set("time "),
        operatorChars: /^[*+\-%<>!=&|^\/#@?~]/,
        support: set("decimallessFloat zerolessFloat binaryNumber hexNumber")
    });
});

/*
  How Properties of Mime Types are used by SQL Mode
  =================================================

  keywords:
    A list of keywords you want to be highlighted.
  builtin:
    A list of builtin types you want to be highlighted (if you want types to be of class "builtin" instead of "keyword").
  operatorChars:
    All characters that must be handled as operators.
  client:
    Commands parsed and executed by the client (not the server).
  support:
    A list of supported syntaxes which are not common, but are supported by more than 1 DBMS.
    * ODBCdotTable: .tableName
    * zerolessFloat: .1
    * doubleQuote
    * nCharCast: N'string'
    * charsetCast: _utf8'string'
    * commentHash: use # char for comments
    * commentSlashSlash: use // for comments
    * commentSpaceRequired: require a space after -- for comments
  atoms:
    Keywords that must be highlighted as atoms,. Some DBMS's support more atoms than others:
    UNKNOWN, INFINITY, UNDERFLOW, NaN...
  dateSQL:
    Used for date/time SQL standard syntax, because not all DBMS's support same temporal types.
*/
