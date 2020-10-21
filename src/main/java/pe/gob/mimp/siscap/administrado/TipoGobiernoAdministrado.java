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
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.siscap.modelo.TipoGobierno;
import pe.gob.mimp.siscap.ws.tipogobierno.cliente.TipoGobiernoCallService;

@ManagedBean
@ViewScoped
public class TipoGobiernoAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoGobierno.class.getName());
    private static final long serialVersionUID = 1L;

    private TipoGobierno entidadSeleccionada;
    private List<TipoGobierno> tipoGobiernoList;

    @Inject
    private TipoGobiernoCallService tipoGobiernoCallService;

    UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());

    public TipoGobiernoAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        this.entidadSeleccionada = new TipoGobierno();
        loadTipoGobiernoList();
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
        TipoGobierno tipoGobierno = null;

        if ((null != id) || (false == id.equals(""))) {
            tipoGobierno = tipoGobiernoCallService.find(new BigDecimal(id));
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

        TipoGobierno tipoGobierno = new TipoGobierno();

        tipoGobierno.setTxtTipoGobierno(this.entidadSeleccionada.getTxtTipoGobierno());
        tipoGobierno.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());
        tipoGobierno.setTxtPc(Internet.obtenerNombrePC());
        tipoGobierno.setTxtIp(Internet.obtenerIPPC());
        tipoGobierno.setFecEdicion(new Date());
        tipoGobierno.setFlgActivo(BigInteger.ONE);

        tipoGobiernoCallService.crearTipoGobierno(tipoGobierno);
    }

    public void editarTipoGobierno(TipoGobierno entidadSeleccionada) throws UnknownHostException, Exception {
        if (null != entidadSeleccionada) {
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());

            tipoGobiernoCallService.editarTipoGobierno(entidadSeleccionada);
        }

        this.entidadSeleccionada = entidadSeleccionada;
    }

    public void anularTipoGobierno(TipoGobierno entidadSeleccionada) throws UnknownHostException, Exception {
        if (null != entidadSeleccionada) {
            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
            entidadSeleccionada.setFecEdicion(new Date());
            entidadSeleccionada.setFlgActivo(BigInteger.ZERO);
            entidadSeleccionada.setNidUsuario(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario().toBigInteger());

            tipoGobiernoCallService.editarTipoGobierno(entidadSeleccionada);
        }
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public String obtenerTipoGobiernoporId(BigInteger nidTipoGobierno) {
        String resultado = "";

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
        return resultado;
    }

    public void limpiarTipoGobierno() {
        entidadSeleccionada = new TipoGobierno();
    }

    private void loadTipoGobiernoList() {
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Starting execution...");
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("flgActivo", CoreConstant.STATUS_ACTIVE);

        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidTipoGobierno");
        this.tipoGobiernoList = tipoGobiernoCallService.loadTipoGobiernoList(findByParamBean);
        logger.info(":: TipoFuncionAdministrado.loadTipoFuncionList :: Execution finish.");
    }

}
