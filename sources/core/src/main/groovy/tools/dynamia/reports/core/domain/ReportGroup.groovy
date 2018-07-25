package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.modules.saas.api.SimpleEntitySaaS

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "rpt_groups")
@Descriptor(fields = ["name", "active"])
class ReportGroup extends SimpleEntitySaaS {

    String name
    boolean active = true

    @Override
    String toString() {
        return name
    }

    static List<ReportGroup> findActives() {
        return DomainUtils.lookupCrudService().find(ReportGroup, QueryParameters.with("active", true).orderBy("name"))
    }
}
