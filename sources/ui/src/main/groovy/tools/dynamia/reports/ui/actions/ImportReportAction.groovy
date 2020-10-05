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

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.zkoss.zul.Fileupload
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.Messages
import tools.dynamia.crud.AbstractCrudAction
import tools.dynamia.crud.CrudActionEvent
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.reports.core.domain.*
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.ui.UIMessages

@InstallAction
class ImportReportAction extends AbstractCrudAction {

    private ReportsService service

    @Autowired
    ImportReportAction(ReportsService service) {
        this.service = service
        name = Messages.get(ImportReportAction, "import")
        applicableClass = Report.class
        image = "up"


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
        data["filters"]?.each {
            def filter = new ReportFilter(it)
            filter.report = report
            filter.accountId = report.group.accountId
            report.filters << filter
        }

        report.fields = new ArrayList<>()
        data["fields"]?.each {
            def field = new ReportField(it)
            field.report = report
            field.accountId = report.group.accountId
            report.fields << field
        }

        report.charts = new ArrayList<>()
        data["charts"]?.each {
            def chart = new ReportChart(it)
            chart.report = report
            chart.accountId = report.group.accountId
            report.charts << chart
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
