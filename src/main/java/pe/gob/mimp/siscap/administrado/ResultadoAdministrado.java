package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.modelo.ActividadGobResultado;
import pe.gob.mimp.siscap.modelo.NivelEvaluacion;
import pe.gob.mimp.siscap.modelo.TipoEvaluacion;
import pe.gob.mimp.siscap.ws.actividadgr.cliente.ActividadGRCallService;
import pe.gob.mimp.siscap.ws.nivelevaluacion.cliente.NivelEvaluacionCallService;
import pe.gob.mimp.siscap.ws.tipoevaluacion.cliente.TipoEvaluacionCallService;

@SuppressWarnings("Serial")
@ManagedBean
@ViewScoped
public class ResultadoAdministrado extends AdministradorAbstracto implements Serializable {

    private ActividadGobResultado entidad;
    private ActividadGobResultado entidadSeleccionada;
    private List<ActividadGobResultado> entidades;

    private TipoEvaluacion tipoEvaluacion;
    private NivelEvaluacion nivelEvaluacion;

    @Inject
    private ActividadGRCallService actividadGRCallService;
    @Inject
    private TipoEvaluacionCallService tipoEvaluacionCallService;
    @Inject
    private NivelEvaluacionCallService nivelEvaluacionCallService;

    public ResultadoAdministrado() {

    }

    public ActividadGobResultado getEntidad() {
        return entidad;
    }

    public void setEntidad(ActividadGobResultado entidad) {
        this.entidad = entidad;
    }

    public ActividadGobResultado getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ActividadGobResultado entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ActividadGobResultado> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<ActividadGobResultado> entidades) {
        this.entidades = entidades;
    }

    public TipoEvaluacion getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public NivelEvaluacion getNivelEvaluacion() {
        return nivelEvaluacion;
    }

    public void setNivelEvaluacion(NivelEvaluacion nivelEvaluacion) {
        this.nivelEvaluacion = nivelEvaluacion;
    }

}
