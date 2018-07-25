package tools.dynamia.reports.core

/**
 * Common exception for DynamiaReports services
 */
class ReportsException extends RuntimeException {

    ReportsException() {
    }

    ReportsException(String var1) {
        super(var1)
    }

    ReportsException(String var1, Throwable var2) {
        super(var1, var2)
    }

    ReportsException(Throwable var1) {
        super(var1)
    }
}
