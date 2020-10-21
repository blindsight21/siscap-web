package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.ActividadGobResultado;
import pe.gob.mimp.siscap.modelo.NivelEvaluacion;
import pe.gob.mimp.siscap.modelo.TipoEvaluacion;
import pe.gob.mimp.siscap.ws.actividadgr.cliente.ActividadGRCallService;
import pe.gob.mimp.siscap.ws.nivelevaluacion.cliente.NivelEvaluacionCallService;
import pe.gob.mimp.siscap.ws.tipoevaluacion.cliente.TipoEvaluacionCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ActividadGRAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ActividadGobResultado.class.getName());
    private static final long serialVersionUID = 1L;

    private ActividadGobResultado entidadSeleccionada;
    private List<ActividadGobResultado> actividadGRList;

    List<TipoEvaluacion> tipoEvaluacionList;
    List<NivelEvaluacion> nivelEvaluacionList;

    @Inject
    private ActividadGRCallService actividadGRCallService;
    @Inject
    private TipoEvaluacionCallService tipoEvaluacionCallService;
    @Inject
    private NivelEvaluacionCallService nivelEvaluacionCallService;

    public ActividadGRAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        logger.info(":: ActividadGRAdministrado.initBean :: Starting execution...");
        try {
            this.entidadSeleccionada = new ActividadGobResultado();
            this.entidadSeleccionada.setNidNivelEvaluacion(new NivelEvaluacion());
            loadActividadGRAList();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("txtTipoEvaluacion");
            this.tipoEvaluacionList = tipoEvaluacionCallService.loadTipoEvaluacionList(findByParamBean);

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error method initBean" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGRAdministrado.initBean :: Execution finish.");
    }

    public ActividadGobResultado getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ActividadGobResultado entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ActividadGobResultado> getActividadGRList() {
        return actividadGRList;
    }

    public void setActividadGRList(List<ActividadGobResultado> actividadGRList) {
        this.actividadGRList = actividadGRList;
    }

    public List<TipoEvaluacion> getTipoEvaluacionList() {
        return tipoEvaluacionList;
    }

    public void setTipoEvaluacionList(List<TipoEvaluacion> tipoEvaluacionList) {
        this.tipoEvaluacionList = tipoEvaluacionList;
    }

    public List<NivelEvaluacion> getNivelEvaluacionList() {
        return nivelEvaluacionList;
    }

    public void setNivelEvaluacionList(List<NivelEvaluacion> nivelEvaluacionList) {
        this.nivelEvaluacionList = nivelEvaluacionList;
    }

    public void obtenerEvaluacionByTipoEvaluacion(AjaxBehaviorEvent e) throws Exception {
        logger.info(":: ActividadGRAdministrado.obtenerEvaluacionByTipoEvaluacion :: Starting execution...");
        this.entidadSeleccionada.setNidNivelEvaluacion(null);
        this.nivelEvaluacionList = null;
        loadEvaluacionList();
        logger.info(":: ActividadGRAdministrado.obtenerEvaluacionByTipoEvaluacion :: Execution finish.");
    }

    private void loadEvaluacionList() {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionCallService.find(this.entidadSeleccionada.getNidTipoEvaluacion());
        if (!Funciones.esVacio(tipoEvaluacion)) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidTipoEvaluacion", tipoEvaluacion);
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("txtNivelEvaluacion");
            this.nivelEvaluacionList = nivelEvaluacionCallService.loadNivelEvaluacionList(findByParamBean);
            logger.log(Level.INFO, " Funcion transferida {0}", this.nivelEvaluacionList);
        }
    }

    public boolean validarFormulario() {

        try {
            if (Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoEvaluacion())) {
                return enviarWarnMessage("Seleccione el Tipo Evaluacion");
            }
            if (Funciones.esVacio(this.getEntidadSeleccionada().getNidNivelEvaluacion())) {
                return enviarWarnMessage("Seleccione el Nivel Evaluacion");
            }
            if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtActividadGobResultado().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Resultado");
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

    public void addResultadoList() {
        logger.info(":: ActividadGRAdministrado.addResultadoList :: Starting execution...");
        if (validarFormulario()) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

                this.getEntidadSeleccionada().setNidActividadGob(actividadGobAdministrado.getEntidadSeleccionada());
                this.getEntidadSeleccionada().setFlgActivo(BigInteger.ONE);
                this.getEntidadSeleccionada().setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                this.getEntidadSeleccionada().setTxtPc(Internet.obtenerNombrePC());
                this.getEntidadSeleccionada().setTxtIp(Internet.obtenerIPPC());
                this.getEntidadSeleccionada().setFecEdicion(new Date());
                actividadGobAdministrado.actividadGRList = new ArrayList<>();
                actividadGobAdministrado.actividadGRList.add(this.getEntidadSeleccionada());
                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoResultado').hide()");

            } catch (Exception e) {
            }
        }
        logger.info(":: ActividadGRAdministrado.addResultadoList() :: Execution finish");
    }

    //metodo no usado
    public void removeResultadoList(ActividadGobResultado resultadoSeleccionado) {
        ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
        int c = 0;
        for (ActividadGobResultado actividadGobResultado : actividadGobAdministrado.actividadGRList) {
            c++;
            if (actividadGobResultado.getTxtActividadGobResultado().equals(resultadoSeleccionado.getTxtActividadGobResultado())
                    && actividadGobResultado.getNidNivelEvaluacion().getNidNivelEvaluacion().equals(resultadoSeleccionado.getNidNivelEvaluacion().getNidNivelEvaluacion())
                    && actividadGobResultado.getNidTipoEvaluacion().equals(resultadoSeleccionado.getNidTipoEvaluacion())) {
                break;
            }
        }
        actividadGobAdministrado.actividadGRList.remove(c - 1);
    }

    public void loadActividadGRAList() {
        logger.info(":: ActividadGRAdministrado.loadActividadGRAList :: Starting execution...");
        try {
            ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("nidActividadGob", actividadGobAdministrado.getEntidadSeleccionada());
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidActividadGobResultado");
            actividadGobAdministrado.actividadGRList = actividadGRCallService.loadActividadGRList(findByParamBean);
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadActividadGRAList" + e.getMessage(), Util.tiempo());
        }

        logger.info(":: ActividadGRAdministrado.loadActividadGobList :: Execution finish.");
    }

    public void limpiarResultado() {

        this.entidadSeleccionada = new ActividadGobResultado();
        this.entidadSeleccionada.setNidNivelEvaluacion(new NivelEvaluacion());
    }
}
