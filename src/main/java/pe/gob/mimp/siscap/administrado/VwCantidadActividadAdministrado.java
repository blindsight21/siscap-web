package pe.gob.mimp.siscap.administrado;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.siscap.ws.vwactividad.cliente.VwActividadCallService;

@ManagedBean
@ViewScoped
public class VwCantidadActividadAdministrado {

    private VwCantidadActividadAdministrado entidad;
    private VwCantidadActividadAdministrado entidadSeleccionada;
    private List<VwCantidadActividadAdministrado> entidades;

    @Inject
    private VwActividadCallService vwActividadCallService;

    public VwCantidadActividadAdministrado() {

        this.entidad = new VwCantidadActividadAdministrado();
        this.entidadSeleccionada = new VwCantidadActividadAdministrado();
    }

    public VwCantidadActividadAdministrado getEntidad() {
        return entidad;
    }

    public void setEntidad(VwCantidadActividadAdministrado entidad) {
        this.entidad = entidad;
    }

    public VwCantidadActividadAdministrado getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(VwCantidadActividadAdministrado entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<VwCantidadActividadAdministrado> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<VwCantidadActividadAdministrado> entidades) {
        this.entidades = entidades;
    }

}
