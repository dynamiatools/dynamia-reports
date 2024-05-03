package tools.dynamia.reports.ui.actions;

import tools.dynamia.actions.InstallAction;
import tools.dynamia.crud.AbstractCrudAction;
import tools.dynamia.crud.CrudActionEvent;
import tools.dynamia.crud.CrudState;
import tools.dynamia.reports.core.ReportDataSource;
import tools.dynamia.reports.core.domain.ReportDataSourceConfig;

@InstallAction
public class TestReportDatasourceAction extends AbstractCrudAction {

    public TestReportDatasourceAction() {
        setName("Test");
        setApplicableClass(ReportDataSourceConfig.class);
        setApplicableStates(CrudState.get(CrudState.READ, CrudState.CREATE, CrudState.UPDATE));
    }

    @Override
    public void actionPerformed(CrudActionEvent evt) {
        var config = (ReportDataSourceConfig) evt.getData();
        if(config!=null){
            var connection = ReportDataSource.newConnection(config);
        }
    }
}
