package org.easy.springboot.server.tomcat;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * 判断是否存在tomcat的依赖
 */
public class TomcatCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 通过类加载器加载tomcat jar包核心类判断是否引入该依赖
        try {
            Objects.requireNonNull(context.getClassLoader()).loadClass("org.apache.catalina.startup.Tomcat");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
