package tools.dynamia.reports.core;

import tools.dynamia.reports.core.domain.ReportFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReportFilters {


    private Map<String, Object> values = new HashMap<>();
    private Map<String, ReportFilter> filters = new HashMap<>();

    public void add(ReportFilter filter, Object value) {
        values.put(filter.getName(), value);
        filters.put(filter.getName(), filter);
    }

    public Object getValue(String filterName) {
        return values.get(filterName);
    }

    public ReportFilter getFilter(String filterName) {
        return filters.get(filterName);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Set<String> getFiltersNames() {
        return values.keySet();
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, ReportFilter> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, ReportFilter> filters) {
        this.filters = filters;
    }
}
