package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.TipoDocumentoAdministrado;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.seguridad.modelo.TipoDocumento;
import pe.gob.mimp.siscap.modelo.ActividadGob;
import pe.gob.mimp.siscap.modelo.Participante;
import pe.gob.mimp.siscap.modelo.PersonaSiscap;
import pe.gob.mimp.siscap.modelo.PublicoObjetivo;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.webservices.IdentificacionReniec;
import pe.gob.mimp.siscap.ws.actividadgob.cliente.ActividadGobCallService;
import pe.gob.mimp.siscap.ws.actividadgpo.cliente.ActividadGPOCallService;
import pe.gob.mimp.siscap.ws.participante.cliente.ParticipanteCallService;
import pe.gob.mimp.siscap.ws.personasiscap.cliente.PersonaSiscapCallService;
import pe.gob.mimp.siscap.ws.publicoobjetivo.cliente.PublicoObjetivoCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipodocumento.cliente.TipoDocumentoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ParticipanteAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(Participante.class.getName());
    private static final long serialVersionUID = 1L;

    private IdentificacionReniec identificadorReniec;
    private Participante entidadSeleccionada;
    private PersonaSiscap personaSiscap;

    public ActividadGob actividadGobSeleccionado;

    private Boolean flgNuevoParticipante;

    public List<Participante> participanteList;
    private List<PublicoObjetivo> publicoObjetivoList;

    @Inject
    private ParticipanteCallService participanteCallService;
    @Inject
    private ActividadGobCallService actividadGobCallService;
    @Inject
    private PersonaSiscapCallService personaSiscapCallService;
    @Inject
    private TipoDocumentoCallService tipoDocumentoCallService;
    @Inject
    private ActividadGPOCallService actividadGPOCallService;
    @Inject
    private PublicoObjetivoCallService publicoObjetivoCallService;

    @PostConstruct
    public void initBean() {
        try {
            limpiarParticipante("1");
        } catch (Exception e) {
        }
        this.entidadSeleccionada = new Participante();
        this.actividadGobSeleccionado = new ActividadGob();
    }

    public Participante getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Participante entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public PersonaSiscap getPersonaSiscap() {
        return personaSiscap;
    }

    public IdentificacionReniec getIdentificadorReniec() {
        return identificadorReniec;
    }

    public void setIdentificadorReniec(IdentificacionReniec identificadorReniec) {
        this.identificadorReniec = identificadorReniec;
    }

    public Boolean getFlgNuevoParticipante() {
        return flgNuevoParticipante;
    }

    public void setFlgNuevoParticipante(Boolean flgNuevoParticipante) {
        this.flgNuevoParticipante = flgNuevoParticipante;
    }

    public void setPersonaSiscap(PersonaSiscap personaSiscap) {
        this.personaSiscap = personaSiscap;
    }

    public List<Participante> getParticipanteList() {
        return participanteList;
    }

    public void setParticipanteList(List<Participante> participanteList) {
        this.participanteList = participanteList;
    }

    public List<PublicoObjetivo> getPublicoObjetivoList() {
        return publicoObjetivoList;
    }

    public void setPublicoObjetivoList(List<PublicoObjetivo> publicoObjetivoList) {
        this.publicoObjetivoList = publicoObjetivoList;
    }

    private boolean enviarWarnMessage(String mensaje) {
        adicionarMensajeError("Información", mensaje);
        return false;
    }

    public boolean validarFormulario() { // (true: Crear | false: Modificar) ; (1: programar | 2: Ejecutar)
        long startTime = System.currentTimeMillis();
        try {
            PersonaSiscapAdministrado personaSiscapAdministrado = (PersonaSiscapAdministrado) getFacesContext().getApplication().createValueBinding("#{personaSiscapAdministrado}").getValue(getFacesContext());
            TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
            PublicoObjetivoAdministrado publicoObjetivoAdministrado = (PublicoObjetivoAdministrado) getFacesContext().getApplication().createValueBinding("#{publicoObjetivoAdministrado}").getValue(getFacesContext());

            if (Funciones.esVacio(tipoDocumentoAdministrado.getEntidadSeleccionada())) {
                return enviarWarnMessage("Elija el tipo de Documento");
            }
            if (Funciones.esVacio(tipoDocumentoAdministrado.getEntidadSeleccionada().getNidTipoDocumento())) {
                return enviarWarnMessage("Elija el tipo de Documento");
            }
            if (Funciones.esVacio(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento())) {
                return enviarWarnMessage("Ingrese el Numero de Documento");
            }
            if (Funciones.esVacio(personaSiscapAdministrado.getEntidadSeleccionada().getTxtApellidoMaterno())) {
                return enviarWarnMessage("Ingrese el Apellido Paterno");
            }
            if (Funciones.esVacio(personaSiscapAdministrado.getEntidadSeleccionada().getTxtApellidoPaterno())) {
                return enviarWarnMessage("Ingrese el Apellido Materno");
            }
            if (Funciones.esVacio(personaSiscapAdministrado.getEntidadSeleccionada().getTxtNombres())) {
                return enviarWarnMessage("Ingrese los nombres");
            }
            if (Funciones.esVacio(personaSiscapAdministrado.getEntidadSeleccionada().getTxtSexo())) {
                return enviarWarnMessage("Elija el sexo");
            }
            if (Funciones.esVacio(publicoObjetivoAdministrado.getEntidadSeleccionada())) {
                return enviarWarnMessage("Seleccione el publico objetivo");
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

    public void buscarParticipante() {
        long startTime = System.currentTimeMillis();

        PersonaSiscapAdministrado personaSiscapAdministrado = (PersonaSiscapAdministrado) getFacesContext().getApplication().createValueBinding("#{personaSiscapAdministrado}").getValue(getFacesContext());
        TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());

        try {
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("txtDocumento");
            findAllByFieldBean.setValue(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento());
            List<PersonaSiscap> personaSiscapEncontrado = personaSiscapCallService.findAllByField(findAllByFieldBean);
            switch (tipoDocumentoAdministrado.getEntidadSeleccionada().getTxtDescripcion()) {
                case "DNI":
                    if (personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento().length() == 8) {
                        if (null != personaSiscapEncontrado && 0 < personaSiscapEncontrado.size()) {
                            personaSiscapAdministrado.setEntidadSeleccionada(personaSiscapEncontrado.get(0));
                            setFlgNuevoParticipante(true);
                        } else {

                            this.identificadorReniec = new IdentificacionReniec();
                            /*this.identificadorReniec.obtenerConsultaReniec(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento());
                             switch (this.getIdentificadorReniec().getCOD_ERROR()) {
                             case "0000":
                             String dniIngresadoRecuperado = personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento();
                             personaSiscapAdministrado.setEntidadSeleccionada(new PersonaSiscap());
                             personaSiscapAdministrado.getEntidadSeleccionada().setTxtDocumento(dniIngresadoRecuperado);
                             personaSiscapAdministrado.getEntidadSeleccionada().setTxtApellidoMaterno(this.getIdentificadorReniec().getAPELLIDO_MATERNO());
                             personaSiscapAdministrado.getEntidadSeleccionada().setTxtApellidoPaterno(this.getIdentificadorReniec().getAPELLIDO_PATERNO());
                             personaSiscapAdministrado.getEntidadSeleccionada().setTxtNombres(this.getIdentificadorReniec().getNOMBRES());
                             personaSiscapAdministrado.getEntidadSeleccionada().setFecNacimiento(this.getIdentificadorReniec().getFEC_NACIMIENTO());
                             personaSiscapAdministrado.getEntidadSeleccionada().setTxtSexo(this.identificadorReniec.obtenerGenero(this.getIdentificadorReniec().getGENERO()));
                             setFlgNuevoParticipante(true);
                             break;
                             case "DNE":
                             case "NHC":
                             limpiarParticipante("2");
                             setFlgNuevoParticipante(!enviarWarnMessage("El DNI consultado es invalido"));
                             break;
                             case "5":
                             enviarWarnMessage("Servicio de RENIEC no disponible, realizando la consulta por la PIDE.");*/
                            this.identificadorReniec.obtenerConsultaReniecPIDE(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento());
                            switch (this.getIdentificadorReniec().getCOD_ERROR()) {
                                case "0000":

                                    personaSiscapAdministrado.getEntidadSeleccionada().setTxtApellidoMaterno(this.getIdentificadorReniec().getAPELLIDO_MATERNO());
                                    personaSiscapAdministrado.getEntidadSeleccionada().setTxtApellidoPaterno(this.getIdentificadorReniec().getAPELLIDO_PATERNO());
                                    personaSiscapAdministrado.getEntidadSeleccionada().setTxtNombres(this.getIdentificadorReniec().getNOMBRES());
                                    personaSiscapAdministrado.getEntidadSeleccionada().setFecNacimiento(this.getIdentificadorReniec().getFEC_NACIMIENTO());
                                    //visitanteAdministrado.getEntidadSeleccionada().setTxtSexo(this.identificadorReniec.obtenerGenero(this.getIdentificadorReniec().getGENERO()));
                                    setFlgNuevoParticipante(true);

                                    break;
                                default:
                                    limpiarParticipante("2");
                                    setFlgNuevoParticipante(enviarWarnMessage("Servicio de RENIEC no disponible. Se habilitará los campos del participante para ser registrados manualmente"));
                                    break;
                            }/*
                             break;
                             default:
                             limpiarParticipante("2");
                             setFlgNuevoParticipante(enviarWarnMessage("Servicio de RENIEC no disponible. Se habilitará los campos del participante para ser registrados manualmente"));
                             break;
                             }*/

                        }
                    } else {
                        limpiarParticipante("2");
                        setFlgNuevoParticipante(!enviarWarnMessage("Se requieren 8 digitos para la busqueda por DNI"));
                    }
                    break;
                case "CARNET EXTRANJERIA":
                    if (personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento().length() == 12) {
                        if (null != personaSiscapEncontrado && 0 < personaSiscapEncontrado.size()) {
                            personaSiscapAdministrado.setEntidadSeleccionada(personaSiscapEncontrado.get(0));
                            setFlgNuevoParticipante(true);
                        } else {
                            setFlgNuevoParticipante(false);
                        }
                    } else {
                        limpiarParticipante("2");
                        setFlgNuevoParticipante(!enviarWarnMessage("No puede superar los 12 digitos para la busqueda por carnet de extranjeria"));
                    }
                    break;
                case "PASAPORTE":
                    if (personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento().length() <= 12) {
                        if (null != personaSiscapEncontrado && 0 < personaSiscapEncontrado.size()) {
                            personaSiscapAdministrado.setEntidadSeleccionada(personaSiscapEncontrado.get(0));
                            setFlgNuevoParticipante(true);
                        } else {
                            setFlgNuevoParticipante(false);
                        }
                    } else {
                        limpiarParticipante("2");
                        setFlgNuevoParticipante(!enviarWarnMessage("No puede superar los 12 digitos para la busqueda por pasaporte"));
                    }
                    break;
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
            setFlgNuevoParticipante(!enviarWarnMessage("Debe seleccionar el tipo de documento a buscar"));
        }
    }

    public int cantidadDigitosDocumento() {
        int cantidadDigitos = 0;

        try {
            TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
            switch (tipoDocumentoAdministrado.getEntidadSeleccionada().getTxtDescripcion()) {
                case "DNI":
                    cantidadDigitos = 8;
                    break;
                case "CARNET EXTRANJERIA":
                    cantidadDigitos = 12;
                    break;
                case "PASAPORTE":
                    cantidadDigitos = 12;
                    break;
            }
        } catch (Exception e) {
            cantidadDigitos = 0;
        }

        return cantidadDigitos;
    }

    public void validarCantidadParticipantes(ActividadGob actividadGobSeleccionado) {
        long startTime = System.currentTimeMillis();
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
        //ActividadGob actividadGobierno = null;
        try {
            int contMasculino = 0;
            int contFemenino = 0;
            if (!this.participanteList.isEmpty()) {
                for (Participante participante : this.participanteList) {
                    if (participante.getNidPersonaSiscap().getTxtSexo().equals("F")) {
                        contFemenino++;
                    } else {
                        contMasculino++;
                    }
                }
                //fachadaActividadGob.edit(actividadGobSeleccionado);
            }
            actividadGobSeleccionado.setNumContadorFemenino(BigInteger.valueOf(contFemenino));
            actividadGobSeleccionado.setNumContadorMasculino(BigInteger.valueOf(contMasculino));
            actividadGobSeleccionado.setNumContadorTotal(BigInteger.valueOf(contFemenino + contMasculino));
            actividadGobSeleccionado.setFlgActivo(BigInteger.ONE);
            actividadGobSeleccionado.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            actividadGobSeleccionado.setTxtPc(Internet.obtenerNombrePC());
            actividadGobSeleccionado.setTxtIp(Internet.obtenerIPPC());
            actividadGobSeleccionado.setFecEdicion(new Date());

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
        this.actividadGobSeleccionado = actividadGobSeleccionado;
    }

    public void removeParticipanteList(Participante entidadSeleccionada, ActividadGob actividadGobSeleccionado) {
        long startTime = System.currentTimeMillis();
        logger.info(":: ParticipanteAdministrado.anularParticipante :: Starting execution...");
        try {
            for (Participante participante : participanteList) {
                if (participante.getNidPersonaSiscap().getTxtDocumento().equals(entidadSeleccionada.getNidPersonaSiscap().getTxtDocumento())) {
                    participanteList.remove(entidadSeleccionada);
                    break;
                }
            }
            validarCantidadParticipantes(actividadGobSeleccionado);

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

        logger.info(":: ParticipanteAdministrado.anularParticipante() :: Execution finish");
    }

    public void addParticipanteList(ActividadGob actividadGobSeleccionado) {
        try {
            long startTime = System.currentTimeMillis();
            if (validarFormulario()) {
                Boolean rpta = false;
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
                PersonaSiscapAdministrado personaSiscapAdministrado = (PersonaSiscapAdministrado) getFacesContext().getApplication().createValueBinding("#{personaSiscapAdministrado}").getValue(getFacesContext());
                TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
                PublicoObjetivoAdministrado publicoObjetivoAdministrado = (PublicoObjetivoAdministrado) getFacesContext().getApplication().createValueBinding("#{publicoObjetivoAdministrado}").getValue(getFacesContext());

                //verificar si el participante se encuentra en la lista de participantes.
                for (Participante participante : this.participanteList) {
                    if (participante.getNidPersonaSiscap().getTxtDocumento().equals(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento())) {
                        rpta = true;
                        break;
                    }
                }

                if (!rpta) {
                    FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
                    findAllByFieldBean.setField("txtDocumento");
                    findAllByFieldBean.setValue(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento());
                    List<PersonaSiscap> personaSiscapEncontrado = personaSiscapCallService.findAllByField(findAllByFieldBean);
                    // agregar a la persona a la base de datos cuando es nueva en la consulta de la reniec
                    if (personaSiscapEncontrado.isEmpty()) {
                        PersonaSiscap nuevaPersona = personaSiscapAdministrado.getEntidadSeleccionada();
                        nuevaPersona.setNidTipoDocumento(tipoDocumentoAdministrado.getEntidadSeleccionada().getNidTipoDocumento().toBigInteger());
                        nuevaPersona.setFlgActivo(BigInteger.ONE);
                        nuevaPersona.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        nuevaPersona.setTxtIp(Internet.obtenerIPPC());
                        nuevaPersona.setTxtPc(Internet.obtenerNombrePC());
                        nuevaPersona.setFecEdicion(new Date());
                        personaSiscapCallService.crearPersonaSiscap(nuevaPersona);

                        if (nuevaPersona.getNidPersonaSiscap() != null) {

                            Participante nuevoParticipante = new Participante();
                            nuevoParticipante.setNidPersonaSiscap(nuevaPersona);

                            if (publicoObjetivoAdministrado.getEntidadSeleccionada() != null) {
                                nuevoParticipante.setNidPublicoObjetivo(publicoObjetivoAdministrado.getEntidadSeleccionada());
                            }
                            nuevoParticipante.setNidActividadGob(actividadGobAdministrado.getEntidadSeleccionada());
                            nuevoParticipante.setFlgActivo(BigInteger.ONE);
                            nuevoParticipante.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                            nuevoParticipante.setTxtPc(Internet.obtenerNombrePC());
                            nuevoParticipante.setTxtIp(Internet.obtenerIPPC());
                            nuevoParticipante.setFecEdicion(new Date());

                            this.participanteList.add(nuevoParticipante);
                            RequestContext.getCurrentInstance().execute("PF('dialogoNuevoParticipante').hide()");
                        }
                    } else {
                        Participante nuevoParticipante = new Participante();

                        nuevoParticipante.setNidPersonaSiscap(personaSiscapEncontrado.get(0));
                        if (publicoObjetivoAdministrado.getEntidadSeleccionada() != null) {
                            nuevoParticipante.setNidPublicoObjetivo(publicoObjetivoAdministrado.getEntidadSeleccionada());
                        }
                        nuevoParticipante.setNidActividadGob(actividadGobAdministrado.getEntidadSeleccionada());
                        nuevoParticipante.setFlgActivo(BigInteger.ONE);
                        nuevoParticipante.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        nuevoParticipante.setTxtPc(Internet.obtenerNombrePC());
                        nuevoParticipante.setTxtIp(Internet.obtenerIPPC());
                        nuevoParticipante.setFecEdicion(new Date());

                        this.participanteList.add(nuevoParticipante);
                        RequestContext.getCurrentInstance().execute("PF('dialogoNuevoParticipante').hide()");
                    }
                    validarCantidadParticipantes(actividadGobSeleccionado);
                } else {
                    enviarWarnMessage("El participante ya se encuentra registrado en la lista de participantes");
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
        }
    }

    public void crearParticipante(ActividadGob actividadGobSeleccionado) {
        logger.info(":: ParticipanteAdministrado.crearParticipante :: Starting execution...");

        try {
            long startTime = System.currentTimeMillis();
            if (validarFormulario()) {
                Boolean rpta = false;
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());
                PersonaSiscapAdministrado personaSiscapAdministrado = (PersonaSiscapAdministrado) getFacesContext().getApplication().createValueBinding("#{personaSiscapAdministrado}").getValue(getFacesContext());
                TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
                PublicoObjetivoAdministrado publicoObjetivoAdministrado = (PublicoObjetivoAdministrado) getFacesContext().getApplication().createValueBinding("#{publicoObjetivoAdministrado}").getValue(getFacesContext());

                //verificar si el participante se encuentra en la lista de participantes.
                for (Participante participante : this.participanteList) {
                    if (participante.getNidPersonaSiscap().getTxtDocumento().equals(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento())) {
                        rpta = true;
                        break;
                    }
                }

                if (!rpta) {
                    FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
                    findAllByFieldBean.setField("txtDocumento");
                    findAllByFieldBean.setValue(personaSiscapAdministrado.getEntidadSeleccionada().getTxtDocumento());
                    List<PersonaSiscap> personaSiscapEncontrado = personaSiscapCallService.findAllByField(findAllByFieldBean);

                    if (personaSiscapEncontrado.isEmpty()) {
                        PersonaSiscap nuevaPersona = personaSiscapAdministrado.getEntidadSeleccionada();
                        nuevaPersona.setNidTipoDocumento(tipoDocumentoAdministrado.getEntidadSeleccionada().getNidTipoDocumento().toBigInteger());
                        nuevaPersona.setFlgActivo(BigInteger.ONE);
                        nuevaPersona.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        nuevaPersona.setTxtIp(Internet.obtenerIPPC());
                        nuevaPersona.setTxtPc(Internet.obtenerNombrePC());
                        nuevaPersona.setFecEdicion(new Date());
                        personaSiscapCallService.crearPersonaSiscap(nuevaPersona);

                        if (nuevaPersona.getNidPersonaSiscap() != null) {
                            Participante nuevoParticipante = new Participante();

                            nuevoParticipante.setNidPersonaSiscap(nuevaPersona);

                            if (publicoObjetivoAdministrado.getEntidadSeleccionada() != null) {
                                nuevoParticipante.setNidPublicoObjetivo(publicoObjetivoAdministrado.getEntidadSeleccionada());
                            }
                            nuevoParticipante.setNidActividadGob(actividadGobAdministrado.getEntidadSeleccionada());
                            nuevoParticipante.setFlgActivo(BigInteger.ONE);
                            nuevoParticipante.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                            nuevoParticipante.setTxtPc(Internet.obtenerNombrePC());
                            nuevoParticipante.setTxtIp(Internet.obtenerIPPC());
                            nuevoParticipante.setFecEdicion(new Date());

                            participanteCallService.crearParticipante(nuevoParticipante);
                            if (nuevoParticipante.getNidParticipante() != null) {
                                loadParticipanteList();
                                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoParticipante').hide()");
                            }
                        }
                    } else {
                        Participante nuevoParticipante = new Participante();

                        nuevoParticipante.setNidPersonaSiscap(personaSiscapEncontrado.get(0));
                        if (publicoObjetivoAdministrado.getEntidadSeleccionada() != null) {
                            nuevoParticipante.setNidPublicoObjetivo(publicoObjetivoAdministrado.getEntidadSeleccionada());
                        }
                        nuevoParticipante.setNidActividadGob(actividadGobAdministrado.getEntidadSeleccionada());
                        nuevoParticipante.setFlgActivo(BigInteger.ONE);
                        nuevoParticipante.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        nuevoParticipante.setTxtPc(Internet.obtenerNombrePC());
                        nuevoParticipante.setTxtIp(Internet.obtenerIPPC());
                        nuevoParticipante.setFecEdicion(new Date());
                        participanteCallService.crearParticipante(nuevoParticipante);
                        if (nuevoParticipante.getNidParticipante() != null) {
                            loadParticipanteList();
                            RequestContext.getCurrentInstance().execute("PF('dialogoNuevoParticipante').hide()");
                        }
                    }
                    validarCantidadParticipantes(actividadGobSeleccionado);
                } else {
                    enviarWarnMessage("El participante ya se encuentra registrado en la lista de participantes");
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
        }
        logger.info(":: ParticipanteAdministrado.crearParticipante() :: Execution finish");
    }

    public void anularParticipante(Participante entidadSeleccionada, ActividadGob actividadGobSeleccionado) {
        logger.info(":: ParticipanteAdministrado.anularParticipante :: Starting execution...");
        try {
            long startTime = System.currentTimeMillis();
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

            entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());

            participanteCallService.editarParticipante(entidadSeleccionada);
            loadParticipanteList();
            validarCantidadParticipantes(actividadGobSeleccionado);

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

        logger.info(":: ParticipanteAdministrado.anularParticipante() :: Execution finish");
    }

    public void limpiarParticipante(String num) throws Exception {
        long startTime = System.currentTimeMillis();

        TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext().getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
        PersonaSiscapAdministrado personaSiscapAdministrado = (PersonaSiscapAdministrado) getFacesContext().getApplication().createValueBinding("#{personaSiscapAdministrado}").getValue(getFacesContext());
        PublicoObjetivoAdministrado publicoObjetivoAdministrado = (PublicoObjetivoAdministrado) getFacesContext().getApplication().createValueBinding("#{publicoObjetivoAdministrado}").getValue(getFacesContext());

        switch (num) {
            case "1":
                try {
                this.publicoObjetivoList = new ArrayList<>();
                //ActidviadGob actividadGobId = actividadGobAdministrado.getEntidadSeleccionada();
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                //parameters.put("nidActividadGob", actividadGobId);
                this.publicoObjetivoList = publicoObjetivoCallService.loadPublicoObjetivoList(new FindByParamBean(parameters, "nidPublicoObjetivo"));
                /*List<ActividadGobPubliObje> actividadPO = fachadaActividadGPO.findByParams(parameters, "nidPublicoObjetivo");
                     for (ActividadGobPubliObje agpo : actividadPO) {
                     this.publicoObjetivoList.add(fachadaPublicoObjetivo.find(agpo.getNidPublicoObjetivo().getNidPublicoObjetivo()));
                     }*/
            } catch (Exception e) {

            }
            //this.entidadSeleccionada = new Participante();
            this.flgNuevoParticipante = true;

            publicoObjetivoAdministrado.setEntidadSeleccionada(new PublicoObjetivo());
            personaSiscapAdministrado.setEntidadSeleccionada(new PersonaSiscap());

            TipoDocumento tipoDocumento = tipoDocumentoCallService.find(BigDecimal.ONE);
            tipoDocumentoAdministrado.setEntidadSeleccionada(tipoDocumento);

            break;
            case "2":
                personaSiscapAdministrado.setEntidadSeleccionada(new PersonaSiscap());
                this.flgNuevoParticipante = true;
                break;
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

    private void loadParticipanteList() {
        logger.info(":: ParticipanteAdministrado.loadParticipanteList :: Starting execution...");
        try {
            ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidActividadGob", actividadGobAdministrado.getEntidadSeleccionada());
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            this.participanteList = participanteCallService.loadParticipanteList(new FindByParamBean(parameters, "nidParticipante"));
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error loadParticipanteList" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ParticipanteAdministrado.loadParticipanteList :: Execution finish.");
    }
}
