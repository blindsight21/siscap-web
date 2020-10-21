package pe.gob.mimp.siscap.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.modelo.Departamento;
import pe.gob.mimp.siscap.modelo.VwCantidadActividad;
import pe.gob.mimp.siscap.ws.departamento.cliente.DepartamentoCallService;
import pe.gob.mimp.siscap.ws.vwcantidadactividad.cliente.VwCantidadActividadCallService;

@WebServlet(name = "ActividadServlet", urlPatterns = {"/ActividadServlet"})
public class ActividadServlet extends HttpServlet {

    @Inject
    private VwCantidadActividadCallService vwCantidadActividadCallService;
    @Inject
    private DepartamentoCallService departamentoCallService;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String nombre = request.getParameter("nombre");
        PrintWriter out = response.getWriter();

        try {

            if (null != nombre && !"".equals(nombre)) {

                FindAllByFieldBean findAllByFieldDepartamentosBean = new FindAllByFieldBean();
                findAllByFieldDepartamentosBean.setField("txtDescripcion");
                findAllByFieldDepartamentosBean.setValue(nombre);
                List<Departamento> departamentosEncontrados = departamentoCallService.findAllByField(findAllByFieldDepartamentosBean);

                if (null != departamentosEncontrados && 0 < departamentosEncontrados.size()) {

                    FindAllByFieldBean findAllByFieldCantidadActividadesBean = new FindAllByFieldBean();
                    findAllByFieldCantidadActividadesBean.setField("departamentoActividad");
                    findAllByFieldCantidadActividadesBean.setValue(departamentosEncontrados.get(0).getNidDepartamento().toBigInteger());
                    List<VwCantidadActividad> vwCantidadActividadEncontradas = vwCantidadActividadCallService.findAllByField(findAllByFieldCantidadActividadesBean);

                    if (null != vwCantidadActividadEncontradas && 0 < vwCantidadActividadEncontradas.size()) {

                        JSONArray jsonCantidadActividades = new JSONArray();

                        for (VwCantidadActividad vwCantidad : vwCantidadActividadEncontradas) {

                            JSONObject jsonCantidad = new JSONObject();
                            jsonCantidad.put("departamento", vwCantidad.getDepartamentoActividad().toString());
                            jsonCantidad.put("cantidad", vwCantidad.getCantidadActividad().toString());
                            jsonCantidadActividades.put(jsonCantidad);

                            out.println(jsonCantidadActividades.toString());
                        }
                    } else {
                        Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se pudieron obtener las Actividades " + nombre, Util.tiempo());
                    }
                } else {
                    Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se pudo obtener departamento " + nombre, Util.tiempo());
                }
            } else {
                Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se pudo obtener ningun parametro por GET", Util.tiempo());
            }
        } catch (Exception e) {
            Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.SEVERE, null, e);

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
