package pe.gob.mimp.general.administrado;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Ing. Oscar Mateo
 */
public abstract class AdministradorAbstracto {

    private Boolean modoEdicion;
    private Map<String, Object> userInformation;
    private Boolean validateEntidad;

    public AdministradorAbstracto() {
        this.userInformation = new HashMap<String, Object>();
        this.validateEntidad = false;
    }

    public Boolean getModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(Boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public Map<String, Object> getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(Map<String, Object> userInformation) {
        this.userInformation = userInformation;
    }

    public Boolean getValidateEntidad() {
        return validateEntidad;
    }

    public void setValidateEntidad(Boolean validateEntidad) {
        this.validateEntidad = validateEntidad;
    }

    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public void adicionarMensaje(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void adicionarMensajeError(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void adicionarMensajeWarning(String title, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, title, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void adicionarMensajeError(String resumen, Exception e) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen,
                "Ocurrio un error al realizar esta tarea, por favor comuniquese con su administrador");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void redirigirLogin() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
    }
}
