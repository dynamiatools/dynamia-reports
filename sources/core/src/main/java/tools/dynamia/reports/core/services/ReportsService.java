package tools.dynamia.reports.core.services;

import tools.dynamia.reports.core.ReportData;
import tools.dynamia.reports.core.ReportDataSource;
import tools.dynamia.reports.core.ReportFilters;
import tools.dynamia.reports.core.domain.Report;
import tools.dynamia.reports.core.domain.ReportGroup;

import java.util.List;

public interface ReportsService {
    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource);

    Report loadReportModel(Long id);

    List<Report> findActives();

    List<Report> findActivesByGroup(ReportGroup reportGroup);
}
