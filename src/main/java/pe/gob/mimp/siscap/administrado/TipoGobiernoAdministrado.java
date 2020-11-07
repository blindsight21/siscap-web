package pe.gob.mimp.siscap.administrado;

import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.UnknownHostException;
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
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.siscap.modelo.TipoGobierno;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.rendimiento.cliente.RendimientoCallService;
import pe.gob.mimp.siscap.ws.tipogobierno.cliente.TipoGobiernoCallService;
import pe.gob.mimp.util.EnumFuncionalidad;

@ManagedBean
@ViewScoped
public class TipoGobiernoAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoGobierno.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoGobierno entidadSeleccionada;
    private List<TipoGobierno> tipoGobiernoList;

    @Inject
    private TipoGobiernoCallService tipoGobiernoCallService;

    @Inject
    private RendimientoCallService rendimientoCallService;
    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

    public TipoGobiernoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        try {
            long startTime = System.currentTimeMillis();

            this.entidadSeleccionada = new TipoGobierno();
            loadTipoGobiernoList();

            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
        }
    }

    public TipoGobierno getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(TipoGobierno entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<TipoGobierno> getTipoGobiernoList() {
        return tipoGobiernoList;
    }

    public void setTipoGobiernoList(List<TipoGobierno> tipoGobiernoList) {
        this.tipoGobiernoList = tipoGobiernoList;
    }

    public TipoGobierno getEntidad(String id) {
        long startTime = System.currentTimeMillis();
        TipoGobierno tipoGobierno = null;

        try {
            if ((null != id) || (false == id.equals(""))) {
                tipoGobierno = tipoGobiernoCallService.find(new BigDecimal(id));
            }
            long stopTime = System.currentTimeMillis();
            rendimientoCallService.crearRendimiento(
                    SiscapWebUtil.crearRendimiento(
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                            SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                            usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
            );
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "Error getEntidad" + e.getMessage(), Util.tiempo());
        }
        return tipoGobierno;
    }

    public List<TipoGobierno> obtenerTodos() {

        List<TipoGobierno> gobiernos = null;
        //gobiernos = tipoGobiernoCallService.obtenerTipoGobiernoTodos();

        return gobiernos;
    }

    public List<TipoGobierno> obtenerActivo() {

        List<TipoGobierno> gobiernosActivo = null;
        //gobiernosActivo = tipoGobiernoCallService.obtenerTipoGobiernoActivos();

        return gobiernosActivo;
    }

    public List<TipoGobierno> obtenerTipoGobiernos() {
        //List<TipoGobierno> tipoGobierno = tipoGobiernoCallService.obtenerTipoGobiernoActivos();

        return null;
    }

    public void crearTipoGobierno() throws UnknownHostException, Exception {
        long startTime = System.currentTimeMillis();
        TipoGobierno tipoGobierno = new TipoGobierno();

        tipoGobierno.setTxtTipoGobierno(this.entidadSeleccionada.getTxtTipoGobierno());
        tipoGobierno.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
        tipoGobierno.setTxtPc(Internet.obtenerNombrePC());
        tipoGobierno.setTxtIp(Internet.obtenerIPPC());
        tipoGobierno.setFecEdicion(new Date());
        tipoGobierno.setFlgActivo(BigInteger.ONE);

        tipoGobiernoCallService.crearTipoGobierno(tipoGobierno);

        loadTipoGobiernoList();
        RequestContext.getCurrentInstance().execute("PF('dialogoNuevoTipoGobierno').hide()");

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    public void editarTipoGobierno(TipoGobierno entidadSeleccionada) throws UnknownHostException, Exception {
        long startTime = System.currentTimeMillis();
        if (null != entidadSeleccionada) {
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());

            tipoGobiernoCallService.editarTipoGobierno(entidadSeleccionada);

            loadTipoGobiernoList();
            RequestContext.getCurrentInstance().execute("PF('dialogoEditarTipoGobierno').hide()");
        }

        this.entidadSeleccionada = entidadSeleccionada;

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    public void anularTipoGobierno(TipoGobierno entidadSeleccionada) throws UnknownHostException, Exception {
        long startTime = System.currentTimeMillis();
        if (null != entidadSeleccionada) {
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());

            tipoGobiernoCallService.editarTipoGobierno(entidadSeleccionada);

            // Update List Browser
            loadTipoGobiernoList();
        }
        this.entidadSeleccionada = entidadSeleccionada;

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
    }

    public String obtenerTipoGobiernoporId(BigInteger nidTipoGobierno) throws Exception {
        String resultado = "";
        long startTime = System.currentTimeMillis();
        try {
            if (null != nidTipoGobierno) {
                TipoGobierno tipoGobierno = tipoGobiernoCallService.find(new BigDecimal(nidTipoGobierno));

                if (null != tipoGobierno) {
                    resultado = tipoGobierno.getTxtTipoGobierno();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}Obtener Gobierno" + e.getMessage(), Util.tiempo());
        }
        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        return resultado;
    }

    public void limpiarTipoGobierno() {
        entidadSeleccionada = new TipoGobierno();
    }

    private void loadTipoGobiernoList() throws Exception {
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Starting execution...");
        long startTime = System.currentTimeMillis();

        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoGobierno");
        this.tipoGobiernoList = tipoGobiernoCallService.loadTipoGobiernoList(findByParamBean);

        long stopTime = System.currentTimeMillis();
        rendimientoCallService.crearRendimiento(
                SiscapWebUtil.crearRendimiento(
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        EnumFuncionalidad.TIPO_GOBIERNO.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerTiempoEjecucionMillis(startTime, stopTime),
                        usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger())
        );
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Execution finish.");
    }

}
