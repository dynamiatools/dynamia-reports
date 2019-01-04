package tools.dynamia.reports.ui

import tools.dynamia.navigation.Module
import tools.dynamia.navigation.PageGroup
import tools.dynamia.reports.ui.ReportListViewModel
import tools.dynamia.reports.ui.ReportPage

class DynamiaReportsUserModule extends Module {

    DynamiaReportsUserModule(String id, String name, String description) {
        super(id, name, description)
    }

    DynamiaReportsUserModule(String id, String name) {
        super(id, name)
    }

    DynamiaReportsUserModule() {

    }

    @Override
    Collection<PageGroup> getPageGroups() {
        init()
        return super.getPageGroups()
    }

    private void init() {
        super.getPageGroups().clear()

        def vm = new ReportListViewModel()
        vm.init()

        vm.reports.each { rg ->
            printf "Loading report group $rg.group.name"
            def pageGroup = new PageGroup("group$rg.group.id", rg.group.name)
            addPageGroup(pageGroup)

            rg.list.each { report ->
                printf "   Loading report $report.name"
                pageGroup.addPage(new ReportPage(report))

            }
        }
    }

}
