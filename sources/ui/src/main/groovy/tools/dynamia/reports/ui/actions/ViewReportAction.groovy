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

import org.springframework.beans.factory.annotation.Autowired
import tools.dynamia.actions.FastAction
import tools.dynamia.actions.InstallAction
import tools.dynamia.commons.Messages
import tools.dynamia.crud.AbstractCrudAction
import tools.dynamia.crud.CrudActionEvent
import tools.dynamia.crud.CrudState
import tools.dynamia.integration.Containers
import tools.dynamia.navigation.NavigationManager
import tools.dynamia.reports.core.ReportsUtils
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.reports.core.services.impl.ReportDataSource
import tools.dynamia.reports.ui.ReportPage
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
        NavigationManager.getCurrent().currentPage = new ReportPage(report)
    }
}
