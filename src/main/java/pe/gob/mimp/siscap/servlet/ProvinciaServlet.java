/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import pe.gob.mimp.bean.FindAllByFieldBean;
import pe.gob.mimp.core.Util;
import pe.gob.mimp.general.modelo.Provincia;
import pe.gob.mimp.general.modelo.ProvinciaCoordenadas;
import pe.gob.mimp.siscap.ws.provincia.cliente.ProvinciaCallService;
import pe.gob.mimp.siscap.ws.provinciacoordenadas.cliente.ProvinciaCoordenadasCallService;

/**
 *
 * @author Hydra
 */
public class ProvinciaServlet extends HttpServlet {

    @Inject
    private ProvinciaCoordenadasCallService provinciaCoordenadasCallService;
    @Inject
    private ProvinciaCallService provinciaCallService;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException {
        response.setContentType("text/html;charset=UTF-8");

        String nombre = request.getParameter("nombre");
        PrintWriter out = response.getWriter();

        try {
            if (null != nombre && !"".equals(nombre)) {
                FindAllByFieldBean findAllByFieldBean = new FindAllByFieldBean();
                findAllByFieldBean.setField("txtDescripcion");
                findAllByFieldBean.setValue(nombre);
                List<Provincia> provincias = provinciaCallService.findAllByField(findAllByFieldBean);

                if (null != provincias && 0 < provincias.size()) {                    
                    List<ProvinciaCoordenadas> coordenadas = provinciaCoordenadasCallService.obtenerCoordenadas(provincias.get(0));

                    if (null != coordenadas && 0 < coordenadas.size()) {
                        JSONArray jsonCoordenadas = new JSONArray();

                        for (ProvinciaCoordenadas coordenada : coordenadas) {
                            JSONObject jsonCoordenada = new JSONObject();
                            jsonCoordenada.put("lat", coordenada.getNumLatitud());
                            jsonCoordenada.put("lng", coordenada.getNumLongitud());
                            jsonCoordenada.put("nombre", coordenada.getProvincia().getTxtDescripcion());
                            jsonCoordenada.put("ubigeo", coordenada.getProvincia().getCidProvincia());
                            jsonCoordenada.put("padre", coordenada.getProvincia().getDepartamento().getTxtDescripcion());
                            jsonCoordenadas.put(jsonCoordenada);
                        }

                        out.println(jsonCoordenadas.toString());
                    } else {
                        Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se pudieron obtener las coordenadas de " + nombre, Util.tiempo());
                    }
                } else {
                    Logger.getLogger(Thread.currentThread().getStackTrace()[1].getMethodName()).log(Level.INFO, "{0}No se pudo obtener la provincia " + nombre, Util.tiempo());
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
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(ProvinciaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(ProvinciaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
