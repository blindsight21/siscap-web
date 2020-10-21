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
import pe.gob.mimp.siscap.modelo.TipoObjetivo;
import pe.gob.mimp.siscap.ws.tipoobjetivo.cliente.TipoObjetivoCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class TipoObjetivoAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoObjetivo.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoObjetivo entidadSeleccionada;
    private List<TipoObjetivo> tipoObjetivoList;

    @Inject
    private TipoObjetivoCallService tipoObjetivoCallService;

    public TipoObjetivoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new TipoObjetivo();
        loadTipoObjetivoList();
    }

    public TipoObjetivo getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoObjetivo entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoObjetivo> getTipoObjetivoList() {
        return tipoObjetivoList;
    }

    public void setTipoObjetivoList(List<TipoObjetivo> tipoObjetivoList) {
        this.tipoObjetivoList = tipoObjetivoList;
    }

    public TipoObjetivo getEntidad(String id) {
        TipoObjetivo tipoObjetivo = null;

        if ((null != id) || (false == id.equals(""))) {
            tipoObjetivo = tipoObjetivoCallService.find(new BigDecimal(id));
        }
        return tipoObjetivo;
    }

    public List<TipoObjetivo> obtenerTodos() {

        List<TipoObjetivo> tipoObjetivos = null;
        //tipoObjetivos = tipoObjetivoCallService.obtenerTipoObjetivoTodos();

        return tipoObjetivos;
    }

    public List<TipoObjetivo> obtenerActivos() {

        List<TipoObjetivo> tipoObjetivosActivo = null;
        //tipoObjetivosActivo = tipoObjetivoCallService.obtenerTipoObjetivoActivos();

        return tipoObjetivosActivo;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtTipoObjetivo().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtTipoObjetivo", this.getEntidadSeleccionada().getTxtTipoObjetivo().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = tipoObjetivoCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "TipoObjetivo encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("TipoObjetivo: Ya Existe Registro");
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

    public void crearTipoObjetivo() {
        logger.info(":: TipoObjetivoAdministrado.crearTipoObjetivo() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                TipoObjetivo tipoObjetivo = new TipoObjetivo();
                tipoObjetivo.setTxtTipoObjetivo(this.entidadSeleccionada.getTxtTipoObjetivo());
                tipoObjetivo.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                tipoObjetivo.setTxtPc(Internet.obtenerNombrePC());
                tipoObjetivo.setTxtIp(Internet.obtenerIPPC());
                tipoObjetivo.setFecEdicion(new Date());
                tipoObjetivo.setFlgActivo(BigInteger.ONE);
                tipoObjetivoCallService.crearTipoObjetivo(tipoObjetivo);

//                if (null != tipoObjetivo.getNidTipoObjetivo()) {
                    // Update List Browser
                    loadTipoObjetivoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoTipoObjetivo').hide()");
//                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearTipoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoObjetivoAdministrado.crearTipoObjetivo() :: Execution finish.");
    }

    public void editarTipoObjetivo(TipoObjetivo entidadSeleccionada) {
        logger.info(":: TipoObjetivoAdministrado.editarTipoObjetivo() :: Starting execution...");
        if (validarFormulario(false)) {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            try {
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    tipoObjetivoCallService.editarTipoObjetivo(entidadSeleccionada);
                    // Update List Browser
                    loadTipoObjetivoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarTipoObjetivo').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarTipoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoObjetivoAdministrado.editarTipoObjetivo() :: Execution finish.");
    }

    public void anularTipoObjetivo(TipoObjetivo entidadSeleccionada) {
        logger.info(":: TipoObjetivoAdministrado.anularTipoObjetivo() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                tipoObjetivoCallService.editarTipoObjetivo(entidadSeleccionada);
                // Update List Browser
                loadTipoObjetivoList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularTipoObjetivo" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: TipoObjetivoAdministrado.anularTipoObjetivo() :: Execution finish.");
    }

    public String obtenerTipoObjetivoporId(BigInteger nidTipoObjetivo) {
        String resultado = "";

        try {
            if (null != nidTipoObjetivo) {
                TipoObjetivo tipoObjetivo = tipoObjetivoCallService.find(new BigDecimal(nidTipoObjetivo));

                if (null != tipoObjetivo) {
                    resultado = tipoObjetivo.getTxtTipoObjetivo();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Medio" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarTipoObjetivo() {

        entidadSeleccionada = new TipoObjetivo();
    }

    private void loadTipoObjetivoList() {
        logger.info(":: TipoObjetivoAdministrado.loadTipoObjetivoList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoObjetivo");
        this.tipoObjetivoList = tipoObjetivoCallService.loadTipoObjetivoList(findByParamBean);
        logger.info(":: TipoObjetivoAdministrado.loadTipoObjetivoList :: Execution finish.");
    }

}
