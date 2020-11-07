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
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipoobjetivo.cliente.TipoObjetivoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class TipoObjetivoAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

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
        try {
            long startTime = System.currentTimeMillis();

            this.entidadSeleccionada = new TipoObjetivo();
            loadTipoObjetivoList();

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {

        }
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

        long startTime = System.currentTimeMillis();
        TipoObjetivo tipoObjetivo = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                tipoObjetivo = tipoObjetivoCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error getEntidad" + e.getMessage(), Util.tiempo());
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
            long startTime = System.currentTimeMillis();
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
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
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

    public void crearTipoObjetivo() {
        logger.info(":: TipoObjetivoAdministrado.crearTipoObjetivo() :: Starting execution...");
        long startTime = System.currentTimeMillis();
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

                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearTipoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoObjetivoAdministrado.crearTipoObjetivo() :: Execution finish.");
    }

    public void editarTipoObjetivo(TipoObjetivo entidadSeleccionada) {
        logger.info(":: TipoObjetivoAdministrado.editarTipoObjetivo() :: Starting execution...");
        long startTime = System.currentTimeMillis();
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
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarTipoObjetivo" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoObjetivoAdministrado.editarTipoObjetivo() :: Execution finish.");
    }

    public void anularTipoObjetivo(TipoObjetivo entidadSeleccionada) {
        logger.info(":: TipoObjetivoAdministrado.anularTipoObjetivo() :: Starting execution...");
        long startTime = System.currentTimeMillis();
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
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularTipoObjetivo" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: TipoObjetivoAdministrado.anularTipoObjetivo() :: Execution finish.");
    }

    public String obtenerTipoObjetivoporId(BigInteger nidTipoObjetivo) {
        long startTime = System.currentTimeMillis();
        String resultado = "";

        try {
            if (null != nidTipoObjetivo) {
                TipoObjetivo tipoObjetivo = tipoObjetivoCallService.find(new BigDecimal(nidTipoObjetivo));

                if (null != tipoObjetivo) {
                    resultado = tipoObjetivo.getTxtTipoObjetivo();
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Medio" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarTipoObjetivo() {

        entidadSeleccionada = new TipoObjetivo();
    }

    private void loadTipoObjetivoList() throws Exception {
        logger.info(":: TipoObjetivoAdministrado.loadTipoObjetivoList :: Starting execution...");

        long startTime = System.currentTimeMillis();

        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoObjetivo");
        this.tipoObjetivoList = tipoObjetivoCallService.loadTipoObjetivoList(findByParamBean);

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_OBJETIVO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: TipoObjetivoAdministrado.loadTipoObjetivoList :: Execution finish.");
    }

}
