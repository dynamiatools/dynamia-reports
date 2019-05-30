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
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.domain.enums.TextAlign

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "rpt_reports_fields")
@Descriptor(fields = ["name", "label", "dataType", "align", "width", "cellStyle", "columnStyle",
        "format", "description", "upperCase", "order"], viewParams = "columns: 4")
class ReportField extends SimpleEntitySaaS {

    @ManyToOne
    Report report
    @NotEmpty
    @NotNull
    String name
    @NotEmpty
    String label
    DataType dataType = DataType.TEXT
    @Column(name = "fieldOrder")
    int order = 0
    TextAlign align = TextAlign.LEFT
    String description
    String format
    String width
    String cellStyle
    String columnStyle
    boolean upperCase

    @Override
    String toString() {
        return name
    }
}
