package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.siscap.modelo.PublicoObjetivo;
import pe.gob.mimp.siscap.ws.publicoobjetivo.cliente.PublicoObjetivoCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class PublicoObjetivoAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(PublicoObjetivo.class.getName());
    private static final long serialVersionUID = 1L;

    private PublicoObjetivo entidadSeleccionada;
    private List<PublicoObjetivo> publicoObjetivoList;

    @Inject
    private PublicoObjetivoCallService publicoObjetivoCallService;

    public PublicoObjetivoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new PublicoObjetivo();
        loadPublicoObjetivoList();
    }

    public PublicoObjetivo getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(PublicoObjetivo entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<PublicoObjetivo> getPublicoObjetivoList() {
        return publicoObjetivoList;
    }

    public void setPublicoObjetivoList(List<PublicoObjetivo> publicoObjetivoList) {
        this.publicoObjetivoList = publicoObjetivoList;
    }

    public PublicoObjetivo getEntidad(String id) {
        PublicoObjetivo publicoObjetivo = null;

        if ((null != id) || (false == id.equals(""))) {
            publicoObjetivo = publicoObjetivoCallService.find(new BigDecimal(id));
        }
        return publicoObjetivo;
    }

    public List<PublicoObjetivo> obtenerTodos() {

        List<PublicoObjetivo> publicoObjetivo = null;

        //publicoObjetivo = publicoObjetivoCallService.obtenerPublicoObjetivoTodos();
        return publicoObjetivo;
    }

    public List<PublicoObjetivo> obtenerActivo() {

        List<PublicoObjetivo> publicoObjetivoActivo = null;

        //publicoObjetivoActivo = publicoObjetivoCallService.obtenerPublicoObjetivoActivos();
        return publicoObjetivoActivo;
    }

    public List<PublicoObjetivo> obtenerPublicoObjetivos() {

        List<PublicoObjetivo> publicoObjetivos = null;

        //publicoObjetivos = publicoObjetivoCallService.obtenerPublicoObjetivoActivos();
        return publicoObjetivos;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtPublicoObjetivo().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtPublicoObjetivo", this.getEntidadSeleccionada().getTxtPublicoObjetivo().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = publicoObjetivoCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "PublicoObjetivo encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("Estado: Ya Existe Registro");
                    }
                }
            }
            return true;
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error validarFormulario" + e.getMessage(), Util.tiempo());
            return false;
        }
    }

    private boolean enviarWarnMessage(String mensaje) {
        adicionarMensajeError("Información", mensaje);
        return false;
    }

    public void crearPublicoObjetivo() {
        logger.info(":: PublicoObjetivoAdministrado.crearPublicoObjetivo() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                PublicoObjetivo publicoObjetivo = new PublicoObjetivo();
                publicoObjetivo.setTxtPublicoObjetivo(this.entidadSeleccionada.getTxtPublicoObjetivo());
                publicoObjetivo.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                publicoObjetivo.setTxtPc(Internet.obtenerNombrePC());
                publicoObjetivo.setTxtIp(Internet.obtenerIPPC());
                publicoObjetivo.setFecEdicion(new Date());
                publicoObjetivo.setFlgActivo(BigInteger.ONE);
                publicoObjetivoCallService.crearPublicoObjetivo(publicoObjetivo);

//                if (null != publicoObjetivo.getNidPublicoObjetivo()) {
                    // Update List Browser
                    loadPublicoObjetivoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoPublicoObjetivo').hide()");
//                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearPublicoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: PublicoObjetivoAdministrado.crearPublicoObjetivo() :: Execution finish.");
    }

    public void editarPublicoObjetivo(PublicoObjetivo entidadSeleccionada) {
        logger.info(":: PublicoObjetivoAdministrado.editarPublicoObjetivo() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    publicoObjetivoCallService.editarPublicoObjetivo(entidadSeleccionada);
                    // Update List Browser
                    loadPublicoObjetivoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarPublicoObjetivo').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarPublicoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: PublicoObjetivoAdministrado.editarPublicoObjetivo() :: Execution finish.");
    }

    public void anularPublicoObjetivo(PublicoObjetivo entidadSeleccionada) {
        logger.info(":: PublicoObjetivoAdministrado.anularPublicoObjetivo() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);                
                publicoObjetivoCallService.editarPublicoObjetivo(entidadSeleccionada);
                // Update List Browser
                loadPublicoObjetivoList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularPublicoObjetivo" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: PublicoObjetivoAdministrado.anularPublicoObjetivo() :: Execution finish.");
    }

    public String obtenerPublicoObjetivoporId(BigInteger nidPublicoObjetivo) {
        String resultado = "";

        try {
            if (null != nidPublicoObjetivo) {
                PublicoObjetivo publicoObjetivo = publicoObjetivoCallService.find(new BigDecimal(nidPublicoObjetivo));

                if (null != publicoObjetivo) {
                    resultado = publicoObjetivo.getTxtPublicoObjetivo();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener PublicoObjetivo" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarPublicoObjetivo() {

        entidadSeleccionada = new PublicoObjetivo();
    }

    private void loadPublicoObjetivoList() {
        logger.info(":: PublicoObjetivoAdministrado.loadPublicoObjetivoList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidPublicoObjetivo");
        this.publicoObjetivoList = publicoObjetivoCallService.loadPublicoObjetivoList(findByParamBean);
        logger.info(":: PublicoObjetivoAdministrado.loadPublicoObjetivoList :: Execution finish.");
    }

}
