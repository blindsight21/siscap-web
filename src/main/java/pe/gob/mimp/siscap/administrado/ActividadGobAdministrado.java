package pe.gob.mimp.siscap.administrado;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.bean.UsuarioModuloBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Archivo;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.general.modelo.Distrito;
import pe.gob.mimp.seguridad.administrado.AreaAdministrado;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.seguridad.converter.ModuloCast;
import pe.gob.mimp.seguridad.converter.UsuarioCast;
import pe.gob.mimp.seguridad.modelo.Area;
import pe.gob.mimp.seguridad.modelo.Modulo;
import pe.gob.mimp.seguridad.modelo.Perfil;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.ActividadGobEActGob;
import pe.gob.mimp.siscap.modelo.ActividadGobPubliObje;
import pe.gob.mimp.siscap.modelo.ActividadGobPubliProc;
import pe.gob.mimp.siscap.modelo.ActividadGobResultado;
import pe.gob.mimp.siscap.modelo.ArchivoActividadGob;
import pe.gob.mimp.siscap.modelo.EstadoActividadGob;
import pe.gob.mimp.siscap.modelo.FuncionTransferida;
import pe.gob.mimp.siscap.modelo.Gobierno;
import pe.gob.mimp.siscap.modelo.ModalidadActividad;
import pe.gob.mimp.siscap.modelo.ParametroSiscap;
import pe.gob.mimp.siscap.modelo.Participante;
import pe.gob.mimp.siscap.modelo.ProgramacionFecha;
import pe.gob.mimp.siscap.modelo.PublicoObjetivo;
import pe.gob.mimp.siscap.modelo.TipoFuncion;
import pe.gob.mimp.siscap.modelo.TipoGobierno;
import pe.gob.mimp.siscap.modelo.TipoModalidad;
import pe.gob.mimp.siscap.modelo.TipoObjetivo;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.actividadge.cliente.ActividadGECallService;
import pe.gob.mimp.siscap.ws.actividadgob.cliente.ActividadGobCallService;
import pe.gob.mimp.siscap.ws.actividadgpo.cliente.ActividadGPOCallService;
import pe.gob.mimp.siscap.ws.actividadgpp.cliente.ActividadGPPCallService;
import pe.gob.mimp.siscap.ws.actividadgr.cliente.ActividadGRCallService;
import pe.gob.mimp.siscap.ws.archivoactividad.cliente.ArchivoActividadCallService;
import pe.gob.mimp.siscap.ws.area.cliente.AreaCallService;
import pe.gob.mimp.siscap.ws.correopersonasiscap.cliente.CorreoPersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.distrito.cliente.DistritoCallService;
import pe.gob.mimp.siscap.ws.estadoactividadgob.cliente.EstadoActividadGobCallService;
import pe.gob.mimp.siscap.ws.funciontransferida.cliente.FuncionTransferidaCallService;
import pe.gob.mimp.siscap.ws.gobierno.cliente.GobiernoCallService;
import pe.gob.mimp.siscap.ws.modalidadactividad.cliente.ModalidadActividadCallService;
import pe.gob.mimp.siscap.ws.modulo.cliente.ModuloCallService;
import pe.gob.mimp.siscap.ws.parametrosiscap.cliente.ParametroSiscapCallService;
import pe.gob.mimp.siscap.ws.participante.cliente.ParticipanteCallService;
import pe.gob.mimp.siscap.ws.perfil.cliente.PerfilCallService;
import pe.gob.mimp.siscap.ws.persona.cliente.PersonaCallService;
import pe.gob.mimp.siscap.ws.programacionfecha.cliente.ProgramacionFechaCallService;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;
import pe.gob.mimp.siscap.ws.publicoobjetivo.cliente.PublicoObjetivoCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.telefonopersonasiscap.cliente.TelefonoPersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.tipofuncion.cliente.TipoFuncionCallService;
import pe.gob.mimp.siscap.ws.tipogobierno.cliente.TipoGobiernoCallService;
import pe.gob.mimp.siscap.ws.tipomodalidad.cliente.TipoModalidadCallService;
import pe.gob.mimp.siscap.ws.tipoobjetivo.cliente.TipoObjetivoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ActividadGobAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(ActividadGob.class.getName());
    private static final long serialVersionUID = 1L;

    private ActividadGob entidadSeleccionada;
    private List<ActividadGob> actividadGobList;

    List<TipoObjetivo> tipoObjetivoList;
    List<TipoModalidad> tipoModalidadList;
    List<ModalidadActividad> modalidadActividadList;
    List<TipoGobierno> tipoGobiernoList;
    List<Gobierno> gobiernoSedeList;
    List<Gobierno> gobiernoPublicoList;
    List<TipoFuncion> tipoFuncionList;
    List<FuncionTransferida> funcionTransferidaList;
    List<PublicoObjetivo> publicoObjetivoList;
    List<ActividadGob> actividadGobCoincidenciaList;

    List<EstadoActividadGob> estadoActividadList;
    List<ActividadGobPubliObje> actividadPOList;
    List<ActividadGobResultado> actividadGRList;

    private List<Gobierno> gobiernosEncontrados;
    private List<PublicoObjetivo> publicoObjetivosEncontrados;

    private Archivo archivoActividadGob;
    private StreamedContent archivoActividadPDF;

    private BigInteger anio;
    private BigInteger trimestre;
    private String alertaActividad;
    private int tipoBoton; //1 = Crear \ 2 = Ver \ 3 = Editar \ 4 = Anular \ 5 = Reprogramar

    SimpleDateFormat date = new SimpleDateFormat("yyyy");
    private Calendar car;

    @Inject
    private ActividadGobCallService actividadGobCallService;
    @Inject
    private ModuloCallService moduloCallService;
    @Inject
    private PerfilCallService perfilCallService;
    @Inject
    private TipoObjetivoCallService tipoObjetivoCallService;
    @Inject
    private TipoModalidadCallService tipoModalidadCallService;
    @Inject
    private ModalidadActividadCallService modalidadActividadCallService;
    @Inject
    private TipoGobiernoCallService tipoGobiernoCallService;
    @Inject
    private GobiernoCallService gobiernoCallService;
    @Inject
    private TipoFuncionCallService tipoFuncionCallService;
    @Inject
    private FuncionTransferidaCallService funcionTransferidaCallService;
    @Inject
    private PublicoObjetivoCallService publicoObjetivoCallService;
    @Inject
    private DistritoCallService distritoCallService;
    @Inject
    private ProvinciaCallService provinciaCallService;
    @Inject
    private PersonaCallService personaCallService;
    @Inject
    private AreaCallService areaCallService;
    @Inject
    private EstadoActividadGobCallService estadoActividadGobCallService;
    @Inject
    private ActividadGECallService actividadGECallService;
    @Inject
    private ActividadGPOCallService actividadGPOCallService;
    @Inject
    private ActividadGPPCallService actividadGPPCallService;
    @Inject
    private ActividadGRCallService actividadGRACallService;
    @Inject
    private ParticipanteCallService participanteCallService;
    @Inject
    private TelefonoPersonaSiscapCallService telefonoPersonaSiscapCallService;
    @Inject
    private CorreoPersonaSiscapCallService correoPersonaSiscapCallService;
    @Inject
    private ProgramacionFechaCallService programacionFechaCallService;
    @Inject
    private ArchivoActividadCallService archivoActividadCallService;
    @Inject
    private ParametroSiscapCallService parametroSiscapCallService;

    public ActividadGobAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        logger.info(":: ActividadGobAdministrado.initBean :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication().createValueBinding("#{areaAdministrado}").getValue(getFacesContext());

            areaAdministrado.setEntidadSeleccionada(new Area());
            this.entidadSeleccionada = new ActividadGob();
            this.entidadSeleccionada.setNidFuncionTransferida(new FuncionTransferida());
            this.entidadSeleccionada.setNidGobierno(new Gobierno());
            this.entidadSeleccionada.setNidTipoModalidad(new TipoModalidad());
            this.entidadSeleccionada.setNidModalidadActividad(new ModalidadActividad());
            this.entidadSeleccionada.setNidTipoObjetivo(new TipoObjetivo());

            car = Calendar.getInstance();
            this.anio = new BigInteger(date.format(new Date()));
            this.trimestre = this.obtenerNumeroTrimestre();
            this.alertaActividad = "";
            this.tipoBoton = 1;
            loadActividadGobList();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            this.tipoObjetivoList = tipoObjetivoCallService.loadTipoObjetivoList(new FindByParamBean(parameters, "txtTipoObjetivo"));
            this.tipoModalidadList = tipoModalidadCallService.loadTipoModalidadList(new FindByParamBean(parameters, "txtTipoModalidad"));
            this.modalidadActividadList = modalidadActividadCallService.loadModalidadActividadList(new FindByParamBean(parameters, "txtModalidadActividad"));
            this.tipoGobiernoList = tipoGobiernoCallService.loadTipoGobiernoList(new FindByParamBean(parameters, "txtTipoGobierno"));
            this.tipoFuncionList = tipoFuncionCallService.loadTipoFuncionList(new FindByParamBean(parameters, "txtTipoFuncion"));
            this.publicoObjetivoList = publicoObjetivoCallService.loadPublicoObjetivoList(new FindByParamBean(parameters, "txtPublicoObjetivo"));
            this.gobiernoPublicoList = gobiernoCallService.loadGobiernoList(new FindByParamBean(parameters, "txtGobierno"));
            this.estadoActividadList = estadoActividadGobCallService.loadEstadoActividadGobList(new FindByParamBean(parameters, "txtEstadoActividadGob"));

            this.publicoObjetivosEncontrados = new ArrayList<>();
            this.gobiernosEncontrados = new ArrayList<>();

            this.actividadGobCoincidenciaList = new ArrayList<>();

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error method initBean" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.initBean :: Execution finish.");
    }

    public ActividadGob getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ActividadGob entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoObjetivo> getTipoObjetivoList() {
        return tipoObjetivoList;
    }

    public void setTipoObjetivoList(List<TipoObjetivo> tipoObjetivoList) {
        this.tipoObjetivoList = tipoObjetivoList;
    }

    public List<TipoModalidad> getTipoModalidadList() {
        return tipoModalidadList;
    }

    public void setTipoModalidadList(List<TipoModalidad> tipoModalidadList) {
        this.tipoModalidadList = tipoModalidadList;
    }

    public List<ModalidadActividad> getModalidadActividadList() {
        return modalidadActividadList;
    }

    public void setModalidadActividadList(List<ModalidadActividad> modalidadActividadList) {
        this.modalidadActividadList = modalidadActividadList;
    }

    public List<TipoGobierno> getTipoGobiernoList() {
        return tipoGobiernoList;
    }

    public void setTipoGobiernoList(List<TipoGobierno> tipoGobiernoList) {
        this.tipoGobiernoList = tipoGobiernoList;
    }

    public List<Gobierno> getGobiernoSedeList() {
        return gobiernoSedeList;
    }

    public void setGobiernoSedeList(List<Gobierno> gobiernoSedeList) {
        this.gobiernoSedeList = gobiernoSedeList;
    }

    public List<Gobierno> getGobiernoPublicoList() {
        return gobiernoPublicoList;
    }

    public void setGobiernoPublicoList(List<Gobierno> gobiernoPublicoList) {
        this.gobiernoPublicoList = gobiernoPublicoList;
    }

    public List<TipoFuncion> getTipoFuncionList() {
        return tipoFuncionList;
    }

    public void setTipoFuncionList(List<TipoFuncion> tipoFuncionList) {
        this.tipoFuncionList = tipoFuncionList;
    }

    public List<FuncionTransferida> getFuncionTransferidaList() {
        return funcionTransferidaList;
    }

    public void setFuncionTransferidaList(List<FuncionTransferida> funcionTransferidaList) {
        this.funcionTransferidaList = funcionTransferidaList;
    }

    public List<PublicoObjetivo> getPublicoObjetivoList() {
        return publicoObjetivoList;
    }

    public void setPublicoObjetivoList(List<PublicoObjetivo> publicoObjetivoList) {
        this.publicoObjetivoList = publicoObjetivoList;
    }

    public Archivo getArchivoActividadGob() {
        return archivoActividadGob;
    }

    public void setArchivoActividadGob(Archivo archivoActividadGob) {
        this.archivoActividadGob = archivoActividadGob;
    }

    public List<ActividadGob> getActividadGobList() {
        return actividadGobList;
    }

    public void setActividadGobList(List<ActividadGob> actividadGobList) {
        this.actividadGobList = actividadGobList;
    }

    public List<EstadoActividadGob> getEstadoActividadList() {
        return estadoActividadList;
    }

    public void setEstadoActividadList(List<EstadoActividadGob> estadoActividadList) {
        this.estadoActividadList = estadoActividadList;
    }

    public List<ActividadGobPubliObje> getActividadPOList() {
        return actividadPOList;
    }

    public void setActividadPOList(List<ActividadGobPubliObje> actividadPOList) {
        this.actividadPOList = actividadPOList;
    }

    public List<ActividadGobResultado> getActividadGRList() {
        return actividadGRList;
    }

    public void setActividadGRList(List<ActividadGobResultado> actividadGRList) {
        this.actividadGRList = actividadGRList;
    }

    public List<Gobierno> getGobiernosEncontrados() {
        return gobiernosEncontrados;
    }

    public void setGobiernosEncontrados(List<Gobierno> gobiernosEncontrados) {
        this.gobiernosEncontrados = gobiernosEncontrados;
    }

    public List<PublicoObjetivo> getPublicoObjetivosEncontrados() {
        return publicoObjetivosEncontrados;
    }

    public void setPublicoObjetivosEncontrados(List<PublicoObjetivo> publicoObjetivosEncontrados) {
        this.publicoObjetivosEncontrados = publicoObjetivosEncontrados;
    }

    public List<ActividadGob> getActividadGobCoincidenciaList() {
        return actividadGobCoincidenciaList;
    }

    public void setActividadGobCoincidenciaList(List<ActividadGob> actividadGobCoincidenciaList) {
        this.actividadGobCoincidenciaList = actividadGobCoincidenciaList;
    }

    public BigInteger getAnio() {
        return anio;
    }

    public void setAnio(BigInteger anio) {
        this.anio = anio;
    }

    public BigInteger getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(BigInteger trimestre) {
        this.trimestre = trimestre;
    }

    public String getAlertaActividad() {
        return alertaActividad;
    }

    public void setAlertaActividad(String alertaActividad) {
        this.alertaActividad = alertaActividad;
    }

    public Integer getTipoBoton() {
        return tipoBoton;
    }

    public void setTipoBoton(Integer tipoBoton) {
        this.tipoBoton = tipoBoton;
    }

    public ActividadGob getEntidad(BigInteger id) throws Exception {
        long startTime = System.currentTimeMillis();
        ActividadGob actividadGob = null;
        if (null != id) {
            actividadGob = actividadGobCallService.find(new BigDecimal(id));
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return actividadGob;
    }

    public void onRowSelectPublicoObjetivo(SelectEvent event) {
        try {
            PublicoObjetivo publicoObjetivoSeleccionado = (PublicoObjetivo) event.getObject();

            if (!this.publicoObjetivosEncontrados.contains(publicoObjetivoSeleccionado)) {
                this.getPublicoObjetivosEncontrados().add(publicoObjetivoSeleccionado);
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error onRowSelectPublicoObjetivo" + e.getMessage(), Util.tiempo());
        }
    }

    public void onRowUnselectPublicoObjetivo(UnselectEvent event) {
        try {
            PublicoObjetivo publicoObjetivoDeseleccionado = (PublicoObjetivo) event.getObject();

            if (this.publicoObjetivosEncontrados.contains(publicoObjetivoDeseleccionado)) {
                this.getPublicoObjetivosEncontrados().remove(publicoObjetivoDeseleccionado);
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error onRowUnselectPublicoObjetivo" + e.getMessage(), Util.tiempo());
        }
    }

    public void onRowSelectGobierno(SelectEvent event) {
        try {
            Gobierno gobiernoSeleccionado = (Gobierno) event.getObject();

            if (!this.gobiernosEncontrados.contains(gobiernoSeleccionado)) {
                this.getGobiernosEncontrados().add(gobiernoSeleccionado);
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error onRowSelectGobierno" + e.getMessage(), Util.tiempo());
        }
    }

    public void onRowUnselectGobierno(UnselectEvent event) {
        try {
            Gobierno gobiernoDeseleccionado = (Gobierno) event.getObject();

            if (this.gobiernosEncontrados.contains(gobiernoDeseleccionado)) {
                this.getGobiernosEncontrados().remove(gobiernoDeseleccionado);
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error onRowUnselectGobierno" + e.getMessage(), Util.tiempo());
        }
    }

    public void obtenerFuncionByTipoFuncion(AjaxBehaviorEvent e) throws Exception {
        logger.info(":: ActividadGobAdministrado.obtenerFuncionByTipoFuncion :: Starting execution...");
        long startTime = System.currentTimeMillis();
        this.entidadSeleccionada.setNidFuncionTransferida(null);
        this.funcionTransferidaList = null;
        loadFuncionList();
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: ActividadGobAdministrado.obtenerFuncionByTipoFuncion :: Execution finish.");
    }

    private void loadFuncionList() throws Exception {
        long startTime = System.currentTimeMillis();
        TipoFuncion tipoFuncion = tipoFuncionCallService.find(this.entidadSeleccionada.getNidTipoFuncion());
        if (!Funciones.esVacio(tipoFuncion)) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidTipoFuncion", tipoFuncion);
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            this.funcionTransferidaList = funcionTransferidaCallService.loadFuncionTransferidaList(new FindByParamBean(parameters, "txtFuncionTransferida"));
            logger.log(Level.INFO, " Funcion transferida {0}", this.funcionTransferidaList);
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    public void obtenerGobiernoByTipoGobierno(AjaxBehaviorEvent e) throws Exception {
        logger.info(":: ActividadGobAdministrado.obtenerGobiernoByTipoGobierno :: Starting execution...");
        long startTime = System.currentTimeMillis();
        this.entidadSeleccionada.setNidGobierno(null);
        this.gobiernoSedeList = null;
        loadGobiernoSedeList();
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: ActividadGobAdministrado.obtenerGobiernoByTipoGobierno :: Execution finish.");
    }

    private void loadGobiernoSedeList() throws Exception, Exception {
        logger.info(":: ActividadGobAdministrado.loadGobiernoSedeList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        TipoGobierno tipoGobierno = tipoGobiernoCallService.find(this.entidadSeleccionada.getNidTipoGobierno());
        if (!Funciones.esVacio(tipoGobierno)) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidTipoGobierno", tipoGobierno);
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

            this.gobiernoSedeList = gobiernoCallService.loadGobiernoList(new FindByParamBean(parameters, "txtGobierno"));
            logger.log(Level.INFO, " Gobierno {0}", this.gobiernoSedeList);
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: ActividadGobAdministrado.loadGobiernoSedeList :: Execution finish.");
    }

    public boolean validarFormulario(boolean isNew, int num) { // (true: Crear | false: Modificar) ; (1: programar | 2: Ejecutar | 3: Reprogramar | 4: Justificacion | )
        long startTime = System.currentTimeMillis();
        ParticipanteAdministrado participanteAdministrado = (ParticipanteAdministrado) getFacesContext().getApplication().createValueBinding("#{participanteAdministrado}").getValue(getFacesContext());
        FileUploadViewAdministrado fileUploadViewAdministrado = (FileUploadViewAdministrado) getFacesContext().getApplication().createValueBinding("#{fileUploadViewAdministrado}").getValue(getFacesContext());
        ActividadGEAdministrado actividadGEAdministrado = (ActividadGEAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGEAdministrado}").getValue(getFacesContext());
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
        AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication().createValueBinding("#{areaAdministrado}").getValue(getFacesContext());
        try {
            if (num == 1) {
                if (isNew) {
                    if (Funciones.esVacio(this.getEntidadSeleccionada().getFecInicio())) {
                        return enviarWarnMessage("Ingrese Fecha de Inicio de Actividad");
                    } else {
                        if (this.getEntidadSeleccionada().getFecInicio().before(Funciones.fechaActual())) {
                            return enviarWarnMessage("La Fecha de Inicio de Actividad debe ser mayor a la Fecha Actual");
                        }
                    }
                    if (Funciones.esVacio(this.getEntidadSeleccionada().getFecFin())) {
                        return enviarWarnMessage("Ingrese Fecha de Fin de Actividad");
                    } else {
                        if (this.getEntidadSeleccionada().getFecFin().before(this.getEntidadSeleccionada().getFecInicio())) {
                            return enviarWarnMessage("La Fecha de Fin de Actividad no debe ser mayor a la Fecha Inicial");
                        }
                    }
                    if (Funciones.esVacio(this.getPublicoObjetivosEncontrados()) || Funciones.esVacio(this.getPublicoObjetivosEncontrados().size())) {
                        return enviarWarnMessage("Seleccione Publico Objetivo");
                    }
                    if (Funciones.esVacio(this.getGobiernosEncontrados()) || Funciones.esVacio(this.getGobiernosEncontrados().size())) {
                        return enviarWarnMessage("Seleccione Gobierno de Procedencia");
                    }
                }

                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtObjetivo().toUpperCase())) {
                    return enviarWarnMessage("Ingrese Objetivo.");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoObjetivo()) || Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoObjetivo().getNidTipoObjetivo())) {
                    return enviarWarnMessage("Seleccione Tipo Objetivo");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoModalidad()) || Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoModalidad().getNidTipoModalidad())) {
                    return enviarWarnMessage("Seleccione Tipo Modalidad");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidModalidadActividad()) || Funciones.esVacio(this.getEntidadSeleccionada().getNidModalidadActividad().getNidModalidadActividad())) {
                    return enviarWarnMessage("Seleccione Modalidad Actividad");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtTema().toUpperCase())) {
                    return enviarWarnMessage("Ingrese Tema.");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoGobierno())) {
                    return enviarWarnMessage("Seleccione Tipo Gobierno");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidGobierno()) || Funciones.esVacio(this.getEntidadSeleccionada().getNidGobierno().getNidGobierno())) {
                    return enviarWarnMessage("Seleccione Gobierno");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidTipoFuncion())) {
                    return enviarWarnMessage("Seleccione Tipo Funcion");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getNidFuncionTransferida()) || Funciones.esVacio(this.getEntidadSeleccionada().getNidFuncionTransferida().getNidFuncionTransferida())) {
                    return enviarWarnMessage("Seleccione Funcion Transferida");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtResponsableApepat().toUpperCase())) {
                    return enviarWarnMessage("Ingrese Apellido Paterno del Responsable");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtResponsableApemat().toUpperCase())) {
                    return enviarWarnMessage("Ingrese Apellido Materno del Responsable");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtResponsableNombre())) {
                    return enviarWarnMessage("Ingrese Nombre del Responsable");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtResponsableTelef())) {
                    return enviarWarnMessage("Ingrese Telefono del Responsable");
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getTxtResponsableCorreo())) {
                    return enviarWarnMessage("Ingrese Correo del Responsable");
                }

                if (usuarioAdministrado.usuarioActivoEsAdministrador(usuarioAdministrado.getEntidadSeleccionada())) {
                    if (tipoBoton == 1) {
                        if (Funciones.esVacio(areaAdministrado.getEntidadSeleccionada().getNidArea())) {
                            return enviarWarnMessage("Seleccione Area");
                        }
                    }
                } else {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("fecInicio", this.getEntidadSeleccionada().getFecInicio());
                    parameters.put("nidGobierno", this.getEntidadSeleccionada().getNidGobierno());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    List<ActividadGob> listaActividadGobEncontrados = actividadGobCallService.loadActividadGobList(new FindByParamBean(parameters, "nidActividadGob"));
                    int cont = 0;
                    if (!listaActividadGobEncontrados.isEmpty()) {
                        for (ActividadGob actividadGob : listaActividadGobEncontrados) {

                            if (actividadGEAdministrado.obtenerEstado(actividadGob).equals("PROGRAMADO")
                                    || actividadGEAdministrado.obtenerEstado(actividadGob).equals("REPROGRAMADO")) {
                                cont++;
                            }
                        }
                        //int count = actividadGobCallService.getRecordCount(parameters);
                        logger.log(Level.INFO, "Actividades programadas encontrados: {0}", cont);

                        if (cont > 0) {
                            return enviarWarnMessage("No puede registrar una nueva actividad.Existen : " + cont + " Actividades Programadas y/o Reprogramadas");
                        }
                    }
                }
            } else if (num == 2) {
                if (participanteAdministrado.participanteList.isEmpty()) {
                    return enviarWarnMessage("Debe llenar la lista de participantes");
                }
                if (fileUploadViewAdministrado.archivoAsistenciaList.isEmpty()) {
                    return enviarWarnMessage("Debe colgar un archivo de asistencia");
                }
                if (this.actividadGRList.isEmpty()) {
                    return enviarWarnMessage("Debe registrar un resultado");
                }
            } else if (num == 3) {
                if (Funciones.esVacio(this.getEntidadSeleccionada().getFecInicio())) {
                    return enviarWarnMessage("Ingrese Fecha de inicio de Actividad Reprogramada");
                }

                if (Funciones.esVacio(this.getEntidadSeleccionada().getFecFin())) {
                    return enviarWarnMessage("Ingrese Fecha de Fin de Actividad Reprogramada");
                }
                if (this.getEntidadSeleccionada().getTxtJustificacionReprogramado().equals("")) {
                    return enviarWarnMessage("Debe justificar la reprogramacion de la actividad");
                }

            } else if (num == 4) {
                if (this.getEntidadSeleccionada().getTxtJustificacionAnulado().equals("")) {
                    return enviarWarnMessage("Debe justificar la anulacion de la actividad");
                }
            } else {
                if (Funciones.esVacio(this.getEntidadSeleccionada().getFecInicio())) {
                    return enviarWarnMessage("Ingrese Fecha de Inicio de Actividad");
                } else {
                    if (this.getEntidadSeleccionada().getFecInicio().before(Funciones.fechaActual())) {
                        return enviarWarnMessage("La Fecha de Inicio de Actividad debe ser mayor a la Fecha Actual");
                    }
                }
                if (Funciones.esVacio(this.getEntidadSeleccionada().getFecFin())) {
                    return enviarWarnMessage("Ingrese Fecha de Fin de Actividad");
                } else {
                    if (this.getEntidadSeleccionada().getFecFin().before(this.getEntidadSeleccionada().getFecInicio())) {
                        return enviarWarnMessage("La Fecha de Fin de Actividad no debe ser mayor a la Fecha Inicial");
                    }
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
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

    public Date generarFecha(boolean fechaActual, boolean fechaHoy, boolean fechaHoyPlus, Date fecha) {
        Date date = new Date();
        Calendar cal;

        if (fechaActual) {
            cal = emptyDate(date);
            if (fechaHoy) {
                //dia actual
                return cal.getTime();
            } else {
                //agregar un dia mas
                cal.add(Calendar.DATE, 1);
                return cal.getTime();
            }
        } else {
            cal = emptyDate(fecha);
            if (fechaHoyPlus) {//agregar una fecha adicional a la fecha seteada
                cal.add(Calendar.DATE, 1);
                return cal.getTime();
            } else {
                return cal.getTime();
            }
        }
    }

    public void obtenerTrimestreyAlerta(Boolean alertaCoincidencia) {
        long startTime = System.currentTimeMillis();
        ActividadGEAdministrado actividadGEAdministrado = (ActividadGEAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGEAdministrado}").getValue(getFacesContext());
        try {
            actividadGobCoincidenciaList = new ArrayList<>();
            if (null != this.getEntidadSeleccionada().getFecInicio()) {
                int mes = (this.getEntidadSeleccionada().getFecInicio().getMonth()) + 1;

                if (1 <= mes && mes < 4) {
                    this.getEntidadSeleccionada().setNumTrimestre(BigInteger.ONE);
                } else if (3 < mes && mes < 7) {
                    this.getEntidadSeleccionada().setNumTrimestre(BigInteger.valueOf(2));
                } else if (6 < mes && mes < 10) {
                    this.getEntidadSeleccionada().setNumTrimestre(BigInteger.valueOf(3));
                } else {
                    this.getEntidadSeleccionada().setNumTrimestre(BigInteger.valueOf(4));
                }

                if (alertaCoincidencia) {
                    if (this.getEntidadSeleccionada().getFecInicio() != null && this.getEntidadSeleccionada().getNidGobierno().getNidGobierno() != null) {

                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("fecInicio", this.getEntidadSeleccionada().getFecInicio());
                        parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                        parameters.put("nidGobierno", this.getEntidadSeleccionada().getNidGobierno());
                        List<ActividadGob> actividadGobEncontrados = actividadGobCallService.loadActividadGobList(new FindByParamBean(parameters, "nidActividadGob"));

                        if (!pe.gob.mimp.util.Util.esListaVacia(actividadGobEncontrados)) {
                            for (ActividadGob actividadGob : actividadGobEncontrados) {
                                if (actividadGEAdministrado.obtenerEstado(actividadGob).equals("PROGRAMADO")
                                        || actividadGEAdministrado.obtenerEstado(actividadGob).equals("REPROGRAMADO")) {
                                    actividadGobCoincidenciaList.add(actividadGob);
                                }
                            }
                            int cant = actividadGobCoincidenciaList.size();
                            //int count = actividadGobCallService.getRecordCount(parameters);
                            logger.log(Level.INFO, "Actividades programadas y/o reprogramadas encontrados: {0}", cant);

                            if (cant > 0) {
                                this.alertaActividad = "EXISTEN " + cant + " ACTIVIDADES PROGRAMADAS EN ESA FECHA.";
                                //enviarWarnMessage("No puede registrar una nueva actividad.Existen : " + cont + " Acticidades Programadas y/o Reprogramadas");
                            } else {
                                this.alertaActividad = "";
                            }
                        } else {
                            this.alertaActividad = "";
                        }
                    } else {
                        this.alertaActividad = "";
                    }
                }
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
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error obtenerTrimestreyAlerta" + e.getMessage(), Util.tiempo());
            this.alertaActividad = "";
        }
    }

    public BigInteger obtenerNumeroTrimestre() {

        BigInteger mesTrimestre = null;
        int mes = 0;

        mes = (new Date().getMonth()) + 1;

        try {
            if (0 < mes) {
                if (1 <= mes && mes < 4) {
                    mesTrimestre = BigInteger.ONE;
                } else if (3 < mes && mes < 7) {
                    mesTrimestre = BigInteger.valueOf(2);
                } else if (6 < mes && mes < 10) {
                    mesTrimestre = BigInteger.valueOf(3);
                } else if (9 < mes && mes <= 12) {
                    mesTrimestre = BigInteger.valueOf(4);
                } else {
                    mesTrimestre = null;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error obtenerNumeroTrimestre" + e.getMessage(), Util.tiempo());
        }
        return mesTrimestre;
    }

    public void cargarActividadGob(boolean isEdit) {
        logger.info(":: ActividadGobAdministrado.cargarActividadGob :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {

            FuncionTransferida funcionTransferida = this.getEntidadSeleccionada().getNidFuncionTransferida();
            Gobierno gobierno = this.getEntidadSeleccionada().getNidGobierno();
            this.entidadSeleccionada.setNidTipoFuncion(tipoFuncionCallService.find(funcionTransferida.getNidTipoFuncion().getNidTipoFuncion()).getNidTipoFuncion());
            this.entidadSeleccionada.setNidTipoGobierno(tipoGobiernoCallService.find(gobierno.getNidTipoGobierno().getNidTipoGobierno()).getNidTipoGobierno());
            this.publicoObjetivoList = new ArrayList<>();
            this.gobiernoPublicoList = new ArrayList<>();
            this.alertaActividad = "";

            loadFuncionList();
            loadGobiernoSedeList();
            ActividadGob ActividadGobId = this.getEntidadSeleccionada();
            if (isEdit) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                this.publicoObjetivoList = publicoObjetivoCallService.loadPublicoObjetivoList(new FindByParamBean(parameters, "txtPublicoObjetivo"));
                this.gobiernoPublicoList = gobiernoCallService.loadGobiernoList(new FindByParamBean(parameters, "txtGobierno"));
                parameters.put("nidActividadGob", ActividadGobId);

                this.entidadSeleccionada.setNidEstadoActividad(actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGobEActGob")).get(0).getNidEstadoActividadGob().getNidEstadoActividadGob());

            } else {

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                parameters.put("nidActividadGob", ActividadGobId);
                List<ActividadGobPubliObje> actividadPO = actividadGPOCallService.loadActividadGPOList(new FindByParamBean(parameters, "nidPublicoObjetivo"));
                List<ActividadGobPubliProc> actividadPP = actividadGPPCallService.loadActividadGPPList(new FindByParamBean(parameters, "nidGobierno"));

                for (ActividadGobPubliObje agpo : actividadPO) {
                    this.publicoObjetivoList.add(publicoObjetivoCallService.find(agpo.getNidPublicoObjetivo().getNidPublicoObjetivo()));
                }
                for (ActividadGobPubliProc agpp : actividadPP) {
                    this.gobiernoPublicoList.add(gobiernoCallService.find(agpp.getNidGobierno().getNidGobierno()));
                }
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
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error cargarActividadGob" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.cargarActividadGob :: Execution finish.");
    }

    public void crearActividadGob() throws Exception {
        logger.info(":: ActividadGobAdministrado.crearActividadGob :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(true, 1)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication().createValueBinding("#{areaAdministrado}").getValue(getFacesContext());

                this.getEntidadSeleccionada().setNumAnio(new BigInteger(date.format(new Date())));
                this.getEntidadSeleccionada().setFecCreacion(new Date());

                if (usuarioAdministrado.usuarioActivoEsAdministrador(usuarioAdministrado.getEntidadSeleccionada())) {
                    this.getEntidadSeleccionada().setNidArea(areaAdministrado.getEntidadSeleccionada().getNidArea().toBigInteger());
                } else {
                    this.getEntidadSeleccionada().setNidArea(usuarioAdministrado.getEntidadSeleccionada().getPersona().getNidArea());
                }

                FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
                findAllByFieldBean.setField("nidDistrito");
                findAllByFieldBean.setValue(this.getEntidadSeleccionada().getNidGobierno().getNidDistrito());
                List<Distrito> distritoEncontrado = distritoCallService.findAllByField(findAllByFieldBean);

                this.getEntidadSeleccionada().setNidDepartamento(provinciaCallService.find(distritoEncontrado.get(0).getProvincia().getNidProvincia()).getDepartamento().getNidDepartamento().toBigInteger());

                this.getEntidadSeleccionada().setNumContadorFemenino(BigInteger.ZERO);
                this.getEntidadSeleccionada().setNumContadorMasculino(BigInteger.ZERO);
                this.getEntidadSeleccionada().setNumContadorTotal(BigInteger.ZERO);

                this.getEntidadSeleccionada().setFlgActivo(BigInteger.ONE);
                this.getEntidadSeleccionada().setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                this.getEntidadSeleccionada().setTxtPc(Internet.obtenerNombrePC());
                this.getEntidadSeleccionada().setTxtIp(Internet.obtenerIPPC());
                this.getEntidadSeleccionada().setFecEdicion(new Date());

                ActividadGob nuevoActividad = this.getEntidadSeleccionada();

                actividadGobCallService.crearActividadGob(nuevoActividad);

                ActividadGobEActGob nuevoActividadEstado = new ActividadGobEActGob();

                nuevoActividadEstado.setNidActividadGob(nuevoActividad);
                nuevoActividadEstado.setNidEstadoActividadGob(estadoActividadGobCallService.find(BigDecimal.valueOf(2)));
                nuevoActividadEstado.setFlgActivo(BigInteger.ONE);
                nuevoActividadEstado.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                nuevoActividadEstado.setTxtPc(Internet.obtenerNombrePC());
                nuevoActividadEstado.setTxtIp(Internet.obtenerIPPC());
                nuevoActividadEstado.setFecEdicion(new Date());

                actividadGECallService.crearActividadGE(nuevoActividadEstado);

                for (PublicoObjetivo PublicoObjetivo : this.getPublicoObjetivosEncontrados()) {

                    ActividadGobPubliObje nuevoPublicoObjetivo = new ActividadGobPubliObje();

                    nuevoPublicoObjetivo.setNidActividadGob(nuevoActividad);
                    nuevoPublicoObjetivo.setNidPublicoObjetivo(PublicoObjetivo);
                    nuevoPublicoObjetivo.setFlgActivo(BigInteger.ONE);
                    nuevoPublicoObjetivo.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    nuevoPublicoObjetivo.setTxtPc(Internet.obtenerNombrePC());
                    nuevoPublicoObjetivo.setTxtIp(Internet.obtenerIPPC());
                    nuevoPublicoObjetivo.setFecEdicion(new Date());

                    actividadGPOCallService.crearActividadGPO(nuevoPublicoObjetivo);
                }

                for (Gobierno gobierno : this.getGobiernosEncontrados()) {

                    ActividadGobPubliProc nuevoGobiernoProcedencia = new ActividadGobPubliProc();

                    nuevoGobiernoProcedencia.setNidActividadGob(nuevoActividad);
                    nuevoGobiernoProcedencia.setNidGobierno(gobierno);
                    nuevoGobiernoProcedencia.setFlgActivo(BigInteger.ONE);
                    nuevoGobiernoProcedencia.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    nuevoGobiernoProcedencia.setTxtPc(Internet.obtenerNombrePC());
                    nuevoGobiernoProcedencia.setTxtIp(Internet.obtenerIPPC());
                    nuevoGobiernoProcedencia.setFecEdicion(new Date());

                    actividadGPPCallService.crearActividadGPP(nuevoGobiernoProcedencia);
                }
                if (null != nuevoActividad.getNidActividadGob()) {
                    // Update List Browser
                    loadActividadGobList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoActividadGob').hide()");
                }

            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearActividadGob" + e.getMessage(), Util.tiempo());
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "No se encontro el distrito del gobierno: " + this.getEntidadSeleccionada().getNidGobierno(), Util.tiempo());
            }
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: ActividadGobAdministrado.crearActividadGob :: Execution finish.");
    }

    public void editarActividadGob(ActividadGob entidadSeleccionada) {
        logger.info(":: ActividadGobAdministrado.editarActividadGob :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(false, 1)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                entidadSeleccionada.setFlgActivo(BigInteger.ONE);
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                actividadGobCallService.editarActividadGob(entidadSeleccionada);

                loadActividadGobList();
                RequestContext.getCurrentInstance().execute("PF('dialogoEditarActividadGob').hide()");

                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarActividadGob" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ActividadGobAdministrado.editarActividadGob() :: Execution finish");
    }

    public void anularActividadGob(ActividadGob entidadSeleccionada) {
        logger.info(":: ActividadGobAdministrado.anularActividadGob :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            parameters.put("nidActividadGob", entidadSeleccionada);

            List<ActividadGobEActGob> estadoActividad = actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGobEActGob"));
            for (ActividadGobEActGob anularEstadoAct : estadoActividad) {
                anularEstadoAct.setFlgActivo(BigInteger.ZERO);
                anularEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                anularEstadoAct.setTxtPc(Internet.obtenerNombrePC());
                anularEstadoAct.setTxtIp(Internet.obtenerIPPC());
                anularEstadoAct.setFecEdicion(new Date());
                actividadGECallService.editarActividadGE(anularEstadoAct);
            }

            ActividadGobEActGob nuevoEstadoAct = new ActividadGobEActGob();

            nuevoEstadoAct.setNidEstadoActividadGob(estadoActividadGobCallService.find(new BigDecimal(1)));//Anulado
            nuevoEstadoAct.setNidActividadGob(entidadSeleccionada);
            nuevoEstadoAct.setFlgActivo(BigInteger.ONE);
            nuevoEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            nuevoEstadoAct.setTxtPc(Internet.obtenerNombrePC());
            nuevoEstadoAct.setTxtIp(Internet.obtenerIPPC());
            nuevoEstadoAct.setFecEdicion(new Date());
            actividadGECallService.crearActividadGE(nuevoEstadoAct);

            entidadSeleccionada.setTxtObservacionEliminado(null); //falta
            //entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            actividadGobCallService.editarActividadGob(entidadSeleccionada);

            loadActividadGobList();
            RequestContext.getCurrentInstance().execute("PF('dialogoJustificarActividadGob').hide()");

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularActividadGob" + e.getMessage(), Util.tiempo());
        }

        logger.info(":: ActividadGobAdministrado.anularActividadGob() :: Execution finish");
    }

    public String obtenerAnio() {
        int year = this.car.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    /*metodo que resetea cualquier fecha a la media noche. Ejm: 12/12/12 15:20:23 to 12/12/12 00:00:00*/
    public Calendar emptyDate(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public BigInteger obtenerEstado(ActividadGob actividadGobierno) {

        BigInteger estado = null;

        try {
            long startTime = System.currentTimeMillis();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidActividadGob", actividadGobierno);
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            List<ActividadGobEActGob> estados = actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGob"));

            if (null != estados && 0 < estados.size()) {
                estado = estados.get(0).getNidEstadoActividadGob().getNidEstadoActividadGob().toBigInteger();
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

        return estado;
    }

    public void justificarActividadGob(ActividadGob entidadSeleccionada) {
        try {
            long startTime = System.currentTimeMillis();
            if (this.tipoBoton == 4) {
                if (validarFormulario(true, 4)) {
                    this.anularActividadGob(this.entidadSeleccionada);
                    loadActividadGobList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoJustificarActividadGob').hide()");
                }
            } else {
                if (validarFormulario(true, 3)) {
                    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                    entidadSeleccionada.setFlgActivo(BigInteger.ONE);
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    actividadGobCallService.editarActividadGob(entidadSeleccionada);

                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    parameters.put("nidActividadGob", this.getEntidadSeleccionada());

                    List<ActividadGobEActGob> estadoActividad = actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGobEActGob"));
                    for (ActividadGobEActGob anularEstadoAct : estadoActividad) {
                        anularEstadoAct.setFlgActivo(BigInteger.ZERO);
                        anularEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        anularEstadoAct.setTxtPc(Internet.obtenerNombrePC());
                        anularEstadoAct.setTxtIp(Internet.obtenerIPPC());
                        anularEstadoAct.setFecEdicion(new Date());
                        actividadGECallService.editarActividadGE(anularEstadoAct);
                    }

                    ActividadGobEActGob nuevoEstadoAct = new ActividadGobEActGob();

                    nuevoEstadoAct.setNidEstadoActividadGob(estadoActividadGobCallService.find(new BigDecimal(3)));//reprogramado
                    nuevoEstadoAct.setNidActividadGob(entidadSeleccionada);
                    nuevoEstadoAct.setFlgActivo(BigInteger.ONE);
                    nuevoEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    nuevoEstadoAct.setTxtPc(Internet.obtenerNombrePC());
                    nuevoEstadoAct.setTxtIp(Internet.obtenerIPPC());
                    nuevoEstadoAct.setFecEdicion(new Date());

                    actividadGECallService.crearActividadGE(nuevoEstadoAct);
                    loadActividadGobList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoJustificarActividadGob').hide()");
                }
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
    }

    public Boolean valideButtons(ActividadGob entidadSeleccionada, String tipoValidacion) {
        logger.info(":: ActividadGobAdministrado.valideButtons :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            Boolean statusAnuladoyEjecutado = false;
            BigInteger estado = null;
            switch (tipoValidacion) {
                case "programado":
                    estado = new BigInteger("2");//PROGRAMADO
                    break;
                case "ejecutado":
                    estado = new BigInteger("4");//EJECUTADO
                    //Si el estado esta en anular, el boton se bloqueará
                    statusAnuladoyEjecutado = (obtenerEstado(entidadSeleccionada).equals(BigInteger.valueOf(1))) && tipoValidacion.equals("ejecutado");
                    break;
                case "all":
                    estado = obtenerEstado(entidadSeleccionada);
                    break;
            }

            if (statusAnuladoyEjecutado) {
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
                return true;
            } else {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("txtAnio", this.anio.toString());
                parameters.put("numTrimestre", this.trimestre);

                parameters.put("nidTipoActividad", estado);
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

                List<ProgramacionFecha> programacionFechaEncontrado = programacionFechaCallService.loadProgramacionFechaList(new FindByParamBean(parameters, "nidProgramacionFecha"));

                if (programacionFechaEncontrado.size() > 0) {
                    ProgramacionFecha programacionFecha = programacionFechaEncontrado.get(0);
                    Date fecHoy = emptyDate(Calendar.getInstance().getTime()).getTime();
                    if (fecHoy.compareTo(programacionFecha.getFecInicio()) >= 0 && fecHoy.compareTo(programacionFecha.getFecFin()) <= 0) {
                        logger.info(":: ActividadGobAdministrado.valideButtons :: Execution finish.");
                        long stopTime = System.currentTimeMillis();
                        rendimientoCallService.crearRendimiento(
                                SiscapWebUtil.crearRendimiento(
                                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                        );
                        return false;
                    } else {
                        logger.info(":: ActividadGobAdministrado.valideButtons :: Execution finish.");
                        long stopTime = System.currentTimeMillis();
                        rendimientoCallService.crearRendimiento(
                                SiscapWebUtil.crearRendimiento(
                                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                        );
                        return true;
                    }
                } else {
                    logger.info(":: ActividadGobAdministrado.valideButtons :: Execution finish.");
                    long stopTime = System.currentTimeMillis();
                    rendimientoCallService.crearRendimiento(
                            SiscapWebUtil.crearRendimiento(
                                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                                    EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                    SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                    usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                    );
                    return true;
                }
            }
        } catch (Exception e) {
            logger.info(":: ActividadGobAdministrado.valideButtons :: Execution finish.");
            return true;
        }
    }

    public void cargarEjecucion() {
        long startTime = System.currentTimeMillis();
        if (tipoBoton == 4) {
            this.getEntidadSeleccionada().setFecInicio(null);
            this.getEntidadSeleccionada().setFecFin(null);
        }
        logger.info(":: ActividadGobAdministrado.cargarEjecucion :: Starting execution...");
        try {

            FileUploadViewAdministrado fileUploadViewAdministrado = (FileUploadViewAdministrado) getFacesContext().getApplication().createValueBinding("#{fileUploadViewAdministrado}").getValue(getFacesContext());
            ParticipanteAdministrado participanteAdministrado = (ParticipanteAdministrado) getFacesContext().getApplication().createValueBinding("#{participanteAdministrado}").getValue(getFacesContext());
            ActividadGob ActividadGobId = this.getEntidadSeleccionada();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            parameters.put("nidActividadGob", ActividadGobId);

            //encontrar el estado actual de la actividad
            BigDecimal estadoActividad = actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGobEActGob")).get(0).getNidEstadoActividadGob().getNidEstadoActividadGob();

            //if (estadoActividad.equals(new BigDecimal(4))) {
            //cargar el combo publico objetivo en panel participante
            this.actividadPOList = actividadGPOCallService.loadActividadGPOList(new FindByParamBean(parameters, "nidPublicoObjetivo"));
            //cargar la tabla gobierno resultado
            this.actividadGRList = actividadGRACallService.loadActividadGRList(new FindByParamBean(parameters, "nidActividadGobResultado"));
            participanteAdministrado.setParticipanteList(participanteCallService.loadParticipanteList(new FindByParamBean(parameters, "nidParticipante")));
            participanteAdministrado.validarCantidadParticipantes(this.entidadSeleccionada);
            //fileUploadViewAdministrado.setArchivoActividadList(fachadaArchivoActividad.findByParams(parameters, "nidArchivoActividad"));
            fileUploadViewAdministrado.loadArchivoAsistenciaList();
            //}
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error cargarActividadGob" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.cargarEjecucion :: Execution finish.");
    }

    public void ejecutarActividadGob(ActividadGob entidadSeleccionada) {
        logger.info(":: ActividadGobAdministrado.ejecutarActividadGob :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(true, 2)) {
            try {
                ParticipanteAdministrado participanteAdministrado = (ParticipanteAdministrado) getFacesContext().getApplication().createValueBinding("#{participanteAdministrado}").getValue(getFacesContext());
                //FileUploadViewAdministrado fileUploadViewAdministrado = (FileUploadViewAdministrado) getFacesContext().getApplication().createValueBinding("#{fileUploadViewAdministrado}").getValue(getFacesContext());
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                entidadSeleccionada.setFlgActivo(BigInteger.ONE);
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                actividadGobCallService.editarActividadGob(entidadSeleccionada);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                parameters.put("nidActividadGob", this.getEntidadSeleccionada());

                List<ActividadGobEActGob> estadoActividad = actividadGECallService.loadActividadGEList(new FindByParamBean(parameters, "nidActividadGobEActGob"));
                for (ActividadGobEActGob anularEstadoAct : estadoActividad) {
                    anularEstadoAct.setFlgActivo(BigInteger.ZERO);
                    anularEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    anularEstadoAct.setTxtPc(Internet.obtenerNombrePC());
                    anularEstadoAct.setTxtIp(Internet.obtenerIPPC());
                    anularEstadoAct.setFecEdicion(new Date());
                    actividadGECallService.editarActividadGE(anularEstadoAct);
                }

                ActividadGobEActGob nuevoEstadoAct = new ActividadGobEActGob();

                nuevoEstadoAct.setNidEstadoActividadGob(estadoActividadGobCallService.find(new BigDecimal(4)));//ejecutado
                nuevoEstadoAct.setNidActividadGob(entidadSeleccionada);
                nuevoEstadoAct.setFlgActivo(BigInteger.ONE);
                nuevoEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                nuevoEstadoAct.setTxtPc(Internet.obtenerNombrePC());
                nuevoEstadoAct.setTxtIp(Internet.obtenerIPPC());
                nuevoEstadoAct.setFecEdicion(new Date());

                actividadGECallService.crearActividadGE(nuevoEstadoAct);
                for (ActividadGobResultado actividadGobResultado : this.actividadGRList) {
                    actividadGRACallService.crearActividadGR(actividadGobResultado);
                }
                for (Participante participante : participanteAdministrado.participanteList) {
                    participanteCallService.crearParticipante(participante);
                }
                actividadGobCallService.editarActividadGob(participanteAdministrado.actividadGobSeleccionado);

                /*for (ArchivoActividad archivoActividad : fileUploadViewAdministrado.archivoAsistenciaList) {
                 fachadaArchivoActividad.create(archivoActividad);
                 }*/
                //}
                loadActividadGobList();
                RequestContext.getCurrentInstance().execute("PF('dialogoEjecutarActividadGob').hide()");

                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error ejecutarActividadGob" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ActividadGobAdministrado.ejecutarActividadGob :: Execution finish.");

        /* EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
         UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

         try {
         if (null != entidadSeleccionada) {

         if (null != estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob()) {
         if (estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob().equals(BigDecimal.valueOf(3))) {

         entidadSeleccionada.setFecInicio(this.getEntidadSeleccionada().getFecInicio());
         entidadSeleccionada.setFecFin(this.getEntidadSeleccionada().getFecFin());
         entidadSeleccionada.setNumTrimestre(this.getEntidadSeleccionada().getNumTrimestre());
         entidadSeleccionada.setFlgActivo(BigInteger.ONE);
         entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
         entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
         entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
         entidadSeleccionada.setFecEdicion(new Date());
         actividadGobCallService.edit(entidadSeleccionada);

         List<ActividadGobEActGob> actividadGobEActGobs = fachadaActividadGobEActGob.obtenerActivos(entidadSeleccionada);
         if (null != actividadGobEActGobs && 0 < actividadGobEActGobs.size()) {
         for (ActividadGobEActGob actividadGobEstadoAct : actividadGobEActGobs) {
         actividadGobEstadoAct.setFlgActivo(BigInteger.ZERO);
         actividadGobEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
         actividadGobEstadoAct.setTxtPc(Internet.obtenerNombrePC());
         actividadGobEstadoAct.setTxtIp(Internet.obtenerIPPC());
         actividadGobEstadoAct.setFecEdicion(new Date());
         fachadaActividadGobEActGob.edit(actividadGobEstadoAct);
         }
         }

         ActividadGobEActGob ejecutarActividadGobEActGob = new ActividadGobEActGob();

         ejecutarActividadGobEActGob.setNidActividadGobierno(entidadSeleccionada);
         ejecutarActividadGobEActGob.setNidEstadoActividadGob(estadoActividadGobAdministrado.getEntidadSeleccionada());
         ejecutarActividadGobEActGob.setFlgActivo(BigInteger.ONE);
         ejecutarActividadGobEActGob.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
         ejecutarActividadGobEActGob.setTxtPc(Internet.obtenerNombrePC());
         ejecutarActividadGobEActGob.setTxtIp(Internet.obtenerIPPC());
         ejecutarActividadGobEActGob.setFecEdicion(new Date());
         fachadaActividadGobEActGob.create(ejecutarActividadGobEActGob);

         } else {

         List<ActividadGobEActGob> actividadGobEActGobs = fachadaActividadGobEActGob.obtenerActivos(entidadSeleccionada);
         if (null != actividadGobEActGobs && 0 < actividadGobEActGobs.size()) {
         for (ActividadGobEActGob actividadGobEstadoAct : actividadGobEActGobs) {
         actividadGobEstadoAct.setFlgActivo(BigInteger.ZERO);
         actividadGobEstadoAct.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
         actividadGobEstadoAct.setTxtPc(Internet.obtenerNombrePC());
         actividadGobEstadoAct.setTxtIp(Internet.obtenerIPPC());
         actividadGobEstadoAct.setFecEdicion(new Date());
         fachadaActividadGobEActGob.edit(actividadGobEstadoAct);
         }
         }

         ActividadGobEActGob ejecutarActividadGobEActGob = new ActividadGobEActGob();

         ejecutarActividadGobEActGob.setNidActividadGobierno(entidadSeleccionada);
         ejecutarActividadGobEActGob.setNidEstadoActividadGob(estadoActividadGobAdministrado.getEntidadSeleccionada());
         ejecutarActividadGobEActGob.setFlgActivo(BigInteger.ONE);
         ejecutarActividadGobEActGob.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
         ejecutarActividadGobEActGob.setTxtPc(Internet.obtenerNombrePC());
         ejecutarActividadGobEActGob.setTxtIp(Internet.obtenerIPPC());
         ejecutarActividadGobEActGob.setFecEdicion(new Date());
         fachadaActividadGobEActGob.create(ejecutarActividadGobEActGob);
         }
         } else {
         Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} EjecutarActividadGob - No se encuentra estado", Util.tiempo());
         }
         }
         } catch (Exception e) {
         Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0} Error limpiarActividadGob" + e.getMessage(), Util.tiempo());
         }*/
    }

    public void subirArchivo(FileUploadEvent event) {
        long startTime = System.currentTimeMillis();
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

        try {
            if (null != event.getFile()) {

                FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
                findAllByFieldBean.setField("cidParametro");
                findAllByFieldBean.setValue("RUTAARCHIVOACTIVIDAD");
                List<ParametroSiscap> parametrosSiscap = parametroSiscapCallService.findAllByField(findAllByFieldBean);
                if (null != parametrosSiscap && 0 < parametrosSiscap.size()) {

                    String rutaFile = parametrosSiscap.get(0).getTxtValor();

                    this.archivoActividadGob = new Archivo(event.getFile(), BigDecimal.ZERO, "archivoActividadGob");
                    InputStream is = new ByteArrayInputStream(this.archivoActividadGob.getArchivoArreglo());
                    archivoActividadPDF = new DefaultStreamedContent(is);

                    if (null != archivoActividadPDF) {

                        String archivoId = event.getFile().getFileName();
                        Archivo.subirArchivo(this.archivoActividadGob.getArchivoArreglo(), rutaFile + archivoId, rutaFile);

                        ArchivoActividadGob nuevoaArchivoActividadGob = new ArchivoActividadGob();

                        nuevoaArchivoActividadGob.setNidActividadGobierno(this.entidadSeleccionada);
                        nuevoaArchivoActividadGob.setTxtRuta(rutaFile + archivoId);
                        nuevoaArchivoActividadGob.setFlgActivo(BigInteger.ONE);
                        nuevoaArchivoActividadGob.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        nuevoaArchivoActividadGob.setTxtPc(Internet.obtenerNombrePC());
                        nuevoaArchivoActividadGob.setTxtIp(Internet.obtenerIPPC());
                        nuevoaArchivoActividadGob.setFecEdicion(new Date());

                        //fachadaArchivoActividad.create(nuevoaArchivoActividadGob);
                    }
                }
            } else {

                adicionarMensajeError("SISCAP", "NO SE SUBIÒ ARCHIVO");
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
    }

    public void loadActividadGobList() {
        logger.info(":: ActividadGobAdministrado.loadActividadGobList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

            Modulo modulo = moduloCallService.find(new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("Modulo")));
            UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();
            usuarioModuloBean.setUsuarioBean(UsuarioCast.castUsuarioToUsuarioBean(usuarioAdministrado.getEntidadSeleccionada()));
            usuarioModuloBean.setModuloBean(ModuloCast.castModuloToModuloBean(modulo));
            List<Perfil> perfiles = perfilCallService.obtenerPerfiles(usuarioModuloBean);
            for (Perfil perfil : perfiles) {
                if (true == perfil.getTxtPerfil().equals("ADMINISTRADOR")) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("numAnio", anio);
                    parameters.put("numTrimestre", trimestre);
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    this.actividadGobList = actividadGobCallService.loadActividadGobList(new FindByParamBean(parameters, "fecCreacion"));
                    break;
                } else {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("numAnio", anio);
                    parameters.put("numTrimestre", trimestre);
                    parameters.put("nidUsuario", usuarioAdministrado.getEntidadSeleccionada().getNidUsuario());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    this.actividadGobList = actividadGobCallService.loadActividadGobList(new FindByParamBean(parameters, "fecCreacion"));
                    break;
                }
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
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadActividadGobList" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ActividadGobAdministrado.loadActividadGobList :: Execution finish.");
    }

    public void clearFormActividad() throws Exception {
        logger.info(":: ActividadGobAdministrado.clearFormActividad :: Starting execution...");
        long startTime = System.currentTimeMillis();
        AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication().createValueBinding("#{areaAdministrado}").getValue(getFacesContext());

        areaAdministrado.setEntidadSeleccionada(new Area());

        this.alertaActividad = "";

        this.entidadSeleccionada = new ActividadGob();
        this.entidadSeleccionada.setNidFuncionTransferida(new FuncionTransferida());
        this.entidadSeleccionada.setNidGobierno(new Gobierno());
        this.entidadSeleccionada.setNidTipoModalidad(new TipoModalidad());
        this.entidadSeleccionada.setNidModalidadActividad(new ModalidadActividad());
        this.entidadSeleccionada.setNidTipoObjetivo(new TipoObjetivo());

        this.funcionTransferidaList = null;
        this.gobiernoSedeList = null;

        this.publicoObjetivosEncontrados = new ArrayList<>();
        this.gobiernosEncontrados = new ArrayList<>();

        this.publicoObjetivoList = new ArrayList<>();
        this.gobiernoPublicoList = new ArrayList<>();
        this.actividadGobCoincidenciaList = new ArrayList<>();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
        this.publicoObjetivoList = publicoObjetivoCallService.loadPublicoObjetivoList(new FindByParamBean(parameters, "txtPublicoObjetivo"));
        this.gobiernoPublicoList = gobiernoCallService.loadGobiernoList(new FindByParamBean(parameters, "txtGobierno"));

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: ActividadGobAdministrado.clearFormActividad :: Execution finish.");
    }
}
