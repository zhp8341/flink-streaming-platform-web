package com.flink.streaming.web.common.util;

import com.flink.streaming.web.exceptions.BizException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2018/7/19
 * @time 下午6:18
 */
@Slf4j
public final class IpUtil {

  private static String ip;

  private static IpUtil ipUtil = new IpUtil();

  private IpUtil() {
    ip = getIp();
    log.info("本机ip：{}", ip);
  }

  public static IpUtil getInstance() {
    return ipUtil;
  }


  /**
   * 获取本机的ip地址
   */
  public String getLocalIP() {
    if (StringUtils.isEmpty(ip)) {
      return getIp();
    }
    return ip;
  }

  private String getIp() {
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

  public String getHostIp() {
    try {
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = allNetInterfaces.nextElement();
        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress ip = addresses.nextElement();
          if (ip != null
              && ip instanceof Inet4Address
              && !ip.isLoopbackAddress()
              //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
              && ip.getHostAddress().indexOf(":") == -1) {
            log.info("本机的IP = {} ", ip.getHostAddress());
            return ip.getHostAddress();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  private List<String> getIpAddress() {
    try {
      List<String> list = new LinkedList<>();
      Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
      while (enumeration.hasMoreElements()) {
        NetworkInterface network = (NetworkInterface) enumeration.nextElement();
        if (network.isVirtual() || !network.isUp()) {
          continue;
        } else {
          Enumeration addresses = network.getInetAddresses();
          while (addresses.hasMoreElements()) {
            InetAddress address = (InetAddress) addresses.nextElement();
            if (address != null && (address instanceof Inet4Address)) {
              list.add(address.getHostAddress());
            }
          }
        }
      }
      return list;
    } catch (Exception e) {
      return null;
    }
  }


  public static String getHostName() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostName(); //获取本机计算机名称
    } catch (Exception e) {
      log.error("getHostName is error", e);
      throw new BizException(e.getMessage());
    }


  }

  public static void main(String[] args) {
    System.out.println(IpUtil.getInstance().getLocalIP());
    System.out.println(IpUtil.getInstance().getLocalIP());
    System.out.println(IpUtil.getInstance().getIpAddress());
    System.out.println(IpUtil.getInstance().getHostIp());
    System.out.println(getHostName());
    System.out.println(System.getProperty("user.name"));
  }
}
