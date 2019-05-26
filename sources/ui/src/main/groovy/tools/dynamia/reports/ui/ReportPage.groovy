/*-
 * #%L
 * DynamiaReports - UI
 * %%
 * Copyright (C) 2018 - 2019 Dynamia Soluciones IT SAS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
