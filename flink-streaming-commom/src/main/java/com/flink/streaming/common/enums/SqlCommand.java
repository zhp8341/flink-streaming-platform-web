package com.flink.streaming.common.enums;

import com.flink.streaming.common.constant.SystemConstant;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 02:49
 */
@Getter
public enum SqlCommand {
    INSERT_INTO(
            "(INSERT\\s+INTO.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    INSERT_OVERWRITE(
            "(INSERT\\s+OVERWRITE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    CREATE_TABLE(
            "(CREATE\\s+TABLE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    CREATE_FUNCTION(
            "(CREATE\\s+FUNCTION.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    CREATE_VIEW(
            "(CREATE\\s+VIEW.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    USE(
            "(USE\\s+(?!CATALOG)(.*))",
            (operands) -> Optional.of(new String[]{operands[0]})),


    USE_CATALOG(
            "(USE\\s+CATALOG.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    DROP_TABLE(
            "(DROP\\s+TABLE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),
    DROP_DATABASE(
            "(DROP\\s+DATABASE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),
    DROP_VIEW(
            "(DROP\\s+VIEW.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),
    DROP_FUNCTION(
            "(DROP\\s+FUNCTION.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    ALTER_TABLE(
            "(ALTER\\s+TABLE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),
    ALTER_DATABASE(
            "(ALTER\\s+DATABASE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),
    ALTER_FUNCTION(
            "(ALTER\\s+FUNCTION.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    SELECT(
            "(WITH.*SELECT.*|SELECT.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    SHOW_CATALOGS(
            "SHOW\\s+CATALOGS",
            (operands) -> Optional.of(new String[]{"SHOW CATALOGS"})),

    SHOW_DATABASES(
            "SHOW\\s+DATABASES",
            (operands) -> Optional.of(new String[]{"SHOW DATABASES"})),

    SHOW_TABLES(
            "SHOW\\s+TABLES",
            (operands) -> Optional.of(new String[]{"SHOW TABLES"})),

    SHOW_FUNCTIONS(
            "SHOW\\s+FUNCTIONS",
            (operands) -> Optional.of(new String[]{"SHOW FUNCTIONS"})),

    SHOW_MODULES(
            "SHOW\\s+MODULES",
            (operands) -> Optional.of(new String[]{"SHOW MODULES"})),

    CREATE_CATALOG(
            "(CREATE\\s+CATALOG.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    SET(
            "SET(\\s+(\\S+)\\s*=(.*))?",
            (operands) -> {
                if (operands.length >= 3) {
                    if (operands[0] == null) {
                        return Optional.of(new String[0]);
                    }
                } else {
                    return Optional.empty();
                }
                return Optional.of(new String[]{operands[1], operands[2]});
            }),
    
    BEGIN_STATEMENT_SET("BEGIN\\s+STATEMENT\\s+SET", (operands) -> Optional.of(new String[]{"BEGIN STATEMENT SET"})),
    
    END("END", (operands) -> Optional.of(new String[]{"END"}))
    ;

    public final Pattern pattern;

    public final Function<String[], Optional<String[]>> operandConverter;


    SqlCommand(String matchingRegex, Function<String[], Optional<String[]>> operandConverter) {
        this.pattern = Pattern.compile(matchingRegex, SystemConstant.DEFAULT_PATTERN_FLAGS);
        this.operandConverter = operandConverter;
    }


}
