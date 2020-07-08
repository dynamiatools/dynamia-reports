package tools.dynamia.reports.core

import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup

class Reports {
    ReportGroup group
    List<Report> list = new ArrayList<>()

    static List<Reports> loadAll() {
        List<Reports> reports = new ArrayList<>()

        Report.findActives().each { rp ->
            Reports currentReports = reports.find { it.group.name == rp.group.name }
            if (currentReports == null) {
                currentReports = new Reports(group: rp.group)
                reports << currentReports
            }
            currentReports.list << rp
        }
        reports = reports.sort { a, b -> a.group.name <=> b.group.name }

        return reports
    }
}
