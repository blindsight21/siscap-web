package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.modelo.CorreoPersonaSiscap;
import pe.gob.mimp.siscap.ws.correopersonasiscap.cliente.CorreoPersonaSiscapCallService;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class CorreoPersonaSiscapAdministrado extends AdministradorAbstracto implements Serializable {

    private CorreoPersonaSiscap entidad;
    private CorreoPersonaSiscap entidadSeleccionada;
    private List<CorreoPersonaSiscap> entidades;

    @Inject
    private CorreoPersonaSiscapCallService correoPersonaSiscapCallService;

    public CorreoPersonaSiscapAdministrado() {

        this.entidad = new CorreoPersonaSiscap();
        this.entidadSeleccionada = new CorreoPersonaSiscap();
    }

    public CorreoPersonaSiscap getEntidad() {
        return entidad;
    }

    public void setEntidad(CorreoPersonaSiscap entidad) {
        this.entidad = entidad;
    }

    public CorreoPersonaSiscap getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(CorreoPersonaSiscap entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<CorreoPersonaSiscap> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<CorreoPersonaSiscap> entidades) {
        this.entidades = entidades;
    }

}
