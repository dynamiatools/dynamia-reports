package tools.dynamia.reports.ui

import org.zkoss.zk.ui.Component
import tools.dynamia.commons.Messages
import tools.dynamia.integration.Containers
import tools.dynamia.reports.core.ReportsUtils
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.reports.ui.actions.ViewReportAction
import tools.dynamia.zk.navigation.ComponentPage

class ReportPage extends ComponentPage {

    private Report report

    ReportPage(Report report) {
        super("report${report.id}", report.name, (Component) null)
        this.report = report
        this.alwaysAllowed = true
        def title = Messages.get(ViewReportAction, "pageTitle")
        setLongNameSupplier {
            return "$title: $report.name".toString()
        }
    }

    @Override
    Component renderPage() {
        def datasource = ReportsUtils.findDatasource(report)
        def service = Containers.get().findObject(ReportsService)
        def viewer = new ReportViewer(service, report, datasource)
        viewer.execute()
        return viewer
    }
}
