/*-
 * #%L
 * DynamiaReports - Core
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
package tools.dynamia.reports.core.services

import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.services.impl.ReportDataSource


interface ReportsService {

    ReportData execute(Report report, ReportFilters filters, ReportDataSource datasource)

    Report loadReportModel(Long id)
}
