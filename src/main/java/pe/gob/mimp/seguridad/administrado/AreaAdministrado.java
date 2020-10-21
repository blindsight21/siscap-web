package pe.gob.mimp.seguridad.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.modelo.Area;
import pe.gob.mimp.siscap.ws.area.cliente.AreaCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class AreaAdministrado extends AdministradorAbstracto implements Serializable {

    private Area entidad;

    private Area entidadSeleccionada;

    private List<Area> entidades;

    @Inject
    private AreaCallService areaCallService;

    public AreaAdministrado() {
        setEntidad(new Area());
        setEntidadSeleccionada(new Area());
    }

    public Area getEntidad() {
        return entidad;
    }

    public void setEntidad(Area entidad) {
        this.entidad = entidad;
    }

    public Area getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Area entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<Area> getEntidades() {
        entidades = areaCallService.findAll();

        return entidades;
    }

    public void setEntidades(List<Area> entidades) {
        this.entidades = entidades;
    }

    public Area getEntidad(String id) {
        Area tipoDocumento = null;

        if (!id.equals("")) {
            tipoDocumento = areaCallService.find(new BigDecimal(id));
        }

        return tipoDocumento;
    }

    public String obtenerEntidad(BigInteger id) {
        String area = null;
        if (null != id) {
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("nidArea");
            findAllByFieldBean.setValue(id);
            List<Area> areaEncontrados = areaCallService.findAllByField(findAllByFieldBean);
            if (areaEncontrados != null && !areaEncontrados.isEmpty()) {
                area = areaEncontrados.get(0).getTxtArea();
            } else {
                area = "--NO SE ENCONTRO EN LA BASE DE DATOS EL AREA: " + id;
            }
        }
        return area;
    }

    public Area obtenerAreaporNombre(String numCelular) {

        Area tipoDocumento = null;

        if (!numCelular.equals("")) {

        }

        return tipoDocumento;
    }

    public String obtenerEntidadSigla(BigInteger id) {
        String areaSigla = null;
        if (null != id) {
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("nidArea");
            findAllByFieldBean.setValue(id);
            List<Area> areaEncontrados = areaCallService.findAllByField(findAllByFieldBean);
            areaSigla = areaEncontrados.get(0).getTxtSigla();
        }
        return areaSigla;
    }

}
