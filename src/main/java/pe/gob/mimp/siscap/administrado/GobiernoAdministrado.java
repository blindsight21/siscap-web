package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.modelo.Departamento;
import pe.gob.mimp.general.modelo.Distrito;
import pe.gob.mimp.general.modelo.Provincia;
import pe.gob.mimp.siscap.modelo.Gobierno;
import pe.gob.mimp.siscap.modelo.TipoGobierno;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.distrito.cliente.DistritoCallService;
import pe.gob.mimp.siscap.ws.gobierno.cliente.GobiernoCallService;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipogobierno.cliente.TipoGobiernoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.utils.Funciones;

@ManagedBean
@ViewScoped
public class GobiernoAdministrado extends AdministradorAbstracto implements Serializable {

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
    @Inject
    private RendimientoCallService rendimientoCallService;

    private Logger logger = Logger.getLogger(Gobierno.class.getName());
    private static final long serialVersionUID = 1L;

    private Gobierno entidadSeleccionada;
    private List<Gobierno> gobiernoList;

    List<Departamento> departamentoList;
    List<Provincia> provinciaList;
    List<Distrito> distritoList;
    List<TipoGobierno> tipoGobiernoList;

    @Inject
    private GobiernoCallService gobiernoCallService;
    @Inject
    private DepartamentoCallService departamentoCallService;
    @Inject
    private ProvinciaCallService provinciaCallService;
    @Inject
    private DistritoCallService distritoCallService;
    @Inject
    private TipoGobiernoCallService tipoGobiernoCallService;

    public GobiernoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        logger.info(":: GobiernoAdministrado.initBean :: Starting execution...");

