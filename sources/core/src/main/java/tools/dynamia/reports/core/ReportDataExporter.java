package tools.dynamia.reports.core;

public interface ReportDataExporter<T> {
    T export(ReportData reportData);
}
