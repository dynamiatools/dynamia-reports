package tools.dynamia.reports.ui

import converters.CurrencySimple
import org.zkoss.zhtml.H2
import org.zkoss.zhtml.Text
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.SortEvent
import org.zkoss.zul.*
import tools.dynamia.actions.Action
import tools.dynamia.actions.ActionEvent
import tools.dynamia.actions.ActionEventBuilder
import tools.dynamia.commons.StringUtils
import tools.dynamia.commons.ValueWrapper
import tools.dynamia.commons.reflect.AccessMode
import tools.dynamia.commons.reflect.PropertyInfo
import tools.dynamia.crud.FilterCondition
import tools.dynamia.domain.query.QueryCondition
import tools.dynamia.domain.query.QueryParameters
import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportFilterOption
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportField
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.services.ReportsService
import tools.dynamia.reports.core.services.impl.ReportDataSource
import tools.dynamia.reports.excel.ExcelFileWriter
import tools.dynamia.ui.MessageType
import tools.dynamia.ui.UIMessages
import tools.dynamia.viewers.Field
import tools.dynamia.viewers.impl.DefaultViewDescriptor
import tools.dynamia.zk.actions.ButtonActionRenderer
import tools.dynamia.zk.crud.ui.EntityFiltersPanel

class ReportViewer extends Div implements ActionEventBuilder {


    private ReportsService service
    private Report report
    private ReportDataSource dataSource
    private Borderlayout layout
    private EntityFiltersPanel filtersPanel
    private Listbox dataView
    private ReportData reportData
    private Button executeButton
    private Button exportButton
    private Hlayout buttons
    private List<Action> actions

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
            appendChild(new North())
            appendChild(new Center())
            appendChild(new East())
            appendChild(new South())

