package tools.dynamia.reports.ui.actions

import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.zkoss.zul.Filedownload
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.StringUtils
import tools.dynamia.crud.AbstractCrudAction
import tools.dynamia.crud.CrudActionEvent
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.ui.MessageType
import tools.dynamia.ui.UIMessages

@InstallAction
class ExportReportAction extends AbstractCrudAction {

    private ReportsService service

    @Autowired
    ExportReportAction(ReportsService service) {
        this.service = service
        name = "Export"
        applicableClass = Report.class
        menuSupported = true
    }

    @Override
    void actionPerformed(CrudActionEvent evt) {
        def report = evt.data as Report
        if (report != null) {
            report = service.loadReportModel(report.id)
            def generator = new JsonGenerator.Options()
                    .excludeNulls()
                    .excludeFieldsByName('id', 'report', 'accountId')
                    .build()

            def json = generator.toJson(report)

            File tmpDir = File.createTempDir()
            File file = new File(tmpDir, "REPORT_${StringUtils.simplifiedString(report.name).toUpperCase()}.json")
            file.write(JsonOutput.prettyPrint(json))
            Filedownload.save(file, "text/json")
        } else {
            UIMessages.showMessage("Select report to export", MessageType.WARNING)
        }
    }
}
