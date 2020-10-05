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

package tools.dynamia.reports.ui

import converters.CurrencySimple
import converters.Decimal
import converters.Integer
import org.zkoss.zhtml.Hr
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.SortEvent
import org.zkoss.zul.*
import tools.dynamia.actions.Action
import tools.dynamia.actions.ActionEvent
import tools.dynamia.actions.ActionEventBuilder
import tools.dynamia.commons.ClassMessages
import tools.dynamia.commons.StringUtils
import tools.dynamia.commons.ValueWrapper
import tools.dynamia.commons.reflect.AccessMode
import tools.dynamia.commons.reflect.PropertyInfo
import tools.dynamia.crud.FilterCondition
import tools.dynamia.domain.Identifiable
import tools.dynamia.domain.ValidationError
import tools.dynamia.domain.query.QueryCondition
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.reports.api.EnumFilterProvider
import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportFilterOption
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.ReportsUtils
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportField
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.reports.core.services.impl.ReportDataSource
import tools.dynamia.ui.MessageType
import tools.dynamia.ui.UIMessages
import tools.dynamia.viewers.Field
import tools.dynamia.viewers.impl.DefaultViewDescriptor
import tools.dynamia.web.util.HttpUtils
import tools.dynamia.zk.actions.ButtonActionRenderer
import tools.dynamia.zk.addons.chartjs.CategoryChartjsData
import tools.dynamia.zk.addons.chartjs.Chartjs
import tools.dynamia.zk.crud.ui.EntityFiltersPanel

class ReportViewer extends Div implements ActionEventBuilder {


    public static final int MAX_RESULT_TO_DISPLAY = 2000
    private ClassMessages messages = ClassMessages.get(ReportViewer.class)
    private ReportsService service
    private Report report
    private ReportDataSource dataSource
    private Borderlayout layout
    private EntityFiltersPanel filtersPanel
    private Listbox dataView
    private ReportData reportData
    private Button executeButton
    private Button exportButton
    private Button reloadButton
    private Hlayout buttons
    private List<Action> actions
    private List<Chartjs> currentCharts
    private Component filtersContainer
    private Component dataViewContainer
    private Component chartsContainer
    private ReportFilters filters

    ReportViewer(ReportsService service, Report report, ReportDataSource dataSource) {
        this.service = service
        this.report = report
        this.dataSource = dataSource
        this.actions = new ArrayList<>()
        if (this.report != null) {
            init()
        }
    }

    private void init() {
        this.report = this.service.loadReportModel(this.report.id)
        initUI()
        initFiltersPanel()
        initDataView()
    }

    void reload() {
        children.clear()
        init()
        renderActions()
    }


    def initUI() {
        vflex = "1"
        layout = new Borderlayout()
        layout.with {
            appendChild(new Center())
            appendChild(new South())
            vflex = "1"
            hflex = "1"
            south.style = "padding-top: 3px "
        }

        dataViewContainer = layout.center

        if (HttpUtils.isSmartphone()) {
            initMobileLayout()
        } else {
            initDesktopLayout()
        }
        appendChild(layout)

        this.buttons = new Hlayout()
        executeButton = new Button(messages.get("execute"))
        executeButton.addEventListener(Events.ON_CLICK, { execute() })
        executeButton.zclass = "btn btn-primary"
        executeButton.iconSclass = "fa fa-play"
        buttons.appendChild(executeButton)

        exportButton = new Button(messages.get("exportExcel"))
        exportButton.iconSclass = "fa fa-file-excel-o"
        exportButton.addEventListener(Events.ON_CLICK, { export() })
        exportButton.zclass = "btn btn-success"
        buttons.appendChild(exportButton)

        reloadButton = new Button(messages.get("reload"))
        reloadButton.iconSclass = "fa fa-refresh"
        reloadButton.addEventListener(Events.ON_CLICK, { reload() })
        reloadButton.zclass = "btn btn-danger"
        buttons.appendChild(reloadButton)
        layout.south.appendChild(buttons)
    }

