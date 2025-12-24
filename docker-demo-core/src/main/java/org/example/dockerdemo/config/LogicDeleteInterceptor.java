package org.example.dockerdemo.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class LogicDeleteInterceptor implements Interceptor {

    private static final String DISABLE_TOKEN = "/*logic_delete:off*/";
    private static final String DELETE_FLAG_COLUMN = "delete_flag";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        String originalSql = (String) metaObject.getValue("delegate.boundSql.sql");
        if (originalSql == null) return invocation.proceed();

        String sqlTrimmedLower = originalSql.trim().toLowerCase();

        if (!sqlTrimmedLower.startsWith("select") || sqlTrimmedLower.contains(DISABLE_TOKEN.toLowerCase())) {
            return invocation.proceed();
        }

        if (sqlTrimmedLower.contains(DELETE_FLAG_COLUMN)) {
            return invocation.proceed();
        }

        String newSql = appendLogicDeleteCondition(originalSql);
        metaObject.setValue("delegate.boundSql.sql", newSql);
        return invocation.proceed();
    }

    private String appendLogicDeleteCondition(String sql) {
        String trimmed = sql.trim();
        boolean hasWhere = trimmed.toLowerCase().contains(" where ");

        StringBuilder sb = new StringBuilder(trimmed.length() + 32);
        sb.append(trimmed);

        if (trimmed.endsWith(";")) sb.setLength(sb.length() - 1);

        if (hasWhere) sb.append(" AND ").append(DELETE_FLAG_COLUMN).append(" = 0");
        else sb.append(" WHERE ").append(DELETE_FLAG_COLUMN).append(" = 0");

        return sb.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
