package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.general.administrado.ProvinciaAdministrado;
import pe.gob.mimp.general.administrado.DistritoAdministrado;
import pe.gob.mimp.general.administrado.DepartamentoAdministrado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.modelo.Departamento;
import pe.gob.mimp.general.modelo.Distrito;
import pe.gob.mimp.general.modelo.Provincia;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.auxiliar.DatosMarcador;
import pe.gob.mimp.siscap.auxiliar.PoligonoDepartamento;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.departamentocoordenadas.cliente.DepartamentoCoordenadasCallService;
import pe.gob.mimp.siscap.ws.distrito.cliente.DistritoCallService;
import pe.gob.mimp.siscap.ws.distritocoordenadas.cliente.DistritoCoordenadasCallService;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;
import pe.gob.mimp.siscap.ws.provinciacoordenadas.cliente.ProvinciaCoordenadasCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@ManagedBean
@ViewScoped
public class MapaAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private MapModel modelo;
    private Marker marcadorSeleccionado;
    private DatosMarcador datosMarcadorSeleccionado;

    private List<PoligonoDepartamento> poligonosDepartamentos;

    private Distrito distritoSeleccionado;
    private Provincia provinciaSeleccionada;
    private Departamento departamentoSeleccionado;

    private boolean poligonosCargados;
    private String coordenadas;
    private String lat;
    private String lng;
    private int accordionCombos;

    @Inject
    private DistritoCallService distritoCallService;
    @Inject
    private ProvinciaCallService provinciaCallService;
    @Inject
    private DepartamentoCallService departamentoCallService;
    @Inject
    private DepartamentoCoordenadasCallService departamentoCoordenadasCallService;
    @Inject
    private ProvinciaCoordenadasCallService provinciaCoordenadasCallService;
    @Inject
    private DistritoCoordenadasCallService distritoCoordenadasCallService;

    public MapaAdministrado() {

        this.poligonosCargados = false;
        this.modelo = new DefaultMapModel();
        this.poligonosDepartamentos = new ArrayList<PoligonoDepartamento>();
        this.coordenadas = "";
        this.lat = "";
        this.lng = "";
        this.accordionCombos = 0;
    }

    public MapModel getModelo() {
        return modelo;
    }

    public void setModelo(MapModel modelo) {
        this.modelo = modelo;
    }

    public Marker getMarcadorSeleccionado() {
        return marcadorSeleccionado;
    }

    public void setMarcadorSeleccionado(Marker marcadorSeleccionado) {
        this.marcadorSeleccionado = marcadorSeleccionado;
    }

    public DatosMarcador getDatosMarcadorSeleccionado() {
        return datosMarcadorSeleccionado;
    }

    public void setDatosMarcadorSeleccionado(DatosMarcador datosMarcadorSeleccionado) {
        this.datosMarcadorSeleccionado = datosMarcadorSeleccionado;
    }

    public List<PoligonoDepartamento> getPoligonosDepartamentos() {
        return poligonosDepartamentos;
    }

    public void setPoligonosDepartamentos(List<PoligonoDepartamento> poligonosDepartamentos) {
        this.poligonosDepartamentos = poligonosDepartamentos;
    }

    public Distrito getDistritoSeleccionado() {
        return distritoSeleccionado;
    }

    public void setDistritoSeleccionado(Distrito distritoSeleccionado) {
        this.distritoSeleccionado = distritoSeleccionado;
    }

    public Provincia getProvinciaSeleccionada() {
        return provinciaSeleccionada;
    }

    public void setProvinciaSeleccionada(Provincia provinciaSeleccionada) {
        this.provinciaSeleccionada = provinciaSeleccionada;
    }

    public Departamento getDepartamentoSeleccionado() {
        return departamentoSeleccionado;
    }

    public void setDepartamentoSeleccionado(Departamento departamentoSeleccionado) {
        this.departamentoSeleccionado = departamentoSeleccionado;
    }

    public boolean isPoligonosCargados() {
        return poligonosCargados;
    }

    public void setPoligonosCargados(boolean poligonosCargados) {
        this.poligonosCargados = poligonosCargados;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getAccordionCombos() {
        return accordionCombos;
    }

    public void setAccordionCombos(int accordionCombos) {
        this.accordionCombos = accordionCombos;
    }

    public void onMarkerSelect(OverlaySelectEvent event) {
        marcadorSeleccionado = (Marker) event.getOverlay();
    }

    public void cargarPoligonosDepartamentos() {
        long startTime = System.currentTimeMillis();

        DepartamentoAdministrado departamentoAdministrado = (DepartamentoAdministrado) getFacesContext().getApplication().createValueBinding("#{departamentoAdministrado}").getValue(getFacesContext());
        try {
            if (false == this.poligonosCargados) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Se inicia la carga de los poligonos", Util.tiempo());
                List<Departamento> departamentos = departamentoCallService.obtenerActivos();

                if (null != departamentos && 0 < departamentos.size()) {
                    for (Departamento departamento : departamentos) {

                        PoligonoDepartamento poligono = new PoligonoDepartamento(
                                true,
                                departamento.getNidDepartamento(),
                                departamentoAdministrado.getColores().get(departamento.getNidDepartamento().intValue()),
                                departamentoAdministrado.getColores().get(departamento.getNidDepartamento().intValue()));

                        this.poligonosDepartamentos.add(poligono);
                    }

                    Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Los poligonos fueron cargados satisfactoriamente", Util.tiempo());
                } else {
                    Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se encontraron departamentos", Util.tiempo());
                }

                this.poligonosCargados = true;
            } else {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Ya se han cargado los poligonos", Util.tiempo());
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
            //cambiarSeleccionDepartamentoProvinciaDistrito();  
        } catch (Exception e) {
            adicionarMensaje("cargarDepartamentos", e.getMessage());
        }
    }

    public List<String> departamentosLista() {
        long startTime = System.currentTimeMillis();
        List<String> lista = new ArrayList<>();
        List<Departamento> departamento = new ArrayList<>();
        String valor = "";
        try {
            departamento = departamentoCallService.obtenerActivos();

            if (null != departamento && 0 < departamento.size()) {
                for (Departamento departamentoTemporal : departamento) {
                    valor = '"' + departamentoTemporal.getTxtDescripcion() + '"';
                    lista.add(valor);
                }
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Correcto", Util.tiempo());
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

            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
        return lista;
    }

    public void limpiarCombos(int departamento, int provincia, int distrito) {
        DepartamentoAdministrado departamentoAdministrado = (DepartamentoAdministrado) getFacesContext().getApplication().createValueBinding("#{departamentoAdministrado}").getValue(getFacesContext());
        ProvinciaAdministrado provinciaAdministrado = (ProvinciaAdministrado) getFacesContext().getApplication().createValueBinding("#{provinciaAdministrado}").getValue(getFacesContext());
        DistritoAdministrado distritoAdministrado = (DistritoAdministrado) getFacesContext().getApplication().createValueBinding("#{distritoAdministrado}").getValue(getFacesContext());

        if (departamento == 0) {
            departamentoAdministrado.setEntidadSeleccionada(null);
        }

        if (provincia == 0) {
            provinciaAdministrado.setEntidadSeleccionada(null);
        }

        if (distrito == 0) {
            distritoAdministrado.setEntidadSeleccionada(null);
        }
    }

}
