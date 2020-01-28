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
class ReportsServiceImpl extends AbstractService implements ReportsService {

    @Autowired
    private AccountServiceAPI accountServiceAPI


    @Override
    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource) {
        log("Executing query for report: $report.name - $report.queryLang")
        long start = System.currentTimeMillis()
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
        long end = System.currentTimeMillis()
        log("Report $report.name executed in ${end - start}ms")
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
        report.charts.size()

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
        query = query.replace("\n", " ").replace("\t", " ")
        StringBuilder filters_sql = new StringBuilder("")

        if (!filters.empty && !query.contains("where")) {
            filters_sql.append("where 1=1 ")
        }

        for (String filterName : filters.filtersNames) {
            ReportFilter filter = filters.getFilter(filterName)
            if (filter.condition) {
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