    def initMobileLayout() {
        Tabbox content = new Tabbox()
        layout.center.children.clear()
        layout.center.appendChild(content)
        content.with {
            appendChild(new Tabs())
            appendChild(new Tabpanels())
            vflex = "1"
            hflex = "1"
        }

        if (report.filters) {
            content.tabs.appendChild(new Tab(messages.get("filters")))
            filtersContainer = new Tabpanel()
            content.tabpanels.appendChild(filtersContainer)
        }

        content.tabs.appendChild(new Tab(messages.get("result")))
        dataViewContainer = new Tabpanel()
        content.tabpanels.appendChild(dataViewContainer)

        if (report.chartable) {
            content.tabs.appendChild(new Tab(messages.get("charts")))
            chartsContainer = new Tabpanel()
            content.tabpanels.appendChild(chartsContainer)
        }
    }

    private void initDesktopLayout() {
        if (report.filters) {
            layout.with {
                appendChild(new West())
                west.width = "20%"
                west.collapsible = true
                west.splittable = true
                west.title = messages.get("filters")
            }
            filtersContainer = layout.west
        }

        if (report.chartable) {
            layout.with {
                appendChild(new East())
                east.width = "40%"
                east.splittable = true
                east.autoscroll = true
            }
            chartsContainer = layout.east
        }
    }

    def initFiltersPanel() {
        if (!report.filters.empty) {

            def descriptor = new DefaultViewDescriptor()
            report.filters.each { filter ->
                Field field = new Field(filter.name, filter.dataType.typeClass)
                field.propertyInfo = new PropertyInfo(field.name, field.fieldClass, Report, AccessMode.READ_WRITE)
                field.label = filter.label
                field.required = filter.required
                field.addParam("condition", FilterCondition.EQUALS.name())

                if (filter.dataType == DataType.ENUM && filter.enumClassName != null) {
                    EnumFilterProvider provider = ReportsUtils.findEnumFilterProvider(filter.enumClassName);
                    if (provider != null) {
                        field.fieldClass = Class.forName(filter.enumClassName)
                        field.propertyInfo = new PropertyInfo(field.name, field.fieldClass, Report, AccessMode.READ_WRITE)
                        field.addParam("enumValues", Arrays.asList(provider.values))
                    } else {
                        field.visible = false
                    }

                } else if (filter.dataType == DataType.ENTITY && filter.entityClassName != null) {
                    field.fieldClass = Class.forName(filter.entityClassName)
                    field.propertyInfo = new PropertyInfo(field.name, field.fieldClass, Report, AccessMode.READ_WRITE)
                } else if (filter.queryValues != null && !filter.queryValues.empty) {
                    List<ReportFilterOption> options = filter.loadOptions(dataSource)
                    field.component = "combobox"
                    field.addParam("readonly", true)
                    field.addParam("model", new ValueWrapper(new ListModelList(options), ListModel))
                    field.addParam("itemRenderer", new ValueWrapper(new ReportFilterOptionItemRenderer(), ComboitemRenderer))
                } else if (filter.dataType == DataType.BOOLEAN) {
                    field.component = "booleanbox"
                }
                descriptor.addField(field)
            }

            filtersPanel = new EntityFiltersPanel(Report)
            filtersPanel.viewDescriptor = descriptor
            filtersPanel.addEventListener(EntityFiltersPanel.ON_SEARCH, { execute() })
            filtersContainer.appendChild(filtersPanel)
            filtersPanel.south.detach()
        }
    }

    def initDataView() {
        dataView = new Listbox()
        dataView.sclass = "table-view"
        dataView.with {
            appendChild(new Listhead())
            appendChild(new Listfoot())
            vflex = "1"
            hflex = "1"
            mold = "paging"
            sizedByContent = true
        }

        addColumnNumber()

        if (!report.autofields) {

            report.fields.toSorted { a, b -> a.order <=> b.order }.each { f ->
                Listheader col = new Listheader(f.label)
                col.sortAscending = new FieldComparator(f.name, true)
                col.sortDescending = new FieldComparator(f.name, false)
                setupColumn(f, col)
                dataView.listhead.appendChild(col)

                createFooter(f.name)
            }
        }

        dataViewContainer.appendChild(dataView)

    }

    private void createFooter(String name) {
        Listfooter footer = new Listfooter()
        footer.attributes["reportFieldName"] = name
        dataView.listfoot.appendChild(footer)
    }

