/*-
 * #%L
 * DynamiaReports - Core
 * %%
 * Copyright (C) 2018 - 2019 Dynamia Soluciones IT SAS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package tools.dynamia.reports.core

import tools.dynamia.domain.ValidationError
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.reports.api.EntityFilterProvider
import tools.dynamia.reports.api.EnumFilterProvider
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.impl.ReportDataSource

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import java.sql.Connection

class ReportsUtils {

    static Connection getJdbcConnection(ReportDataSource dataSource) {
        if (dataSource.delegate instanceof Connection) {
            return dataSource.delegate as Connection
        } else if (dataSource.delegate instanceof DataSource) {
            return (dataSource.delegate as DataSource).connection
        } else {
            throw new ReportsException("Error obtaining database connection from report datasource $dataSource")
        }
    }

    static EntityManager getJpaEntityManager(ReportDataSource dataSource) {
        if (dataSource.delegate instanceof EntityManager) {
            return dataSource.delegate as EntityManager
        } else if (dataSource.delegate instanceof EntityManagerFactory) {
            return (dataSource.delegate as EntityManagerFactory).createEntityManager()
        } else {
            throw new ReportsException("Error getting entity manager from datasource " + dataSource)
        }
    }

    static EnumFilterProvider findEnumFilterProvider(String className) {
        return Containers.get().findObjects(EnumFilterProvider).find { it.enumClassName == className }
    }

    static EntityFilterProvider findEntityFilterProvider(String className) {
        return Containers.get().findObjects(EntityFilterProvider).find { it.entityClassName == className }
    }

    static List<EnumFilterProvider> findEnumFiltersProviders() {
        return Containers.get().findObjects(EnumFilterProvider)
    }

    static List<EntityFilterProvider> findEntityFiltersProvider() {
        return Containers.get().findObjects(EntityFilterProvider)
    }

    static String checkQuery(String query) {
        if (query == null) {
            throw new ValidationError("Invalid Query")
        }
        if (query.toLowerCase().contains("delete ") || query.toLowerCase().contains("update ")) {
            throw new ValidationError("Danger query detected: $query")
        }

        if (query.contains(":accountId")) {
            AccountServiceAPI accountServiceAPI = Containers.get().findObject(AccountServiceAPI)
            if (accountServiceAPI != null) {
                query = query.replaceAll(":accountId", String.valueOf(accountServiceAPI.getCurrentAccountId()))
            }
        }
        return query
    }

    static ReportDataSource findDatasource(Report report) {
        if (report.queryLang == "sql") {
            DataSource dataSource = Containers.get().findObject(DataSource.class)
            return new ReportDataSource("Database", dataSource)
        } else {
            EntityManagerFactory em = Containers.get().findObject(EntityManagerFactory.class)
            return new ReportDataSource("EntityManager", em)
        }

    }
}
