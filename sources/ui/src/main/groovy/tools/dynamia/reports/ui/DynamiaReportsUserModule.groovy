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

import tools.dynamia.navigation.Module
import tools.dynamia.navigation.PageGroup
import tools.dynamia.reports.core.Reports
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
            def pageGroup = new PageGroup("group$rg.group.id", rg.group.name)
            addPageGroup(pageGroup)

            rg.list.each { report ->
                pageGroup.addPage(new ReportPage(report))

            }
        }
    }

}
