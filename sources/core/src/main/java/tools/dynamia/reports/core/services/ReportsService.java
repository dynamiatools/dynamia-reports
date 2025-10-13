package tools.dynamia.reports.core.services;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import tools.dynamia.reports.api.ReportDTO;
import tools.dynamia.reports.core.ReportData;
import tools.dynamia.reports.core.ReportDataSource;
import tools.dynamia.reports.core.ReportFilters;
import tools.dynamia.reports.core.domain.Report;
import tools.dynamia.reports.core.domain.ReportGroup;

import java.io.File;
import java.util.List;

public interface ReportsService {
    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource);

    Report loadReportModel(Long id);

    List<Report> findActives();

    List<Report> findActivesByGroup(ReportGroup reportGroup);

    Report findByEndpoint(String endpoint);

    @Transactional
    Report findByEndpoint(String group, String endpoint);

    File exportReport(Report report);

    Report importReport(File file);

    List<Report> findExportableReports();
}
