/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */
package tools.dynamia.reports.core.services.impl

import org.springframework.jdbc.datasource.AbstractDataSource

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

class ReportDataSource extends AbstractDataSource {

    private String name
    private Object delegate

    ReportDataSource(String name, Object delegate) {
        this.name = name
        this.delegate = delegate

    }

    Object getDelegate() {
        return delegate
    }

    String getName() {
        return name
    }


    @Override
    String toString() {
        return name
    }

    @Override
    Connection getConnection() throws SQLException {
        if (delegate instanceof Connection) {
            return (Connection) delegate
        } else if (delegate instanceof DataSource) {
            return delegate.connection
        }
        return null
    }

    @Override
    Connection getConnection(String username, String password) throws SQLException {
        return getConnection()
    }
}
