/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.seguridad.administrado;

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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.bean.UsuarioModuloBean;
import pe.gob.mimp.bean.ValidarBean;
import pe.gob.mimp.constant.CoreConstant;
import pe.gob.mimp.core.Encriptador;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.seguridad.bean.entidades.ModuloBean;
import pe.gob.mimp.seguridad.bean.entidades.UsuarioBean;
import pe.gob.mimp.seguridad.converter.ModuloCast;
import pe.gob.mimp.seguridad.converter.UsuarioCast;
import pe.gob.mimp.seguridad.modelo.Funcionalidad;
import pe.gob.mimp.seguridad.modelo.Modulo;
import pe.gob.mimp.seguridad.modelo.ParametroSeguridad;
import pe.gob.mimp.seguridad.modelo.Perfil;
import pe.gob.mimp.seguridad.modelo.Persona;
import pe.gob.mimp.seguridad.modelo.Usuario;
import pe.gob.mimp.seguridad.modelo.UsuarioBeneficencia;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.ws.modulo.cliente.ModuloCallService;
import pe.gob.mimp.siscap.ws.parametroseguridad.cliente.ParametroSeguridadCallService;
import pe.gob.mimp.siscap.ws.perfil.cliente.PerfilCallService;
import pe.gob.mimp.siscap.ws.usuario.cliente.UsuarioCallService;
import pe.gob.mimp.siscap.ws.usuariobeneficencia.cliente.UsuarioBeneficenciaCallService;
import pe.gob.mimp.utils.Funciones;

/**
 *
 * @author Omar
 */
/**
 *
 * @author Ing. Oscar Mateo
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
/**
 * Controlador para la entidad Usuario
 */
public class UsuarioAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private UsuarioCallService usuarioCallService;

    @Inject
    private ModuloCallService moduloCallService;

    @Inject
    private ParametroSeguridadCallService parametroSeguridadCallService;

    @Inject
    private PerfilCallService perfilCallService;
