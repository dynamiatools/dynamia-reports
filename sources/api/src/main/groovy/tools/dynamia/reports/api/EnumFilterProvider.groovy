package tools.dynamia.reports.api

interface EnumFilterProvider<T extends Enum> {

    /**
     * Fully qualified enum class name
     * @return
     */
    String getEnumClassName()

    T[] getValues()

    String getName()

}
