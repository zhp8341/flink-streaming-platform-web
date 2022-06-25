package com.flink.streaming.web.exceptions;

import com.flink.streaming.web.enums.SysErrorEnum;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 21:59
 */
@Data
public class BizException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private static final String ERROR_CODE_500 = SysErrorEnum.CUSTOMER_SYSTEM_ERROR.getCode();
  private static final String ERROR_MESSAGE_500 = SysErrorEnum.SYSTEM_ERROR.getErrorMsg();


  /**
   * 错误异常业务码
   */
  private String code;

  private String errorMsg;


  public BizException(String errorMsg) {
    super(errorMsg);
    this.code = ERROR_CODE_500;
    this.errorMsg = errorMsg;
  }

  public BizException(SysErrorEnum sysErrorEnum) {
    super(sysErrorEnum.getErrorMsg());
    this.code = sysErrorEnum.getCode();
    this.errorMsg = sysErrorEnum.getErrorMsg();
  }

  public BizException(String errorMsg, String code) {
    super(errorMsg);
    this.code = code;
    this.errorMsg = errorMsg;
  }

  public BizException(String errorMsg, Throwable cause, String code) {
    super(errorMsg, cause);
    this.code = code;
    this.errorMsg = errorMsg;
  }

  public BizException(Throwable cause, String code) {
    super(cause);
    this.code = code;
    this.errorMsg = ERROR_MESSAGE_500;
  }

}
