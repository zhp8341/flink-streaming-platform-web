package com.flink.streaming.common.model;


import com.flink.streaming.common.enums.SqlCommand;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 02:56
 */
@Data
public class SqlCommandCall {

    public SqlCommand sqlCommand;

    public String[] operands;

    public SqlCommandCall(SqlCommand sqlCommand, String[] operands) {
        this.sqlCommand = sqlCommand;
        this.operands = operands;
    }

    public SqlCommandCall(String[] operands) {
        this.operands = operands;
    }
}
