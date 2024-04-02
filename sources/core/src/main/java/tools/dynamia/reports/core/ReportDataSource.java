package tools.dynamia.reports.core;

import org.springframework.jdbc.datasource.AbstractDataSource;

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
        if (delegate instanceof Connection) {
            return (Connection) delegate;
        } else if (delegate instanceof DataSource) {
            return ((DataSource) delegate).getConnection();
        }

        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

}
