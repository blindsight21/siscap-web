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
import pe.gob.mimp.siscap.modelo.ModalidadActividad;
import pe.gob.mimp.siscap.ws.modalidadactividad.cliente.ModalidadActividadCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ModalidadActividadAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ModalidadActividad.class.getName());
    private static final long serialVersionUID = 1L;

    private ModalidadActividad entidadSeleccionada;
    private List<ModalidadActividad> modalidadActividadList;

    @Inject
    private ModalidadActividadCallService modalidadActividadCallService;

    public ModalidadActividadAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new ModalidadActividad();
        loadModalidadList();
    }

    public ModalidadActividad getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ModalidadActividad entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ModalidadActividad> getModalidadActividadList() {
        return modalidadActividadList;
    }

    public void setModalidadActividadList(List<ModalidadActividad> modalidadActividadList) {
        this.modalidadActividadList = modalidadActividadList;
    }

    public ModalidadActividad getEntidad(String id) {
        ModalidadActividad modalidadActividad = null;

        if ((null != id) || (false == id.equals(""))) {
            modalidadActividad = modalidadActividadCallService.find(new BigDecimal(id));
        }
        return modalidadActividad;
    }

    public List<ModalidadActividad> obtenerTodos() {

        List<ModalidadActividad> modalidadActividades = null;

        //modalidadActividades = modalidadActividadCallService.obtenerModalidadActividadTodos();
        return modalidadActividades;
    }

    public List<ModalidadActividad> obtenerActivo() {

        List<ModalidadActividad> modalidadActividadActivo = null;

        //modalidadActividadActivo = modalidadActividadCallService.obtenerModalidadActividadActivos();
        return modalidadActividadActivo;
    }

    private boolean validarFormulario(boolean isNuevo) {
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtModalidadActividad().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtModalidadActividad", this.getEntidadSeleccionada().getTxtModalidadActividad().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = modalidadActividadCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "ModalidadActividad encontrados: {0}", count);

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

    public void crearModalidadActividad() {
        logger.info(":: ModalidadActividadAdministrado.crearModalidadActividad() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                ModalidadActividad modalidadActividad = new ModalidadActividad();
                modalidadActividad.setTxtModalidadActividad(this.entidadSeleccionada.getTxtModalidadActividad());
                modalidadActividad.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                modalidadActividad.setTxtPc(Internet.obtenerNombrePC());
                modalidadActividad.setTxtIp(Internet.obtenerIPPC());
                modalidadActividad.setFecEdicion(new Date());
                modalidadActividad.setFlgActivo(BigInteger.ONE);
                modalidadActividadCallService.crearModalidadActividad(modalidadActividad);

                if (null != modalidadActividad.getNidModalidadActividad()) {
                    // Update List Browser
                    loadModalidadList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoModalidadActividad').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearModalidadActividad" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ModalidadActividadAdministrado.crearModalidadActividad() :: Execution finish.");
    }

    public void editarModalidadActividad(ModalidadActividad entidadSeleccionada) {
        logger.info(":: ModalidadActividadAdministrado.editarModalidadActividad() :: Starting execution...");
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    modalidadActividadCallService.editarModalidadActividad(entidadSeleccionada);
                    // Update List Browser
                    loadModalidadList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarModalidadActividad').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarModalidadActividad" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ModalidadActividadAdministrado.editarModalidadActividad() :: Execution finish.");
    }

    public void anularModalidadActividad(ModalidadActividad entidadSeleccionada) {
        logger.info(":: ModalidadActividadAdministrado.editarModalidadActividad() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                modalidadActividadCallService.editarModalidadActividad(entidadSeleccionada);
                // Update List Browser
                loadModalidadList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarModalidadActividad" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ModalidadActividadAdministrado.anularModalidadActividad() :: Execution finish.");
    }

    public String obtenerModalidadActividadporId(BigInteger nidModalidadActividad) {
        String resultado = "";

        try {
            if (null != nidModalidadActividad) {
                ModalidadActividad modalidadActividad = modalidadActividadCallService.find(new BigDecimal(nidModalidadActividad));

                if (null != modalidadActividad) {
                    resultado = modalidadActividad.getTxtModalidadActividad();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener ModalidadActividad" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarModalidadActividad() {

        entidadSeleccionada = new ModalidadActividad();
    }

    private void loadModalidadList() {
        logger.info(":: ModalidadActividadAdministrado.loadModalidadList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidModalidadActividad");
        this.modalidadActividadList = modalidadActividadCallService.loadModalidadActividadList(findByParamBean);
        logger.info(":: ModalidadActividadAdministrado.loadModalidadList :: Execution finish.");
    }
}
