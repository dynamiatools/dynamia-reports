package tools.dynamia.reports.core

import tools.dynamia.reports.core.domain.ReportFilter

class ReportFilterOption {

    private ReportFilter filter
    private String name
    private Object value

    ReportFilterOption(ReportFilter filter, String name, Object value) {
        this.filter = filter
        this.name = name
        this.value = value
    }

    ReportFilter getFilter() {
        return filter
    }

    String getName() {
        return name
    }

    Object getValue() {
        return value
    }
}
