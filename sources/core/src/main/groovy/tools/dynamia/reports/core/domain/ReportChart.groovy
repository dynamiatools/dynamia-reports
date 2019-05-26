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


package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "rpt_reports_charts")
@Descriptor(fields = ["title", "labelField", "valueField", "type", "grouped", "order"], viewParams = "columns: 3")
class ReportChart extends SimpleEntitySaaS {

    @ManyToOne
    Report report
    @NotEmpty
    @NotNull
    String title
    @NotEmpty
    String labelField
    @NotEmpty
    String valueField
    @Descriptor(params = "placeholder: pie, bar, horizontalbar, line, doughnut")
    String type
    @Column(name = "fieldOrder")
    @Descriptor(params = "width: 100px")
    int order = 0
    boolean grouped

    @Override
    String toString() {
        return title
    }
}
