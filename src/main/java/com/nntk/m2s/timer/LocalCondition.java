package com.nntk.m2s.timer;

import cn.hutool.system.SystemUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Conditional(LocalCondition.class)
public class LocalCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {

        return  SystemUtil.getOsInfo().isMac();

    }
}