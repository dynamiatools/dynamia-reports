package tools.dynamia.reports.core

import tools.dynamia.integration.Containers
import tools.dynamia.reports.api.EntityFilterProvider
import tools.dynamia.reports.api.EnumFilterProvider
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
}
