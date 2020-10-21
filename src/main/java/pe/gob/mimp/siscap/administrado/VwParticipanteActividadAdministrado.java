//package pe.gob.mimp.siscap.administrado;
//
//import java.io.Serializable;
//import java.util.List;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//import pe.gob.mimp.siscap.modelo.VwParticipanteActividad;
//
//@SuppressWarnings("serial")
//@ManagedBean
//@ViewScoped
//public class VwParticipanteActividadAdministrado extends AdministradorAbstracto implements Serializable {
//
//    private VwParticipanteActividad entidad;
//    private VwParticipanteActividad entidadSeleccionada;
//    private List<VwParticipanteActividad> entidades;
//
////    @Inject
////    private VwParticipanteActividadFacadeLocal fachada;
//
//    public VwParticipanteActividadAdministrado() {
//
//        this.entidad = new VwParticipanteActividad();
//        this.entidadSeleccionada = new VwParticipanteActividad();
//    }
//
//    public VwParticipanteActividad getEntidad() {
//        return entidad;
//    }
//
//    public void setEntidad(VwParticipanteActividad entidad) {
//        this.entidad = entidad;
//    }
//
//    public VwParticipanteActividad getEntidadSeleccionada() {
//        return entidadSeleccionada;
//    }
//
//    public void setEntidadSeleccionada(VwParticipanteActividad entidadSeleccionada) {
//        this.entidadSeleccionada = entidadSeleccionada;
//    }
//
//    public List<VwParticipanteActividad> getEntidades() {
//        this.entidades = fachada.findAll();
//
//        return entidades;
//    }
//
//    public void setEntidades(List<VwParticipanteActividad> entidades) {
//        this.entidades = entidades;
//    }
//
//}
