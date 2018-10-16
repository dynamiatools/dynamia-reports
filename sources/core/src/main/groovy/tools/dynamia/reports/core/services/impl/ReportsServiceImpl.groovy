package tools.dynamia.reports.core.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import tools.dynamia.domain.jdbc.JdbcHelper
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.services.AbstractService
import tools.dynamia.integration.sterotypes.Service
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.ReportsUtils
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportFilter
import tools.dynamia.reports.core.services.ReportsService

import javax.persistence.EntityManager
import javax.persistence.Query
import javax.sql.DataSource
import java.sql.Connection

@Service
class ReportsServiceImp extends AbstractService implements ReportsService {

    @Autowired
    private AccountServiceAPI accountServiceAPI


    @Override
    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource) {
        ReportData data = null
        loadDefaultFilters(report, filters)
        switch (report.queryLang) {
            case "sql":
                data = executeSQL(report, filters, datasource)
                break
            case "jpql":
                data = executeJPQL(report, filters, datasource)
                break
        }

        return data
    }

    def loadDefaultFilters(Report report, ReportFilters reportFilters) {
        boolean checkQuery = true
        if (!reportFilters.empty) {
            def filter = reportFilters.getFilter("accountId")
            if (filter != null) {
                reportFilters.add(filter, accountServiceAPI.currentAccountId)
                checkQuery = false
            }
        }

        if (checkQuery && report.queryScript.contains(":accountId")) {
            def filter = new ReportFilter(name: "accountId")
            def systemAccountId = accountServiceAPI.systemAccountId
            if (report.accountId != systemAccountId) {
                reportFilters.add(filter, report.accountId)
            } else {
                reportFilters.add(filter, accountServiceAPI.currentAccountId)
            }

        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Report loadReportModel(Long id) {
        def report = crudService().findSingle(Report, QueryParameters.with("id", id).add("accountId", QueryConditions.isNotNull()))
        report.fields.size()
        report.filters.size()
        report.queryScript.size()

        return report
    }

    static ReportData executeSQL(Report report, ReportFilters filters, ReportDataSource dataSource) {


        def jdbc = null
        if (dataSource.delegate instanceof DataSource) {
            jdbc = new JdbcHelper(dataSource.delegate as DataSource)
        } else {
            Connection connection = ReportsUtils.getJdbcConnection(dataSource)
            jdbc = new JdbcHelper(connection)
        }

        String sql = buildSqlScript(report.queryScript, filters)


        def result = filters.empty ? jdbc.query(sql) : jdbc.query(sql, filters.values)

        return ReportData.build(report, result)
    }

    static ReportData executeJPQL(Report report, ReportFilters filters, ReportDataSource dataSource) {
        EntityManager em = ReportsUtils.getJpaEntityManager(dataSource)


        String jpql = buildSqlScript(report.queryScript, filters)
        Query query = em.createQuery(jpql)
        filters.values.each { k, v ->
            query.setParameter(k, v)
        }

        List result = query.getResultList()

        return ReportData.build(report, result)
    }


    static String buildSqlScript(String query, ReportFilters filters) {
        StringBuilder filters_sql = new StringBuilder()

        if (!filters.empty && !query.contains("where")) {
            filters_sql.append("where 1=1 ")
            for (String filterName : filters.filtersNames) {
                ReportFilter filter = filters.getFilter(filterName)
                filters_sql.append(" and ").append(filter.condition)
            }
        }

        if (query.contains("<FILTERS>")) {
            query = query.replace("<FILTERS>", filters_sql.toString())
        } else {
            query = "$query $filters_sql"
        }

        return query
    }
}
