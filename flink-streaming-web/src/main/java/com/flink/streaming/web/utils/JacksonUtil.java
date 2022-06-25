package com.flink.streaming.web.utils;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * JacksonUtil
 *
 * @author 文香炯
 * @version V1.0
 * @date 2017年1月11日 下午2:42:09
 */
public class JacksonUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  /**
   * 对象转换成Json字符串
   *
   * @param obj
   * @return
   * @throws JsonProcessingException
   * @author 文香炯
   * @date 2017年1月11日 下午2:55:10
   * @version V1.0
   */
  public static String toJsonString(Object obj) {
    if (obj == null) {
      return null;
    }
    String json = null;
    try {
      json = mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return json;
  }

  /**
   * json字符串转换成JsonNode
   *
   * @param json
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @author 文香炯
   * @date 2017年1月11日 下午8:31:06
   * @version V1.0
   */
  public static JsonNode parse(String json) {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    try {
      return mapper.readTree(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Json字符串转换成对象
   *
   * @param json
   * @param clazz
   * @return
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   * @author 文香炯
   * @date 2017年1月11日 下午4:35:50
   * @version V1.0
   */
  public static <T> Object parse(String json, Class<T> clazz) {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取子JsonNode
   *
   * @param json
   * @param fieldName
   * @return
   * @throws IOException
   * @throws JsonProcessingException
   * @author 文香炯
   * @date 2017年1月12日 下午8:48:00
   * @version V1.0
   */
  public static JsonNode parseByField(String json, String fieldName)
      throws JsonProcessingException, IOException {
    JsonNode node = mapper.readTree(json);
    JsonNode pnode = node.findPath(fieldName);
    if (pnode == null || pnode instanceof MissingNode) {
      return null;
    }
    return pnode;
  }

  /**
   * pojo对象转换成JsonNode
   *
   * @param json
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @author 文香炯
   * @date 2017年1月11日 下午8:31:06
   * @version V1.0
   */
  public static JsonNode toJsonNode(Object pojo) {
    if (pojo == null) {
      return null;
    }
    return mapper.valueToTree(pojo);
  }

  /**
   * 获取子JsonNode
   *
   * @param json
   * @param fieldName
   * @return
   * @author 文香炯
   * @date 2017年1月12日 下午8:48:00
   * @version V1.0
   */
  public static JsonNode toJsonNodeByField(JsonNode json, String fieldName) {
    JsonNode pnode = json.findPath(fieldName);
    if (pnode == null || pnode instanceof MissingNode) {
      return null;
    }
    return pnode;
  }

  /**
   * JsonNode指定转换成对象
   *
   * @param json
   * @param paramname
   * @param clazz
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @author 文香炯
   * @date 2017年1月11日 下午8:26:52
   * @version V1.0
   */
  @SuppressWarnings("unchecked")
  public static <T> T toObject(JsonNode json, Class<T> clazz)
      throws JsonProcessingException, IOException {
    if (json == null || json instanceof MissingNode) {
      return null;
    }
    if (!(json instanceof TextNode) && clazz.getName().equals(String.class.getName())) {
      return (T) json.toString();
    }
    return mapper.treeToValue(json, clazz);
  }

  public static <T> T toObject(String json, JavaType javatype)
      throws JsonProcessingException, IOException {
    return mapper.readValue(json, javatype);
  }

  public static <T> T toObject(JsonNode json, JavaType javatype)
      throws JsonProcessingException, IOException {
    return mapper.convertValue(json, javatype);
  }

  /**
   * 指定类（泛型）的转换
   *
   * @param json
   * @param clazz
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @author 文香炯
   * @date 2018年8月8日 上午9:47:39
   * @version V1.0
   */
  public static <T> T toObject(String json, Class<?>... clazz)
      throws JsonProcessingException, IOException {
    if (clazz == null || clazz.length == 0) {
      return null;
    }
    Class<?> parametrized = clazz[0];
    Class<?>[] parameterClasses = new Class[clazz.length - 1];
    for (int i = 1; i < clazz.length; i++) {
      parameterClasses[i - 1] = clazz[i];
    }
    JavaType javatype = mapper.getTypeFactory()
        .constructParametricType(parametrized, parameterClasses);
    return mapper.readValue(json, javatype);
  }

  /**
   * 获取Jackson的JavaType
   *
   * @param parametrized
   * @param parameterClasses
   * @return
   * @author 文香炯
   * @date 2017年1月12日 下午8:12:28
   * @version V1.0
   */
  public static JavaType getJavaType(Class<?> parametrized, Class<?>[] parameterClasses) {
    if (parametrized == null || parameterClasses == null || parameterClasses.length == 0) {
      return null;
    }
    JavaType javatype = mapper.getTypeFactory()
        .constructParametricType(parametrized, parameterClasses);
    return javatype;
  }

  static {
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    // 反序列化时忽略多余的字段
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 自定义格式化日期
    mapper.setDateFormat(new MyDateTimeFormat("yyyy-MM-dd HH:mm:ss"));
    // 解决时间戳为整数转换的问题
    MyDateDeserializer myDateDeserializer = new MyDateDeserializer();
    SimpleModule newModule = new SimpleModule("MyDateDeserializer", PackageVersion.VERSION);
    newModule.addDeserializer(Date.class, myDateDeserializer);
    mapper.registerModule(newModule);
  }

  /**
   * 自定义格式化日期类
   */
  private static class MyDateTimeFormat extends SimpleDateFormat {

    private static final long serialVersionUID = 1899483410257705750L;

     MyDateTimeFormat(String pattern) {
      super(pattern);
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
      if (date.getTime() % 1000 > 0) {
        SimpleDateFormat formatTimstamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatTimstamp.format(date, toAppendTo, fieldPosition);
      } else {
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatDatetime.format(date, toAppendTo, fieldPosition);
      }
    }

    //CHECKSTYLE:OFF
    @Override
    public Date parse(String source, ParsePosition pos) {
      if (source == null || source.length() == 0) {
        return null;
      }
      if (StringUtils.isNumeric(source)) {
        return new Date(Long.valueOf(source));
      } else if (source.length() <= 10) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        return formatDate.parse(source, pos);
      } else if (source.length() <= 19) {
        SimpleDateFormat formatDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatDatetime.parse(source, pos);
      } else if (source.length() == 23) {
        SimpleDateFormat formatTimstamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatTimstamp.parse(source, pos);
      }
      return super.parse(source, pos);
    }
  }
  //CHECKSTYLE:ON

  /**
   * 解决时间戳为整数转换的问题
   */
  private static class MyDateDeserializer extends DateDeserializer {

    private static final long serialVersionUID = -6218693745160760598L;

    @Override
    protected Date _parseDate(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      Date parseDate = null;
      try {
        parseDate = super._parseDate(jp, ctxt);
      } catch (Exception ex) {
        String dateStr = jp.getText().trim();
        parseDate = new Date(Long.valueOf(dateStr));
      }
      return parseDate;
    }
  }
}
