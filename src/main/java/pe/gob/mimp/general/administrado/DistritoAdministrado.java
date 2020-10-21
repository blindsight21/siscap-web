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
import pe.gob.mimp.general.modelo.Distrito;
import pe.gob.mimp.general.modelo.Provincia;
import pe.gob.mimp.siscap.ws.distrito.cliente.DistritoCallService;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class DistritoAdministrado extends AdministradorAbstracto  implements Serializable{
	
    private Distrito entidad;

    private Distrito entidadSeleccionada;

    private List<Distrito> entidades;

    private List<Distrito> entidadesSeleccionadas;
            
    @Inject
    private DistritoCallService distritoCallService;
    
    @Inject
    private ProvinciaCallService provinciaCallService;

    public DistritoAdministrado() 
    {
        instanciar();
    }

     public void instanciar()
    {
       entidad = new Distrito();
        entidadSeleccionada = new Distrito();
    }

    public List<Distrito> getEntidadesSeleccionadas() {
        return entidadesSeleccionadas;
    }

    public void setEntidadesSeleccionadas(List<Distrito> entidadesSeleccionadas) {
        this.entidadesSeleccionadas = entidadesSeleccionadas;
    }
    
     
    public Distrito getEntidad() {
        return entidad;
    }

    public void setEntidad(Distrito entidad) {
        this.entidad = entidad;
    }

    public Distrito getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Distrito selectedEntitie) {
        this.entidadSeleccionada = selectedEntitie;
    }

    public List<Distrito> getEntidades() {
        //entidades = distritoCallService.findAll();

        return entidades;
    }

    public void setEntidades(List<Distrito> entidades) {
        this.entidades = entidades;
    }

    public Distrito getEntidad(String id) {
        return distritoCallService.find(new BigDecimal(id));
    }
    
    
    
//    public void crear(Distrito entidadSeleccionada) throws UnknownHostException, Exception 
//    {
//        Distrito distrito = new Distrito();
//
//        distrito.setTxtDescripcion(entidadSeleccionada.getTxtDescripcion());		
//
//        distrito.setTxtPc(Internet.obtenerNombrePC());
//        distrito.setTxtIp(Internet.obtenerIPPC());
//        distrito.setFecEdicion(new Date());
//        
//        distrito.setFlgActivo(BigInteger.ONE);
//        distrito.setNidUsuario(BigInteger.ONE);
//
//        distritoCallService.create(distrito);
//    }
//    
//    public void editar(Distrito entidadSeleccionada) throws UnknownHostException, Exception 
//    {
//        if(null != entidadSeleccionada)
//        {
//            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
//            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
//            entidadSeleccionada.setFecEdicion(new Date());
//
//            distritoCallService.edit(entidadSeleccionada);
//        }
//
//        this.entidadSeleccionada = entidadSeleccionada;
//    }
    
    public List<Distrito> obtener(Provincia provincia) 
    {
        entidades = distritoCallService.obtenerDistritos(provincia);

        return entidades;
    }
    
    public List<Distrito> obtenerTodos(Provincia provincia) 
    {
        entidades = distritoCallService.obtenerDistritos(provincia);

        return entidades;
    }
    
    public void actualizarDistritos(Provincia provincia) 
    {
        entidades = distritoCallService.obtenerDistritos(provincia);
    }

