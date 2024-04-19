package tools.dynamia.reports.core;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import tools.dynamia.domain.jdbc.JdbcHelper;
import tools.dynamia.reports.core.domain.ReportDataSourceConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ReportDataSource extends AbstractDataSource {


    private String name;
    private Object delegate;

    public ReportDataSource(String name, Object delegate) {
        this.name = name;
        this.delegate = delegate;

    }

    public Object getDelegate() {
        return delegate;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (delegate instanceof Connection connection) {
            return connection;
        } else if (delegate instanceof DataSource dataSource) {
            return dataSource.getConnection();
        } else if (delegate instanceof ReportDataSourceConfig config) {
            return newConnection(config);
        }

        return null;
    }

    public static Connection newConnection(ReportDataSourceConfig config) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ReportsException("Cannot create database connection using datasource: " + config.getName());
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

}