            vflex = "1"
            hflex = "1"
            east.width = "25%"
            east.collapsible = true
            east.splittable = true
            east.title = "Filters"
        }
        appendChild(layout)

        def title = new Div()
        def h2 = new H2()
        h2.appendChild(new Text(report.name))

        title.appendChild(h2)
        title.appendChild(new Label(report.description))
        layout.north.appendChild(title)

        this.buttons = new Hlayout()
        executeButton = new Button("Execute")
        executeButton.addEventListener(Events.ON_CLICK, { execute() })
        executeButton.zclass = "btn btn-primary"
        buttons.appendChild(executeButton)

        exportButton = new Button("Export to Excel")
        exportButton.iconSclass = "fa fa-file-excel-o"
        exportButton.addEventListener(Events.ON_CLICK, { export() })
        exportButton.zclass = "btn btn-success"
        buttons.appendChild(exportButton)

        layout.south.sclass = "pd10"
        layout.south.appendChild(buttons)
    }

    def initFiltersPanel() {
        if (!report.filters.empty) {

            def descriptor = new DefaultViewDescriptor()
            report.filters.each { filter ->
                Field field = new Field(filter.name, filter.dataType.typeClass)
                field.propertyInfo = new PropertyInfo(field.name, field.fieldClass, Report, AccessMode.READ_WRITE)
                field.label = filter.label
                field.addParam("condition", FilterCondition.EQUALS.name())

                if (filter.dataType == DataType.ENUM) {
                    field.fieldClass = Class.forName(filter.enumClassName)
                } else if (filter.dataType == DataType.ENTITY) {
                    field.fieldClass = Class.forName(filter.entityClassName)
                }

                if (filter.queryValues != null && !filter.queryValues.empty) {
                    List<ReportFilterOption> options = filter.loadOptions(dataSource)
                    field.component = "combobox"

                    field.addParam("readonly", true)
                    field.addParam("model", new ValueWrapper(new ListModelList(options), ListModel))
                    field.addParam("itemRenderer", new ValueWrapper(new ReportFilterOptionItemRenderer(), ComboitemRenderer))
                }
                descriptor.addField(field)
            }

            filtersPanel = new EntityFiltersPanel(Report)
            filtersPanel.viewDescriptor = descriptor
            filtersPanel.addEventListener(EntityFiltersPanel.ON_SEARCH, { execute() })

            layout.east.appendChild(filtersPanel)

        } else {
            layout.east.detach()
        }
    }

    def initDataView() {
        dataView = new Listbox()
        dataView.sclass = "table-view"
        dataView.with {
            appendChild(new Listhead())

            vflex = "1"
            hflex = "1"
            mold = "paging"
            sizedByContent = true

        }



        if (!report.autofields) {

            report.fields.toSorted { a, b -> a.order <=> b.order }.each { f ->
                Listheader col = new Listheader(f.label)
                col.sortAscending = new FieldComparator(f.name, true)
                col.sortDescending = new FieldComparator(f.name, false)
                setupColumn(f, col)
                dataView.listhead.appendChild(col)
            }
        }

        layout.center.appendChild(dataView)

    }

    def execute() {
        try {
            def filters = new ReportFilters()

            if (filtersPanel != null) {
                QueryParameters params = filtersPanel.queryParameters
                params.each { k, v -> filters.add(report.findFilter(k), getFilterValue(v)) }
            }

            this.reportData = service.execute(report, filters, dataSource)
            if (reportData.empty) {
                UIMessages.showMessage("No result", MessageType.WARNING)
            } else {
                UIMessages.showMessage("$reportData.size results found")
            }
            if (report.autofields) {
                buildAutoColumns()
            }
            updateDataView()
        } catch (Exception e) {
            Messagebox.show(e.message)
        }

    }

    def getFilterValue(Object filterValue) {
        if (filterValue instanceof QueryCondition) {

            if (filterValue.value instanceof ReportFilterOption) {
                def opt = filterValue.value as ReportFilterOption
                return opt.value
            } else {
                return filterValue.value
            }
        } else {
            return filterValue
        }
    }

    def export() {
        if (reportData != null) {
            def file = File.createTempFile(report.name.replace(" ", "_") + "_", ".xlsx")
            def exporter = new ExcelFileWriter(file)
            //columns
            if (report.autofields) {
                reportData.fieldNames.each { f ->
                    ReportField reportField = report.fields.find { it.name == f }
                    if (reportField != null) {
                        exporter.addCell(reportField.label)
                    } else {
                        exporter.addCell(StringUtils.capitalizeAllWords(StringUtils.addSpaceBetweenWords(f)))
                    }
                }
            } else {
                report.fields.toSorted { a, b -> a.order <=> b.order }.each { f ->
                    exporter.addCell(f.label)
                }
            }
            reportData.entries.each { data ->
                exporter.newRow()
                reportData.fieldNames.each { f ->
                    exporter.addCell(data.values[f])
                }
            }
            exporter.write()
            exporter.close()

            Filedownload.save(file, "application/excel")
        }
    }

    def updateDataView() {


        def fieldsNames = report.autofields ? reportData.fieldNames : report.fields.collect { it.name }


        dataView.items.clear()
        reportData.entries.each { data ->
            def row = new Listitem()
            dataView.appendChild(row)
            fieldsNames.each { f ->
                Object cellData = data.values[f]

                def cell = new Listcell()
                cell.attributes["data"] = cellData
                def cellValue = new Label()

                ReportField reportField = report.fields.find { it.name == f }
                if (reportField != null) {
                    switch (reportField.dataType) {
                        case DataType.CURRENCY:
                            cellData = new CurrencySimple().coerceToUi(cellData, cellValue)
                            break
                    }
                    cell.style = reportField.cellStyle
                    cellData = reportField.upperCase ? cellData?.toString()?.toUpperCase() : cellData
                }

                cellValue.value = cellData?.toString()
                cell.appendChild(cellValue)
                row.appendChild(cell)
            }
        }
    }

    private void buildAutoColumns() {
        dataView.listhead.children.clear()
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
        }
    }

    private void setupColumn(ReportField reportField, Listheader col) {
        col.attributes["reportField"] = reportField
        col.attributes["reportFieldName"] = reportField.name
        col.align = reportField.align.name()
        col.label = reportField.label
        col.style = reportField.columnStyle
        col.width = reportField.width


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