//     public List<String> obtenerDescripcionTodos(String query) 
//    {
//        List<String> distritos = new ArrayList<String>();
//
//        ProvinciaAdministrado provinciaAdministrado = (ProvinciaAdministrado) getFacesContext().getApplication().createValueBinding("#{provinciaAdministrado}").getValue(getFacesContext());
//        String nombreProvincia = provinciaAdministrado.getEntidadSeleccionada().getTxtDescripcion();
//
//        try 
//        {
//            if (null != nombreProvincia && 0 < nombreProvincia.trim().length()) 
//            {
//                List<Provincia> provincias = provinciaCallService.findAllByField("txtDescripcion", nombreProvincia);
//
//                if (null != provincias && 0 < provincias.size()) 
//                {
//                    entidades = distritoCallService.obtenerPorAproximacion(provincias.get(0), "txtDescripcion", query);
//
//                    if (null != entidades && 0 < entidades.size()) 
//                    {
//                        for (Distrito distrito : entidades) 
//                        {
//                            distritos.add(distrito.getTxtDescripcion());
//                        }
//                    } 
//                    else 
//                    {
//                        adicionarMensajeError("obtenerDescripcionTodos", "No se pudieron obtener provincias");
//                    }
//                } 
//                else 
//                {
//                    adicionarMensajeError("Obtener Provincias", "No pudo encontrar al departamento " + nombreProvincia);
//                }
//            } 
//            else 
//            {
//                adicionarMensajeError("Obtener Provincias", "No se encontro un nombre de departamento valido");
//            }
//        } 
//        catch (Exception e) 
//        {
//            adicionarMensajeError("obtenerDescripcionTodos", e.getMessage());
//        }
//
//        return distritos;
//    }
     
    public String obtenerDepartamento(BigInteger nidDistrito)
    {
        String resultado = "";
        
        try
        {
            if(null != nidDistrito)
            {
                Distrito distrito = distritoCallService.find(new BigDecimal(nidDistrito));
                
                if(null != distrito)
                {
                    resultado = distrito.getProvincia().getDepartamento().getTxtDescripcion();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("obtenerDepartamento: " + e.getMessage());
        }
        
        return resultado;
    }
    
    public String obtenerProvincia(BigInteger nidDistrito)
    {
        String resultado = "";
        
        try
        {
            if(null != nidDistrito)
            {
                Distrito distrito = distritoCallService.find(new BigDecimal(nidDistrito));
                
                if(null != distrito)
                {
                    resultado = distrito.getProvincia().getTxtDescripcion();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("obtenerProvincia: " + e.getMessage());
        }
        
        return resultado;
    }
    
    public String obtenerDistrito(BigInteger nidDistrito)
    {
        String resultado = "";
        
        try
        {
            if(null != nidDistrito)
            {
                Distrito distrito = distritoCallService.find(new BigDecimal(nidDistrito));
                
                if(null != distrito)
                {
                    resultado = distrito.getTxtDescripcion();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("obtenerDistrito: " + e.getMessage());
        }
        
        return resultado;
    }
    
    public Distrito obtenerDistritoporId(BigInteger nidDistrito)
    {
        Distrito resultado = null;
        
        try
        {
            if(null != nidDistrito)
            {
                Distrito distrito = distritoCallService.find(new BigDecimal(nidDistrito));
                
                if(null != distrito)
                {
                    resultado = distrito;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("obtenerDistrito: " + e.getMessage());
        }
        
        return resultado;
    }
    
//    public List<String> obtenerDescripcion(String query) {
//        List<String> Distritos = new ArrayList<String>();
//
//        try 
//        {
//            if (0 < query.trim().length()) 
//            {
//                String NewQuery = query.toUpperCase();
//                entidades = distritoCallService.obtenerPorAproximacion("txtDescripcion", NewQuery);
//            } 
//            else 
//            {
//                entidades = null;
//            }
//            
//            if(0 < entidades.size())
//            {
//                for(Distrito distrito: entidades)
//                {
//                    Distritos.add(distrito.getTxtDescripcion());
//                }
//            }
//            else
//            {
//                adicionarMensajeError("obtenerDescripcion", "No se pudieron obtener los distritos");
//            }
//        } 
//        catch (Exception e) 
//        {
//            adicionarMensajeError("obtenerDescripcion", e.getMessage());
//        }
//
//        return Distritos;
//    }
    
//    public List<Distrito> obtenerTodosString(String provincia) {
//        List<Provincia> provincias = provinciaCallService.findAllByField("txtDescripcion", provincia);
//        if (null != provincias && 0 < provincias.size()) {
//            entidades = distritoCallService.obtenerDistritos(provincias.get(0));
//        }
//        return entidades;
//    }
    
//    public Distrito obtenerDistritoSegunUbigeo(String ubigeo)
//     {
//         Distrito distritoEncontrado = null;
//         if(null != ubigeo && 6 == ubigeo.length())
//         {
//             try 
//             {
//                 List<Distrito> distritoEncontrados = distritoCallService.findAllByField("cidDistrito", ubigeo);
//                 if (null != distritoEncontrados && 0 < distritoEncontrados.size()) 
//                 {
//                     distritoEncontrado = distritoEncontrados.get(0);
//                 } 
//                 else 
//                 {
//                     System.out.println("No se encontro el ubigeo de la Distrito: " + ubigeo);
//                 }
//             } catch (Exception e) {
//                 System.out.println("obtenerDistritoSegunUbigeo: " + e.getMessage());
//             }  
//         }
//         else
//         {
//
//         }          
//         return distritoEncontrado;
//     }
    
    public List<Distrito> obtenerActivos()
    {
        return distritoCallService.obtenerActivos();
    }
    
//    public List<Distrito> obtenerTodos()
//    {
//        return distritoCallService.findAll();
//    }

//    public Distrito obtener(String departamento, String provincia, String distrito)
//    {
//        DepartamentoAdministrado departamentoAdministrado = (DepartamentoAdministrado) getFacesContext().getApplication().createValueBinding("#{departamentoAdministrado}").getValue(getFacesContext());
//        ProvinciaAdministrado provinciaAdministrado = (ProvinciaAdministrado) getFacesContext().getApplication().createValueBinding("#{provinciaAdministrado}").getValue(getFacesContext());
//        
//        Departamento departamentoEncontrado = null;
//        Provincia provinciaEncontrada = null;      
//        Distrito distritoEncontrado = null;
//        
//        try
//        {
//            if(null != departamento && null != provincia && null != distrito)
//            {
//                departamentoEncontrado = departamentoAdministrado.obtenerPorNombre(departamento);
//                
//                if(null != departamentoEncontrado)
//                {
//                    provinciaEncontrada = provinciaAdministrado.obtenerPorNombre(departamentoEncontrado, provincia);
//                    
//                    if(null != provinciaEncontrada)
//                    { 
//                        List<Distrito> distritosEncontrados = null;
//                        
//                        distritosEncontrados = distritoCallService.obtenerDistritosPorNombre(provinciaEncontrada, distrito);
//                        
//                        if(null != distritosEncontrados && 0 < distritosEncontrados.size())
//                        {
//                            distritoEncontrado = distritosEncontrados.get(0);
//                        }
//                    }
//                }
//            }
//            else
//            {
//                adicionarMensajeError("Obtener Distrito", "El departamento, provincia o distrito no son validos");
//            }
//        }
//        catch(Exception e)
//        {
//            adicionarMensajeError("Obtener Distrito", e);
//        }
//        
//        return distritoEncontrado;
//    }
    
//    public Distrito obtener(Provincia provincia, String nombre)
//    {
//        Distrito distrito = null;
//        
//        try
//        {
//            List<Distrito> encontrados = distritoCallService.obtenerDistritosPorNombre(provincia, nombre);
//            
//            if(null != encontrados && 0 < encontrados.size())
//            {
//                distrito = encontrados.get(0);
//            }
//        }
//        catch(Exception e)
//        {
//            
//        }
//        
//        return distrito;
//    }
}