    def execute() {
        try {
            this.filters = new ReportFilters()

            if (filtersPanel != null) {
                QueryParameters params = filtersPanel.queryParameters
                validate(params)
                params.each { k, v -> filters.add(report.findFilter(k), getFilterValue(v)) }
            }


            this.reportData = service.execute(report, filters, dataSource)
            if (reportData.empty) {
                UIMessages.showMessage(messages.get("noresult"), MessageType.WARNING)
            } else {
                UIMessages.showMessage("$reportData.size ${messages.get("results")}")
            }
            if (reportData.size > MAX_RESULT_TO_DISPLAY) {
                UIMessages.showQuestion("El resultado de la consulta es muy grande ($reportData.size) para visualizarse. Desea exportarlo a excel?",{
                    export()
                })
            } else {
                if (report.autofields) {
                    buildAutoColumns()
                }
                updateDataView()
            }
        } catch (ValidationError e) {
            UIMessages.showMessage(e.message, MessageType.ERROR)
        } catch (Exception e) {
            if (e.message.contains("interrupted")) {
                Messagebox.show("La consulta demora mucho tiempo en procesarse, por favor utilice otros filtros" +
                        " o intente mas tarde. Por ejemplo, si esta usando un rango de fechas reduzca la diferencia.", "Error al Consultar",
                        Messagebox.OK, Messagebox.ERROR)
            } else {
                Messagebox.show(e.message)
                e.printStackTrace()
            }
        }

    }

    def validate(QueryParameters params) {
        def requiredFilters = report.filters.findAll { it.required }
        def filter = requiredFilters.find { !params.containsKey(it.name) }
        if (filter != null) {
            throw new ValidationError(messages.get("errorfiltersRequired", filter.label))
        }
    }

    def getFilterValue(Object filterValue) {
        if (filterValue instanceof QueryCondition) {

            if (filterValue.value instanceof ReportFilterOption) {
                def opt = filterValue.value as ReportFilterOption
                return opt.value
            } else if (filterValue.value instanceof Enum && report.queryLang == "sql") {
                return filterValue.value.ordinal()
            } else if (filterValue.value instanceof Identifiable) {
                return filterValue.value.id
            } else {
                return filterValue.value
            }
        } else {
            return filterValue
        }
    }

    def export() {
        if (report.exportWithoutFormat) {
            new ExcelReportDataExporter(report).export(reportData)
        } else {
            new ExcelFormattedReportDataExporter(report, filters).export(reportData)
        }
    }

    def updateDataView() {

        def fieldsNames = report.autofields ? reportData.fieldNames : report.fields.collect { it.name }

        dataView.items.clear()
        def totals = [:]
        def count = 0
        reportData.entries.each { data ->
            def row = new Listitem()
            dataView.appendChild(row)

            count++
            def cellCount = new Listcell(count.toString())
            cellCount.sclass = "grey lighten-2"
            cellCount.setStyle("font-weight: bold")
            cellCount.setParent(row)

            fieldsNames.each { String fieldName ->
                Object cellData = data.values[fieldName]

                //Compute Totales
                if (cellData instanceof Number) {

                    def fieldTotal = totals[fieldName]
                    if (fieldTotal == null) {
                        if (cellData instanceof BigDecimal) {
                            fieldTotal = BigDecimal.ZERO
                        } else if (cellData instanceof Double) {
                            fieldTotal = 0.0
                        } else {
                            fieldTotal = 0
                        }
                    }
                    totals[fieldName] = fieldTotal + cellData
                }

                def cell = new Listcell()
                cell.attributes["result"] = cellData

                def cellValue = new Label()

                ReportField reportField = report.findField(fieldName)
                if (reportField != null) {
                    switch (reportField.dataType) {
                        case DataType.CURRENCY:
                            if (cellData instanceof Number) {
                                cellData = new CurrencySimple().format(cellData)
                            }
                            break
                    }
                    cell.sclass = reportField.cellStyle
                    cellData = reportField.upperCase ? cellData?.toString()?.toUpperCase() : cellData
                }

                cellValue.value = cellData?.toString()
                cell.appendChild(cellValue)
                row.appendChild(cell)
            }
        }

        //render totals
        totals.entrySet().each { entry ->
            Listfooter footer = dataView.listfoot.children.find {
                ((Listfooter) it).attributes["reportFieldName"] == entry.key
            }

            if (footer != null) {
                def footerValue = entry.value
                if (footerValue != null) {
                    ReportField reportField = report.findField(entry.key);
                    if (reportField != null) {
                        switch (reportField.dataType) {
                            case DataType.CURRENCY:
                                footerValue = new CurrencySimple().format(footerValue)
                                break
                            case DataType.NUMBER:
                                if (footerValue instanceof Double) {
                                    footerValue = new Decimal().format(footerValue)
                                } else {
                                    footerValue = new Integer().format(footerValue)
                                }
                        }
                        footer.style = reportField.cellStyle
                    }
                    footer.setLabel(footerValue.toString())
                }
            }
        }
        try {
            updateChartView()
        } catch (Exception e) {
            UIMessages.showMessage(messages.get("errorCharting") + ":${e.message}", MessageType.ERROR)
            layout.west?.detach()
        }

    }

