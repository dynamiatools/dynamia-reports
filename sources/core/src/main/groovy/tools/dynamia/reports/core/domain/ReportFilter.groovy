package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.domain.jdbc.JdbcDataSet
import tools.dynamia.domain.jdbc.JdbcHelper
import tools.dynamia.domain.jdbc.Row
import tools.dynamia.modules.saas.api.SimpleEntitySaaS
import tools.dynamia.reports.api.EnumFilterProvider
import tools.dynamia.reports.core.ReportFilterOption
import tools.dynamia.reports.core.ReportsException
import tools.dynamia.reports.core.ReportsUtils
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.services.impl.ReportDataSource

import javax.management.Descriptor
import javax.persistence.*
import java.sql.Connection

@Entity
@Table(name = "rpt_reports_filters")
class ReportFilter extends SimpleEntitySaaS {


    @ManyToOne
    Report report
    @NotEmpty
    String name
    @NotEmpty
    String label

    @Column(name = "filterCondition")
    String condition
    String defaultValue
    DataType dataType = DataType.TEXT

    @Column(name = "filterValues")
    String values

    String enumClassName
    String entityClassName
    @Column(name = "filterOrder")
    int order
    boolean required

    String queryValues


    List<ReportFilterOption> loadOptions(ReportDataSource dataSource) {
        if (dataType == DataType.ENUM && enumClassName != null) {
            return loadEnumOptions()
        } else if (dataType == DataType.ENTITY && entityClassName != null) {
            return loadEntityOptions()
        } else if (queryValues != null && !queryValues.empty) {
            return queryAndLoadOptions(dataSource)
        }
    }

    private List<ReportFilterOption> loadEnumOptions() {
        EnumFilterProvider provider = ReportsUtils.findEnumFilterProvider(enumClassName)
        if (provider != null) {
            provider.values.collect { new ReportFilterOption(this, it.name().replace("_", " "), it) }
        } else {
            throw new ReportsException("Cannot find enum provider for $enumClassName")
        }
    }

    private List<ReportFilterOption> loadEntityOptions() {
        return Collections.emptyList()
    }

    private List<ReportFilterOption> queryAndLoadOptions(ReportDataSource dataSource) {
        List<ReportFilterOption> options = new ArrayList<>()
        if (report.queryLang == "sql") {
            Connection connection = ReportsUtils.getJdbcConnection(dataSource)
            def jdbc = new JdbcHelper(connection)
            JdbcDataSet result = jdbc.query(queryValues)

            result.columnsLabels.empty
            for (Row row : result) {
                row.loadAll(result.columnsLabels)
                Object value = row.col(result.columnsLabels[0])
                if (result.columnsLabels.size() > 0) {
                    options << new ReportFilterOption(this, row.col(result.columnsLabels[1]).toString(), value)
                } else {
                    options << new ReportFilterOption(this, value.toString(), value)
                }

            }


        } else if (report.queryLang == "jpql") {
            EntityManager em = ReportsUtils.getJpaEntityManager(dataSource)
            def result = em.createQuery(queryValues).resultList
            result.each { b ->
                if (b.class.array) {
                    Object[] values = (Object[]) b
                    if (values.length > 1) {
                        options << new ReportFilterOption(this, values[1].toString(), values[0])
                    } else {
                        options << new ReportFilterOption(this, values[0].toString(), values[0])
                    }
                } else {
                    options << new ReportFilterOption(this, b.toString(), b)
                }
            }
        }
        return options
    }

}

