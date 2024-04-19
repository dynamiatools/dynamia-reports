/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
