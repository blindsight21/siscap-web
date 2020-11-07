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
import pe.gob.mimp.siscap.modelo.TipoFuncion;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipofuncion.cliente.TipoFuncionCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class TipoFuncionAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(TipoFuncion.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoFuncion entidadSeleccionada;
    private List<TipoFuncion> tipoFuncionList;

    @Inject
    private TipoFuncionCallService tipoFuncionCallService;

    public TipoFuncionAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        try {
            long startTime = System.currentTimeMillis();
            
            this.entidadSeleccionada = new TipoFuncion();
            loadTipoFuncionList();
            long stopTime = System.currentTimeMillis();
            
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

        }
    }

    public TipoFuncion getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoFuncion entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoFuncion> getTipoFuncionList() {
        return tipoFuncionList;
    }

    public void setTipoFuncionList(List<TipoFuncion> tipoFuncionList) {
        this.tipoFuncionList = tipoFuncionList;
    }

    public TipoFuncion getEntidad(String id) {
        long startTime = System.currentTimeMillis();
        TipoFuncion tipoFuncion = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                tipoFuncion = tipoFuncionCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error getEntidad" + e.getMessage(), Util.tiempo());
        }
        return tipoFuncion;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            long startTime = System.currentTimeMillis();
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtTipoFuncion().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtTipoFuncion", this.getEntidadSeleccionada().getTxtTipoFuncion().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = tipoFuncionCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "TipoFuncion encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("TipoFuncion: Ya Existe Registro");
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
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

    public void crearTipoFuncion() {
        logger.info(":: TipoFuncionAdministrado.crearTipoObjetivo() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                TipoFuncion tipoFuncion = new TipoFuncion();
                tipoFuncion.setTxtTipoFuncion(this.entidadSeleccionada.getTxtTipoFuncion());
                tipoFuncion.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                tipoFuncion.setTxtPc(Internet.obtenerNombrePC());
                tipoFuncion.setTxtIp(Internet.obtenerIPPC());
                tipoFuncion.setFecEdicion(new Date());
                tipoFuncion.setFlgActivo(BigInteger.ONE);
                tipoFuncionCallService.crearTipoFuncion(tipoFuncion);

//                if (null != tipoFuncion.getNidTipoFuncion()) {
                // Update List Browser
                loadTipoFuncionList();
                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoTipoFuncion').hide()");
//                }
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearTipoFuncion" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoFuncionAdministrado.crearTipoObjetivo() :: Execution finish.");
    }

    public void editarTipoFuncion(TipoFuncion entidadSeleccionada) {
        logger.info(":: TipoFuncionAdministrado.editarTipoFuncion() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    tipoFuncionCallService.editarTipoFuncion(entidadSeleccionada);
                    // Update List Browser
                    loadTipoFuncionList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarTipoFuncion').hide()");
                }
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarTipoFuncion" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoFuncionAdministrado.editarTipoFuncion() :: Execution finish.");
    }

    public void anularTipoFuncion(TipoFuncion entidadSeleccionada) {
        logger.info(":: TipoFuncionAdministrado.anularTipoFuncion() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                tipoFuncionCallService.editarTipoFuncion(entidadSeleccionada);
                // Update List Browser
                loadTipoFuncionList();
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularTipoFuncion" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: TipoFuncionAdministrado.anularTipoFuncion() :: Execution finish.");
    }

    public String obtenerTipoModalidadporId(BigInteger nidTipoFuncion) {
        String resultado = "";
        long startTime = System.currentTimeMillis();
        try {
            if (null != nidTipoFuncion) {
                TipoFuncion tipoFuncion = tipoFuncionCallService.find(new BigDecimal(nidTipoFuncion));

                if (null != tipoFuncion) {
                    resultado = tipoFuncion.getTxtTipoFuncion();
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Tipo Funcion" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarTipoFuncion() {

        this.entidadSeleccionada = new TipoFuncion();
    }

    private void loadTipoFuncionList() throws Exception {
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoFuncion");
        this.tipoFuncionList = tipoFuncionCallService.loadTipoFuncionList(findByParamBean);
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_FUNCION.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Execution finish.");
    }

}
