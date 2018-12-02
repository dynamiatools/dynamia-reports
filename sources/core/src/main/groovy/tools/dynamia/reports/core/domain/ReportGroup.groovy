package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.modules.saas.api.SimpleEntitySaaS

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "rpt_groups")
@Descriptor(fields = ["name", "module", "active"])
class ReportGroup extends SimpleEntitySaaS {

    String name
    String module
    boolean active = true

    @Override
    String toString() {
        return name
    }

    static List<ReportGroup> findActives() {
        def accountsApi = Containers.get().findObject(AccountServiceAPI)
        def accounts = new ArrayList([accountsApi.systemAccountId, accountsApi.currentAccountId])
        return DomainUtils.lookupCrudService().find(ReportGroup, QueryParameters.with("active", true)
                .add("accountId", QueryConditions.in(accounts))
                .orderBy("name"))
    }
}
