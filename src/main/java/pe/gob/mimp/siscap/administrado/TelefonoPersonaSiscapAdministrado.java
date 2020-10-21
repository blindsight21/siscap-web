package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.modelo.TelefonoPersonaSiscap;
import pe.gob.mimp.siscap.ws.telefonopersonasiscap.cliente.TelefonoPersonaSiscapCallService;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class TelefonoPersonaSiscapAdministrado extends AdministradorAbstracto implements Serializable {

    private TelefonoPersonaSiscap entidad;
    private TelefonoPersonaSiscap entidadSeleccionada;
    private List<TelefonoPersonaSiscap> entidades;

    @Inject
    private TelefonoPersonaSiscapCallService telefonoPersonaSiscapCallService;

    public TelefonoPersonaSiscapAdministrado() {

        this.entidad = new TelefonoPersonaSiscap();
        this.entidadSeleccionada = new TelefonoPersonaSiscap();

    }

    public TelefonoPersonaSiscap getEntidad() {
        return entidad;
    }

    public void setEntidad(TelefonoPersonaSiscap entidad) {
        this.entidad = entidad;
    }

    public TelefonoPersonaSiscap getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TelefonoPersonaSiscap entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TelefonoPersonaSiscap> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<TelefonoPersonaSiscap> entidades) {
        this.entidades = entidades;
    }

}