    def updateChartView() {
        if (report.chartable && report.charts && chartsContainer) {
            chartsContainer.children.clear()
            currentCharts = []

            def chartLayout = new Vlayout()
            chartsContainer.appendChild(chartLayout)

            report.charts.each { c ->
                def data = new CategoryChartjsData()
                if (c.grouped) {
                    Map<String, Number> groups = new HashMap<>()
                    reportData.getEntries().each {
                        def label = it.values[c.labelField].toString()
                        def value = (Number) it.values[c.valueField]
                        if (value == null) {
                            value = 0
                        }

                        def sum = groups.get(label)
                        sum = sum == null ? value : sum + value
                        groups.put(label, sum)
                    }
                    groups.each { k, v -> data.add(k, v) }
                } else {
                    reportData.getEntries().each {
                        def label = it.values[c.labelField].toString()
                        def value = (Number) it.values[c.valueField]
                        data.add(label, value)
                    }
                }

                def chart = new Chartjs()
                chart.type = c.type
                chart.data = data
                chart.title = c.title
                currentCharts << chart


                chartLayout.appendChild(chart)
                chartLayout.appendChild(new Hr())
            }
        }
    }

    private void buildAutoColumns() {
        dataView.listhead.children.clear()
        dataView.listfoot.children.clear()

        addColumnNumber()

        reportData.fieldNames.each { fieldName ->

            Listheader col = new Listheader(StringUtils.addSpaceBetweenWords(StringUtils.capitalizeAllWords(fieldName)))
            col.attributes["reportFieldName"] = fieldName
            col.sort = "auto"
            ReportField reportField = report.fields.find { it.name == fieldName }
            if (reportField != null) {
                setupColumn(reportField, col)
            } else {
                col.addEventListener(Events.ON_SORT, { SortEvent evt ->
                    reportData.sort(fieldName, evt.ascending)
                    updateDataView()
                })
            }

            dataView.listhead.appendChild(col)
            createFooter(fieldName)
        }
    }

    private void addColumnNumber() {
        Listheader colNum = new Listheader("N.")
        colNum.sclass = "grey color-white"
        dataView.listhead.appendChild(colNum)

        Listfooter footNum = new Listfooter()
        dataView.listfoot.appendChild(footNum)
    }

    private void setupColumn(ReportField reportField, Listheader col) {
        col.attributes["reportField"] = reportField
        col.attributes["reportFieldName"] = reportField.name
        col.align = reportField.align.name()
        col.label = reportField.label
        col.width = reportField.width
        col.sclass = reportField.columnStyle


        col.addEventListener(Events.ON_SORT, { SortEvent evt ->
            reportData.sort(reportField.name, evt.ascending)
            updateDataView()
        })

    }

    Button getExecuteButton() {
        return executeButton
    }

    Button getExportButton() {
        return exportButton
    }

    Button getReloadButton() {
        return reloadButton
    }

    Report getReport() {
        return report
    }

    List<Chartjs> getCurrentCharts() {
        return currentCharts
    }

    ReportData getReportData() {
        return reportData
    }

    Borderlayout getLayout() {
        return layout
    }

    Listbox getDataView() {
        return dataView
    }

    void addAction(Action action) {
        actions << action
    }

    def renderActions() {
        ButtonActionRenderer renderer = new ButtonActionRenderer()
        renderer.zclass = "btn btn-default"
        actions.each { action ->
            buttons.appendChild(renderer.render(action, this))
        }

    }

    @Override
    ActionEvent buildActionEvent(Object source, Map<String, Object> params) {
        return new ActionEvent(report, this, params)
    }

}
