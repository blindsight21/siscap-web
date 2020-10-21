/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.filtro;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pe.gob.mimp.seguridad.administrado.UsuarioAdministrado;

/**
 *
 * @author Ing. Oscar Mateo
 */
public abstract class FiltroAbstracto implements Filter {

    private List<String> rutasDesprotegidas = null;

    private FilterConfig filtroConfiguracion = null;

    public FilterConfig getFiltroConfiguracion() {
        return filtroConfiguracion;
    }

    public void setFiltroConfiguracion(FilterConfig filtroConfiguracion) {
        this.filtroConfiguracion = filtroConfiguracion;
    }

    public List<String> getRutasDesprotegidas() {
        return rutasDesprotegidas;
    }

    public void setRutasDesprotegidas(List<String> rutasDesprotegidas) {
        this.rutasDesprotegidas = rutasDesprotegidas;
    }

    public FiltroAbstracto() {
        rutasDesprotegidas = new ArrayList<String>();

        rutasDesprotegidas.add("login.xhtml");
        rutasDesprotegidas.add("index_externo.xhtml");
        rutasDesprotegidas.add("bienvenida.xhtml");
        rutasDesprotegidas.add("resources");
        rutasDesprotegidas.add("javax.faces.resource");
    }

    public void adicionarRutaDesprotegida(String ruta) {
        this.rutasDesprotegidas.add(ruta);
    }

    protected boolean permitir(String url) {
        boolean acceder = false;

        for (String item : getRutasDesprotegidas()) {
            if (true == url.endsWith(item)
                    || (url.contains("resources"))
                    || (url.contains("javax.faces.resource"))
                    || (url.contains("servlet"))
                    || (url.contains("Servlet"))) {
                acceder = true;

                break;
            }
        }

        return acceder;
    }

    protected void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {

            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());

                t.printStackTrace(ps);
                ps.close();

                response.getOutputStream().close();
            } catch (Exception ex) {

            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {

        }

        return stackTrace;
    }

    public void log(String msg) {
        filtroConfiguracion.getServletContext().log(msg);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        UsuarioAdministrado loggedIn = (UsuarioAdministrado) req.getSession().getAttribute("usuarioAdministrado");

        String urlStr = req.getRequestURL().toString().toLowerCase();

        if (true == permitir(urlStr)) {
            chain.doFilter(request, response);
        } else {
            if (loggedIn == null || (false == loggedIn.estaLogeado())) {
                res.sendRedirect(req.getContextPath() + "/faces/login.xhtml");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        
    }

    @Override
    public void init(FilterConfig filtroConfiguracion) {
        setFiltroConfiguracion(filtroConfiguracion);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LoginFilter(");

        sb.append(getFiltroConfiguracion());
        sb.append(")");

        return (sb.toString());
    }
}
