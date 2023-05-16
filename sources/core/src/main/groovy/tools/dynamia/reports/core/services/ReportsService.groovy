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
package tools.dynamia.reports.core.services

import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup
import tools.dynamia.reports.core.services.impl.ReportDataSource


interface ReportsService {

    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource)

    Report loadReportModel(Long id)

    List<Report> findActives()

    List<Report> findActivesByGroup(ReportGroup reportGroup)
}
