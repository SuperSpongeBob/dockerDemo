package org.example.dockerdemo.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.example.dockerdemo.entity.base.BaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AutoFillInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];

        if (SqlCommandType.INSERT == sqlCommandType) {
            fillBaseEntity(parameter, true);
        } else if (SqlCommandType.UPDATE == sqlCommandType) {
            fillBaseEntity(parameter, false);
        }

        return invocation.proceed();
    }

    private void fillBaseEntity(Object parameter, boolean isInsert) {
        if (parameter == null) return;

        if (parameter instanceof BaseEntity) {
            if (isInsert) ((BaseEntity) parameter).preInsert();
            else ((BaseEntity) parameter).preUpdate();
            return;
        }

        if (parameter instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameter;
            for (Object value : paramMap.values()) {
                if (value instanceof BaseEntity) {
                    if (isInsert) ((BaseEntity) value).preInsert();
                    else ((BaseEntity) value).preUpdate();
                } else if (value instanceof Collection) {
                    for (Object item : (Collection<?>) value) {
                        if (item instanceof BaseEntity) {
                            if (isInsert) ((BaseEntity) item).preInsert();
                            else ((BaseEntity) item).preUpdate();
                        }
                    }
                }
            }
        }

        try {
            MetaObject metaObject = SystemMetaObject.forObject(parameter);
            for (String name : metaObject.getGetterNames()) {
                Object value = metaObject.getValue(name);
                if (value instanceof BaseEntity) {
                    if (isInsert) ((BaseEntity) value).preInsert();
                    else ((BaseEntity) value).preUpdate();
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
