package pe.gob.mimp.seguridad.administrado;

import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import pe.gob.mimp.bean.UsuarioModuloBean;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.seguridad.bean.entidades.ModuloBean;
import pe.gob.mimp.seguridad.bean.entidades.UsuarioBean;
import pe.gob.mimp.seguridad.converter.ModuloCast;
import pe.gob.mimp.seguridad.converter.UsuarioCast;
import pe.gob.mimp.seguridad.modelo.Perfil;
import pe.gob.mimp.seguridad.modelo.Modulo;
import pe.gob.mimp.seguridad.modelo.PerfilUsuario;
import pe.gob.mimp.seguridad.modelo.PerfilUsuarioPK;
import pe.gob.mimp.seguridad.modelo.Usuario;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.ws.modulo.cliente.ModuloCallService;
import pe.gob.mimp.siscap.ws.perfil.cliente.PerfilCallService;
import pe.gob.mimp.siscap.ws.perfilusuario.cliente.PerfilUsuarioCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
/**
 * Controlador para la entidad Perfil
 */
public class PerfilAdministrado extends AdministradorAbstracto implements Serializable {

    private Perfil entidad;
    private List<Perfil> entidades;
    private Perfil entidadSeleccionada;

    @Inject
    private PerfilCallService perfilCallService;
    @Inject
    private ModuloCallService moduloCallService;
    @Inject
    private PerfilUsuarioCallService perfilUsuarioCallService;

    /**
     * Constructor que inicializa objetos.
     */
    public PerfilAdministrado() {
        instanciar();
    }
    public void instanciar()
    {
        entidad = new Perfil();
        entidades = new ArrayList<Perfil>();
        entidadSeleccionada = new Perfil();        
    }

    public Perfil getEntidad() {
        return entidad;
    }

    public void setEntidad(Perfil entidad) {
        this.entidad = entidad;
    }

    public Perfil getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Perfil entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<Perfil> getEntidades() {
        entidades = perfilCallService.findAll();

        return entidades;
    }

    public void setEntidades(List<Perfil> entidades) {
        this.entidades = entidades;
    }

    public Perfil getEntidad(String id) {
        Perfil persona = null;

        if ((null != id) && (false == id.equals(""))) {
            persona = perfilCallService.find(new BigDecimal(id));
        }

        return persona;
    }

    public Perfil getEntidad(BigInteger id) {
        Perfil persona = null;

        if (BigInteger.ZERO != id) {
            persona = perfilCallService.find(new BigDecimal(id));
        }

        return persona;
    }

