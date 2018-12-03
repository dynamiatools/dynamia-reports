/*
 * Copyright (C) 2009 - 2018 - Dynamia Soluciones IT SAS (NIT 900302344-1).
 *
 * Licenciado bajo la licencia de código propietario de software de Dynamia Soluciones IT. Prohibida la copia
 * o distribución de este archivo de forma parcial o completa sin autorización previa de los autores.
 * Si desea conocer los detalles de esta licencia ingrese al sitio web:
 *
 * http://www.dynamiasoluciones.com/licencia
 *
 * Todos los derechos reservados
 */

package tools.dynamia.reports.core.domain

import tools.dynamia.domain.Descriptor
import tools.dynamia.domain.contraints.NotEmpty
import tools.dynamia.modules.saas.api.SimpleEntitySaaS
import tools.dynamia.reports.core.domain.enums.DataType
import tools.dynamia.reports.core.domain.enums.TextAlign

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "rpt_reports_charts")
@Descriptor(fields = ["title", "labelField", "valueField", "type", "grouped", "order"], viewParams = "columns: 3")
class ReportChart extends SimpleEntitySaaS {

    @ManyToOne
    Report report
    @NotEmpty
    @NotNull
    String title
    @NotEmpty
    String labelField
    @NotEmpty
    String valueField
    @Descriptor(params = "placeholder: pie, bar, horizontalbar, line, doughnut")
    String type
    @Column(name = "fieldOrder")
    @Descriptor(params = "width: 100px")
    int order = 0
    boolean grouped

    @Override
    String toString() {
        return title
    }
}
