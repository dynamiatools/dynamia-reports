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

import tools.dynamia.crud.CrudPage
import tools.dynamia.navigation.Module
import tools.dynamia.navigation.Page
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportGroup

class DynamiaReportsModule extends Module {


    private Page reportDesignPage
    private Page reportViewerPage
    private Page reportGroupsPage



    DynamiaReportsModule(String id, String name, String description) {
        super(id, name, description)

        this.reportGroupsPage = new CrudPage("groups", "Reports Groups", ReportGroup.class)
        this.reportDesignPage = new CrudPage("design", "Reports Design", Report.class)
        this.reportViewerPage = new Page("viewer", "Reports Viewer", "classpath:/zk/dynamia/reports/pages/viewer.zul")


        addPage(reportGroupsPage)
        addPage(reportDesignPage)
        addPage(reportViewerPage)
    }

    Page getReportGroupsPage() {
        return reportGroupsPage
    }

    Page getReportDesignPage() {
        return reportDesignPage
    }

    Page getReportViewerPage() {
        return reportViewerPage
    }
}
