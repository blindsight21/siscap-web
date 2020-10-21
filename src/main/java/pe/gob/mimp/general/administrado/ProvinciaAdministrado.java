package pe.gob.mimp.general.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import pe.gob.mimp.core.Internet;
import pe.gob.mimp.general.modelo.Departamento;
import pe.gob.mimp.general.modelo.Provincia;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ProvinciaAdministrado extends AdministradorAbstracto implements Serializable {

    private Provincia entidad;

    private Provincia entidadSeleccionada;

    private List<Provincia> entidades;
    
     private List<Departamento> entidadesSeleccionadas;

    @Inject
    private ProvinciaCallService provinciaCallService;

    @Inject
    private DepartamentoCallService departamentoCallService;

    public ProvinciaAdministrado() {
        instanciar();

    }

    public List<Departamento> getEntidadesSeleccionadas() {
        return entidadesSeleccionadas;
    }

    public void setEntidadesSeleccionadas(List<Departamento> entidadesSeleccionadas) {
        this.entidadesSeleccionadas = entidadesSeleccionadas;
    }

    public void instanciar() {
        entidad = new Provincia();
        entidadSeleccionada = new Provincia();
    }

    public Provincia getEntidad() {
        return entidad;
    }

    public void setEntidad(Provincia entidad) {
        this.entidad = entidad;
    }

    public Provincia getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Provincia entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<Provincia> getEntidades() {
        ///entidades = provinciaCallService.findAll();

        return entidades;
    }

    public void setEntidades(List<Provincia> entidades) {
        this.entidades = entidades;
    }

    public Provincia getEntidad(String id) {
        return provinciaCallService.find(new BigDecimal(id));
    }

//    public void crear(Provincia entidadSeleccionada) throws UnknownHostException, Exception {
//        Provincia provincia = new Provincia();
//
//        provincia.setTxtDescripcion(entidadSeleccionada.getTxtDescripcion());
//
//        provincia.setTxtPc(Internet.obtenerNombrePC());
//        provincia.setTxtIp(Internet.obtenerIPPC());
//        provincia.setFecEdicion(new Date());
//        provincia.setFlgActivo(BigInteger.ONE);
//        provincia.setNidUsuario(BigInteger.ONE);
//
//        provinciaCallService.create(provincia);
//    }
//
//    public void editar(Provincia entidadSeleccionada) throws UnknownHostException, Exception {
//        if (null != entidadSeleccionada) {
//            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
//            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
//            entidadSeleccionada.setFecEdicion(new Date());
//
//            provinciaCallService.edit(entidadSeleccionada);
//        }
//
//        this.entidadSeleccionada = entidadSeleccionada;
//    }

    public List<Provincia> obtenerProvincias(Departamento departamento) {
        entidades = provinciaCallService.obtenerProvincias(departamento);

        return entidades;
    }
    
    public void actualizarProvincias(Departamento departamento) 
    {
        this.entidades = provinciaCallService.obtenerProvincias(departamento);
    }

    public List<Provincia> obtenerTodos(Departamento departamento) {
        entidades = provinciaCallService.obtenerProvincias(departamento);

        return entidades;
    }

//    public List<String> obtenerDescripcionTodos(String query) {
//        List<String> provincias = new ArrayList<String>();
//
//        DepartamentoAdministrado departamentoAdministrado = (DepartamentoAdministrado) getFacesContext().getApplication().createValueBinding("#{departamentoAdministrado}").getValue(getFacesContext());
//        String nombreDepartamento = departamentoAdministrado.getEntidadSeleccionada().getTxtDescripcion();
//
//        try {
//            if (null != nombreDepartamento && 0 < nombreDepartamento.trim().length()) {
//                List<Departamento> departamentos = fachadaDepartamento.findAllByField("txtDescripcion", nombreDepartamento);
//
//                if (null != departamentos && 0 < departamentos.size()) {
//                    entidades = provinciaCallService.obtenerPorAproximacion(departamentos.get(0), "txtDescripcion", query);
//
//                    if (null != entidades && 0 < entidades.size()) {
//                        for (Provincia provincia : entidades) {
//                            provincias.add(provincia.getTxtDescripcion());
//                        }
//                    } else {
//                        adicionarMensajeError("obtenerDescripcionTodos", "No se pudieron obtener provincias");
//                    }
//                } else {
//                    adicionarMensajeError("Obtener Provincias", "No pudo encontrar al departamento " + nombreDepartamento);
//                }
//            } else {
//                adicionarMensajeError("Obtener Provincias", "No se encontro un nombre de departamento valido");
//            }
//        } catch (Exception e) {
//            adicionarMensajeError("obtenerDescripcionTodos", e.getMessage());
//        }
//
//        return provincias;
//    }
    
//     public List<String> obtenerDescripcion(String query) {
//        List<String> Provincias = new ArrayList<String>();
//
//        try 
//        {
//            if (0 < query.trim().length()) 
//            {
//                String NewQuery = query.toUpperCase();
//                entidades = provinciaCallService.obtenerPorAproximacion("txtDescripcion", NewQuery);
//            } 
//            else 
//            {
//                entidades = null;
//            }
//            
//            if(0 < entidades.size())
//            {
//                for(Provincia provincia: entidades)
//                {
//                    Provincias.add(provincia.getTxtDescripcion());
//                }
//            }
//            else
//            {
//                adicionarMensajeError("obtenerDescripcion", "No se pudieron obtener las procincias");
//            }
//        } 
//        catch (Exception e) 
//        {
//            adicionarMensajeError("obtenerDescripcion", e.getMessage());
//        }
//
//        return Provincias;
//    }

//    public List<Provincia> obtenerTodosString(String departamento) {
//        List<Departamento> departamentos = fachadaDepartamento.findAllByField("txtDescripcion", departamento);
//        if (null != departamentos && 0 < departamentos.size()) {
//            entidades = provinciaCallService.obtenerProvincias(departamentos.get(0));
//        }
//
//        return entidades;
//    }
    
//    public Provincia obtenerProvinciaSegunUbigeo(String ubigeo)
//     {
//         Provincia provinciaEncontrado = null;
//         if(null != ubigeo && 4 == ubigeo.length())
//         {
//             try 
//             {
//                 List<Provincia> provinciaEncontrados = provinciaCallService.findAllByField("cidProvincia", ubigeo);
//                 if (null != provinciaEncontrados && 0 < provinciaEncontrados.size()) 
//                 {
//                     provinciaEncontrado = provinciaEncontrados.get(0);
//                 } 
//                 else 
//                 {
//                     System.out.println("No se encontro el ubigeo de la Provincia: " + ubigeo);
//                 }
//             } catch (Exception e) {
//                 System.out.println("obtenerProvinciaSegunUbigeo: " + e.getMessage());
//             }  
//         }
//         else
//         {
//
//         }
//         
//                     
//         return provinciaEncontrado;
//     }
    
    public List<Provincia> obtenerActivos()
    {
        return provinciaCallService.obtenerActivos();
    }
    
//    public List<Provincia> obtenerTodos()
//    {
//        return provinciaCallService.findAll();
//    }
    
//    public Provincia obtenerPorNombre(Departamento departamento, String nombre)
//    {
//        Provincia provinciaEncontrada = null;
//                
//        try
//        {
//            List<Provincia> provinciasEncontradas = provinciaCallService.obtenerProvinciasPorNombre(departamento, nombre.toUpperCase());
//            
//            if(null != provinciasEncontradas && 0 < provinciasEncontradas.size())
//            {
//                provinciaEncontrada = provinciasEncontradas.get(0);
//            }
//        }
//        catch(Exception e)
//        {
//            adicionarMensajeError("Obtener por nombre", e);
//        }
//        
//        return provinciaEncontrada;
//    }
    
//    public Provincia obtener(Departamento departamento, String nombre)
//    {
//        Provincia provincia = null;
//        
//        try
//        {
//            List<Provincia> encontrados = provinciaCallService.obtenerProvinciasPorNombre(departamento, nombre);
//            
//            if(null != encontrados && 0 < encontrados.size())
//            {
//                provincia = encontrados.get(0);
//            }
//        }
//        catch(Exception e)
//        {
//            
//        }
//        
//        return provincia;
//    }
}
