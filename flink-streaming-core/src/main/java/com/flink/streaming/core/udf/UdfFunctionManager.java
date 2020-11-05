package com.flink.streaming.core.udf;

import com.flink.streaming.core.utils.LoadJarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;

import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-28
 * @time 19:58
 */
@Slf4j
public class UdfFunctionManager {

    /**
     * 注册自定义的udfs
     * @author zhuhuipei
     * @date 2020-08-28
     * @time 21:39
     */
    public static void registerTableUDF(TableEnvironment tEnv, String udfJarPath, Map<String, String> udfMap) throws Exception {

        if (udfMap == null || StringUtils.isEmpty(udfJarPath)) {
            System.out.println("没有启用udf函数");
            return;
        }

        log.info("udfJarPath ={} udfMap={}", udfJarPath, udfMap);
        ClassLoader levelClassLoader = tEnv.getClass().getClassLoader();

        LoadJarUtil.loadJar(udfJarPath, levelClassLoader);

        for (Map.Entry<String, String> entry : udfMap.entrySet()) {
            String funcName = entry.getKey().trim();
            String funcClassName = entry.getValue().trim();
            log.info("注册udf 函数名{} ,类名:{}", funcName, funcClassName);
            levelClassLoader.loadClass(funcClassName);
            String className = Class.forName(funcClassName, Boolean.FALSE, levelClassLoader).getSuperclass().getName();
            switch (className) {
                case "org.apache.flink.table.functions.ScalarFunction":
                    registerScalaUDF(funcName, funcClassName, tEnv, levelClassLoader);
                    break;
                case "org.apache.flink.table.functions.TableFunction":
                    registerTableUDF(funcName, funcClassName, tEnv, levelClassLoader);
                    break;
                case "org.apache.flink.table.functions.AggregateFunction":
                    registerAggregateUDF(funcName, funcClassName, tEnv, levelClassLoader);
                    break;
                default:
                    throw new RuntimeException("udf只支持 (TABLE, SCALA, AGGREGATE) 当前函数是：" + className);
            }


        }
    }


    /**
     * udf
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 02:07
     */
    private static void registerScalaUDF(String funcName, String funcClassName, TableEnvironment tEnv, ClassLoader levelClassLoader) throws Exception {
        ScalarFunction udfFunc = Class.forName(funcClassName, Boolean.FALSE, levelClassLoader).asSubclass(ScalarFunction.class).newInstance();
        tEnv.createTemporarySystemFunction(funcName, udfFunc);
    }


    /**
     * UDTF
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 02:16
     */
    private static void registerTableUDF(String funcName, String funcClassName, TableEnvironment tEnv, ClassLoader levelClassLoader) throws Exception {
        checkStreamTableEnv(tEnv);
        TableFunction udfFunc = Class.forName(funcClassName, Boolean.FALSE, levelClassLoader).asSubclass(TableFunction.class).newInstance();
        ((StreamTableEnvironment) tEnv).createTemporarySystemFunction(funcName, udfFunc);
    }


    /**
     * UDAF
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 02:16
     */
    private static void registerAggregateUDF(String funcName, String funcClassName, TableEnvironment tEnv, ClassLoader levelClassLoader) throws Exception {
        checkStreamTableEnv(tEnv);
        AggregateFunction udfFunc = Class.forName(funcClassName, Boolean.FALSE, levelClassLoader).asSubclass(AggregateFunction.class).newInstance();
        ((StreamTableEnvironment) tEnv).createTemporarySystemFunction(funcName, udfFunc);
    }


    private static void checkStreamTableEnv(TableEnvironment tableEnv) {
        if (!(tableEnv instanceof StreamTableEnvironment)) {
            throw new RuntimeException("no support tableEnvironment class for " + tableEnv.getClass().getName());
        }
    }


}
