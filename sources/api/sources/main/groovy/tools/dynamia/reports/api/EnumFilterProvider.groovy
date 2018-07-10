package tools.dynamia.reports.api

interface EnumFilterProvider<T extends Enum> {

    Class<T> getEnumClass()

    T[] getValues()

    String getName()

}
