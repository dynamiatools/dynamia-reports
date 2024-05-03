package tools.dynamia.reports.core.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.dynamia.commons.DateTimeUtils;
import tools.dynamia.domain.util.DomainUtils;
import tools.dynamia.reports.core.NestedMapReportDataExporter;
import tools.dynamia.reports.core.ReportFilters;
import tools.dynamia.reports.core.ReportsUtils;
import tools.dynamia.reports.core.domain.Report;
import tools.dynamia.reports.core.domain.ReportFilter;
import tools.dynamia.reports.core.services.ReportsService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class ReportsExportController {

    private final ReportsService reportsService;

    public ReportsExportController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping(value = "/reports/{endpoint}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable("endpoint") String endpoint) {
        return getReport(endpoint, null);
    }

    @PostMapping(value = "/reports/{endpoint}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable("endpoint") String endpoint, @RequestBody(required = false) ReportFilters filters) {
        try {
            Report report = reportsService.findByEndpoint(endpoint);
            if (report == null) {
                return ResponseEntity.notFound().build();
            }

            if (!report.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("X-Error-Message", "Report [" + report.getName() + "] is not active")
                        .body(Map.of("error", "Report [" + report.getName() + "] is not active", "valid", false));

            }

            if (!report.getExportEndpoint()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("X-Error-Message", "Report [" + report.getName() + "] is not exported as endpoint")
                        .body(Map.of("error", "Report [" + report.getName() + "] is not exported as endpoint", "valid", false));
            }

            var loadedFilters = loadFilters(report, filters);
            var datasource = ReportsUtils.findDatasource(report);

            var reportData = reportsService.execute(report, loadedFilters, datasource);
            var map = new NestedMapReportDataExporter().export(reportData);

            return ResponseEntity.ok(map);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Endpoint [" + endpoint + "] error: " + e.getMessage())
                    .body(Map.of("error", "Endpoint [" + endpoint + "] error: " + e.getMessage(), "valid", false));
        }

    }

    private ReportFilters loadFilters(Report report, ReportFilters requestFilters) {
        ReportFilters loaded = new ReportFilters();
        if (requestFilters != null) {
            requestFilters.getOptions().forEach(reqOpt -> {
                if (reqOpt.getValue() != null) {
                    report.getFilters().stream().filter(f -> f.getName().equals(reqOpt.getName()))
                            .findFirst().ifPresent(f -> loaded.add(f, convertFilterValue(f, reqOpt.getValue())));
                }
            });
        }
        return loaded;
    }

    private Object convertFilterValue(ReportFilter filter, Object value) {
        try {
            return switch (filter.getDataType()) {
                case BOOLEAN -> "true".equalsIgnoreCase(value.toString());
                case ENUM -> convertToEnum(filter.getEnumClassName(), value);
                case NUMBER -> new BigDecimal(value.toString());
                case ENTITY -> convertToEntity(filter.getEntityClassName(), value);
                case DATE -> DateTimeUtils.parse(value.toString(), "YYYY-MM-DD");
                case DATE_TIME -> DateTimeUtils.parse(value.toString(), "YYYY-MM-DD hh:mm:ss");
                case TIME -> DateTimeUtils.parse(value.toString(), "hh:mm:ss");
                case TEXT -> value.toString();
                default -> value;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private Object convertToEntity(String entityClassName, Object value) throws ClassNotFoundException {
        Long id = Long.parseLong(value.toString());
        return DomainUtils.lookupCrudService().find(Class.forName(entityClassName), id);
    }

    private Object convertToEnum(String enumClassName, Object value) throws ClassNotFoundException {
        return Enum.valueOf((Class<Enum>) Class.forName(enumClassName), value.toString());
    }
}
