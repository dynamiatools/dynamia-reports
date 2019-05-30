package tools.dynamia.reports.ui

import org.zkoss.zul.Filedownload
import tools.dynamia.commons.StringUtils
import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportDataEntry
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportField
import tools.dynamia.reports.excel.ExcelFileWriter

class ExcelReportDataExporter implements ReportDataExporter {

    private Report report


    ExcelReportDataExporter(Report report) {
        this.report = report
    }

    def export(ReportData reportData) {
        if (reportData != null) {
            def file = File.createTempFile(report.name.replace(" ", "_") + "_", ".xlsx")
            def exporter = new ExcelFileWriter(file)
            //columns
            exportColumns(reportData, exporter)
            exportRows(reportData, exporter)
            exporter.write()
            exporter.close()

            Filedownload.save(file, "application/excel")
        }
    }

    private List<ReportDataEntry> exportRows(ReportData reportData, ExcelFileWriter exporter) {
        reportData.entries.each { data ->
            exporter.newRow()
            reportData.fieldNames.each { f ->
                ReportField reportField = report.fields.find { it.name == f }
                def value = data.values[f]

                exporter.addCell(value)
            }
        }
    }

    private void exportColumns(ReportData reportData, ExcelFileWriter exporter) {
        if (report.autofields) {
            reportData.fieldNames.each { f ->
                ReportField reportField = report.fields.find { it.name == f }
                if (reportField != null) {
                    exporter.addCell(reportField.label)
                } else {
                    exporter.addCell(StringUtils.capitalizeAllWords(StringUtils.addSpaceBetweenWords(f)))
                }
            }
        } else {
            report.fields.toSorted { a, b -> a.order <=> b.order }.each { f ->
                exporter.addCell(f.label)
            }
        }
    }
}
