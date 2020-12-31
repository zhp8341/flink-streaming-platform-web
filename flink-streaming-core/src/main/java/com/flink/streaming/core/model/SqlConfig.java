package com.flink.streaming.core.model;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 02:24
 */
@Data
public class SqlConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> ddlList = new ArrayList<>();

    private List<String> dmlList = new ArrayList<>();

    private Map<String, String> mapConfig = new HashMap<>();

    private Map<String, String> udfMap = new HashMap<>();

    private List<String> udfList = new ArrayList<>();

    private List<String> viewList = new ArrayList<>();

    public static SqlConfig toSqlConfig(List<SqlCommandCall> sqlCommandCallList) {
        if (CollectionUtils.isEmpty(sqlCommandCallList)) {
            return null;
        }

        SqlConfig sqlConfig = new SqlConfig();

        for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {

            switch (sqlCommandCall.sqlCommand) {
                case SET:
                    sqlConfig.getMapConfig().put(sqlCommandCall.operands[0], sqlCommandCall.operands[1]);
                    break;
                case CREATE_FUNCTION:
                    sqlConfig.getUdfList().add(sqlCommandCall.operands[0]);
                    break;
                case CREATE_VIEW:
                    sqlConfig.getViewList().add(sqlCommandCall.operands[0]);
                    break;
                case CREATE_TABLE:
                    sqlConfig.getDdlList().add(sqlCommandCall.operands[0]);
                    break;
                case INSERT_INTO:
                    sqlConfig.getDmlList().add(sqlCommandCall.operands[0]);
                    break;
                default:
                    break;
            }
        }
        return sqlConfig;
    }


}
