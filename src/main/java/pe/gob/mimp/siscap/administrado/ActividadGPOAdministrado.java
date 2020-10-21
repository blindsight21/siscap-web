package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.ActividadGobPubliObje;
import pe.gob.mimp.siscap.ws.actividadgpo.cliente.ActividadGPOCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ActividadGPOAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ActividadGobPubliObje.class.getName());
    private static final long serialVersionUID = 1L;

    private ActividadGobPubliObje entidadSeleccionada;
    private List<ActividadGobPubliObje> actividadGPObjList;

    @Inject
    private ActividadGPOCallService actividadGPOCallService;

    public ActividadGPOAdministrado() {

    }

    public void initBean() {
        logger.info(":: ActividadGobAdministrado.initBean :: Starting execution...");
        try {
            this.entidadSeleccionada = new ActividadGobPubliObje();
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error method initBean" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.initBean :: Execution finish.");
    }

    public ActividadGobPubliObje getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ActividadGobPubliObje entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ActividadGobPubliObje> getActividadGPObjList() {
        return actividadGPObjList;
    }

    public void setActividadGPObjList(List<ActividadGobPubliObje> actividadGPObjList) {
        this.actividadGPObjList = actividadGPObjList;
    }

    public boolean validarFormularioGPO() {

        try {
            if (Funciones.esVacio(this.getEntidadSeleccionada().getNumContadorMarculino())) {
                return enviarWarnMessage("Ingrese el Cantidad de Participantes del Sexo Masculino.");
            }
            if (Funciones.esVacio(this.getEntidadSeleccionada().getNumContadorFemenino())) {
                return enviarWarnMessage("Ingrese el Cantidad de Participantes del Sexo Femenino.");
            }
            return true;
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error validarFormularioGPO" + e.getMessage(), Util.tiempo());
            return false;
        }
    }

    private boolean enviarWarnMessage(String mensaje) {
        adicionarMensajeError("Información", mensaje);
        return false;
    }

    public void editarActividadGPO(ActividadGobPubliObje entidadSeleccionada) {
        logger.info(":: ActividadGobAdministrado.editarActividadGob :: Starting execution...");
        if (validarFormularioGPO()) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                this.entidadSeleccionada.setNumContadorTotal(this.entidadSeleccionada.getNumContadorMarculino().add(this.entidadSeleccionada.getNumContadorFemenino()));
                this.entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                this.entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                this.entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                this.entidadSeleccionada.setFecEdicion(new Date());
                actividadGPOCallService.editarActividadGPO(entidadSeleccionada);

                RequestContext.getCurrentInstance().execute("PF('dialogoEditarPublicoObjetivo').hide()");

            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarActividadGob" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ActividadGobAdministrado.editarActividadGob() :: Execution finish");
    }
}
