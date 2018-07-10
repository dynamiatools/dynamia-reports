package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.modules.saas.api.SimpleEntitySaaS

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "rpt_groups")
@Descriptor(fields = "name")
class ReportGroup extends SimpleEntitySaaS {

    String name

    @Override
    String toString() {
        return name
    }
}
