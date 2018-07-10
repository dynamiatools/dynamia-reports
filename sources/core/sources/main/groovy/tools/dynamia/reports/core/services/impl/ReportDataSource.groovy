package tools.dynamia.reports.core.services.impl

class ReportDataSource {

    private String name
    private Object delegate

    ReportDataSource(String name, Object delegate) {
        this.name = name
        this.delegate = delegate

    }

    Object getDelegate() {
        return delegate
    }

    String getName() {
        return name
    }


    @Override
    String toString() {
        return name
    }
}