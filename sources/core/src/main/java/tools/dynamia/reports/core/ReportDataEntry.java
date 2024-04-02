package tools.dynamia.reports.core

import tools.dynamia.commons.BeanUtils
import tools.dynamia.domain.jdbc.Row

class ReportDataEntry {

    String name
    Object value
    Map<String, Object> values = new HashMap<>()
    boolean singleValue

    static ReportDataEntry build(List<String> names, Row row) {
        def entry = new ReportDataEntry(singleValue: false)
        names.each { name ->
            entry.values[name] = row.col(name)
        }
        return entry
    }

    static ReportDataEntry build(List<String> names, Object bean) {
        def entry = new ReportDataEntry(singleValue: false)
        entry.name = bean.toString()
        entry.value = bean
        names.each { name ->
            entry.values[name] = BeanUtils.invokeBooleanGetMethod(bean, name)
        }
        return entry
    }


}
