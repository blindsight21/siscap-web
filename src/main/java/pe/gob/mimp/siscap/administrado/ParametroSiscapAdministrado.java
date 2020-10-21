package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.siscap.modelo.ParametroSiscap;
import pe.gob.mimp.siscap.ws.parametrosiscap.cliente.ParametroSiscapCallService;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ParametroSiscapAdministrado extends AdministradorAbstracto implements Serializable {

    private ParametroSiscap entidadSeleccionada;
    private List<ParametroSiscap> entidades;

    @Inject
    private ParametroSiscapCallService parametroSiscapCallService;

    public ParametroSiscapAdministrado() {

        this.entidadSeleccionada = new ParametroSiscap();
        this.entidades = new ArrayList<>();
    }

    public ParametroSiscap getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ParametroSiscap entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ParametroSiscap> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<ParametroSiscap> entidades) {
        this.entidades = entidades;
    }

    public int tiempoAlertas() {
        int valor = 0;
        try {
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("cidParametro");
            findAllByFieldBean.setValue("TIEMPOALERTAS");
            List<ParametroSiscap> parametro = parametroSiscapCallService.findAllByField(findAllByFieldBean);
            if (null != parametro && 0 < parametro.size()) {
                valor = parametro.get(0).getNumValor1().intValue();
            } else {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}tiempoAlertas: No se ha definido un parámetro para este valor", Util.tiempo());
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }

        return valor;
    }

    public int tiempoIdle() {
        int valor = 0;
        try {
            
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("cidParametro");
            findAllByFieldBean.setValue("TIEMPOSESION");
            List<ParametroSiscap> parametro = parametroSiscapCallService.findAllByField(findAllByFieldBean);
            if (null != parametro && 0 < parametro.size()) {
                valor = parametro.get(0).getNumValor1().intValue();
            } else {
                System.out.println("tiempoIdle: tiempoAlertas: No se ha definido un parámetro para este valor.");
            }
        } catch (Exception e) {
            System.out.println("tiempoIdle: " + e);
        }

        return valor;
    }

    public void editar(ParametroSiscap entidadSeleccionada) throws UnknownHostException, Exception {
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
        if (null != entidadSeleccionada) {

            entidadSeleccionada.setTxtValor(this.entidadSeleccionada.getTxtValor());
            entidadSeleccionada.setNumValor1(this.entidadSeleccionada.getNumValor1());
            entidadSeleccionada.setNumValor2(this.entidadSeleccionada.getNumValor2());

            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            entidadSeleccionada.setFlgActivo(BigInteger.ONE);
            parametroSiscapCallService.editarParametroSiscap(entidadSeleccionada);
        }
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public void invalidarSesion() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
    }

    public void redirigir() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/");
        ec.invalidateSession();
    }

}
