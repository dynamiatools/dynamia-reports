package tools.dynamia.reports.ui.actions

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.zkoss.zul.Fileupload
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.Messages
import tools.dynamia.crud.AbstractCrudAction
import tools.dynamia.crud.CrudActionEvent
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportField
import tools.dynamia.reports.core.domain.ReportFilter
import tools.dynamia.reports.core.domain.ReportGroup
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.ui.UIMessages

@InstallAction
class ImportReportAction extends AbstractCrudAction {

    private ReportsService service

    @Autowired
    ImportReportAction(ReportsService service) {
        this.service = service
        name = Messages.get(ImportReportAction,"import")
        applicableClass = Report.class

    }

    @Override
    void actionPerformed(CrudActionEvent evt) {
        def json = new JsonSlurper()

        Fileupload.get { uevt ->
            uevt.medias.each { m ->
                if (m.name.endsWith(".json")) {
                    def obj = null
                    if (m.inMemory()) {
                        obj = json.parse(m.byteData)
                    } else {
                        obj = json.parse(m.readerData)
                    }
                    importReport(obj)
                }
                UIMessages.showMessage("OK")
                evt.controller.doQuery()
            }
        }
    }

    def importReport(Map data) {
        def report = new Report(data)
        report.group = findGroup(report.group.name)
        report.accountId = report.group.accountId

        report.filters = new ArrayList<>()
        data["filters"].each {
            def filter = new ReportFilter(it)
            filter.report = report
            filter.accountId = report.group.accountId
            report.filters << filter
        }

        report.fields = new ArrayList<>()
        data["fields"].each {
            def field = new ReportField(it)
            field.report = report
            field.accountId = report.group.accountId
            report.fields << field
        }

        report.name = "$report.name (imported)"
        report.save()

    }

    ReportGroup findGroup(String name) {
        def group = crudService().findSingle(ReportGroup, "name", QueryConditions.eq(name))
        if (group == null) {
            group = new ReportGroup(name: name)
            group.save()
        }
        return group
    }
}