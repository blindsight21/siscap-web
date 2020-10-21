package pe.gob.mimp.siscap.filtro;

import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "FiltroWeb", urlPatterns = {"/*"})
public class FiltroWeb extends FiltroAbstracto {

    public FiltroWeb() {
        getRutasDesprotegidas().add("siscap-web/");

        getRutasDesprotegidas().add("DepartamentoServlet");
        getRutasDesprotegidas().add("DistritoServlet");
        getRutasDesprotegidas().add("ProvinciaServlet");
        getRutasDesprotegidas().add("ActividadServlet");
    }
}
