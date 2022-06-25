package com.flink.streaming.web.model.vo;

/**
 * 常量定义
 *
 * @author wxj
 * @version V1.0
 * @date 2021年11月30日 上午10:20:27
 */
public class Constant {

  /**
   * 前端请求响应状态：成功
   */
  public static final String RESPONE_STATUS_SUCCESS = "200";
  /**
   * 前端请求响应状态：重定向
   */
  public static final String RESPONE_STATUS_MOVED = "301";
  /**
   * 前端请求响应状态：未认证
   */
  public static final String RESPONE_STATUS_UNAUTH = "401";
  /**
   * 前端请求响应状态：未授权
   */
  public static final String RESPONE_STATUS_FORBIDDEN = "403";
  /**
   * 前端请求响应状态：资源缺失
   */
  public static final String RESPONE_STATUS_NOTFOUND = "404";
  /**
   * 前端请求响应状态：应用错误
   */
  public static final String RESPONE_STATUS_ERROR = "500";

  /**
   * 超级用户角色
   */
  public static final int ADMIN_ROLE_ID = 1;
  /**
   * 超级用户编号
   */
  public static final int ADMIN_OP_ID = 1;
  /**
   * 超级用户帐号
   */
  public static final String ADMIN_OP_NAME = "admin";
  /**
   * 超级用户权限
   */
  public static final String AUTH_ADMIN = "ROLE_ADMIN";
  /**
   * 角色权限编码前缀
   */
  public static final String AUTH_ROLE_PREFIX = "ROLE_";
  /**
   * 数据删除状态
   */
  public static final short DATA_STATUS_DELETE = -1;
  /**
   * 数据有效状态
   */
  public static final short DATA_STATUS_ENABEL = 1;
  /**
   * 数据无效状态
   */
  public static final short DATA_STATUS_DISABLE = 0;
  /**
   * 数据锁定状态
   */
  public static final short DATA_STATUS_LOCKED = 2;

  /**
   * 根结点(应用模块)
   */
  public static final int MENU_TYPE_ROOT = 1;
  /**
   * 菜单项
   */
  public static final int MENU_TYPE_ITEM = 2;
  /**
   * 叶子菜单项
   */
  public static final int MENU_TYPE_LEAF = 3;
  /**
   * 功能按钮项
   */
  public static final int MENU_TYPE_BUTTON = 4;
  /**
   * 功能权限
   */
  public static final int MENU_TYPE_FUNCTION = 5;

}
