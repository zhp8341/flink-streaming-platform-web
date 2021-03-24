package com.flink.streaming.common;

import com.flink.streaming.common.enums.SqlCommand;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/21
 * @time 22:35
 */
public class TestSqlCommand {

    @Test
    public void testCommands() {
       // testValidSqlCommand("select  'xxx', f0,f1,f2 from source_table",SqlCommand.SELECT);
        testValidSqlCommand("show CATALOGS ",SqlCommand.SHOW_CATALOGS);
        testValidSqlCommand(" USE CATALOGS xxx ",SqlCommand.USE);
    }


    private void testValidSqlCommand( String matcherStr, SqlCommand sqlCommand) {

        final Matcher matcher = sqlCommand.getPattern() .matcher(matcherStr);
        if (matcher.matches()) {
            System.out.println("WITH 匹配成功:"+true+"matcherStr="+matcherStr);
            final String[] groups = new String[matcher.groupCount()];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i + 1);
            }
            System.out.println("匹配到的值是："+ Arrays.toString(groups));
        } else {
            System.out.println("WITH 匹配成功:"+true+"matcherStr="+matcherStr);
        }
        System.out.println("######################### \n \n");
    }
}
