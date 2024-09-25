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
package tools.dynamia.reports.ui

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import tools.dynamia.integration.Containers
import tools.dynamia.navigation.ModuleContainer
import tools.dynamia.navigation.NavigationRestrictions
import tools.dynamia.reports.core.Reports
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
        reports = Reports.loadAll()

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



    @Command
    def viewReport(@BindingParam("report") Report report) {
        if (report != null) {

            action.view(report, false)
        }
    }
}
