package tools.dynamia.reports.ui

import tools.dynamia.crud.CrudPage
import tools.dynamia.navigation.Module
import tools.dynamia.navigation.Page
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup

class DynamiaReportsModule extends Module {


    private Page reportDesignPage
    private Page reportViewerPage
    private Page reportGroupsPage



    DynamiaReportsModule(String id, String name, String description) {
        super(id, name, description)

        this.reportGroupsPage = new CrudPage("groups", "Reports Groups", ReportGroup.class)
        this.reportDesignPage = new CrudPage("design", "Reports Design", Report.class)
        this.reportViewerPage = new Page("viewer", "Reports Viewer", "classpath:/zk/dynamia/reports/pages/viewer.zul")


        addPage(reportGroupsPage)
        addPage(reportDesignPage)
        addPage(reportViewerPage)
    }

    Page getReportGroupsPage() {
        return reportGroupsPage
    }

    Page getReportDesignPage() {
        return reportDesignPage
    }

    Page getReportViewerPage() {
        return reportViewerPage
    }
}
