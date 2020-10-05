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
package tools.dynamia.reports.ui.controllers

import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.zk.crud.CrudController

class ReportCrudController extends CrudController<Report> {

    private AccountServiceAPI accountServiceAPI = Containers.get().findObject(AccountServiceAPI)

    @Override
    protected void afterCreate() {
        entity.accountId = accountServiceAPI.currentAccountId
    }

    @Override
    protected void beforeQuery() {
        if (getParameter("accountId") == null) {
            setParemeter("accountId", QueryConditions.isNotNull())
        }
    }

    @Override
    protected void afterEdit() {
        setEntity(crudService.findSingle(Report, QueryParameters.with("id", selected.id)
                .add("accountId", QueryConditions.isNotNull())))
    }
}
