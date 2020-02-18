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

import tools.dynamia.navigation.Module
import tools.dynamia.navigation.PageGroup
import tools.dynamia.reports.ui.ReportPage

class DynamiaReportsUserModule extends Module {

    DynamiaReportsUserModule(String id, String name, String description) {
        super(id, name, description)
    }

    DynamiaReportsUserModule(String id, String name) {
        super(id, name)
    }

    DynamiaReportsUserModule() {

    }

    @Override
    Collection<PageGroup> getPageGroups() {
        init()
        return super.getPageGroups()
    }

    private void init() {
        super.getPageGroups().clear()

        def vm = new ReportListViewModel()
        vm.init()

        vm.reports.each { rg ->
            printf "Loading report group $rg.group.name"
            def pageGroup = new PageGroup("group$rg.group.id", rg.group.name)
            addPageGroup(pageGroup)

            rg.list.each { report ->
                printf "   Loading report $report.name"
                pageGroup.addPage(new ReportPage(report))

            }
        }
    }

}
