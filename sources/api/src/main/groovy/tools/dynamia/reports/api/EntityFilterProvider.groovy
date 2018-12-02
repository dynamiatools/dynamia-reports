package tools.dynamia.reports.api

interface EntityFilterProvider {

    /**
     * Fully qualified class name
     * @return
     */
    String getEntityClassName()

    String getName()

}
