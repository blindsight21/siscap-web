/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.seguridad.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class Logout extends AdministradorAbstracto implements Serializable {

    public void welcomeListener() {

    }

    public void logoutListener() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
    }
}