    public void editarPerfil(Usuario usuario) 
    {
        UsuarioAdministrado usuarioAdministrado = (UsuarioAdministrado) getFacesContext().getApplication().createValueBinding("#{usuarioAdministrado}").getValue(getFacesContext());
        PerfilAdministrado perfilAdministrado = (PerfilAdministrado) getFacesContext().getApplication().createValueBinding("#{perfilAdministrado}").getValue(getFacesContext());
        
        try 
        {
            if (null != usuario.getNidUsuario()) 
            {
                Modulo modulo = obtenerModulo();
                UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();
                
                ModuloBean moduloBean = ModuloCast.castModuloToModuloBean(modulo);
                UsuarioBean usuarioBean = UsuarioCast.castUsuarioToUsuarioBean(usuarioAdministrado.getEntidad());
                
                usuarioModuloBean.setModuloBean(moduloBean);
                usuarioModuloBean.setUsuarioBean(usuarioBean);
                
                List<PerfilUsuario> perfilesUsuarioTemporales = perfilUsuarioCallService.obtenerPerfilUsuarioPorModulo(usuarioModuloBean);
                if (null != perfilesUsuarioTemporales && 0 < perfilesUsuarioTemporales.size()) 
                {
                    for (PerfilUsuario perfilusuarioTemporal : perfilesUsuarioTemporales) 
                    {
                        perfilUsuarioCallService.remove(perfilusuarioTemporal);
                    }
                    
                    PerfilUsuarioPK perfilUsuarioPK = new PerfilUsuarioPK();
                    perfilUsuarioPK.setNidPerfil(perfilAdministrado.getEntidadSeleccionada().getNidPerfil().toBigInteger());
                    perfilUsuarioPK.setNidUsuario(usuarioAdministrado.getEntidad().getNidUsuario().toBigInteger());

                    PerfilUsuario nuevoPerfilUsuario = new PerfilUsuario();
                    nuevoPerfilUsuario.setUsuario(usuarioAdministrado.getEntidad());
                    nuevoPerfilUsuario.setPerfil(perfilAdministrado.getEntidadSeleccionada());
                    nuevoPerfilUsuario.setPerfilUsuarioPK(perfilUsuarioPK);

                    nuevoPerfilUsuario.setNidUsuario2(usuarioAdministrado.getEntidadSeleccionada().getNidUsuario2());
                    nuevoPerfilUsuario.setTxtPc(Internet.obtenerNombrePC());
                    nuevoPerfilUsuario.setTxtIp(Internet.obtenerIPPC());
                    nuevoPerfilUsuario.setFecModificacion(new Date());
                    nuevoPerfilUsuario.setFlgActivo(new Short("1"));
                    perfilUsuarioCallService.crearPerfilUsuario(nuevoPerfilUsuario);
                } 
                else 
                {
                    System.out.println("editarPerfil: Se recibio un perfilUsuario nulo o vacio");
                }
            } 
            else 
            {
                System.out.println("editarPerfil: Entidad nula");
            }
        } 
        catch (Exception e) 
        {
            System.out.println("editarPerfil:" + e.getMessage());
        }
    }

    public Perfil obtenerPerfilPorModulo(Usuario usuario, int id) {
        PerfilAdministrado perfilAdministrado = (PerfilAdministrado) getFacesContext().getApplication().createValueBinding("#{perfilAdministrado}").getValue(getFacesContext());
        Perfil perfil = null;
        try {
            if (null != usuario.getNidUsuario()) {
                Modulo modulo = obtenerModulo();
                UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();
                
                ModuloBean moduloBean = ModuloCast.castModuloToModuloBean(modulo);
                UsuarioBean usuarioBean = UsuarioCast.castUsuarioToUsuarioBean(usuario);
                
                usuarioModuloBean.setModuloBean(moduloBean);
                usuarioModuloBean.setUsuarioBean(usuarioBean);
                
                List<Perfil> perfilesTemporales = perfilCallService.obtenerPerfiles(usuarioModuloBean);
                if (null != perfilesTemporales && 0 < perfilesTemporales.size()) {
                    if (id == 1) {
                        perfil = perfilesTemporales.get(0);
                    } else {
                        perfilAdministrado.setEntidadSeleccionada(perfilesTemporales.get(0));
                    }
                } else {
                    System.out.println("obtenerPerfilPorModulo: Se recibio una lista nula o vacia");
                }
            } else {
                System.out.println("obtenerPerfilPorModulo: Se recibio un usuario nulo");
            }
        } catch (Exception e) {
            System.out.println("obtenerPerfilPorModulo:" + e.getMessage());
        }
        return perfil;
    }

    public List<Perfil> obtenerPerfilesPorModulo() {
        List<Perfil> perfiles = null;
        try {
            Modulo modulo = obtenerModulo();
            if (null != modulo.getNidModulo()) {
                List<Perfil> perfilesTemporales = perfilCallService.obtenerPerfilesDelModulo(modulo);
                if (null != perfilesTemporales && 0 < perfilesTemporales.size()) {
                    perfiles = perfilesTemporales;
                } else {
                    System.out.println("ObtenerPerfilesPorModulo: Se recibio una lista nula o vacia");
                }
            } else {
                System.out.println("ObtenerPerfilesPorModulo: Se recibio un modulo nulo");
            }
        } catch (Exception e) {
            System.out.println("ObtenerPerfilesPorModulo:" + e.getMessage());
        }
        return perfiles;
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

}
