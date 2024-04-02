package tools.dynamia.reports.core.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.dynamia.domain.jdbc.JdbcHelper;
import tools.dynamia.domain.query.QueryConditions;
import tools.dynamia.domain.query.QueryParameters;
import tools.dynamia.domain.services.AbstractService;
import tools.dynamia.integration.sterotypes.Service;
import tools.dynamia.modules.saas.api.AccountServiceAPI;
import tools.dynamia.reports.core.*;
import tools.dynamia.reports.core.domain.Report;
import tools.dynamia.reports.core.domain.ReportFilter;
import tools.dynamia.reports.core.domain.ReportGroup;
import tools.dynamia.reports.core.services.ReportsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "reports")
public class ReportsServiceImpl extends AbstractService implements ReportsService {

    @Autowired
    private AccountServiceAPI accountServiceAPI;

    @Override
    public ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource) {
        log("Executing query for report: " + report.getName() + " - " + report.getQueryLang());
        long start = System.currentTimeMillis();
        ReportData data = null;
        loadDefaultFilters(report, filters);
        switch (report.getQueryLang()) {
            case "sql":
                data = executeSQL(report, filters, datasource);
                break;
            case "jpql":
                data = executeJPQL(report, filters, datasource);
                break;
        }
        long end = System.currentTimeMillis();
        log("Report " + report.getName() + " executed in " + (end - start) + "ms");
        return data;
    }

    private void loadDefaultFilters(Report report, ReportFilters reportFilters) {
        boolean checkQuery = true;
        if (!reportFilters.isEmpty()) {
            ReportFilter filter = reportFilters.getFilter("accountId");
            if (filter != null) {
                reportFilters.add(filter, accountServiceAPI.getCurrentAccountId());
                checkQuery = false;
            }
        }

        if (checkQuery && report.getQueryScript().contains(":accountId")) {
            ReportFilter filter = new ReportFilter("accountId");
            Long systemAccountId = accountServiceAPI.getSystemAccountId();
            if (!Objects.equals(report.getAccountId(), systemAccountId)) {
                reportFilters.add(filter, report.getAccountId());
            } else {
                reportFilters.add(filter, accountServiceAPI.getCurrentAccountId());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(key = "'Report-' + #id")
    public Report loadReportModel(Long id) {
        Report report = crudService().findSingle(Report.class, QueryParameters.with("id", id).add("accountId", QueryConditions.isNotNull()));
        report.getFields().size();
        report.getFilters().size();
        report.getCharts().size();
        return report;
    }

    private static ReportData executeSQL(Report report, ReportFilters filters, ReportDataSource dataSource) {
        ReportData data = null;

        try (Connection connection = ReportsUtils.getJdbcConnection(dataSource)) {
            var jdbc = new JdbcHelper(new ReportDataSource("delegate", connection));
            jdbc.setShowSQL(false);
            String sql = buildSqlScript(report.getQueryScript(), filters);

            var result = filters.isEmpty() ? jdbc.query(sql) : jdbc.query(sql, filters.getValues());
            data = ReportData.build(report, result);
        } catch (SQLException e) {
            throw new ReportsException(e);
        }

        return data;
    }

    private static ReportData executeJPQL(Report report, ReportFilters filters, ReportDataSource dataSource) {
        EntityManager em = ReportsUtils.getJpaEntityManager(dataSource);
        String jpql = buildSqlScript(report.getQueryScript(), filters);
        Query query = em.createQuery(jpql);
        filters.getValues().forEach(query::setParameter);
        List result = query.getResultList();
        return ReportData.build(report, result);
    }

    private static String buildSqlScript(String query, ReportFilters filters) {
        query = query.replace("\n", " ").replace("\t", " ");
        StringBuilder filtersSql = new StringBuilder("");
        if (!filters.isEmpty() && !query.contains("where")) {
            filtersSql.append("where 1=1 ");
        }
        for (String filterName : filters.getFiltersNames()) {
            ReportFilter filter = filters.getFilter(filterName);
            if (filter.getCondition() != null) {
                filtersSql.append(" and ").append(filter.getCondition());
            }
        }
        if (query.contains("<FILTERS>")) {
            query = query.replace("<FILTERS>", filtersSql.toString());
        } else {
            query = query + " " + filtersSql;
        }
        return query;
    }

    @Cacheable(key = "'ActiveReport'")
    public List<Report> findActives() {
        List<Long> accounts = new ArrayList<>();
        accounts.add(accountServiceAPI.getSystemAccountId());
        accounts.add(accountServiceAPI.getCurrentAccountId());
        QueryParameters params = QueryParameters.with("active", true)
                .add("group.active", true)
                .add("accountId", QueryConditions.in(accounts))
                .orderBy("name");
        return crudService().find(Report.class, params);
    }

    @Cacheable(key = "'ActiveReportByGroup-' + #reportGroup.id")
    public List<Report> findActivesByGroup(ReportGroup reportGroup) {
        return crudService().find(Report.class, QueryParameters.with("group.name", QueryConditions.eq(reportGroup.getName()))
                .add("active", true)
                .add("accountId", accountServiceAPI.getSystemAccountId()).orderBy("name"));
    }
}
