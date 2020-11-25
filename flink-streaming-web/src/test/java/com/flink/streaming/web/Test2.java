package com.flink.streaming.web;

import com.flink.streaming.web.common.RestResult;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-21
 * @time 23:39
 */
public class Test2 {

    public static void main(String[] args) {


        checkFlinkRunConfig("-p 1231 -yjm -yn 123");
    }

    private static RestResult checkFlinkRunConfig(String flinkRunConfig) {
        try {
            String[] config = flinkRunConfig.split(" ");
            Options options = new Options();
            options.addOption("p", false, "");
            options.addOption("yjm", false, "");
            options.addOption("yn", false, "");
            options.addOption("ytm", false, "");
            options.addOption("ys", false, "");
            options.addOption("yD", false, "");
            new DefaultParser().parse(options, config);
        } catch (UnrecognizedOptionException e) {
            e.printStackTrace();
            return RestResult.error("flink运行配置参数校验通不过,不允许使用参数：" + e.getOption() + " 参数只支持 -p -yjm -yn -ytm -ys -yD");
        } catch (Exception e) {
            e.printStackTrace();
            return RestResult.error("flink运行配置参数校验通不过");
        }

        return null;
    }

}
