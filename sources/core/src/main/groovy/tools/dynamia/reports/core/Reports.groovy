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

package tools.dynamia.reports.core

import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup

class Reports {
    ReportGroup group
    List<Report> list = new ArrayList<>()

    static List<Reports> loadAll() {
        List<Reports> reports = new ArrayList<>()

        Report.findActives().each { rp ->
            Reports currentReports = reports.find { it.group.name == rp.group.name }
            if (currentReports == null) {
                currentReports = new Reports(group: rp.group)
                reports << currentReports
            }
            currentReports.list << rp
        }
        reports = reports.sort { a, b -> a.group.name <=> b.group.name }

        return reports
    }
}
