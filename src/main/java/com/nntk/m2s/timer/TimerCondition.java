package com.nntk.m2s.timer;

import cn.hutool.system.SystemUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Conditional(TimerCondition.class)
public class TimerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {

        return !(SystemUtil.getOsInfo().isWindows() || SystemUtil.getOsInfo().isMac());

    }
}