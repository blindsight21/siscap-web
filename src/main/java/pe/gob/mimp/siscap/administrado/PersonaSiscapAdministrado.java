package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.seguridad.modelo.TipoDocumento;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.CorreoPersonaSiscap;
import pe.gob.mimp.siscap.modelo.Participante;
import pe.gob.mimp.siscap.modelo.PersonaSiscap;
import pe.gob.mimp.siscap.modelo.TelefonoPersonaSiscap;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.actividadgob.cliente.ActividadGobCallService;
import pe.gob.mimp.siscap.ws.correopersonasiscap.cliente.CorreoPersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.participante.cliente.ParticipanteCallService;
import pe.gob.mimp.siscap.ws.personasiscap.cliente.PersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.telefonopersonasiscap.cliente.TelefonoPersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.tipodocumento.cliente.TipoDocumentoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class PersonaSiscapAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(Participante.class.getName());

    private PersonaSiscap entidad;
    private PersonaSiscap entidadSeleccionada;
    private List<PersonaSiscap> entidades;

    private CorreoPersonaSiscap correoPersonaSiscap;
    private TelefonoPersonaSiscap telefonoPersonaSiscap;
    private Participante participante;
    private ActividadGob actividaGob;

    @Inject
    private PersonaSiscapCallService personaSiscapCallService;
    @Inject
    private CorreoPersonaSiscapCallService correoPersonaSiscapCallService;
    @Inject
    private TelefonoPersonaSiscapCallService telefonoPersonaSiscapCallService;
    @Inject
    private ParticipanteCallService participanteCallService;
    @Inject
    private TipoDocumentoCallService tipoDocumentoCallService;
    @Inject
    private ActividadGobCallService actividadGobCallService;

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    public PersonaSiscapAdministrado() {

        this.entidad = new PersonaSiscap();
        this.entidadSeleccionada = new PersonaSiscap();

        this.entidades = new ArrayList<>();

        this.telefonoPersonaSiscap = new TelefonoPersonaSiscap();
        this.correoPersonaSiscap = new CorreoPersonaSiscap();
        this.participante = new Participante();
        this.actividaGob = new ActividadGob();

    }

    public PersonaSiscap getEntidad() {
        return entidad;
    }

    public void setEntidad(PersonaSiscap entidad) {
        this.entidad = entidad;
    }

    public PersonaSiscap getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(PersonaSiscap entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<PersonaSiscap> getEntidades() {
        return entidades;
    }

    public CorreoPersonaSiscap getCorreoPersonaSiscap() {
        return correoPersonaSiscap;
    }

    public void setCorreoPersonaSiscap(CorreoPersonaSiscap correoPersonaSiscap) {
        this.correoPersonaSiscap = correoPersonaSiscap;
    }

    public TelefonoPersonaSiscap getTelefonoPersonaSiscap() {
        return telefonoPersonaSiscap;
    }

    public void setTelefonoPersonaSiscap(TelefonoPersonaSiscap telefonoPersonaSiscap) {
        this.telefonoPersonaSiscap = telefonoPersonaSiscap;
    }

    public void setEntidades(List<PersonaSiscap> entidades) {
        this.entidades = entidades;
    }

    public PersonaSiscap getEntidad(String id) throws Exception {
        long startTime = System.currentTimeMillis();
        PersonaSiscap personaSiscap = null;

        if ((null != id) && (false == id.equals(""))) {
            personaSiscap = personaSiscapCallService.find(new BigDecimal(id));
        }

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.ACTIVIDADES.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return personaSiscap;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public ActividadGob getActividaGob() {
        return actividaGob;
    }

    public void setActividaGob(ActividadGob actividaGob) {
        this.actividaGob = actividaGob;
    }

    public PersonaSiscap getEntidad(BigInteger id) {
        PersonaSiscap personaSiscap = null;

        if (BigInteger.ZERO != id) {
            personaSiscap = personaSiscapCallService.find(new BigDecimal(id));
        }

        return personaSiscap;
    }

    public List<TipoDocumento> obtenerTiposDocumentoParticipante() throws Exception {
        long startTime = System.currentTimeMillis();
        List<TipoDocumento> tiposDocumento = tipoDocumentoCallService.findAll();
        List<TipoDocumento> tiposDocumentoPersonaNatural = new ArrayList<TipoDocumento>();

        for (TipoDocumento tipo : tiposDocumento) {
            if (true == tipo.getTxtDescripcion().equals("DNI")) {
                tiposDocumentoPersonaNatural.add(tipo);
            }
            if (true == tipo.getTxtDescripcion().equals("CARNET EXTRANJERIA")) {
                tiposDocumentoPersonaNatural.add(tipo);
            }
            if (true == tipo.getTxtDescripcion().equals("PASAPORTE")) {
                tiposDocumentoPersonaNatural.add(tipo);
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
        return tiposDocumentoPersonaNatural;
    }

    /*public void addTemporalPersonaSiscap() {
        logger.info(":: ParticipanteAdministrado.addTablePersonaSiscap :: Starting execution...");
        try {
            Boolean rpta = false;
            TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
            if (tipoDocumentoAdministrado.getEntidadSeleccionada() != null) {
                this.getEntidadSeleccionada().setNidTipoDocumento(tipoDocumentoAdministrado.getEntidadSeleccionada().getNidTipoDocumento().toBigInteger());
            }
            if (!this.entidades.isEmpty()) {
                for (PersonaSiscap personaSiscap : this.entidades) {
                    if ((this.entidadSeleccionada.getTxtDocumento().equals(personaSiscap.getTxtDocumento()))) {
                        rpta = true;
                        break;
                    }
                }
            }
            if (!rpta) {
                this.entidades.add(this.entidadSeleccionada);
            } else {
                System.out.println("Persona ya registrada con DNI: " + this.entidadSeleccionada.getTxtDocumento());
            }
        } catch (Exception e) {
        }
        logger.info(":: ParticipanteAdministrado.addTablePersonaSiscap :: Execution finishing...");
    }

    public void deleteTemporalPersonaSiscap(PersonaSiscap entidadSeleccionada) {
        int c = 0;
        for (PersonaSiscap personaSiscap : this.entidades) {
            c++;
            if ((entidadSeleccionada.getTxtDocumento().equals(personaSiscap.getTxtDocumento()))) {
                break;
            }
        }
        this.entidades.remove(c - 1);
    }

    public void refreshTemporalPersonaSiscap() {
        TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
        tipoDocumentoAdministrado.setEntidadSeleccionada(new TipoDocumento());
        this.entidadSeleccionada = new PersonaSiscap();
    }*/
    public void crearPersonaSiscap() {
        long startTime = System.currentTimeMillis();
        try {

            if (null != this.getEntidadSeleccionada()) {

                PersonaSiscap nuevoPersonaSiscap = new PersonaSiscap();

                nuevoPersonaSiscap.setTxtApellidoPaterno(this.entidadSeleccionada.getTxtApellidoPaterno());
                nuevoPersonaSiscap.setTxtApellidoMaterno(this.entidadSeleccionada.getTxtApellidoMaterno());
                nuevoPersonaSiscap.setTxtNombres(this.entidadSeleccionada.getTxtNombres());
                nuevoPersonaSiscap.setNidTipoDocumento(this.entidadSeleccionada.getNidTipoDocumento());
                nuevoPersonaSiscap.setTxtDocumento(this.entidadSeleccionada.getTxtDocumento());
                nuevoPersonaSiscap.setFecNacimiento(this.entidadSeleccionada.getFecNacimiento());
                nuevoPersonaSiscap.setTxtSexo(this.entidadSeleccionada.getTxtSexo());
                nuevoPersonaSiscap.setFlgActivo(BigInteger.ONE);
                nuevoPersonaSiscap.setNidUsuario(BigInteger.ONE);
                nuevoPersonaSiscap.setTxtPc(Internet.obtenerNombrePC());
                nuevoPersonaSiscap.setTxtIp(Internet.obtenerIPPC());
                nuevoPersonaSiscap.setFecEdicion(new Date());

                personaSiscapCallService.crearPersonaSiscap(nuevoPersonaSiscap);

                if (null != nuevoPersonaSiscap.getNidPersonaSiscap()) {

                    TelefonoPersonaSiscap nuevoTelefonoPersonaSiscap = new TelefonoPersonaSiscap();

                    nuevoTelefonoPersonaSiscap.setNidTipoTelefono(this.telefonoPersonaSiscap.getNidTipoTelefono());
                    nuevoTelefonoPersonaSiscap.setTxtTelefonoPersonaSiscap(this.telefonoPersonaSiscap.getTxtTelefonoPersonaSiscap());
                    nuevoTelefonoPersonaSiscap.setNidPersonaSiscap(nuevoPersonaSiscap);
                    nuevoTelefonoPersonaSiscap.setFlgActivo(BigInteger.ONE);
                    nuevoTelefonoPersonaSiscap.setNidUsuario(BigInteger.ONE);
                    nuevoTelefonoPersonaSiscap.setTxtPc(Internet.obtenerNombrePC());
                    nuevoTelefonoPersonaSiscap.setTxtIp(Internet.obtenerIPPC());
                    nuevoTelefonoPersonaSiscap.setFecEdicion(new Date());

                    telefonoPersonaSiscapCallService.crearTelefonoPersonaSiscap(nuevoTelefonoPersonaSiscap);

                    CorreoPersonaSiscap nuevoCorreoPersonaSiscap = new CorreoPersonaSiscap();

                    nuevoCorreoPersonaSiscap.setTxtCorreoPersonaSiscap(this.correoPersonaSiscap.getTxtCorreoPersonaSiscap());
                    nuevoCorreoPersonaSiscap.setNidPersonaSiscap(nuevoPersonaSiscap);
                    nuevoCorreoPersonaSiscap.setFlgActivo(BigInteger.ONE);
                    nuevoCorreoPersonaSiscap.setNidUsuario(BigInteger.ONE);
                    nuevoCorreoPersonaSiscap.setTxtPc(Internet.obtenerNombrePC());
                    nuevoCorreoPersonaSiscap.setTxtIp(Internet.obtenerIPPC());
                    nuevoCorreoPersonaSiscap.setFecEdicion(new Date());

                    correoPersonaSiscapCallService.crearCorreoPersonaSiscap(nuevoCorreoPersonaSiscap);

                    ActividadGob actividadGobEncontrado = actividadGobCallService.find(this.actividaGob.getNidActividadGob());
                    if (null != actividadGobEncontrado) {

                        Participante nuevoParticipante = new Participante();

                        nuevoParticipante.setNidPersonaSiscap(nuevoPersonaSiscap);
                        nuevoParticipante.setNidActividadGob(actividadGobEncontrado);
                        nuevoParticipante.setFlgActivo(BigInteger.ONE);
                        nuevoParticipante.setNidUsuario(BigInteger.ONE);
                        nuevoParticipante.setTxtPc(Internet.obtenerNombrePC());
                        nuevoParticipante.setTxtIp(Internet.obtenerIPPC());
                        nuevoParticipante.setFecEdicion(new Date());

                        participanteCallService.crearParticipante(nuevoParticipante);
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
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiar() {

        this.entidadSeleccionada = new PersonaSiscap();
        this.correoPersonaSiscap = new CorreoPersonaSiscap();
        this.telefonoPersonaSiscap = new TelefonoPersonaSiscap();
    }
}
