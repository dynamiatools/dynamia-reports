package tools.dynamia.reports.ui


import org.apache.poi.ss.usermodel.BuiltinFormats
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.streaming.SXSSFRow
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.zkoss.zul.Filedownload
import tools.dynamia.commons.DateTimeUtils
import tools.dynamia.commons.StringUtils
import tools.dynamia.domain.util.DomainUtils
import tools.dynamia.integration.Containers
import tools.dynamia.reports.api.ReportGlobalParameterProvider
import tools.dynamia.reports.core.ReportData
import tools.dynamia.reports.core.ReportDataEntry
import tools.dynamia.reports.core.ReportFilters
import tools.dynamia.reports.core.domain.Report
import tools.dynamia.reports.core.domain.ReportField
import tools.dynamia.reports.core.domain.ReportFilter
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.domain.enums.TextAlign
import tools.dynamia.templates.VelocityTemplateEngine

class ExcelFormattedReportDataExporter implements ReportDataExporter {

    public static final String DATE_PATTERN = "yyyy-MM-dd"
    private Report report
    private ReportFilters filters
    private Map<String, Object> globalParams = new HashMap<>();

    private SXSSFWorkbook workbook
    private SXSSFSheet sheet


    ExcelFormattedReportDataExporter(Report report, ReportFilters filters) {
        this.report = report
        this.filters = filters
        Containers.get().findObjects(ReportGlobalParameterProvider).each { globalParams.putAll(it.params) }
    }

    def export(ReportData reportData) {
        if (reportData != null) {

            def file = File.createTempFile(report.name.replace(" ", "_") + "_", ".xlsx")
            this.workbook = new SXSSFWorkbook(200);
            workbook.setCompressTempFiles(true);

            sheet = workbook.createSheet(report.name);
            sheet.createFreezePane(0, 5)
            exportTitle()
            exportFilters()
            exportColumns(reportData)
            exportRows(reportData)
            workbook.write(new FileOutputStream(file))
            workbook.close()

            Filedownload.save(file, "application/excel")
        }
    }


    def exportTitle() {

        def font = workbook.createFont()
        font.bold = true


        def style = workbook.createCellStyle()
        style.font = font

        def row = sheet.createRow(0)
        def cell = row.createCell(0)
        cell.cellType = CellType.STRING
        cell.cellValue = report.name.toUpperCase()
        cell.cellStyle = style

        row = sheet.createRow(1)
        cell = row.createCell(0)
        cell.cellType = CellType.STRING

        String title = report.title
        if (title == null) {
            title = globalParams["DEFAULT_REPORT_TITLE"]
        }
        cell.cellValue = parse(title)
        cell.cellStyle = style

        String subtitle = report.subtitle
        if (subtitle == null) {
            subtitle = globalParams["DEFAULT_REPORT_SUBTITLE"]
        }

        row = sheet.createRow(2)
        cell = row.createCell(0)
        cell.cellType = CellType.STRING
        cell.cellValue = parse(subtitle)
        cell.cellStyle = style
    }

    def addParam(String name, Object value) {
        globalParams[name] = value
    }

    private String parse(String s) {
        if (s != null) {
            def engine = new VelocityTemplateEngine()
            return engine.evaluate(s, globalParams)
        } else {
            return ""
        }
    }

    def exportFilters() {

        def font = workbook.createFont()
        font.bold = true


        def style = workbook.createCellStyle()
        style.font = font

        int lastCell = 0
        def row = sheet.createRow(3)

        report.filters.each { filter ->
            def value = filters.getValue(filter.name)
            if (filter != null && value != null) {
                String filterValue = formatFilterValue(filter, value)
                if (filterValue != null) {
                    def label = row.createCell(lastCell++, CellType.STRING)
                    label.cellValue = filter.label
                    label.cellStyle = style
                    row.createCell(lastCell++, CellType.STRING).cellValue = filterValue;
                }
            }
        }
    }

    String formatFilterValue(ReportFilter filter, Object value) {
        if (value instanceof String) {
            return value
        } else if (value instanceof Date) {
            return DateTimeUtils.format(value, DATE_PATTERN)
        } else if (value instanceof Number && filter.dataType == DataType.ENTITY) {
            return loadEntityFilter(value.longValue(), filter.entityClassName)
        } else if (DomainUtils.isEntity(value) || value instanceof Enum) {
            return value.toString()
        } else if (filter.dataType == DataType.ENUM) {
            return value.toString()
        }


        return null
    }

    String loadEntityFilter(long id, String className) {
        try {
            Class clazz = Class.forName(className)
            return DomainUtils.lookupCrudService().find(clazz, (Long) id)?.toString()
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    private List<ReportDataEntry> exportRows(ReportData reportData) {
        int rowNum = 5

        def currencyStyle = workbook.createCellStyle()
        currencyStyle.alignment = HorizontalAlignment.RIGHT
        currencyStyle.dataFormat = workbook.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(6))

        def dateStyle = workbook.createCellStyle()
        dateStyle.dataFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd")

        def centerStyle = workbook.createCellStyle()
        centerStyle.alignment = HorizontalAlignment.CENTER


        reportData.entries.each { data ->
            def row = sheet.createRow(rowNum++)
            def colNum = 0
            reportData.fieldNames.each { f ->
                def cell = row.createCell(colNum++)

                ReportField reportField = report.fields.find { it.name == f }
                def value = data.values[f]
                if (value == null) {
                    value = ""
                }

                if (value instanceof Number) {
                    cell.cellType = CellType.NUMERIC
                    cell.cellValue = value as Double
                } else if (value instanceof Date) {
                    cell.cellType = CellType.STRING
                    cell.cellValue = value as Date
                    cell.cellStyle = dateStyle
                } else {
                    cell.cellType = CellType.STRING
                    cell.cellValue = value.toString()
                }

                if (reportField?.dataType == DataType.CURRENCY) {
                    if (reportField.format != null) {
                        currencyStyle.dataFormat = workbook.createDataFormat().getFormat(reportField.format)
                    }
                    cell.cellStyle = currencyStyle
                }

                if (reportField?.align == TextAlign.CENTER) {
                    cell.cellStyle = centerStyle
                }

            }
        }
    }

    private void exportColumns(ReportData reportData) {

        def row = sheet.createRow(4)
        if (report.autofields) {
            int column = 0
            reportData.fieldNames.each { f ->
                ReportField reportField = report.fields.find { it.name == f }

                if (reportField != null) {
                    addColumn(reportField.label, reportField.align, column++, row)
                } else {
                    String label = StringUtils.capitalizeAllWords(StringUtils.addSpaceBetweenWords(f))
                    addColumn(label, TextAlign.LEFT, column++, row)
                }
            }
        } else {
            int column = 0
            report.fields.toSorted { a, b -> a.order <=> b.order }.each { f ->
                addColumn(f.label, f.align, column++, row)
            }
        }
    }

    private void addColumn(String title, TextAlign align, int index, SXSSFRow row) {
        def cell = row.createCell(index, CellType.STRING)
        cell.cellValue = title

        def style = workbook.createCellStyle()
        style.alignment = HorizontalAlignment.valueOf(align.name())


        def font = workbook.createFont()
        font.bold = true
        style.font = font

        cell.cellStyle = style


    }
}