        long startTime = System.currentTimeMillis();
        try {
            this.entidadSeleccionada = new Gobierno();
            this.entidadSeleccionada.setNidTipoGobierno(new TipoGobierno());
            loadGobiernoList();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidTipoGobierno");
            this.tipoGobiernoList = tipoGobiernoCallService.loadTipoGobiernoList(findByParamBean);
            this.departamentoList = departamentoCallService.obtenerActivos();

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error method initBean" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: GobiernoAdministrado.initBean :: Execution finish.");
    }

    public Gobierno getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Gobierno entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<Gobierno> getGobiernoList() {
        return gobiernoList;
    }

    public void setGobiernoList(List<Gobierno> gobiernoList) {
        this.gobiernoList = gobiernoList;
    }

    public List<Departamento> getDepartamentoList() {
        return departamentoList;
    }

    public void setDepartamentoList(List<Departamento> departamentoList) {
        this.departamentoList = departamentoList;
    }

    public List<Provincia> getProvinciaList() {
        return provinciaList;
    }

    public void setProvinciaList(List<Provincia> provinciaList) {
        this.provinciaList = provinciaList;
    }

    public List<Distrito> getDistritoList() {
        return distritoList;
    }

    public void setDistritoList(List<Distrito> distritoList) {
        this.distritoList = distritoList;
    }

    public List<TipoGobierno> getTipoGobiernoList() {
        return tipoGobiernoList;
    }

    public void setTipoGobiernoList(List<TipoGobierno> tipoGobiernoList) {
        this.tipoGobiernoList = tipoGobiernoList;
    }

    public Gobierno getEntidad(String id) {
        long startTime = System.currentTimeMillis();
        Gobierno gobierno = null;

        try {

            if ((null != id) || (false == id.equals(""))) {
                gobierno = gobiernoCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );

        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Gobierno" + e.getMessage(), Util.tiempo());
        }
        return gobierno;
    }

    public List<Gobierno> obtenerActivo() {

        List<Gobierno> gobiernos = null;

        //gobiernos = gobiernoCallService.obtenerGobiernoActivos();
        return gobiernos;
    }

    public List<Gobierno> obtenerTodosporTipoSede(TipoGobierno tipoGobierno) {

        List<Gobierno> gobiernoSede = null;

        if (null != tipoGobierno) {
            //gobiernoSede = gobiernoCallService.obtenerGobiernosporTipo(tipoGobierno);
        }

        return gobiernoSede;
    }

    public void obtenerProvinciaByDepartamentoId(AjaxBehaviorEvent e) throws Exception {
        logger.info(":: GobiernoAdministrado.obtenerProvinciaByDepartamentoId() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        this.entidadSeleccionada.setNidProvincia(null);
        this.provinciaList = null;
        this.entidadSeleccionada.setNidDistrito(null);
        this.distritoList = null;
        loadProvinciaList();
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: GobiernoAdministrado.obtenerProvinciaByDepartamentoId() :: Execution finish.");
    }

    public void obtenerDistritoByProvinciaId(AjaxBehaviorEvent e) throws Exception {
        logger.info(":: BeneficenciaMB.obtenerDistritoByProvinciaId :: Starting execution...");
        long startTime = System.currentTimeMillis();
        this.entidadSeleccionada.setNidDistrito(null);
        this.distritoList = null;
        loadDistritoList();
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: BeneficenciaMB.obtenerDistritoByProvinciaId :: Execution finish.");
    }

    private void loadProvinciaList() throws Exception {
        long startTime = System.currentTimeMillis();
        BigDecimal departamentoId = this.entidadSeleccionada.getNidDepartamento();
        if (!Funciones.esVacio(departamentoId)) {
            Departamento departamento = departamentoCallService.find(departamentoId);
            if (!Funciones.esVacio(departamento)) {
                this.provinciaList = provinciaCallService.obtenerProvincias(departamento);
            }
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    private void loadDistritoList() throws Exception {
        long startTime = System.currentTimeMillis();
        BigDecimal provinciaId = this.entidadSeleccionada.getNidProvincia();
        if (!Funciones.esVacio(provinciaId)) {
            Provincia provincia = provinciaCallService.find(provinciaId);
            if (!Funciones.esVacio(provincia)) {
                this.distritoList = distritoCallService.obtenerDistritos(provincia);
                logger.log(Level.INFO, " distritos {0}", this.distritoList);
            }
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    public boolean validarFormulario(boolean isNuevo) {
        long startTime = System.currentTimeMillis();
        try {
            if (Funciones.esVacio(this.entidadSeleccionada.getTxtGobierno().toUpperCase())) {
                return enviarWarnMessage("Ingrese el Descripción.");
            } else {
                if (isNuevo) {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("txtGobierno", this.getEntidadSeleccionada().getTxtGobierno().toUpperCase());
                    parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);
                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    int count = gobiernoCallService.getRecordCount(findByParamBean);
                    logger.log(Level.INFO, "Gobierno encontrados: {0}", count);

                    if (count > 0) {
                        return enviarWarnMessage("Gobierno: Ya Existe Registro");
                    }
                }
            }
            if (Funciones.esVacio(this.entidadSeleccionada.getNidTipoGobierno()) || Funciones.esVacio(this.entidadSeleccionada.getNidTipoGobierno().getNidTipoGobierno())) {
                return enviarWarnMessage("Seleccione el Tipo Gobierno");
            }
            if (Funciones.esVacio(this.entidadSeleccionada.getNidDepartamento())) {
                return enviarWarnMessage("Seleccione el Departamento");
            }
            if (Funciones.esVacio(this.entidadSeleccionada.getNidProvincia())) {
                return enviarWarnMessage("Seleccione el Provincia");
            }
            if (Funciones.esVacio(this.entidadSeleccionada.getNidDistrito())) {
                return enviarWarnMessage("Seleccione el Distrito");
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
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

    public void crearGobierno() {
        logger.info(":: GobiernoAdministrado.crearGobierno() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(true)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

                Gobierno gobierno = new Gobierno();
                gobierno.setTxtGobierno(this.getEntidadSeleccionada().getTxtGobierno().toUpperCase());
                gobierno.setNidTipoGobierno(this.getEntidadSeleccionada().getNidTipoGobierno());
                gobierno.setNidDistrito(this.getEntidadSeleccionada().getNidDistrito());
                gobierno.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                gobierno.setTxtPc(Internet.obtenerNombrePC());
                gobierno.setTxtIp(Internet.obtenerIPPC());
                gobierno.setFecEdicion(new Date());
                gobierno.setFlgActivo(BigInteger.ONE);
                gobiernoCallService.crearGobierno(gobierno);

//                if (null != gobierno.getNidGobierno()) {
                // Update List Browser
                loadGobiernoList();
                RequestContext.getCurrentInstance().execute("PF('dialogoNuevoGobierno').hide()");
//                }

                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error crearGobierno" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: GobiernoAdministrado.crearGobierno() :: Execution finish.");
    }

    public void editarGobierno(Gobierno entidadSeleccionada) {
        logger.info(":: GobiernoAdministrado.editarGobierno() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        if (validarFormulario(false)) {
            try {
                UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
                if (null != entidadSeleccionada) {
                    entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                    entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                    entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                    entidadSeleccionada.setFecEdicion(new Date());
                    gobiernoCallService.editarGobierno(entidadSeleccionada);
                    // Update List Browser
                    loadGobiernoList();
                    RequestContext.getCurrentInstance().execute("PF('dialogoEditarGobierno').hide()");
                }
                long stopTime = System.currentTimeMillis();
                rendimientoCallService.crearRendimiento(
                        SiscapWebUtil.crearRendimiento(
                                Thread.currentThread().getStackTrace()[1].getMethodName(),
                                EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                                SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                                usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
                );
            } catch (Exception e) {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error editarGobierno" + e.getMessage(), Util.tiempo());
            }
        }
        logger.info(":: GobiernoAdministrado.editarGobierno() :: Execution finish.");
    }

    public void anularGobierno(Gobierno entidadSeleccionada) {
        logger.info(":: GobiernoAdministrado.anularGobierno() :: Starting execution...");
        long startTime = System.currentTimeMillis();
        try {
            UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
            if (null != entidadSeleccionada) {
                entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
                entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
                entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
                entidadSeleccionada.setFecEdicion(new Date());
                entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
                gobiernoCallService.editarGobierno(entidadSeleccionada);
                // Update List Browser
                loadGobiernoList();
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error anularGobierno" + e.getMessage(), Util.tiempo());
        }
        logger.info(":: GobiernoAdministrado.editarGobierno() :: Execution finish.");
    }

    public String obtenerGobiernoporId(BigInteger nidGobierno) {
        String resultado = "";

        long startTime = System.currentTimeMillis();
        try {
            if (null != nidGobierno) {
                Gobierno gobierno = gobiernoCallService.find(new BigDecimal(nidGobierno));

                if (null != gobierno) {
                    resultado = gobierno.getTxtGobierno();
                }
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Gobierno" + e.getMessage(), Util.tiempo());
        }
        return resultado;
    }

    public void limpiarGobierno() {
        logger.info(":: GobiernoAdministrado.limpiarGobierno :: Starting execution...");
        this.entidadSeleccionada = new Gobierno();
        this.entidadSeleccionada.setNidTipoGobierno(new TipoGobierno());
        logger.info(":: GobiernoAdministrado.limpiarGobierno :: Execution finish.");
    }

    private void loadGobiernoList() throws Exception {
        logger.info(":: GobiernoAdministrado.loadFuncionList :: Starting execution...");
        long startTime = System.currentTimeMillis();
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidGobierno");
        this.gobiernoList = gobiernoCallService.loadGobiernoList(findByParamBean);
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: GobiernoAdministrado.loadFuncionList :: Execution finish.");
    }
}
