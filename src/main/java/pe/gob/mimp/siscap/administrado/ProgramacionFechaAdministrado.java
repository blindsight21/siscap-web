package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import pe.gob.mimp.siscap.modelo.EstadoActividadGob;
import pe.gob.mimp.siscap.modelo.ProgramacionFecha;
import pe.gob.mimp.siscap.ws.programacionfecha.cliente.ProgramacionFechaCallService;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class ProgramacionFechaAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(ProgramacionFecha.class.getName());
    private static final long serialVersionUID = 1L;

    private ProgramacionFecha entidadSeleccionada;
    private List<ProgramacionFecha> programacionFechaList;

    @Inject
    private ProgramacionFechaCallService programacionFechaCallService;

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new ProgramacionFecha();
        loadProgramacionFechaList();
    }

    public ProgramacionFecha getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(ProgramacionFecha entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<ProgramacionFecha> getProgramacionFechaList() {
        return programacionFechaList;
    }

    public void setProgramacionFechaList(List<ProgramacionFecha> programacionFechaList) {
        this.programacionFechaList = programacionFechaList;
    }

    public void cargarProgramacionFecha() {
        try {
            EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
            if (null != estadoActividadGobAdministrado.getEntidadSeleccionada()) {
                estadoActividadGobAdministrado.getEntidadSeleccionada().setNidEstadoActividadGob(new BigDecimal(this.getEntidadSeleccionada().getNidTipoActividad()));
            }
        } catch (Exception e) {
        }
    }

    public boolean validarFormulario(boolean isNuevo) {
        EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
        ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

        try {
            String anioActual = actividadGobAdministrado.obtenerAnio();

            if (Funciones.esVacio(this.entidadSeleccionada.getTxtAnio())) {
                return enviarWarnMessage("Seleccione el año");
            } else if (Funciones.esVacio(this.entidadSeleccionada.getNumTrimestre())) {
                return enviarWarnMessage("Seleccione el trimestre");
            } else if (Funciones.esVacio(this.entidadSeleccionada.getFecInicio())) {
                return enviarWarnMessage("Ingrese la fecha de inicio");
            } else if (Funciones.esVacio(this.entidadSeleccionada.getFecFin())) {
                return enviarWarnMessage("Ingrese la fecha de fin");
            /*} else if (this.entidadSeleccionada.getFecInicio().after(this.entidadSeleccionada.getFecFin())) {
                return enviarWarnMessage("La fecha de inicio debe ser menor o igual a la fecha fin");*/
            } else if (Funciones.esVacio(estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob())) {
                return enviarWarnMessage("Seleccione el estado de la actividad");
            /*} else if (!this.getEntidadSeleccionada().getTxtAnio().equals(anioActual)) {
                return enviarWarnMessage("Solo puede programar fechas del presente año: " + anioActual);*/
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtAnio", this.getEntidadSeleccionada().getTxtAnio());
                    parameters.put("numTrimestre", this.getEntidadSeleccionada().getNumTrimestre());
                    parameters.put("nidTipoActividad", BigInteger.valueOf(2));
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

                    //Si el tipo de actividad seleccionado es igual a programado
                    if (estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob().toBigInteger().equals(BigInteger.valueOf(2))) {
                        return true;
                    } else {
                        int count = programacionFechaCallService.getRecordCount(new FindByParamBean(parameters, ""));
                        logger.log(Level.INFO, "Programacion Fecha Tipo Actividad Programado encontrados: {0}", count);
                        if (count == 0) {
                            return enviarWarnMessage("No puede crear una programación si no existe el programado");
                        } else {
                            List<ProgramacionFecha> programacionFechaEncontrado = programacionFechaCallService.loadProgramacionFechaList(new FindByParamBean(parameters, "nidProgramacionFecha"));
                            if (programacionFechaEncontrado.size() > 0) {
                                ProgramacionFecha programacionFecha = programacionFechaEncontrado.get(0);
                                if (this.getEntidadSeleccionada().getFecInicio().before(programacionFecha.getFecFin())) {
                                    return enviarWarnMessage("La nueva programacion debe ser mayor o igual a la fecha de la actividad programada");
                                } else {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error validarFormulario" + e.getMessage(), Util.tiempo());
            return false;
        }
    }

    private boolean enviarWarnMessage(String mensaje) {
        adicionarMensajeError("Información", mensaje);
        return false;
    }

    public void crearProgramacionFecha() {
        logger.info(":: programacionFechaAdministrado.crearProgramacionFecha() :: Starting execution...");
        if (validarFormulario(true)) {
            EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

            try {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("txtAnio", this.getEntidadSeleccionada().getTxtAnio());
                parameters.put("numTrimestre", this.getEntidadSeleccionada().getNumTrimestre());
                parameters.put("nidTipoActividad", estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob());
                parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                List<ProgramacionFecha> programacionFechaLista = programacionFechaCallService.loadProgramacionFechaList(new FindByParamBean(parameters, "nidProgramacionFecha"));

                if (programacionFechaLista.size() > 0) {
                    ProgramacionFecha programacionFechaEncontrado = programacionFechaLista.get(0);
                    programacionFechaEncontrado.setFlgActivo(BigInteger.ZERO);
                    programacionFechaEncontrado.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    programacionFechaEncontrado.setTxtPc(Internet.obtenerNombrePC());
                    programacionFechaEncontrado.setTxtIp(Internet.obtenerIPPC());
                    programacionFechaEncontrado.setFecEdicion(new Date());
                    programacionFechaCallService.editarProgramacionFecha(programacionFechaEncontrado);
                }

                ProgramacionFecha nuevoProgramacionFecha = new ProgramacionFecha();
                nuevoProgramacionFecha.setTxtAnio(this.getEntidadSeleccionada().getTxtAnio());
                nuevoProgramacionFecha.setNumTrimestre(this.getEntidadSeleccionada().getNumTrimestre());
                nuevoProgramacionFecha.setFecInicio(this.getEntidadSeleccionada().getFecInicio());
                nuevoProgramacionFecha.setFecFin(this.getEntidadSeleccionada().getFecFin());

                if (estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob() != null) {
                    nuevoProgramacionFecha.setNidTipoActividad(estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob().toBigInteger());
                }

                nuevoProgramacionFecha.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                nuevoProgramacionFecha.setTxtPc(Internet.obtenerNombrePC());
                nuevoProgramacionFecha.setTxtIp(Internet.obtenerIPPC());
                nuevoProgramacionFecha.setFecEdicion(new Date());
                nuevoProgramacionFecha.setFlgActivo(BigInteger.ONE);
                programacionFechaCallService.crearProgramacionFecha(nuevoProgramacionFecha);

                if (null != nuevoProgramacionFecha.getNidProgramacionFecha()) {
                    // Update List Browser
                    loadProgramacionFechaList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoNuevoProgramacionFecha').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crear Programacion Fecha" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ProgramacionFechaAdministrado.crearProgramacionFecha() :: Execution finish.");
    }

    public void editarProgramacionFecha(ProgramacionFecha entidadSeleccionada) {
        logger.info(":: ProgramacionFechaAdministrado.editarProgramacionFecha() :: Starting execution...");
        if (validarFormulario(false)) {
            EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {

                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtAnio", entidadSeleccionada.getTxtAnio());
                    parameters.put("numTrimestre", entidadSeleccionada.getNumTrimestre());
                    parameters.put("nidTipoActividad", estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    List<ProgramacionFecha> programacionFechaLista = programacionFechaCallService.loadProgramacionFechaList(new FindByParamBean(parameters, "nidProgramacionFecha"));

                    if (programacionFechaLista.size() > 0) {
                        ProgramacionFecha programacionFechaEncontrado = programacionFechaLista.get(0);
                        programacionFechaEncontrado.setFlgActivo(BigInteger.ZERO);
                        programacionFechaEncontrado.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                        programacionFechaEncontrado.setTxtPc(Internet.obtenerNombrePC());
                        programacionFechaEncontrado.setTxtIp(Internet.obtenerIPPC());
                        programacionFechaEncontrado.setFecEdicion(new Date());
                        programacionFechaCallService.editarProgramacionFecha(programacionFechaEncontrado);
                    }

                    if (estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob() != null) {
                        entidadSeleccionada.setNidTipoActividad(estadoActividadGobAdministrado.getEntidadSeleccionada().getNidEstadoActividadGob().toBigInteger());
                    }
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    programacionFechaCallService.editarProgramacionFecha(entidadSeleccionada);
                    // Update List Browser
                    loadProgramacionFechaList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarProgramacionFecha').hide()");
                }
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarProgramacionFecha" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: ProgramacionFechaAdministrado.editarProgramacionFecha() :: Execution finish.");
    }

    public void anularProgramacionFecha(ProgramacionFecha entidadSeleccionada) {
        logger.info(":: ProgramacionFechaAdministrado.anularProgramacionFecha() :: Starting execution...");
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                programacionFechaCallService.editarProgramacionFecha(entidadSeleccionada);
                // Update List Browser
                loadProgramacionFechaList();
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularProgramacionFecha" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: ProgramacionFechaAdministrado.anularProgramacionFecha() :: Execution finish.");
    }

    public String getProgramacionFechaById(BigInteger nidProgramacionFecha) {
        String resultado = "";

        try {
            if (null != nidProgramacionFecha) {
                ProgramacionFecha programacionFecha = programacionFechaCallService.find(new BigDecimal(nidProgramacionFecha));

                if (null != programacionFecha) {
                    //resultado = programacionFecha.get();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener ProgramacionFecha" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarProgramacionFecha(String mode) {

        switch (mode) {
            case "all":
                EstadoActividadGobAdministrado estadoActividadGobAdministrado = (EstadoActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{estadoActividadGobAdministrado}").getValue(getFacesContext());
                ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

                estadoActividadGobAdministrado.setEntidadSeleccionada(new EstadoActividadGob());

                entidadSeleccionada = new ProgramacionFecha();
                entidadSeleccionada.setTxtAnio(actividadGobAdministrado.obtenerAnio());
                break;
            case "fechas":
                entidadSeleccionada.setFecInicio(null);
                entidadSeleccionada.setFecFin(null);
                break;
        }
    }
    
    private void loadProgramacionFechaList() {
        logger.info(":: ProgramacionFechaAdministrado.loadProgramacionFechaList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        this.programacionFechaList = programacionFechaCallService.loadProgramacionFechaList(new FindByParamBean(parameters, "nidProgramacionFecha"));
        logger.info(":: ProgramacionFechaAdministrado.loadProgramacionFechaList :: Execution finish.");
    }

    public String generarFecha(String tipo) {
        String fechaReturn = "";
        try {
            //ActividadGobAdministrado actividadGobAdministrado = (ActividadGobAdministrado) getFacesContext().getApplication().createValueBinding("#{actividadGobAdministrado}").getValue(getFacesContext());

            //String anio = String.valueOf(actividadGobAdministrado.getAnio());
            String anio = this.getEntidadSeleccionada().getTxtAnio();
            String anio2ultimos = anio.substring(2, anio.length());
            if (this.getEntidadSeleccionada().getNumTrimestre() != null) {
                switch (tipo) {
                    case "inicio":
                        if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(1))) {
                            fechaReturn = "1/1/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(2))) {
                            fechaReturn = "1/4/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(3))) {
                            fechaReturn = "1/7/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(4))) {
                            fechaReturn = "1/10/" + anio2ultimos;
                        }
                        break;
                    case "fin":
                        if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(1))) {
                            fechaReturn = "31/3/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(2))) {
                            fechaReturn = "30/6/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(3))) {
                            fechaReturn = "30/9/" + anio2ultimos;
                        } else if (this.getEntidadSeleccionada().getNumTrimestre().equals(BigInteger.valueOf(4))) {
                            fechaReturn = "31/12/" + anio2ultimos;
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
        return fechaReturn;
    }
}
