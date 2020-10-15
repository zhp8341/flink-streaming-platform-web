
package com.flink.streaming.web.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class AttributeUtils {

    /**
     * attributes字段中的属性之间的分隔符
     */
    private static final String SEMICOLON = ";";




    public static Set<String> toSet(String attributes) {
        if (StringUtils.isEmpty(attributes)) {
            return Collections.EMPTY_SET;
        }
        String[] keyValue = attributes.split(SEMICOLON);

        return new HashSet<>(Arrays.asList(keyValue));
    }
}
