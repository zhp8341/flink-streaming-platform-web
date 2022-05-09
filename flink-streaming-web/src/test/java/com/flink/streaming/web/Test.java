package com.flink.streaming.web;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 00:06
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        boolean ok= StrUtil.endWith("/123123/12312312/", "/");
//        System.out.println(ok);
//        boolean ok2= StrUtil.endWith("/123123/12312312", "/");
//        System.out.println(ok2);
//
//
//        boolean ok3= StrUtil.startWith("/123123/12312312", "/");
//        System.out.println(ok3);

//        String input="a_123123_flink-";
//        System.out.println(input.matches("[0-9A-Za-z_]*"));

//        String extJarPath = "/u123123/" + "udf/lib/" + DateUtil.formatDate(new Date()) + "/" + UUID.fastUUID();
//        System.out.println(extJarPath);
//
//
//        //String a = "Sun Nov 13 21:56:41 +0800 2011";
//        String a = "08/Sep/2020:16:21:59 +0800";
//       // SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);//MMM dd hh:mm:ss Z yyyy
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z ", Locale.US);//MMM dd hh:mm:ss Z yyyy
//
//        System.out.println(sdf.parse(a));

        String createData = null;


        Timestamp t1 = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd").parse(createData)));


        System.out.println(t1);
//        String dtime1 = "08/Sep/2020";
//        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
//        Date date = sdf1.parse(dtime1);
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String sDt = sdf2.format(date);
//        System.out.println(sDt);

    }
}
