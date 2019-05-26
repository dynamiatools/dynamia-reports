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
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.modules.saas.api.SimpleEntitySaaS
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS

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
