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
import pe.gob.mimp.siscap.modelo.TipoModalidad;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipomodalidad.cliente.TipoModalidadCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class TipoModalidadAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(TipoModalidad.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoModalidad entidadSeleccionada;
    private List<TipoModalidad> tipoModalidadList;

    @Inject
    private TipoModalidadCallService tipoModalidadCallService;

    public TipoModalidadAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        try {
            long startTime = System.currentTimeMillis();

            this.entidadSeleccionada = new TipoModalidad();
            loadTipoModalidadList();

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
        }
    }

    public TipoModalidad getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoModalidad entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoModalidad> getTipoModalidadList() {
        return tipoModalidadList;
    }

    public void setTipoModalidadList(List<TipoModalidad> tipoModalidadList) {
        this.tipoModalidadList = tipoModalidadList;
    }

    public TipoModalidad getEntidad(String id) {

        long startTime = System.currentTimeMillis();
        TipoModalidad tipoModalidad = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                tipoModalidad = tipoModalidadCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error getEntidad" + e.getMessage(), Util.tiempo());
        }
        return tipoModalidad;
    }

    public boolean validarFormulario(boolean isNuevo) {
        try {
            long startTime = System.currentTimeMillis();
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtTipoModalidad().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtTipoModalidad", this.getEntidadSeleccionada().getTxtTipoModalidad().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = tipoModalidadCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "TipoModalidad encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("TipoModalidad: Ya Existe Registro");
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
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

    public void crearTipoModalidad() {
        logger.info(":: TipoModalidadAdministrado.crearTipoModalidad() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                TipoModalidad tipoModalidad = new TipoModalidad();
                tipoModalidad.setTxtTipoModalidad(this.entidadSeleccionada.getTxtTipoModalidad());
                tipoModalidad.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                tipoModalidad.setTxtPc(Internet.obtenerNombrePC());
                tipoModalidad.setTxtIp(Internet.obtenerIPPC());
                tipoModalidad.setFecEdicion(new Date());
                tipoModalidad.setFlgActivo(BigInteger.ONE);
                tipoModalidadCallService.crearTipoModalidad(tipoModalidad);

//                if (null != tipoModalidad.getNidTipoModalidad()) {
                // Update List Browser
                loadTipoModalidadList();
                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoTipoModalidad').hide()");
//                }
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearTipoModalidad" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoModalidadAdministrado.crearTipoModalidad() :: Execution finish.");
    }

    public void editarTipoModalidad(TipoModalidad entidadSeleccionada) {
        logger.info(":: TipoModalidadAdministrado.editarTipoModalidad() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    tipoModalidadCallService.editarTipoModalidad(entidadSeleccionada);
                    // Update List Browser
                    loadTipoModalidadList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarTipoModalidad').hide()");
                }
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarTipoModalidad" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: TipoModalidadAdministrado.editarTipoModalidad() :: Execution finish.");
    }

    public void anularTipoModalidad(TipoModalidad entidadSeleccionada) {
        logger.info(":: TipoModalidadAdministrado.anularTipoModalidad() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                tipoModalidadCallService.editarTipoModalidad(entidadSeleccionada);
                // Update List Browser
                loadTipoModalidadList();
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularTipoModalidad" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: TipoModalidadAdministrado.anularTipoModalidad() :: Execution finish.");
    }

    public String obtenerTipoModalidadporId(BigInteger nidTipoModalidad) {
        String resultado = "";
        long startTime = System.currentTimeMillis();
        try {
            if (null != nidTipoModalidad) {
                TipoModalidad tipoModalidad = tipoModalidadCallService.find(new BigDecimal(nidTipoModalidad));

                if (null != tipoModalidad) {
                    resultado = tipoModalidad.getTxtTipoModalidad();
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Medio" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarTipoModalidad() {

        entidadSeleccionada = new TipoModalidad();
    }

    private void loadTipoModalidadList() throws Exception {
        logger.info(":: TipoModalidadAdministrado.loadTipoModalidadList :: Starting execution...");

        long startTime = System.currentTimeMillis();
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoModalidad");
        this.tipoModalidadList = tipoModalidadCallService.loadTipoModalidadList(findByParamBean);
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_MODALIDAD.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: TipoModalidadAdministrado.loadTipoModalidadList :: Execution finish.");
    }

}
