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
