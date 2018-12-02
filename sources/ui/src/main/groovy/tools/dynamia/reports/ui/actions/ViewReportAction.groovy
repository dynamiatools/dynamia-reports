package tools.dynamia.reports.ui.actions

import org.springframework.beans.factory.annotation.Autowired
import tools.dynamia.actions.FastAction
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.Messages
import tools.dynamia.crud.AbstractCrudAction
import tools.dynamia.crud.CrudActionEvent
import tools.dynamia.crud.CrudState
import tools.dynamia.integration.Containers
import tools.dynamia.navigation.NavigationManager
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.reports.core.services.impl.ReportDataSource
import tools.dynamia.reports.ui.ReportViewer
import tools.dynamia.ui.UIMessages
import tools.dynamia.zk.navigation.ComponentPage

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@InstallAction
class ViewReportAction extends AbstractCrudAction {


    private ReportsService service

    @Autowired
    ViewReportAction(ReportsService service) {
        this.service = service

        name = Messages.get(ViewReportAction, "view")
        applicableClass = Report.class
        applicableStates = CrudState.get(CrudState.READ, CrudState.UPDATE)
        menuSupported = true
        image = "play"
        background = "#28a5d4"
        color = "white"
    }

    @Override
    void actionPerformed(CrudActionEvent evt) {
        def report = evt.data as Report
        if (report != null) {
            view(report, true)
        }
    }

    void view(Report report, boolean reloable) {
        def datasource = findDatasource(report)


        def viewer = new ReportViewer(service, report, datasource)
        if (reloable) {
            viewer.addAction(new FastAction(Messages.get(ViewReportAction, "reload"), {
                viewer.reload()
                UIMessages.showMessage("Reloaded")
            }))
            viewer.renderActions()
        }
        viewer.execute()
        def title = Messages.get(ViewReportAction, "pageTitle")
        def page = new ComponentPage("report$report.id", "${title}: $report.name", viewer)
        page.alwaysAllowed = true


        NavigationManager.getCurrent().currentPage = page
    }

    ReportDataSource findDatasource(Report report) {
        if (report.queryLang == "sql") {
            DataSource dataSource = Containers.get().findObject(DataSource.class)
            return new ReportDataSource("Database", dataSource)
        } else {
            EntityManagerFactory em = Containers.get().findObject(EntityManagerFactory.class)
            return new ReportDataSource("EntityManager", em)
        }

    }

}
