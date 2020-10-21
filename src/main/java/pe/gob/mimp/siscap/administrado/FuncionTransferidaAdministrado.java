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
import pe.gob.mimp.siscap.modelo.FuncionTransferida;
import pe.gob.mimp.siscap.modelo.TipoFuncion;
import pe.gob.mimp.siscap.ws.funciontransferida.cliente.FuncionTransferidaCallService;
import pe.gob.mimp.siscap.ws.tipofuncion.cliente.TipoFuncionCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class FuncionTransferidaAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(FuncionTransferida.class.getName());
    private static final long serialVersionUID = 1L;

    private FuncionTransferida entidadSeleccionada;
    private List<FuncionTransferida> funcionTransferidaList;
    private List<TipoFuncion> tipoFuncionList;

    @Inject
    FuncionTransferidaCallService funcionTransferidaCallService;
    @Inject
    TipoFuncionCallService tipoFuncionCallService;

    public FuncionTransferidaAdministrado() {
    }

    @PostConstruct
    public void initBean() {
        logger.info(":: FuncionTransferidaAdministrado.initBean :: Starting execution...");
        try {
            this.entidadSeleccionada = new FuncionTransferida();
            this.entidadSeleccionada.setNidTipoFuncion(new TipoFuncion());
            loadFuncionList();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("txtTipoFuncion");

            this.tipoFuncionList = tipoFuncionCallService.loadTipoFuncionList(findByParamBean);

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error method initBean" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: BeneficenciaMB.initBean :: Execution finish.");
    }

    public FuncionTransferida getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(FuncionTransferida entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<FuncionTransferida> getFuncionTransferidaList() {
        return funcionTransferidaList;
    }

    public void setFuncionTransferidaList(List<FuncionTransferida> funcionTransferidaList) {
        this.funcionTransferidaList = funcionTransferidaList;
    }

    public List<TipoFuncion> getTipoFuncionList() {
        return tipoFuncionList;
    }

    public void setTipoFuncionList(List<TipoFuncion> tipoFuncionList) {
        this.tipoFuncionList = tipoFuncionList;
    }

    public FuncionTransferida getEntidad(String id) {
        FuncionTransferida funcionTransferida = null;

        if ((null != id) || (false == id.equals(""))) {
            funcionTransferida = funcionTransferidaCallService.find(new BigDecimal(id));
        }
        return funcionTransferida;
    }

    public List<FuncionTransferida> obtenerTodos() {

        List<FuncionTransferida> funcionTransferidas = null;
        //funcionTransferidas = fachada.obtenerFuncionTransferidaTodos();

        return funcionTransferidas;
    }

    public List<FuncionTransferida> obtenerActivo() {

        List<FuncionTransferida> funcionTransferidaActivo = null;

        //funcionTransferidaActivo = fachada.obtenerFuncionTransferidaActivos();
        return funcionTransferidaActivo;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtFuncionTransferida().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtFuncionTransferida", this.getEntidadSeleccionada().getTxtFuncionTransferida().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = funcionTransferidaCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "FuncionTransferida encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("FuncionTransferida: Ya Existe Registro");
                    }
                }
            }
            if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtFuncionTransferida().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            }
            if (Funciones.esVacio(this.entidadSeleccionada.getNidTipoFuncion()) || Funciones.esVacio(this.entidadSeleccionada.getNidTipoFuncion().getNidTipoFuncion())) {
                return enviarWarnMessage("Seleccione el Tipo Funcion");
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

    public void crearFuncionTransferida() {
        logger.info(":: FuncionTransferidaAdministrado.crearFuncionTransferida() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                FuncionTransferida nuevoFuncionTransferida = new FuncionTransferida();
                nuevoFuncionTransferida.setTxtFuncionTransferida(this.entidadSeleccionada.getTxtFuncionTransferida());
                nuevoFuncionTransferida.setTxtFuncionTranferidaDes(this.entidadSeleccionada.getTxtFuncionTranferidaDes());
                nuevoFuncionTransferida.setNidTipoFuncion(this.entidadSeleccionada.getNidTipoFuncion());
                nuevoFuncionTransferida.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                nuevoFuncionTransferida.setTxtPc(Internet.obtenerNombrePC());
                nuevoFuncionTransferida.setTxtIp(Internet.obtenerIPPC());
                nuevoFuncionTransferida.setFecEdicion(new Date());
                nuevoFuncionTransferida.setFlgActivo(BigInteger.ONE);
                funcionTransferidaCallService.crearFuncionTransferida(nuevoFuncionTransferida);

//                if (null != nuevoFuncionTransferida.getNidFuncionTransferida()) {
                // Update List Browser
                loadFuncionList();
                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoFuncion').hide()");
//                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearFuncionTransferida" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: FuncionTransferidaAdministrado.crearTicrearFuncionTransferidapoObjetivo() :: Execution finish.");
    }

    public void editarFuncionTransferida(FuncionTransferida entidadSeleccionada) {
        logger.info(":: FuncionTransferidaAdministrado.editarFuncionTransferida() :: Starting execution...");
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    funcionTransferidaCallService.editarFuncionTransferida(entidadSeleccionada);
                    // Update List Browser
                    loadFuncionList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarFuncion').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarFuncionTransferida" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: FuncionTransferidaAdministrado.editarFuncionTransferida() :: Execution finish.");
    }

    public void anularFuncionTransferida(FuncionTransferida entidadSeleccionada) {
        logger.info(":: FuncionTransferidaAdministrado.anularFuncionTransferida() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                funcionTransferidaCallService.editarFuncionTransferida(entidadSeleccionada);
                // Update List Browser
                loadFuncionList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularFuncionTransferida" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: FuncionTransferidaAdministrado.anularFuncionTransferida() :: Execution finish.");
    }

    public String obtenerFuncionTransferidaporId(BigInteger nidFuncionTransferida) {
        String resultado = "";

        try {
            if (null != nidFuncionTransferida) {
                FuncionTransferida funcionTransferida = funcionTransferidaCallService.find(new BigDecimal(nidFuncionTransferida));

                if (null != funcionTransferida) {
                    resultado = funcionTransferida.getTxtFuncionTransferida();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Funcion Transferida" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public List<FuncionTransferida> obtenerTodosporTipoFuncion(TipoFuncion tipoFuncion) {

        List<FuncionTransferida> funcionTransferida = null;

        if (null != tipoFuncion) {

            //funcionTransferida = fachada.obtenerFuncionTransferidaporTipo(tipoFuncion);
        }

        return funcionTransferida;
    }

    public void limpiarFuncionTransferida() {
        logger.info(":: FuncionTransferidaAdministrado.limpiarFuncionTransferida :: Starting execution...");
        this.entidadSeleccionada = new FuncionTransferida();
        this.entidadSeleccionada.setNidTipoFuncion(new TipoFuncion());
        logger.info(":: FuncionTransferidaAdministrado.limpiarFuncionTransferida :: Starting execution...");
    }

    private void loadFuncionList() {
        logger.info(":: FuncionTransferidaAdministrado.loadFuncionList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidFuncionTransferida");

        this.funcionTransferidaList = funcionTransferidaCallService.loadFuncionTransferidaList(findByParamBean);
        logger.info(":: FuncionTransferidaAdministrado.loadFuncionList :: Execution finish.");
    }
}
