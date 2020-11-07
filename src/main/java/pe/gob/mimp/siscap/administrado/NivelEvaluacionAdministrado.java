package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.Serializable;
import java.math.BigDecimal;
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
import pe.gob.mimp.siscap.modelo.NivelEvaluacion;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.nivelevaluacion.cliente.NivelEvaluacionCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class NivelEvaluacionAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(NivelEvaluacion.class.getName());
    private static final long serialVersionUID = 1L;

    private NivelEvaluacion entidadSeleccionada;
    private List<NivelEvaluacion> nivelEvaluacionList;

    @Inject
    private NivelEvaluacionCallService nivelEvaluacionCallService;

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    public NivelEvaluacionAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new NivelEvaluacion();
        try {
            loadNivelEvaluacionList();
        } catch (Exception e) {
        }
    }

    public NivelEvaluacion getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(NivelEvaluacion entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<NivelEvaluacion> getNivelEvaluacionList() {
        return nivelEvaluacionList;
    }

    public void setNivelEvaluacionList(List<NivelEvaluacion> nivelEvaluacionList) {
        this.nivelEvaluacionList = nivelEvaluacionList;
    }

    public NivelEvaluacion getEntidad(String id) {
        long startTime = System.currentTimeMillis();
        NivelEvaluacion nivelEvaluacion = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                nivelEvaluacion = nivelEvaluacionCallService.find(new BigDecimal(id));
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
        return nivelEvaluacion;
    }

    private void loadNivelEvaluacionList() throws Exception {
        logger.info(":: NivelEvaluacionAdministrado.loadNivelEvaluacionList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        Map<String, Object> parameters = new HashMap<>();

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidNivelEvaluacion");
        this.nivelEvaluacionList = nivelEvaluacionCallService.loadNivelEvaluacionList(findByParamBean);

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: NivelEvaluacionAdministrado.loadNivelEvaluacionList :: Execution finish.");
    }
}
