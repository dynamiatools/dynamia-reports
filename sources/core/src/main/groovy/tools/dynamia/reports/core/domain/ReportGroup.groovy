/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */
package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS

import javax.persistence.Cacheable
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "rpt_groups")
@Descriptor(fields = ["name", "module", "active"])
@Cacheable
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

        return DomainUtils.lookupCrudService().find(ReportGroup, QueryParameters.with("active", true)
                .add("accountId", accountsApi.systemAccountId)
                .orderBy("name"))
    }
}
