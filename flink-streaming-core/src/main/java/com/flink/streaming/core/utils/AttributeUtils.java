
package com.flink.streaming.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class AttributeUtils {

    /**
     * attributes字段中的属性之间的分隔符
     */
    private static final String SEMICOLON = ";";

    /**
     * attributes字段中属性key和value之间的分隔符
     */
    private static final String COLON = "\\|";


    /**
     * 将attributes中的字符串转换成map,key1|v1;key2|v2
     *
     * @param attributes
     * @return
     */
    public static Map<String, String> attributesToMap(String attributes) {
        if ((attributes == null) || ("".equals(attributes))) {
            return new HashMap<String, String>();
        }

        Map<String, String> map = new HashMap<String, String>();

        // 获取所有属性数组
        String[] attrs = attributes.split(SEMICOLON);
        if ((attrs != null) && (attrs.length > 0)) {
            for (String attr : attrs) {
                // 获取key和value
                String[] keyValue = attr.split(COLON);
                if ((keyValue != null) && (keyValue.length > 1) && keyValue[0] != null) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return map;

    }


    public static Set<String> toSet(String attributes) {
        if (StringUtils.isEmpty(attributes)) {
            return Collections.EMPTY_SET;
        }
        String[] keyValue = attributes.split(SEMICOLON);

        return new HashSet<>(Arrays.asList(keyValue));
    }


}
