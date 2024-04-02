package tools.dynamia.reports.core;

import java.util.*;

import tools.dynamia.domain.jdbc.JdbcDataSet;
import tools.dynamia.reports.core.domain.Report;
import tools.dynamia.reports.core.domain.ReportField;

public class ReportData {

    private Report report;
    private List<ReportDataEntry> entries = new ArrayList<>();
    private List<String> fieldNames;

    public static ReportData build(Report report, JdbcDataSet dataSet) {
        ReportData data = new ReportData();
        data.report = report;
        List<String> fields = report.isAutofields() ? dataSet.getColumnsLabels() : report.getFields().stream().map(ReportField::getName).toList();
        data.fieldNames = fields;
        dataSet.getRows().forEach(row -> {
            data.entries.add(ReportDataEntry.build(fields, row));
        });
        dataSet.close();

        return data;
    }

    public static ReportData build(Report report, Collection collection) {
        ReportData data = new ReportData();
        data.report = report;
        if (report.isAutofields()) {
            data.fieldNames = Collections.singletonList("Result");
            collection.forEach(obj -> data.entries.add(new ReportDataEntry(obj.toString(), obj, true)));
        } else if (!report.getFields().isEmpty()) {
            List<String> fields = report.getFields().stream().map(ReportField::getName).toList();
            data.fieldNames = fields;
            collection.forEach(obj -> data.entries.add(ReportDataEntry.build(fields, obj)));
        }

        return data;
    }

    public void sort(String field, boolean ascending) {
        System.out.println("Sorting Ascending? " + ascending);
        entries.sort((e1, e2) -> ascending ? e1.compareTo(field, e2) : e2.compareTo(field, e1));
    }

    public Report getReport() {
        return report;
    }

    public List<ReportDataEntry> getEntries() {
        return entries;
    }

    public int getSize() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }
}
