package tools.dynamia.reports.ui

import tools.dynamia.reports.core.ReportData

interface ReportDataExporter {

    def export(ReportData reportData)

}