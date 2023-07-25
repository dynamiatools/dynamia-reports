/*
 * Copyright (C)  2020. Dynamia Soluciones IT S.A.S - NIT 900302344-1 All Rights Reserved.
 * Colombia - South America
 *
 * This file is free software: you can redistribute it and/or modify it  under the terms of the
 *  GNU Lesser General Public License (LGPL v3) as published by the Free Software Foundation,
 *   either version 3 of the License, or (at your option) any later version.
 *
 *  This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *   See the GNU Lesser General Public License for more details. You should have received a copy of the
 *   GNU Lesser General Public License along with this file.
 *   If not, see <https://www.gnu.org/licenses/>.
 *
 */
package tools.dynamia.reports.core


import tools.dynamia.domain.jdbc.JdbcDataSet
import tools.dynamia.reports.core.domain.Report

class ReportData {

    private Report report
    private List<ReportDataEntry> entries = new ArrayList<>()
    private List<String> fieldNames

    static ReportData build(Report report, JdbcDataSet dataSet) {
        ReportData data = new ReportData(report: report)
        List<String> fields = report.autofields ? dataSet.columnsLabels : report.fields.collect { it.name }
        data.fieldNames = fields
        dataSet.rows.each { row ->
            data.entries << ReportDataEntry.build(fields, row)
        }
        dataSet.close()

        return data
    }

    static ReportData build(Report report, Collection collection) {
        ReportData data = new ReportData(report: report)
        if (report.autofields) {
            data.fieldNames = Collections.singletonList("Result")
            collection.each { obj ->
                data.entries << new ReportDataEntry(singleValue: true, name: obj.toString(), value: obj)
            }
        } else if (!report.fields.empty) {
            def fields = report.fields.collect { it.name }
            data.fieldNames = fields
            collection.each { obj ->
                data.entries << ReportDataEntry.build(fields, obj)
            }
        }

        return data
    }

    void sort(String field, boolean ascending) {
        println "Sorting Ascending? $ascending"
        entries.sort { e1, e2 -> ascending ? e1.values[field] <=> e2.values[field] : e2.values[field] <=> e1.values[field] }
    }

    Report getReport() {
        return report
    }

    List<ReportDataEntry> getEntries() {
        return entries
    }

    int getSize() {
        return entries.size()
    }

    boolean isEmpty() {
        return entries.empty
    }

    List<String> getFieldNames() {
        return fieldNames
    }

}


