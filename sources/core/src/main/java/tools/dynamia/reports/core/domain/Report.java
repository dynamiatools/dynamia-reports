package tools.dynamia.reports.core.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import tools.dynamia.domain.contraints.NotEmpty;
import tools.dynamia.integration.Containers;
import tools.dynamia.modules.saas.jpa.SimpleEntitySaaS;
import tools.dynamia.reports.core.services.ReportsService;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rpt_reports")
@Cacheable
public class Report extends SimpleEntitySaaS {


    @OneToOne
    @NotNull
    private ReportGroup group;
    private String name;
    @Column(length = 2000)
    private String description;
    private String queryLang = "sql";
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotEmpty
    private String queryScript = "";
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportFilter> filters = new ArrayList<>();
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportField> fields = new ArrayList<>();
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportChart> charts = new ArrayList<>();
    private boolean autofields = true;
    private boolean active = true;
    private boolean chartable;
    private String title;
    private String subtitle;
    private Boolean exportWithoutFormat = false;

    private Boolean exportEndpoint;

    private String endpointName;

    @ManyToOne
    private ReportDataSourceConfig dataSourceConfig;


    public static List<Report> findActivesByGroup(ReportGroup reportGroup) {
        return Containers.get().findObject(ReportsService.class).findActivesByGroup(reportGroup);
    }

    public static List<Report> findActives() {
        return Containers.get().findObject(ReportsService.class).findActives();
    }

    public ReportFilter findFilter(String name) {
        return filters.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
    }

    public ReportField findField(String name) {
        return fields.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
    }


    @Override
    public String toString() {
        return name;
    }

    public ReportGroup getGroup() {
        return group;
    }

    public void setGroup(ReportGroup group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueryLang() {
        return queryLang;
    }

    public void setQueryLang(String queryLang) {
        this.queryLang = queryLang;
    }

    public String getQueryScript() {
        return queryScript;
    }

    public void setQueryScript(String queryScript) {
        this.queryScript = queryScript;
    }

    public List<ReportFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ReportFilter> filters) {
        this.filters = filters;
    }

    public List<ReportField> getFields() {
        return fields;
    }

    public void setFields(List<ReportField> fields) {
        this.fields = fields;
    }

    public List<ReportChart> getCharts() {
        return charts;
    }

    public void setCharts(List<ReportChart> charts) {
        this.charts = charts;
    }

    public boolean getAutofields() {
        return autofields;
    }

    public boolean isAutofields() {
        return autofields;
    }

    public void setAutofields(boolean autofields) {
        this.autofields = autofields;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getChartable() {
        return chartable;
    }

    public boolean isChartable() {
        return chartable;
    }

    public void setChartable(boolean chartable) {
        this.chartable = chartable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Boolean getExportWithoutFormat() {
        if (exportWithoutFormat == null) {
            exportWithoutFormat = false;
        }
        return exportWithoutFormat;
    }

    public void setExportWithoutFormat(Boolean exportWithoutFormat) {
        this.exportWithoutFormat = exportWithoutFormat;
    }

    public Boolean getExportEndpoint() {
        if (exportEndpoint == null) {
            exportEndpoint = false;
        }
        return exportEndpoint;
    }

    public void setExportEndpoint(Boolean exportEndpoint) {
        this.exportEndpoint = exportEndpoint;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public ReportDataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(ReportDataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }
}
