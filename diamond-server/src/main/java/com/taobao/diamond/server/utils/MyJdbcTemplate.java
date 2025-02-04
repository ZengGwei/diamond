package com.taobao.diamond.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

/**
 * @Description MyJdbcTemplate
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:41 下午-2025
 */
public class MyJdbcTemplate extends JdbcTemplate {
    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        return this.update(new MyJdbcTemplate.MySimplePreparedStatementCreator(sql), pss);
    }

    protected int update(MyJdbcTemplate.MySimplePreparedStatementCreator psc, final PreparedStatementSetter pss) throws DataAccessException {
        this.logger.debug("Executing prepared SQL update");
        return (Integer)this.execute(psc, new PreparedStatementCallback<Integer>() {
            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
                Integer var3;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }

                    int rows = ps.executeUpdate();
                    if (MyJdbcTemplate.this.logger.isDebugEnabled()) {
                        MyJdbcTemplate.this.logger.debug("SQL update affected " + rows + " rows");
                    }

                    var3 = rows;
                } finally {
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer)pss).cleanupParameters();
                    }

                }

                return var3;
            }
        });
    }

    public <T> T execute(MyJdbcTemplate.MySimplePreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        if (this.logger.isDebugEnabled()) {
            String sql = getSql(psc);
            this.logger.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        }

        Connection con = DataSourceUtils.getConnection(this.getDataSource());
        PreparedStatement ps = null;

        Object var8;
        try {
            Connection conToUse = con;
            if (super.getNativeJdbcExtractor() != null && super.getNativeJdbcExtractor().isNativeConnectionNecessaryForNativePreparedStatements()) {
                conToUse = super.getNativeJdbcExtractor().getNativeConnection(con);
            }

            ps = psc.createPreparedStatement(conToUse);
            this.applyStatementSettings(ps);
            PreparedStatement psToUse = ps;
            if (super.getNativeJdbcExtractor() != null) {
                psToUse = super.getNativeJdbcExtractor().getNativePreparedStatement(ps);
            }

            T result = action.doInPreparedStatement(psToUse);
            this.handleWarnings(ps);
            var8 = result;
        } catch (SQLException var12) {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer)psc).cleanupParameters();
            }

            String sql = getSql(psc);
            psc = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.getExceptionTranslator().translate("PreparedStatementCallback", sql, var12);
        } finally {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer)psc).cleanupParameters();
            }

            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(con, this.getDataSource());
        }

        return (T) var8;
    }

    private static String getSql(Object sqlProvider) {
        return sqlProvider instanceof SqlProvider ? ((SqlProvider)sqlProvider).getSql() : null;
    }

    private static class MySimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
        protected final Log logger = LogFactory.getLog(this.getClass());
        private final String sql;

        public MySimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            con.prepareStatement("set names utf8mb4").executeQuery();
            return con.prepareStatement(this.sql);
        }

        public String getSql() {
            return this.sql;
        }
    }

}
