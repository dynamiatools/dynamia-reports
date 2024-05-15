package tools.dynamia.reports.boot;

import tools.dynamia.domain.util.CrudServiceListenerAdapter;
import tools.dynamia.integration.sterotypes.Listener;
import tools.dynamia.modules.saas.api.AccountAware;
import tools.dynamia.modules.saas.api.AccountServiceAPI;

@Listener
public class DefaultAccountCrudListener extends CrudServiceListenerAdapter<AccountAware> {

    private final AccountServiceAPI accountServiceAPI;

    public DefaultAccountCrudListener(AccountServiceAPI accountServiceAPI) {
        this.accountServiceAPI = accountServiceAPI;
    }

    @Override
    public void beforeCreate(AccountAware entity) {
        if (entity.getAccountId() == null) {
            entity.setAccountId(accountServiceAPI.getCurrentAccountId());
        }
    }
}
