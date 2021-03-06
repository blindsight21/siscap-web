package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.siscap.modelo.VwActividad;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.vwactividad.cliente.VwActividadCallService;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class VwActividadAdministrado extends AdministradorAbstracto implements Serializable {

    private VwActividad entidad;
    private VwActividad entidadSeleccionada;
    private List<VwActividad> entidades;
    private List<VwActividad> entidadConsulta;

    @Inject
    private VwActividadCallService vwActividadCallService;
    @Inject
    private DepartamentoCallService departamentoCallService;

    public VwActividadAdministrado() {

        this.entidad = new VwActividad();
        this.entidadSeleccionada = new VwActividad();
    }

    public VwActividad getEntidad() {
        return entidad;
    }

    public void setEntidad(VwActividad entidad) {
        this.entidad = entidad;
    }

    public VwActividad getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(VwActividad entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<VwActividad> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<VwActividad> entidades) {
        this.entidades = entidades;
    }

    public List<VwActividad> getEntidadConsulta() {
        this.entidadConsulta = vwActividadCallService.findAll();

        return entidadConsulta;
    }

    public void setEntidadConsulta(List<VwActividad> entidadConsulta) {
        this.entidadConsulta = entidadConsulta;
    }

    public List<VwActividad> obtenerActividadesDepartamento(BigDecimal nidDepartamento) {

        List<VwActividad> actividadEncontradas = null;
        try {
            if (null != nidDepartamento) {
                actividadEncontradas = vwActividadCallService.obtenerActividadesDepartamento(nidDepartamento);
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
        return actividadEncontradas;
    }

}
