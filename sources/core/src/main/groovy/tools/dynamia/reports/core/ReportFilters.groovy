package tools.dynamia.reports.core

import tools.dynamia.reports.core.domain.ReportFilter

class ReportFilters {


    private Map<String, Object> values = new HashMap<>()
    private Map<String, ReportFilter> filters = new HashMap<>()


    void add(ReportFilter filter, Object value) {
        values[filter.name] = value
        filters[filter.name] = filter
    }

    Object getValue(String filterName) {
        return values[filterName]
    }

    ReportFilter getFilter(String filterName) {
        return filters.get(filterName)
    }

    boolean isEmpty() {
        return values.isEmpty()
    }

    Set<String> getFiltersNames() {
        return values.keySet()
    }

    Map<String, Object> getValues() {
        return values
    }
}
