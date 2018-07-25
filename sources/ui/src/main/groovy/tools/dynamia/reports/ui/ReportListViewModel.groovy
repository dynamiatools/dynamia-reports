package tools.dynamia.reports.ui

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import tools.dynamia.integration.Containers
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup
import tools.dynamia.reports.ui.actions.ViewReportAction

class ReportListViewModel {

    List<Reports> reports
    ViewReportAction action = Containers.get().findObject(ViewReportAction)

    @Init
    def init() {
        loadReports()
    }

    def loadReports() {
        reports = new ArrayList<>()
        ReportGroup.findActives().each { grp ->
            List<Report> list = Report.findActivesByGroup(grp)
            if (!list.empty) {
                reports << new Reports(group: grp, list: list)
            }
        }
    }

    @Command
    def viewReport(@BindingParam("report") Report report) {
        if (report != null) {

            action.view(report, false)
        }
    }
}

class Reports {
    ReportGroup group
    List<Report> list

}
