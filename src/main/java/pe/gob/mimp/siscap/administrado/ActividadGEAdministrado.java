package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.ActividadGobEActGob;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.actividadge.cliente.ActividadGECallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@ManagedBean
@ViewScoped
public class ActividadGEAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ActividadGobEActGob.class.getName());
    private static final long serialVersionUID = 1L;

    private ActividadGobEActGob entidadSeleccionada;
    private List<ActividadGobEActGob> ActividadEstadoList;

    @Inject
    private ActividadGECallService actividadGECallService;

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    public ActividadGEAdministrado() {

        this.entidadSeleccionada = new ActividadGobEActGob();
    }

    public ActividadGobEActGob getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ActividadGobEActGob entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ActividadGobEActGob> getActividadEstadoList() {
        return ActividadEstadoList;
    }

    public void setActividadEstadoList(List<ActividadGobEActGob> ActividadEstadoList) {
        this.ActividadEstadoList = ActividadEstadoList;
    }

    public String obtenerEstado(ActividadGob actividadGobierno) {
        long startTime = System.currentTimeMillis();
        String estado = "";

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidActividadGob", actividadGobierno);
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidActividadGob");
            List<ActividadGobEActGob> estados = actividadGECallService.loadActividadGEList(findByParamBean);

            if (null != estados && 0 < estados.size()) {
                estado = estados.get(0).getNidEstadoActividadGob().getTxtEstadoActividadGob();
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
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, "Error Obtener estado: "+ e.getMessage(), e);
        }

        return estado;
    }

    public String obtenerColorCeldaEstado(String txtEstado, String color1, String color2, String color3, String color4, String color5) {
        String valor = null;

        try {
            if (null != txtEstado) {
                if (txtEstado.equals("PROGRAMADO")) {
                    valor = color1;
                }
                if (txtEstado.equals("EJECUTADO")) {
                    valor = color2;
                }
                if (txtEstado.equals("REPROGRAMADO")) {
                    valor = color3;
                }
                if (txtEstado.equals("ANULADO")) {
                    valor = color4;
                }
                if (txtEstado.equals("CERRADO")) {
                    valor = color5;
                }
            }
        } catch (Exception e) {
            System.out.println("celdaColor: " + e.getMessage());
        }
        return valor;
    }

}
