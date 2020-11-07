package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.siscap.modelo.TipoEvaluacion;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipoevaluacion.cliente.TipoEvaluacionCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@ManagedBean
@ViewScoped
public class TipoEvaluacionAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoEvaluacion.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoEvaluacion entidadSeleccionada;
    private List<TipoEvaluacion> tipoEvaluacionList;

    @Inject
    private TipoEvaluacionCallService tipoEvaluacionCallService;

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    public TipoEvaluacionAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new TipoEvaluacion();
        loadTipoEvaluacionList();
    }

    public TipoEvaluacion getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoEvaluacion entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoEvaluacion> getTipoEvaluacionList() {
        return tipoEvaluacionList;
    }

    public void setTipoEvaluacionList(List<TipoEvaluacion> tipoEvaluacionList) {
        this.tipoEvaluacionList = tipoEvaluacionList;
    }

    public TipoEvaluacion getEntidad(String id) {
        long startTime = System.currentTimeMillis();
        TipoEvaluacion tipoEvaluacion = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                tipoEvaluacion = tipoEvaluacionCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error getEntidad" + e.getMessage(), Util.tiempo());
        }
        return tipoEvaluacion;
    }

    public String obtenerTipoEvaluacionporId(BigInteger nidTipoEvaluacion) throws Exception {
        String resultado = "";
        long startTime = System.currentTimeMillis();
        try {
            if (null != nidTipoEvaluacion) {
                TipoEvaluacion tipoEvaluacion = tipoEvaluacionCallService.find(new BigDecimal(nidTipoEvaluacion));

                if (null != tipoEvaluacion) {
                    resultado = tipoEvaluacion.getTxtTipoEvaluacion();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Tipo Evaluacion" + e.getMessage(), Util.tiempo());
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return resultado;
    }

    private void loadTipoEvaluacionList() {
        logger.info(":: TipoEvaluacionAdministrado.loadTipoEvaluacionList :: Starting execution...");
        try {
            long startTime = System.currentTimeMillis();
            Map<String, Object> parameters = new HashMap<>();

            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidTipoEvaluacion");
            this.tipoEvaluacionList = tipoEvaluacionCallService.loadTipoEvaluacionList(findByParamBean);
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            
        }
        logger.info(":: TipoEvaluacionAdministrado.loadTipoEvaluacionList :: Execution finish.");
    }

}
