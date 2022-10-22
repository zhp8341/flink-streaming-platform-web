package com.flink.streaming.web.common.util;

import com.flink.streaming.web.exceptions.BizException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
      InetAddress inetAddress = null;
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = allNetInterfaces.nextElement();
        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          inetAddress = addresses.nextElement();
          if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()
              && inetAddress.getHostAddress().indexOf(":") == -1) {
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (Exception e) {
      log.error("getHostIp is error", e);
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
  public static InetAddress getInetAddress() throws SocketException {
    Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
    InetAddress ipHost = null;
    while (allNetInterfaces.hasMoreElements()) {
      NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
      Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
      while (addresses.hasMoreElements()) {
        ipHost = (InetAddress) addresses.nextElement();
        if (ipHost != null && ipHost instanceof Inet4Address) {
          return ipHost;
        }
      }
    }
    return ipHost;
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

  public static void main(String[] args) throws Exception {
    System.out.println(IpUtil.getInstance().getLocalIP());
    System.out.println(IpUtil.getInstance().getLocalIP());
    System.out.println(IpUtil.getInstance().getIpAddress());
    System.out.println(IpUtil.getInstance().getHostIp());
    System.out.println(getHostName());
    System.out.println(System.getProperty("user.name"));
    System.out.println(getInetAddress().getHostName());
  }
}
