package tools.dynamia.reports.ui

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import tools.dynamia.integration.Containers
import tools.dynamia.navigation.ModuleContainer
import tools.dynamia.navigation.NavigationRestrictions
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
                Reports currentReports = findReports(grp)
                if (currentReports == null) {
                    currentReports = new Reports(group: grp)
                    reports << currentReports
                }
                currentReports.list.addAll(list)
            }
        }
        filterReportsByModules()
    }

    def filterReportsByModules() {
        def modules = Containers.get().findObject(ModuleContainer)
        def toRemove = []
        reports.each { r ->
            if (r.group.module != null && !r.group.module.empty) {
                def module = modules.getModuleById(r.group.module)
                if (module != null && !NavigationRestrictions.allowAccess(module)) {
                    toRemove << r
                }
            }
        }
        reports.removeAll(toRemove)
    }

    Reports findReports(ReportGroup reportGroup) {
        return reports.find { it.group.name == reportGroup.name }
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
    List<Report> list = new ArrayList<>()

}
