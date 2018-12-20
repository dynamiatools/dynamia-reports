package tools.dynamia.reports.core.domain

import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.domain.query.QueryConditions
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.modules.saas.api.AccountServiceAPI
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS

import javax.persistence.*
import javax.validation.constraints.NotNull

import static tools.dynamia.domain.query.QueryConditions.eq

@Entity
@Table(name = "rpt_reports")
class Report extends SimpleEntitySaaS {

    @OneToOne
    @NotNull
    ReportGroup group
    String name
    @Column(length = 2000)
    String description
    String queryLang = "sql"
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotEmpty
    String queryScript = ""
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportFilter> filters = new ArrayList<>()
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportField> fields = new ArrayList<>()
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ReportChart> charts = new ArrayList<>()

    boolean autofields = true
    boolean active = true
    boolean chartable


    static List<Report> findActivesByGroup(ReportGroup reportGroup) {
        def accountsApi = Containers.get().findObject(AccountServiceAPI)
        def accounts = new ArrayList([accountsApi.systemAccountId, accountsApi.currentAccountId])

        return DomainUtils.lookupCrudService().find(Report, QueryParameters.with("group.name", eq(reportGroup.name))
                .add("active", true)
                .add("accountId", QueryConditions.in(accounts)).orderBy("name"))
    }

    ReportFilter findFilter(String name) {
        return filters.find { it.name == name }
    }

    ReportField findField(String name) {
        return fields.find { it.name == name }
    }

    @Override
    String toString() {
        return name
    }
}
