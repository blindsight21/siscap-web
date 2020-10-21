package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.siscap.modelo.NivelEvaluacion;
import pe.gob.mimp.siscap.ws.nivelevaluacion.cliente.NivelEvaluacionCallService;

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

    public NivelEvaluacionAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new NivelEvaluacion();
        loadNivelEvaluacionList();
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
        NivelEvaluacion nivelEvaluacion = null;

        if ((null != id) || (false == id.equals(""))) {
            nivelEvaluacion = nivelEvaluacionCallService.find(new BigDecimal(id));
        }
        return nivelEvaluacion;
    }

    private void loadNivelEvaluacionList() {
        logger.info(":: NivelEvaluacionAdministrado.loadNivelEvaluacionList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidNivelEvaluacion");
        this.nivelEvaluacionList = nivelEvaluacionCallService.loadNivelEvaluacionList(findByParamBean);
        logger.info(":: NivelEvaluacionAdministrado.loadNivelEvaluacionList :: Execution finish.");
    }
}