//
//    @Inject
//    private PersonaFacadeLocal fachadaPersona;
//
//    @Inject
//    private EstadoUsuarioFacadeLocal fachadaEstadoUsuario;
//
//    @Inject
//    private DireccionPersonaFacadeLocal fachadaDireccionPersona;
//
//    @Inject
//    private CorreoPersonaFacadeLocal fachadaCorreoPersona;
//
//    @Inject
//    private TelefonoPersonaFacadeLocal fachadaTelefonoPersona;
//
//    @Inject
//    private TipoPersonaFacadeLocal fachadaTipoPersona;
//
//    @Inject
//    private PerfilUsuarioFacadeLocal fachadaPerfilUsuario;
//
    @Inject
    private UsuarioBeneficenciaCallService usuarioBeneficenciaCallService;

    private Usuario entidad; /// < usuario
    private MenuModel menuBar; /// < menu de barra
    private List<Usuario> entidades; /// < coleccion de usuarios
    private boolean logeado = false; /// < flag que indica del estado de logueo
    private Usuario entidadSeleccionada; /// < usuario que se ha logueado
    private Usuario entidadNueva; /// < usuario nuevo
    private List<Funcionalidad> funcionalidades; /// < funcionalidades asignadas
    /// al usuario

    private String antiguaContrasena;
    private String nuevaContrasena;
    private boolean usuarioInterno;

    public String getAntiguaContrasena() {
        return antiguaContrasena;
    }

    public void setAntiguaContrasena(String antiguaContrasena) {
        this.antiguaContrasena = antiguaContrasena;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }

    public boolean isUsuarioInterno() {
        return usuarioInterno;
    }

    public void setUsuarioInterno(boolean usuarioInterno) {
        this.usuarioInterno = usuarioInterno;
    }

    /**
     * Constructor que inicializa objetos.
     */
    public UsuarioAdministrado() {
        instanciar();
    }

    /**
     * Getter de entidad
     *
     * @return Usuario entidad devuelta.
     */
    public Usuario getEntidad() {
        return entidad;
    }

    public void instanciar() {
        entidad = new Usuario();
        entidadNueva = new Usuario();
        entidadSeleccionada = new Usuario();
        funcionalidades = new ArrayList<Funcionalidad>();

        this.entidad.setTxtUsuario("");
        this.entidadSeleccionada.setTxtUsuario("");
        this.entidad.setTxtPassword("");
        this.entidadSeleccionada.setTxtPassword("");

    }

    /**
     * Setter de entidad
     *
     * @param entidad Usuario a almacenar.
     */
    public void setEntidad(Usuario entidad) {
        this.entidad = entidad;
    }

    /**
     * Getter de entidadSeleccionada
     *
     * @return Usuario entidad devuelta.
     */
    public Usuario getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    /**
     * Setter de entidadSeleccionada
     *
     * @param entidadSeleccionada Usuario a almacenar.
     */
    public void setEntidadSeleccionada(Usuario entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    /**
     * Getter de entidadNueva
     *
     * @return Usuario entidad devuelta.
     */
    public Usuario getEntidadNueva() {
        return entidadNueva;
    }

    /**
     * Setter de entidadNueva
     *
     * @param entidadNueva Usuario a almacenar.
     */
    public void setEntidadNueva(Usuario entidadNueva) {
        this.entidadNueva = entidadNueva;
    }

    /**
     * Getter de entidades
     *
     * @return List<Usuario> entidades devueltas.
     */
    public List<Usuario> getEntidades() {
        entidades = usuarioCallService.findAll();

        return entidades;
    }

    /**
     * Setter de entidades
     *
     * @param entidades Usuarios a almacenar.
     */
    public void setEntidades(List<Usuario> entidades) {
        this.entidades = entidades;
    }

    /**
     * Busca un Usuario por identificador
     *
     * @param id identificador buscado
     * @return Usuario entidad encontrada.
     */
    public Usuario getEntidad(String id) {
        Usuario usuario = null;

        if ((null != id) && (false == id.equals(""))) {
            usuario = usuarioCallService.find(new BigDecimal(id));
        }

        return usuario;
    }

    /**
     * Busca los usuarios del modulo actual
     *
     * @return List<Usuario> entidades encontradas.
     */
    public List<Usuario> getEntidadesModulo() {
        Modulo objetoModulo = moduloCallService.find(
                new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("Modulo")));

        if (null != objetoModulo) {
            entidades = usuarioCallService.getEntidadesModulo(objetoModulo);
        } else {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
            // "{0}No se pudo obtener un modulo valido", Util.tiempo());
        }

        return entidades;
    }

    /**
     * Obtiene el nombre de Usuario
     *
     * @param id identificador buscado
     * @return String nombre de usuario encontrado.
     */
    public String getNombre(String id) {
        String nombre = "";

        if ((null != id) && (false == id.equals(""))) {
            Usuario usuario = usuarioCallService.find(new BigDecimal(id));

            if (null != usuario) {
                nombre = usuario.getTxtUsuario();
            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}No se pudo obtener ningun usuario", Util.tiempo());
            }
        } else {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
            // "{0}Se recibio un id de usuario nulo o vacio", Util.tiempo());
        }

        return nombre;
    }

    /**
     * Obtiene el nombre completo de un Usuario
     *
     * @param persona persona buscada.
     * @return String nombre completo de la persona encontrada.
     */
    public String getNombrePersona(Persona persona) {
        String nombre = "";

        if (null != persona) {
            if (null != persona.getTxtApellidoPaterno()) {
                nombre += persona.getTxtApellidoPaterno();
            }
            if (null != persona.getTxtApellidoMaterno()) {
                nombre += " " + persona.getTxtApellidoMaterno();
            }
            if (null != persona.getTxtNombres()) {
                nombre += " " + persona.getTxtNombres();
            }
        } else {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
            // "{0}Se recibio una persona nula o vacia", Util.tiempo());
        }

        return nombre;
    }

    /**
     * Obtiene el nombre completo de un Usuario.
     *
     * @param usuario usuario a ser analizado.
     * @return boolean indica si el usuario logeado es ADMINISTRADOR.
     * @throws java.lang.Exception
     */
    public boolean usuarioActivoEsAdministrador(Usuario usuario) throws Exception {
        boolean esAdministrador = false;
        try {
            if (null != this.entidad) {
                Modulo modulo = obtenerModulo();
                UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();

                ModuloBean moduloBean = ModuloCast.castModuloToModuloBean(modulo);
                UsuarioBean usuarioBean = UsuarioCast.castUsuarioToUsuarioBean(usuario);

                usuarioModuloBean.setModuloBean(moduloBean);
                usuarioModuloBean.setUsuarioBean(usuarioBean);

                List<Perfil> perfiles = perfilCallService.obtenerPerfiles(usuarioModuloBean);

                if ((null != perfiles) && (0 < perfiles.size())) {
                    for (Perfil perfil : perfiles) {
                        if ((true == perfil.getTxtPerfil().equals("ADMINISTRADOR"))
                                && (0 == modulo.getNidModulo().compareTo(perfil.getModulo().getNidModulo()))) {
                            esAdministrador = true;
                            break;
                        }
                    }
                } else {
                    // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                    // "{0}No se pudo obtener ningun perfil asociado al usuario
                    // : " + usuario.getTxtUsuario(), Util.tiempo());
                }
            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}Se recibio una persona nula o vacia", Util.tiempo());
            }
        } catch (Exception e) {
            throw (e);
        }

        return esAdministrador;
    }

    public String encriptar(String cadena) throws Exception {
        String encriptado = null;

        try {

            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("cidParametro");
            findAllByFieldBean.setValue("LLAVE");
            List<ParametroSeguridad> parametros = parametroSeguridadCallService.findAllByField(findAllByFieldBean);

            if ((null != parametros) && (0 < parametros.size())) {
                String llave = parametros.get(0).getTxtValor();

                Encriptador encriptador = new Encriptador(llave);
                encriptado = encriptador.encrypt(cadena);

                logger.info("value encriptado:" + encriptado);

            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}No se encontro el parametro LLAVE", Util.tiempo());
            }
        } catch (Exception e) {
            throw (e);
        }

        return encriptado;
    }

    public String desencriptar(String cadena) throws Exception {
        String desencriptado = null;

        try {
            FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
            findAllByFieldBean.setField("cidParametro");
            findAllByFieldBean.setValue(cadena);
            List<ParametroSeguridad> parametros = parametroSeguridadCallService.findAllByField(findAllByFieldBean);

            if ((null != parametros) && (0 < parametros.size())) {
                String llave = parametros.get(0).getTxtValor();

                Encriptador encriptador = new Encriptador(llave);
                desencriptado = encriptador.decrypt(cadena);

            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}No se encontro el parametro LLAVE", Util.tiempo());
            }
        } catch (Exception e) {
            throw (e);
        }

        return desencriptado;
    }

    private Map<String, Object> validar(String usuario, String password, boolean acceso) throws Exception {
        Map<String, Object> returns = new HashMap<String, Object>();
        Boolean isValide = false;
        String message = "";
        String encriptado = null;
        Modulo modulo = null;

        try {
            modulo = obtenerModulo();
            encriptado = encriptar(password);

            if (null != modulo && null != encriptado) {
                ValidarBean validarBean = new ValidarBean();
                validarBean.setModuloBean(ModuloCast.castModuloToModuloBean(modulo));
                validarBean.setUsuario(usuario);
                validarBean.setPassword(encriptado);
                List<Usuario> usuarios = usuarioCallService.validar(validarBean);
                logger.info(" validando usuarios ->" + usuarios);
                if (null != usuarios && 0 < usuarios.size()) {
                    boolean associateModule = validateAsociateUserWithModule(usuarios.get(0), acceso, modulo);
                    if (associateModule) {
                        isValide = true;
                        entidadSeleccionada = usuarios.get(0);

                        UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();

                        ModuloBean moduloBean = ModuloCast.castModuloToModuloBean(modulo);
                        UsuarioBean usuarioBean = UsuarioCast.castUsuarioToUsuarioBean(entidadSeleccionada);

                        usuarioModuloBean.setModuloBean(moduloBean);
                        usuarioModuloBean.setUsuarioBean(usuarioBean);

                        setFuncionalidades(usuarioCallService.obtenerFuncionalidades(usuarioModuloBean));
                    } else {
                        message = "El usuario no se encuentra asociado al Modulo / Funcionalidades. Comuniquese con el Administrador del Sistema MIMP.";
                    }
                } else {
                    message = "No se pudo obtener un usuario valido";
                    // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                    // "{0}No se pudo obtener un usuario valido",
                    // Util.tiempo());
                }
            } else {
                message = "No se obtuvo el modulo o no se pudo encriptar la clave";
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}No se obtuvo el modulo o no se pudo encriptar la clave",
                // Util.tiempo());
            }
        } catch (Exception e) {
            returns.put("isValide", isValide);
            returns.put("message", message);
            throw (e);
        }
        returns.put("isValide", isValide);
        returns.put("message", message);
        return returns;
    }

    /**
     * Realiza la validacion de un usuario en un modulo
     *
     * @param usuario usuario que se desea validar.
     * @param password clave que se desea validar.
     * @return boolean flag que indica si se supero o no la validacion.
     */
    public Map<String, Object> validar(String usuario, String password) throws Exception {
        return validar(usuario, password, false);
    }

    private boolean validateAsociateUserWithModule(Usuario usuario, boolean acceso, Modulo modulo) {
        logger.info("UsuarioAdministrado.validateAsociateUserWithModule :: Starting execution..." + usuario);
        boolean retorna = true;
        getUserInformation().put(CoreConstant.IS_ASSOCIATE_ENTITY, false);
        getUserInformation().put(CoreConstant.IS_ADMINISTRATOR, false);
        try {
            logger.info("Module -> " + modulo);
            logger.info("Module TxtNombreTecnico -> " + modulo.getTxtNombreTecnico());
            if (!Funciones.esVacio(modulo) && CoreConstant.MODULO_BENEFICENCIA.equals(modulo.getTxtNombreTecnico())) {
                logger.info("es administrador: " + retorna + "; acceso:" + acceso);
                retorna = usuarioActivoEsAdministrador(usuario);
                getUserInformation().put(CoreConstant.IS_ADMINISTRATOR, retorna);
                if (!retorna && acceso) {
                    Long usuarioId = usuario.getNidUsuario().longValue();
                    retorna = false;
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("activo", CoreConstant.STATUS_ACTIVE);
                    parameters.put("usuarioId", usuarioId);

                    FindByParamBean findByParamBean = new FindByParamBean();
                    findByParamBean.setParameters(parameters);
                    findByParamBean.setOrderBy("fechaRegistro");
                    List<UsuarioBeneficencia> usuarioBeneficenciaList = usuarioBeneficenciaCallService.findByParams(findByParamBean);
                    if (!Funciones.esVacio(usuarioBeneficenciaList)) {
                        retorna = true;
                        getUserInformation().put(CoreConstant.IS_ASSOCIATE_ENTITY, true);
                        getUserInformation().put(CoreConstant.ENTITY_ASSOCIATE_ID, usuarioBeneficenciaList.get(0).getBeneficenciaId());
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioAdministrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.info("UsuarioAdministrado.validateAsociateUserWithModule :: Execution finish. " + retorna);
        return retorna;
    }

    public void loginApp(boolean accesoInstitucion) throws Exception {
        logger.info("UsuarioAdministrado.loginApp :: Starting Execution...");
        processLogin(accesoInstitucion);
        logger.info("UsuarioAdministrado.loginApp :: Execution finish.");
    }

    /**
     * Callback llamado por la vista de login
     *
     * @param event evento disparador.
     * @throws java.lang.Exception
     */
    public void login(ActionEvent event) throws Exception {
        processLogin(false);
    }

    private void processLogin(boolean accesoInstitucion) throws Exception {
        logger.info(":: UsuarioAdministrado.login :: Starting execution...");
        PerfilAdministrado perfilAdministrado = (PerfilAdministrado) getFacesContext().getApplication()
                .createValueBinding("#{perfilAdministrado}").getValue(getFacesContext());
        RequestContext context = RequestContext.getCurrentInstance();

        boolean loggedIn = false;

        String perfilIn = "";

        logger.info("   txtUsuario = " + entidadSeleccionada.getTxtUsuario());
        Map<String, Object> values = validar(entidadSeleccionada.getTxtUsuario(), entidadSeleccionada.getTxtPassword(), accesoInstitucion);
        boolean isValide = (Boolean) values.get("isValide");
        String message = (String) values.get("message");
        if (true == isValide) {
            loggedIn = true;
            entidad = entidadSeleccionada;
            setLogeado(true);

            Perfil perfilTemporal = perfilAdministrado.obtenerPerfilPorModulo(entidad, 1);
            perfilIn = perfilTemporal.getTxtPerfil();

        } else {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error de Autenticacion",
                    message);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
        context.addCallbackParam("loggedIn", loggedIn);
        context.addCallbackParam("perfilIn", perfilIn);
        logger.info(" loggedIn=" + loggedIn + "; perfilIn=" + perfilIn);
        logger.info(":: UsuarioAdministrado.login :: Execution finish.");
    }

    /**
     * Indica si se ha logeado o no un usuario
     *
     * @return boolean flag que indica si se ha logeado un usuario.
     */
    public boolean estaLogeado() {
        return isLogeado();
    }

    /**
     * Metodo que realiza el logout
     */
    public void logout() {
        System.out.println("UsuarioAdministrado.logout :: Starting execution...");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        setMenuBar(null);
        setEntidad(null);
        setUserInformation(null);
        setEntidadSeleccionada(null);
        System.out.println("  getEntidadSeleccionada = " + getEntidadSeleccionada());

        session.invalidate();

        setLogeado(false);
        System.out.println("UsuarioAdministrado.logout :: Execution finish");
    }

    /**
     * Indica si el estado es logeado
     *
     * @return boolean flag que indica si el estado es logeado.
     */
    public boolean isLogeado() {
        return logeado;
    }

    /**
     * Setter de logeado
     *
     * @param logeado flag a almacenar.
     */
    public void setLogeado(boolean logeado) {
        this.logeado = logeado;
    }

    /**
     * Getter de menuBar
     *
     * @return MenuBar menu de barras devuelto.
     */
    public MenuModel getMenuBar() {
        return menuBar;
    }

    /**
     * Setter de menuBar
     *
     * @param menuBar menu de barras a almacenar.
     */
    public void setMenuBar(MenuModel menuBar) {
        this.menuBar = menuBar;
    }

    /**
     * Getter de funcionalidades
     *
     * @return the funcionalidades
     */
    public List<Funcionalidad> getFuncionalidades() {
        return funcionalidades;
    }

    /**
     * Setter de funcionalidades, adicionalmente construye menuBar
     *
     * @param funcionalidades funcionalidades a almacenar.
     */
    public void setFuncionalidades(List<Funcionalidad> funcionalidades) {
        this.funcionalidades = funcionalidades;

        if ((null != this.funcionalidades) && (0 < this.funcionalidades.size())) {
            this.menuBar = new DefaultMenuModel();

            for (Funcionalidad funcionalidad : this.funcionalidades) {
                if (BigInteger.ZERO.equals(funcionalidad.getNidFuncionalidadPadre())) {
                    DefaultSubMenu menu = null;

                    menu = new DefaultSubMenu(funcionalidad.getTxtFuncionalidad());

                    if ((null != menu)) {
                        for (Funcionalidad funcionalidadItem : this.funcionalidades) {
                            DefaultMenuItem item = null;

                            if (0 == funcionalidad.getNidFuncionalidad().toBigInteger()
                                    .compareTo(funcionalidadItem.getNidFuncionalidadPadre())) {
                                item = new DefaultMenuItem(funcionalidadItem.getTxtFuncionalidad());
                                item.setUrl(funcionalidadItem.getTxtReferencia());
                                menu.addElement(item);
                            }
                        }
                    }

                    this.menuBar.addElement(menu);
                }
            }
        }
    }

    public Modulo obtenerModulo() {
        Modulo moduloEntidad = null;

        try {
            String sIdentificador = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("Modulo");

            if (null != sIdentificador) {
                BigDecimal bIdentificador = new BigDecimal(sIdentificador);

                moduloEntidad = moduloCallService.find(bIdentificador);
            } else {
                adicionarMensajeError("Obtener Modulo", "No se pudo obtener el identificador guardado");
            }
        } catch (Exception e) {
            adicionarMensajeError("Obtener Modulo", e.getMessage());
        }

        return moduloEntidad;
    }

    public List<Usuario> obtenerEntidadesModuloTodos() {

        Modulo objetoModulo = moduloCallService.find(
                new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("Modulo")));

        if (null != objetoModulo) {
            entidades = usuarioCallService.getEntidadesModuloTodos(objetoModulo);
        } else {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
            // "{0}No se pudo obtener un modulo valido", Util.tiempo());
        }

        return entidades;
    }

    public void actualizarContrasena(Usuario entidad) {
        String encriptado = null;
        try {
            if (null != entidad.getNidUsuario()) {
                if (null != nuevaContrasena) {
                    encriptado = encriptar(nuevaContrasena);

                    entidad.setTxtPassword(encriptado);

                    entidad.setNidUsuario2(this.entidadSeleccionada.getNidUsuario().toBigInteger());
                    entidad.setTxtPc(Internet.obtenerNombrePC());
                    entidad.setTxtIp(Internet.obtenerIPPC());
                    entidad.setFecEdicion(new Date());
                    entidad.setFlgActivo(BigInteger.ONE);
                    usuarioCallService.editarUsuario(entidad);
                    adicionarMensaje("Alerta", "La contraseña ha sido actualizada.");
                } else {
                    // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                    // "{0}Nueva contraseña nula", Util.tiempo());
                }
            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}Entidad seleccionada nula", Util.tiempo());
            }
        } catch (Exception e) {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE,
            // null, e);
        }
    }

    public void anular(Usuario entidad) {
        try {
            if (null != entidad.getNidUsuario()) {
                entidad.setFlgActivo(this.entidad.getFlgActivo());
                entidad.setNidUsuario2(this.entidadSeleccionada.getNidUsuario().toBigInteger());
                entidad.setTxtPc(Internet.obtenerNombrePC());
                entidad.setTxtIp(Internet.obtenerIPPC());
                entidad.setFecEdicion(new Date());
                usuarioCallService.editarUsuario(entidad);
            } else {
                // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO,
                // "{0}Entidad seleccionada nula", Util.tiempo());
            }
        } catch (Exception e) {
            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE,
            // null, e);
        }
    }

    //TODO ver despues
//    public void nuevoUsuario() {
//        PersonaAdministrado personaAdministrado = (PersonaAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{personaAdministrado}").getValue(getFacesContext());
//        CargoAdministrado cargoAdministrado = (CargoAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{cargoAdministrado}").getValue(getFacesContext());
//        TipoDocumentoAdministrado tipoDocumentoAdministrado = (TipoDocumentoAdministrado) getFacesContext()
//                .getApplication().createValueBinding("#{tipoDocumentoAdministrado}").getValue(getFacesContext());
//        AreaAdministrado areaAdministrado = (AreaAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{areaAdministrado}").getValue(getFacesContext());
//
//        DireccionPersonaAdministrado direccionPersonaAdministrado = (DireccionPersonaAdministrado) getFacesContext()
//                .getApplication().createValueBinding("#{direccionPersonaAdministrado}").getValue(getFacesContext());
//        CorreoPersonaAdministrado correoPersonaAdministrado = (CorreoPersonaAdministrado) getFacesContext()
//                .getApplication().createValueBinding("#{correoPersonaAdministrado}").getValue(getFacesContext());
//        TelefonoPersonaAdministrado telefonoPersonaAdministrado = (TelefonoPersonaAdministrado) getFacesContext()
//                .getApplication().createValueBinding("#{telefonoPersonaAdministrado}").getValue(getFacesContext());
//        TipoTelefonoAdministrado tipoTelefonoAdministrado = (TipoTelefonoAdministrado) getFacesContext()
//                .getApplication().createValueBinding("#{tipoTelefonoAdministrado}").getValue(getFacesContext());
//
//        DistritoAdministrado distritoAdministrado = (DistritoAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{distritoAdministrado}").getValue(getFacesContext());
//        LugarAdministrado lugarAdministrado = (LugarAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{lugarAdministrado}").getValue(getFacesContext());
//        ViaAdministrado viaAdministrado = (ViaAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{viaAdministrado}").getValue(getFacesContext());
//
//        PerfilAdministrado perfilAdministrado = (PerfilAdministrado) getFacesContext().getApplication()
//                .createValueBinding("#{perfilAdministrado}").getValue(getFacesContext());
//
//        try {
//            Persona persona = new Persona();
//            if (null != cargoAdministrado.getEntidadSeleccionada()) {
//                persona.setCargo(cargoAdministrado.getEntidadSeleccionada());
//            }
//
//            if (null != areaAdministrado.getEntidadSeleccionada().getNidArea()) {
//                persona.setNidArea(areaAdministrado.getEntidadSeleccionada().getNidArea().toBigInteger());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtSexo()) {
//                persona.setTxtSexo(personaAdministrado.getEntidadSeleccionada().getTxtSexo());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtApellidoPaterno()) {
//                persona.setTxtApellidoPaterno(personaAdministrado.getEntidadSeleccionada().getTxtApellidoPaterno());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtApellidoMaterno()) {
//                persona.setTxtApellidoMaterno(personaAdministrado.getEntidadSeleccionada().getTxtApellidoMaterno());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtNombres()) {
//                persona.setTxtNombres(personaAdministrado.getEntidadSeleccionada().getTxtNombres());
//            }
//
//            if (null != tipoDocumentoAdministrado.getEntidadSeleccionada()) {
//                persona.setTipoDocumento(tipoDocumentoAdministrado.getEntidadSeleccionada());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtDocumento()) {
//                persona.setTxtDocumento(personaAdministrado.getEntidadSeleccionada().getTxtDocumento());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getFecNacimiento()) {
//                persona.setFecNacimiento(personaAdministrado.getEntidadSeleccionada().getFecNacimiento());
//            }
//
//            if (null != personaAdministrado.getEntidadSeleccionada().getTxtWeb()) {
//                persona.setTxtWeb(personaAdministrado.getEntidadSeleccionada().getTxtWeb());
//            }
//
//            List<TipoPersona> tipoPersona = fachadaTipoPersona.findAllByField("txtTipoPersona", "INTERNO");
//            if (null != tipoPersona.get(0)) {
//                persona.setTipoPersona(tipoPersona.get(0));
//            }
//
//            persona.setUsuario(this.getEntidadSeleccionada());
//            persona.setFlgActivo(BigInteger.ONE);
//            persona.setTxtPc(Internet.obtenerNombrePC());
//            persona.setTxtIp(Internet.obtenerIPPC());
//            persona.setFecEdicion(new Date());
//            fachadaPersona.create(persona);
//
//            DireccionPersona direccionPersona = new DireccionPersona();
//
//            direccionPersona.setPersona(persona);
//            if (null != direccionPersonaAdministrado.getEntidadSeleccionada().getTxtDireccion()) {
//                direccionPersona
//                        .setTxtDireccion(direccionPersonaAdministrado.getEntidadSeleccionada().getTxtDireccion());
//            }
//
//            if (null != distritoAdministrado.getEntidadSeleccionada().getNidDistrito()) {
//                direccionPersona
//                        .setNidDistrito(distritoAdministrado.getEntidadSeleccionada().getNidDistrito().toBigInteger());
//            }
//
//            if (null != viaAdministrado.getEntidadSeleccionada().getNidVia()) {
//                direccionPersona.setNidVia(viaAdministrado.getEntidadSeleccionada().getNidVia().toBigInteger());
//            }
//
//            if (null != lugarAdministrado.getEntidadSeleccionada().getNidLugar()) {
//                direccionPersona.setNidLugar(lugarAdministrado.getEntidadSeleccionada().getNidLugar().toBigInteger());
//            }
//
//            if (null != lugarAdministrado.getEntidadSeleccionada().getTxtLugar()) {
//                direccionPersona.setTxtLugar(lugarAdministrado.getEntidadSeleccionada().getTxtLugar());
//            }
//
//            direccionPersona.setNidUsuario(this.getEntidadSeleccionada().getNidUsuario().toBigInteger());
//            direccionPersona.setFlgActivo(BigInteger.ONE);
//            direccionPersona.setTxtPc(Internet.obtenerNombrePC());
//            direccionPersona.setTxtIp(Internet.obtenerIPPC());
//            direccionPersona.setFecEdicion(new Date());
//            fachadaDireccionPersona.create(direccionPersona);
//
//            CorreoPersona correoPersona = new CorreoPersona();
//            correoPersona.setNidPersona(persona.getNidPersona().toBigInteger());
//
//            if (null != correoPersonaAdministrado.getEntidadSeleccionada().getTxtCorreoPersona()) {
//                correoPersona
//                        .setTxtCorreoPersona(correoPersonaAdministrado.getEntidadSeleccionada().getTxtCorreoPersona());
//            }
//
//            correoPersona.setNidUsuario(this.getEntidadSeleccionada().getNidUsuario().toBigInteger());
//            correoPersona.setFlgActivo(BigInteger.ONE);
//            correoPersona.setTxtPc(Internet.obtenerNombrePC());
//            correoPersona.setTxtIp(Internet.obtenerIPPC());
//            correoPersona.setFecEdicion(new Date());
//            fachadaCorreoPersona.create(correoPersona);
//
//            TelefonoPersona telefonoPersona = new TelefonoPersona();
//            telefonoPersona.setNidPersona(persona.getNidPersona().toBigInteger());
//
//            if (null != telefonoPersonaAdministrado.getEntidadSeleccionada().getTxtTelefonoPersona()) {
//                telefonoPersona.setTxtTelefonoPersona(
//                        telefonoPersonaAdministrado.getEntidadSeleccionada().getTxtTelefonoPersona());
//            }
//
//            if (null != tipoTelefonoAdministrado.getEntidadSeleccionada()) {
//                telefonoPersona.setTipoTelefono(tipoTelefonoAdministrado.getEntidadSeleccionada());
//            }
//
//            telefonoPersona.setNidUsuario(this.getEntidadSeleccionada().getNidUsuario().toBigInteger());
//            telefonoPersona.setFlgActivo(BigInteger.ONE);
//            telefonoPersona.setTxtPc(Internet.obtenerNombrePC());
//            telefonoPersona.setTxtIp(Internet.obtenerIPPC());
//            telefonoPersona.setFecEdicion(new Date());
//            fachadaTelefonoPersona.create(telefonoPersona);
//
//            Usuario usuario = new Usuario();
//            usuario.setPersona(persona);
//
//            if (null != this.getEntidadNueva().getTxtUsuario()) {
//                usuario.setTxtUsuario(this.getEntidadNueva().getTxtUsuario());
//            }
//
//            String encriptado = encriptar(nuevaContrasena);
//            usuario.setTxtPassword(encriptado);
//
//            List<EstadoUsuario> estadousuario = fachadaEstadoUsuario.findAllByField("txtEstadoUsuario", "APROBADO");
//            if (null != estadousuario) {
//                usuario.setNidEstadoUsuario(estadousuario.get(0).getNidEstadoUsuario().toBigInteger());
//            }
//
//            usuario.setNidUsuario2(this.getEntidadSeleccionada().getNidUsuario().toBigInteger());
//            usuario.setFlgActivo(BigInteger.ONE);
//            usuario.setTxtPc(Internet.obtenerNombrePC());
//            usuario.setTxtIp(Internet.obtenerIPPC());
//            usuario.setFecEdicion(new Date());
//            usuarioCallService.crearUsuario(usuario);
//
//            PerfilUsuarioPK pk = new PerfilUsuarioPK();
//            if (null != perfilAdministrado.getEntidadSeleccionada().getNidPerfil()) {
//                pk.setNidPerfil(perfilAdministrado.getEntidadSeleccionada().getNidPerfil().toBigInteger());
//            }
//
//            pk.setNidUsuario(usuario.getNidUsuario().toBigInteger());
//
//            PerfilUsuario perfilUsuario = new PerfilUsuario();
//            perfilUsuario.setPerfilUsuarioPK(pk);
//            if (null != perfilAdministrado.getEntidadSeleccionada()) {
//                perfilUsuario.setPerfil(perfilAdministrado.getEntidadSeleccionada());
//            }
//
//            perfilUsuario.setUsuario(usuario);
//
//            perfilUsuario.setNidUsuario2(BigInteger.ONE);
//            perfilUsuario.setFlgActivo(new Short("1"));
//            perfilUsuario.setTxtPc(Internet.obtenerNombrePC());
//            perfilUsuario.setTxtIp(Internet.obtenerIPPC());
//            perfilUsuario.setFecModificacion(new Date());
//            fachadaPerfilUsuario.create(perfilUsuario);
//
//            adicionarMensaje("Información", "El usuario ha sido creado con éxito.");
//
//        } catch (Exception e) {
//            // Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE,
//            // null, e);
//        }
//    }
    public String ObtenerNombreUsuarioPorNid(BigDecimal nidUsuario) {
        String nombreUsuarioEncontrado = "";

        FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
        findAllByFieldBean.setField("nidUsuario");
        findAllByFieldBean.setValue(nidUsuario);
        List<Usuario> usuariosEncontrados = usuarioCallService.findAllByField(findAllByFieldBean);
        if (null != usuariosEncontrados && 0 < usuariosEncontrados.size()) {
            nombreUsuarioEncontrado = usuariosEncontrados.get(0).getTxtUsuario();
        }
        return nombreUsuarioEncontrado;
    }

}
