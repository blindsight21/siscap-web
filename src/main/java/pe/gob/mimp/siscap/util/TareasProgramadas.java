/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.bean.UsuarioModuloBean;
import pe.gob.mimp.seguridad.converter.ModuloCast;
import pe.gob.mimp.seguridad.converter.UsuarioCast;
import pe.gob.mimp.seguridad.modelo.Funcionalidad;
import pe.gob.mimp.seguridad.modelo.Modulo;
import pe.gob.mimp.seguridad.modelo.Usuario;
import pe.gob.mimp.siscap.modelo.Disponibilidad;
import pe.gob.mimp.siscap.ws.disponibilidad.cliente.DisponibilidadCallService;
import pe.gob.mimp.siscap.ws.modulo.cliente.ModuloCallService;
import pe.gob.mimp.siscap.ws.usuario.cliente.UsuarioCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.util.Util;

/**
 *
 * @author Omar
 */
@Singleton
@Startup
@Default
public class TareasProgramadas {

    @Inject
    private DisponibilidadCallService disponibilidadCallService;

    @Inject
    private ModuloCallService moduloCallService;

    @Inject
    private UsuarioCallService usuarioCallService;

    @PostConstruct
    public void onStartUp() {
        System.out.println("onStartUp");
    }

    @PreDestroy
    public void onShutDown() {
        System.out.println("onShutDown");
    }

    @Schedule(second = "*/10", minute = "*", hour = "*")
    public void execute() {
//        System.out.println(":::::::::Iniciando tareas programadas:::::::::::::");
        /*A partir de acá irán las tareas programadas*/
        registrarDisponibilidad();
//        System.out.println(":::::::::Terminando tareas programadas:::::::::::::");
    }

    public void registrarDisponibilidad() {

        List<EnumFuncionalidad> enumFuncionalidadValues = Arrays.asList(EnumFuncionalidad.values());

        UsuarioModuloBean usuarioModuloBean = new UsuarioModuloBean();
        usuarioModuloBean.setModuloBean(ModuloCast.castModuloToModuloBean(obtenerModuloSiscap()));
        usuarioModuloBean.setUsuarioBean(UsuarioCast.castUsuarioToUsuarioBean(obtenerUsuarioAdminSiscap()));
        List<Funcionalidad> listaFuncionalidades = usuarioCallService.obtenerFuncionalidades(usuarioModuloBean);

        for (EnumFuncionalidad enumFuncionalidadValue : enumFuncionalidadValues) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nidFuncionalidad", enumFuncionalidadValue.getNidFuncionalidadBigInteger());
            parameters.put("flgActivo", SiscapWebConstantes.FLG_ACTIVO);

            FindByParamBean findByParamBean = new FindByParamBean();
            findByParamBean.setParameters(parameters);
            findByParamBean.setOrderBy("nidDisponibilidad");

            List<Disponibilidad> listaDisponibilidad = disponibilidadCallService.loadDisponibilidadList(findByParamBean);

            if (Util.esListaVacia(listaDisponibilidad)) {

                Disponibilidad nuevoDisponibilidad = SiscapWebUtil.crearDisponibilidad(
                        enumFuncionalidadValue.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerFechaActual(),
                        null,
                        null,
                        BigInteger.valueOf(Long.valueOf(SiscapWebConstantes.NID_USUARIO_ADMINISTRADOR_SISCAP))//Usuario ADMIN
                );
                disponibilidadCallService.crearDisponibilidad(nuevoDisponibilidad);
                continue;
            }

            Disponibilidad disponibilidadObtenido = listaDisponibilidad.get(0);

            Funcionalidad funcionalidad = listaFuncionalidades
                    .stream()
                    .filter(x -> x.getNidFuncionalidad().equals(enumFuncionalidadValue.getNidFuncionalidadBigDecimal()))
                    .findFirst()
                    .orElse(null);

            if (funcionalidad == null) {
                if (disponibilidadObtenido.getFecNoDisponible() == null) {
                    disponibilidadObtenido.setFecNoDisponible(SiscapWebUtil.obtenerFechaActual());
                    disponibilidadCallService.editarDisponibilidad(disponibilidadObtenido);
                }
                continue;
            }

            if (disponibilidadObtenido.getFecNoDisponible() != null) {

                disponibilidadObtenido.setFlgActivo(SiscapWebConstantes.FLG_INACTIVO);
                disponibilidadObtenido.setNumTiempoMedio(BigInteger.valueOf(
                        SiscapWebUtil.obtenerDiferenciaEnMinutosEntreFechas(
                                disponibilidadObtenido.getFecNoDisponible(),
                                SiscapWebUtil.obtenerFechaActual()
                        )
                ));
                disponibilidadCallService.editarDisponibilidad(disponibilidadObtenido);

                Disponibilidad nuevoDisponibilidad = SiscapWebUtil.crearDisponibilidad(
                        enumFuncionalidadValue.getNidFuncionalidadBigInteger(),
                        SiscapWebUtil.obtenerFechaActual(),
                        null,
                        null,
                        BigInteger.valueOf(Long.valueOf(SiscapWebConstantes.NID_USUARIO_ADMINISTRADOR_SISCAP))//Usuario ADMIN
                );
                disponibilidadCallService.crearDisponibilidad(nuevoDisponibilidad);
            }
        }
    }

    private Modulo obtenerModuloSiscap() {
        return moduloCallService.find(new BigDecimal(SiscapWebConstantes.NID_MODULO_SISCAP));
    }

    private Usuario obtenerUsuarioAdminSiscap() {
        return usuarioCallService.find(new BigDecimal(SiscapWebConstantes.NID_USUARIO_ADMINISTRADOR_SISCAP));
    }

}
