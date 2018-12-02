package tools.dynamia.reports.core.domain.enums

enum DataType {


    TEXT(String), DATE(Date), NUMBER(Long), CURRENCY(BigDecimal), ENUM(Enum), ENTITY(Object)

    private Class typeClass


    DataType(Class typeClass) {
        this.typeClass = typeClass
    }

    Class getTypeClass() {
        return typeClass
    }
}