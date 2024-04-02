package tools.dynamia.reports.api;

import java.util.Map;

/**
 * Provide parameters or variables for reports
 */
public interface ReportGlobalParameterProvider {
    Map<String, Object> getParams();
}
