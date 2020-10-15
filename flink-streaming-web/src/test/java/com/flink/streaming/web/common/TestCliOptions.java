package com.flink.streaming.web.common;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-11
 * @time 01:35
 */
public class TestCliOptions {

    public static void main(String[] args) throws ParseException {

        //String[] config={"-p","x","-yqu","xxx"};
//        String[] config={"-yqu","xxx"};
        String[] config={"-yqu","xxx"};
        Options options = new Options();
        options.addOption("p", false, "");
        options.addOption("yqu", false, "");
        CommandLineParser parser = new DefaultParser();
        CommandLine cl = parser.parse(options, config);
        String yqu = cl.getOptionValue("yqu");
        System.out.println(yqu);
        System.out.println(cl.hasOption("yqu"));

    }
}
