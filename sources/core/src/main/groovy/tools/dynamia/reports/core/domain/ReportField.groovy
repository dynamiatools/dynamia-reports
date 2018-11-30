package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.modules.saas.api.SimpleEntitySaaS
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
