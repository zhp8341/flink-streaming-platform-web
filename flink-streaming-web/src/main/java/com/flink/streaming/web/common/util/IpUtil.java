package com.flink.streaming.web.common.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2018/7/19
 * @time 下午6:18
 */
public class IpUtil {

    private static String ip;

    private static IpUtil ipUtil=new IpUtil();

    private IpUtil(){
        ip = getIp();
    }

    public static IpUtil getInstance(){
        return ipUtil;
    }



    /**
     * 获取本机的ip地址
     */
    public  String getLocalIP() {
        if (StringUtils.isEmpty(ip)) {
            return getIp();
        }
        return ip;
    }

    private  String getIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }
        byte[] ipAddr = addr.getAddress();
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }

    public static void main(String[] args){
     System.out.println(IpUtil.getInstance().getLocalIP());
     System.out.println(IpUtil.getInstance().getLocalIP());
     //System.out.println(IpUtil.getInstance().getLocalIP());
    }
}
