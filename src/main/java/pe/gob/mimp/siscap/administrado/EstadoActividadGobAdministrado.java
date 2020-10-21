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
import pe.gob.mimp.siscap.modelo.EstadoActividadGob;
import pe.gob.mimp.siscap.ws.estadoactividadgob.cliente.EstadoActividadGobCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class EstadoActividadGobAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(EstadoActividadGob.class.getName());
    private static final long serialVersionUID = 1L;

    private EstadoActividadGob entidadSeleccionada;
    private List<EstadoActividadGob> estadoList;

    private List<EstadoActividadGob> estadoEjeList;

    @Inject
    private EstadoActividadGobCallService estadoActividadGobCallService;

    public EstadoActividadGobAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new EstadoActividadGob();
        loadEstadoList();
    }

    public EstadoActividadGob getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(EstadoActividadGob entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public EstadoActividadGob getEntidad(String id) {
        EstadoActividadGob estadoActividadGob = null;

        if ((null != id) || (false == id.equals(""))) {
            estadoActividadGob = estadoActividadGobCallService.find(new BigDecimal(id));
        }
        return estadoActividadGob;
    }

    public List<EstadoActividadGob> getEstadoList() {
        return estadoList;
    }

    public void setEstadoList(List<EstadoActividadGob> estadoList) {
        this.estadoList = estadoList;
    }

    public List<EstadoActividadGob> getEstadoEjeList() {
        return estadoEjeList;
    }

    public void setEstadoEjeList(List<EstadoActividadGob> estadoEjeList) {
        this.estadoEjeList = estadoEjeList;
    }

    public List<EstadoActividadGob> obtenerTodos() {

        List<EstadoActividadGob> estados = null;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidEstadoActividadGob");
        estados = estadoActividadGobCallService.loadEstadoActividadGobList(findByParamBean);
        return estados;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtEstadoActividadGob().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtEstadoActividadGob", this.getEntidadSeleccionada().getTxtEstadoActividadGob().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = estadoActividadGobCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "Estados encontrados: {0}", count);

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

    public void crearEstadoActividadGob() {
        logger.info(":: EstadoActividadGobAdministrado.crearEstadoActividadGob() :: Starting execution...");
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                EstadoActividadGob estadoActividadGob = new EstadoActividadGob();
                estadoActividadGob.setTxtEstadoActividadGob(this.entidadSeleccionada.getTxtEstadoActividadGob().toUpperCase());
                estadoActividadGob.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                estadoActividadGob.setTxtPc(Internet.obtenerNombrePC());
                estadoActividadGob.setTxtIp(Internet.obtenerIPPC());
                estadoActividadGob.setFecEdicion(new Date());
                estadoActividadGob.setFlgActivo(BigInteger.ONE);
                estadoActividadGobCallService.crearEstadoActividadGob(estadoActividadGob);

//                if (null != estadoActividadGob.getNidEstadoActividadGob()) {
                    // Update List Browser
                    loadEstadoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoEstadoActividadGob').hide()");
//                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearEstadoActividadGob" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: EstadoActividadGobAdministrado.crearEstadoActividadGob() :: Execution finish.");
    }

    public void editarEstadoActividadGob(EstadoActividadGob entidadSeleccionada) {
        logger.info(":: EstadoActividadGobAdministrado.editarEstadoActividadGob() :: Starting execution...");
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    estadoActividadGobCallService.editarEstadoActividadGob(entidadSeleccionada);
                    // Update List Browser
                    loadEstadoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarEstadoActividadGob').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarEstadoActividadGob" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: EstadoActividadGobAdministrado.editarEstadoActividadGob() :: Execution finish.");
    }

    public void anularEstadoActividadGob(EstadoActividadGob entidadSeleccionada) {
        logger.info(":: EstadoActividadGobAdministrado.anularEstadoActividadGob() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                estadoActividadGobCallService.editarEstadoActividadGob(entidadSeleccionada);
                // Update List Browser
                loadEstadoList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularEstadoActividadGob" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: EstadoActividadGobAdministrado.anularEstadoActividadGob() :: Execution finish.");
    }

    public String obtenerEstadoActividadGobporId(BigInteger nidEstadoActividadGob) {
        String resultado = "";

        try {
            if (null != nidEstadoActividadGob) {
                EstadoActividadGob estadoActividadGob = estadoActividadGobCallService.find(new BigDecimal(nidEstadoActividadGob));

                if (null != estadoActividadGob) {
                    resultado = estadoActividadGob.getTxtEstadoActividadGob();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener EstadoActividadGob" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarEstadoActividadGob() {

        entidadSeleccionada = new EstadoActividadGob();
    }

    /*public List<EstadoActividadGob> obtenerEstados() {

     Map<String, Object> parameters = new HashMap<>();
     parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
     this.estadoEjeList = estadoActividadGobCallService.findByParams(parameters, "txtEstadoActividadGob");

     List<EstadoActividadGob> estadosBD = estadoActividadGobCallService.obtenerEstadoActividadGobActivos();
     List<EstadoActividadGob> estadosEncontrados = new ArrayList<EstadoActividadGob>();

     for (EstadoActividadGob estadosActual : estadosBD) {
     if (estadosActual.getTxtEstadoActividadGob().equals("REPROGRAMADO")) {
     estadosEncontrados.add(estadosActual);
     }
     if (estadosActual.getTxtEstadoActividadGob().equals("EJECUTADO")) {
     estadosEncontrados.add(estadosActual);
     }
     }
     return null;
     }*/
    private void loadEstadoList() {
        logger.info(":: EstadoActividadGobAdministrado.loadEstadoList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidEstadoActividadGob");
        this.estadoList = estadoActividadGobCallService.loadEstadoActividadGobList(findByParamBean);
        logger.info(":: EstadoActividadGobAdministrado.loadEstadoList :: Execution finish.");
    }
}
