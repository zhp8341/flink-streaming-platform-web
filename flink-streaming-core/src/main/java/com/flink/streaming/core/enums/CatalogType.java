package com.flink.streaming.core.enums;

import lombok.Getter;

/**
 * @author Jim Chen
 * @date 2021-01-21
 * @time 01:18
 */
@Getter
public enum CatalogType {

    MEMORY,

    HIVE,

    JDBC,

    POSTGRES

}
