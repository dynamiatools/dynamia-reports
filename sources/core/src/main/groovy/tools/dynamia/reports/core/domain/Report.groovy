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

import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

import static tools.dynamia.domain.query.QueryConditions.eq

@Entity
@Table(name = "rpt_reports")
@Cacheable
class Report extends SimpleEntitySaaS {

    @OneToOne
    @NotNull
    ReportGroup group
    String name
    @Column(length = 2000)
    String description
    String queryLang = "sql"
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotEmpty
    String queryScript = ""
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportFilter> filters = new ArrayList<>()
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportField> fields = new ArrayList<>()
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportChart> charts = new ArrayList<>()

    boolean autofields = true
    boolean active = true
    boolean chartable
    String title
    String subtitle
    Boolean exportWithoutFormat = false


    static List<Report> findActivesByGroup(ReportGroup reportGroup) {
        def accountsApi = Containers.get().findObject(AccountServiceAPI)


        return DomainUtils.lookupCrudService().find(Report, QueryParameters.with("group.name", eq(reportGroup.name))
                .add("active", true)
                .add("accountId", accountsApi.systemAccountId).orderBy("name"))
    }

    static List<Report> findActives() {
        def accountsApi = Containers.get().findObject(AccountServiceAPI)
        def accounts = new ArrayList([accountsApi.systemAccountId, accountsApi.currentAccountId])

        return DomainUtils.lookupCrudService().find(Report, QueryParameters.with("active", true)
                .add("group.active", true)
                .add("accountId", QueryConditions.in(accounts)).orderBy("name"))
    }

    ReportFilter findFilter(String name) {
        return filters.find { it.name == name }
    }

    ReportField findField(String name) {
        return fields.find { it.name == name }
    }

    @Override
    String toString() {
        return name
    }
}
