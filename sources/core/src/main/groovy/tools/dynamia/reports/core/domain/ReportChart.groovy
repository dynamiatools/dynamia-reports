

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
