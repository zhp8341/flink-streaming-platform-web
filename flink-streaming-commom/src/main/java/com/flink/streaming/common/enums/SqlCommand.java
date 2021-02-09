package com.flink.streaming.common.enums;

import lombok.Getter;
import com.flink.streaming.common.constant.SystemConstant;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 02:49
 */
@Getter
public enum SqlCommand {
    INSERT_INTO(
            "(INSERT\\s+INTO.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    CREATE_TABLE(
            "(CREATE\\s+TABLE.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    CREATE_FUNCTION(
            "(CREATE\\s+FUNCTION.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),

    CREATE_VIEW(
            "(CREATE\\s+VIEW.*)",
            (operands) -> Optional.of(new String[]{operands[0]})),


    SET(
            "SET(\\s+(\\S+)\\s*=(.*))?",
            (operands) -> {
                if (operands.length >= 3) {
                    if (operands[0] == null) {
                        return Optional.of(new String[0]);
                    }
                } else {
                    return Optional.empty();
                }
                return Optional.of(new String[]{operands[1], operands[2]});
            });

    public final Pattern pattern;

    public final Function<String[], Optional<String[]>> operandConverter;


    private static final Function<String[], Optional<String[]>> NO_OPERANDS =
            (operands) -> Optional.of(new String[0]);


    SqlCommand(String matchingRegex, Function<String[], Optional<String[]>> operandConverter) {
        this.pattern = Pattern.compile(matchingRegex, SystemConstant.DEFAULT_PATTERN_FLAGS);
        this.operandConverter = operandConverter;
    }


}
