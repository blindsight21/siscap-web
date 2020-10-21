package pe.gob.mimp.general.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import pe.gob.mimp.general.modelo.Departamento;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.departamentocoordenadas.cliente.DepartamentoCoordenadasCallService;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class DepartamentoAdministrado extends AdministradorAbstracto implements Serializable {

    private Departamento entidad;
    private Departamento entidadSeleccionada;

    private List<Departamento> entidades;

    private List<String> colores;

    private List<Departamento> entidadesSeleccionadas;

    @Inject
    private DepartamentoCallService departamentoCallService;

    @Inject
    private DepartamentoCoordenadasCallService departamentoCoordenadasCallService;

    public DepartamentoAdministrado() {
        instanciar();
    }

    public List<Departamento> getEntidadesSeleccionadas() {
        return entidadesSeleccionadas;
    }

    public void setEntidadesSeleccionadas(List<Departamento> entidadesSeleccionadas) {
        this.entidadesSeleccionadas = entidadesSeleccionadas;
    }

    public void instanciar() {
        entidad = new Departamento();
        entidadSeleccionada = new Departamento();
        this.colores = new ArrayList<String>();
        this.colores.add("#39add1");
        this.colores.add("#3079ab");
        this.colores.add("#c25975");
        this.colores.add("#e15258");
        this.colores.add("#f9845b");
        this.colores.add("#838cc7");
        this.colores.add("#7d669e");
        this.colores.add("#53bbb4");
        this.colores.add("#51b46d");
        this.colores.add("#e0ab18");
        this.colores.add("#637a91");

        this.colores.add("#f092b0");
        this.colores.add("#b7c0c7");
        this.colores.add("#39add1");
        this.colores.add("#3079ab");
        this.colores.add("#c25975");
        this.colores.add("#e15258");
        this.colores.add("#f9845b");
        this.colores.add("#838cc7");
        this.colores.add("#7d669e");
        this.colores.add("#53bbb4");

        this.colores.add("#51b46d");
        this.colores.add("#e0ab18");
        this.colores.add("#637a91");
        this.colores.add("#39add1");
        this.colores.add("#3079ab");
        this.colores.add("#c25975");

    }

    public Departamento getEntidad() {
        return entidad;
    }

    public void setEntidad(Departamento entidad) {
        this.entidad = entidad;
    }

    public Departamento getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(Departamento selectedEntitie) {
        this.entidadSeleccionada = selectedEntitie;
    }

    public List<Departamento> getEntidades() {
        entidades = departamentoCallService.obtenerDepartamentos();

        return entidades;
    }

    public void setEntidades(List<Departamento> entidades) {
        this.entidades = entidades;
    }

    public Departamento getEntidad(String id) {
        return departamentoCallService.find(new BigDecimal(id));
    }

    public List<String> getColores() {
        return colores;
    }

    public void setColores(List<String> colores) {
        this.colores = colores;
    }

//    public void crear(Departamento entidadSeleccionada) throws UnknownHostException, Exception {
//        Departamento departamento = new Departamento();
//
//        departamento.setTxtDescripcion(entidadSeleccionada.getTxtDescripcion());
//
//        departamento.setTxtPc(Internet.obtenerNombrePC());
//        departamento.setTxtIp(Internet.obtenerIPPC());
//        departamento.setFecEdicion(new Date());
//
//        departamento.setFlgActivo(BigInteger.ONE);
//        departamento.setNidUsuario(BigInteger.ONE);
//
//        departamentoCallService.create(departamento);
//    }
//
//    public void editar(Departamento entidadSeleccionada) throws UnknownHostException, Exception {
//        if (null != entidadSeleccionada) {
//            entidadSeleccionada.setTxtPc(Internet.obtenerNombrePC());
//            entidadSeleccionada.setTxtIp(Internet.obtenerIPPC());
//            entidadSeleccionada.setFecEdicion(new Date());
//
//            departamentoCallService.edit(entidadSeleccionada);
//        }
//
//        this.entidadSeleccionada = entidadSeleccionada;
//    }
//
    public List<Departamento> obtenerTodos() {
        entidades = departamentoCallService.obtenerDepartamentos();

        return entidades;
    }
//
//    public List<String> obtenerDescripcionTodos(String query) {
//        List<String> departamentos = new ArrayList<String>();
//
//        try {
//            if (0 < query.trim().length()) {
//                String NewQuery = query.toUpperCase();
//                entidades = departamentoCallService.obtenerPorAproximacion("txtDescripcion", NewQuery);
//            } else {
//                entidades = departamentoCallService.obtenerDepartamentos();
//            }
//
//            if (0 < entidades.size()) {
//                for (Departamento departamento : entidades) {
//                    departamentos.add(departamento.getTxtDescripcion());
//                }
//            } else {
//                adicionarMensajeError("obtenerDescripcionTodos", "No se pudieron obtener departamentos");
//            }
//        } catch (Exception e) {
//            adicionarMensajeError("obtenerDescripcionTodos", e.getMessage());
//        }
//
//        return departamentos;
//    }
//
//    public List<DepartamentoCoordenadas> obtenerCoordenadas() {
//        List<DepartamentoCoordenadas> coordenadas = null;
//
//        try {
//            coordenadas = departamentoCoordenadasCallService.findAll();
//        } catch (Exception e) {
//            adicionarMensajeError("obtenerCoordenadas", e.getMessage());
//        }
//
//        return coordenadas;
//    }
//
//    public Departamento obtenerDepartamentoSegunUbigeo(String ubigeo) {
//        Departamento departamentoEncontrado = null;
//        if (null != ubigeo && 2 == ubigeo.length()) {
//            try {
//                List<Departamento> departamentoEncontrados = departamentoCallService.findAllByField("cidDepartamento", ubigeo);
//                if (null != departamentoEncontrados && 0 < departamentoEncontrados.size()) {
//                    departamentoEncontrado = departamentoEncontrados.get(0);
//                } else {
//                    System.out.println("No se encontro el ubigeo del Departamento: " + ubigeo);
//                }
//            } catch (Exception e) {
//                System.out.println("obtenerDepartamentoSegunUbigeo: " + e.getMessage());
//            }
//        } else {
//
//        }
//
//        return departamentoEncontrado;
//    }
//
//    public List<Departamento> obtenerActivos() {
//        return departamentoCallService.obtenerActivos();
//    }
//
//    public List<Departamento> obtenerTodosDepartamentos() {
//        return departamentoCallService.findAll();
//    }
//
//    public String obtenerJsonDepartamentos() {
//        JSONArray jsonDepartamentos = new JSONArray();
//
//        try {
//            List<Departamento> departamentos = departamentoCallService.obtenerActivos();
//            for (Departamento departamento : departamentos) {
//                JSONObject jsonDepartamento = new JSONObject();
//
//                jsonDepartamento.put("id", departamento.getNidDepartamento());
//                jsonDepartamento.put("nombre", departamento.getTxtDescripcion());
//                jsonDepartamento.put("ubigeo", departamento.getCidDepartamento());
//
//                jsonDepartamentos.put(jsonDepartamento);
//            }
//        } catch (Exception e) {
//            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);
//        }
//
//        return jsonDepartamentos.toString();
//    }
//
//    public Departamento obtenerPorNombre(String nombre) {
//        Departamento departamentoEncontrado = null;
//
//        try {
//            List<Departamento> departamentosEncontrados = departamentoCallService.findAllByField("txtDescripcion", nombre.toUpperCase());
//
//            if (null != departamentosEncontrados && 0 < departamentosEncontrados.size()) {
//                departamentoEncontrado = departamentosEncontrados.get(0);
//            }
//        } catch (Exception e) {
//            adicionarMensajeError("Obtener por nombre", e);
//        }
//
//        return departamentoEncontrado;
//    }
//
//    public Departamento obtener(String nombre) {
//        Departamento departamento = null;
//
//        try {
//            List<Departamento> encontrados = departamentoCallService.findAllByField("txtDescripcion", nombre);
//
//            if (null != encontrados && 0 < encontrados.size()) {
//                departamento = encontrados.get(0);
//            }
//        } catch (Exception e) {
//
//        }
//
//        return departamento;
//    }
}
