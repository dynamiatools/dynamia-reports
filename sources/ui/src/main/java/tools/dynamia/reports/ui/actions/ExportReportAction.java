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
package tools.dynamia.reports.ui.actions

import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.zkoss.zul.Filedownload
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.Messages
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
        name = Messages.get(ExportReportAction, "export")
        image = "down"
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
