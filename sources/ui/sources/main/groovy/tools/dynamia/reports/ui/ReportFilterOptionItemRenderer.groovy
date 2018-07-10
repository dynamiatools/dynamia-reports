package tools.dynamia.reports.ui

import org.zkoss.zul.Comboitem
import org.zkoss.zul.ComboitemRenderer
import tools.dynamia.reports.core.ReportFilterOption

class ReportFilterOptionItemRenderer implements ComboitemRenderer<ReportFilterOption> {


    @Override
    void render(Comboitem item, ReportFilterOption data, int index) throws Exception {
        item.setValue(data.value)
        item.label = data.name

    }
}
