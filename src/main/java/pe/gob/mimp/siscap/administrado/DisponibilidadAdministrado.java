/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.siscap.administrado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import pe.gob.mimp.bean.FindByParamBean;
import pe.gob.mimp.general.administrado.AdministradorAbstracto;
import pe.gob.mimp.siscap.bean.DisponibilidadVO;
import pe.gob.mimp.siscap.modelo.Disponibilidad;
import pe.gob.mimp.siscap.modelo.TipoObjetivo;
import pe.gob.mimp.siscap.util.SiscapWebConstantes;
import pe.gob.mimp.siscap.util.SiscapWebUtil;
import pe.gob.mimp.siscap.ws.disponibilidad.cliente.DisponibilidadCallService;
import pe.gob.mimp.util.EnumFuncionalidad;
import pe.gob.mimp.util.Util;

/**
 *
 * @author Omar
 */
@ManagedBean
@ViewScoped
public class DisponibilidadAdministrado extends AdministradorAbstracto implements Serializable {

    private Logger logger = Logger.getLogger(TipoObjetivo.class.getName());

    @Inject
    private DisponibilidadCallService disponibilidadCallService;

    private DisponibilidadVO entidadSeleccionada;
    private List<DisponibilidadVO> disponibilidadList;

    public DisponibilidadAdministrado() {

    }

    @PostConstruct
    public void initBean() {
        limpiarDisponibilidad();
    }

    public DisponibilidadVO getEntidadSeleccionada() {
        return entidadSeleccionada;
    }

    public void setEntidadSeleccionada(DisponibilidadVO entidadSeleccionada) {
        this.entidadSeleccionada = entidadSeleccionada;
    }

    public List<DisponibilidadVO> getDisponibilidadList() {
        return disponibilidadList;
    }

    public void setDisponibilidadList(List<DisponibilidadVO> disponibilidadList) {
        this.disponibilidadList = disponibilidadList;
    }

    public void limpiarDisponibilidad() {
        this.entidadSeleccionada = new DisponibilidadVO();
        this.disponibilidadList = new ArrayList<>();
    }

    public List<DisponibilidadVO> loadDisponibilidadList() {

        List<Disponibilidad> disponibilidadFromBDList = obtenerListaDisponibilidadFromBD();

        if (Util.esListaVacia(disponibilidadFromBDList)) {
            return null;
        }

        List<DisponibilidadVO> disponibilidadVOList = new ArrayList();

        AtomicReference<BigInteger> itemAtomicReference = new AtomicReference<>(BigInteger.ZERO);
        disponibilidadFromBDList.stream().forEach((Disponibilidad disponibilidad) -> {

            itemAtomicReference.set(itemAtomicReference.get().add(BigInteger.ONE));

            EnumFuncionalidad enumFuncionalidad = obtenerEnumFuncionalidad(disponibilidad.getNidFuncionalidad());

            DisponibilidadVO disponibilidadVO = new DisponibilidadVO();
            disponibilidadVO.setItem(itemAtomicReference.get());
            disponibilidadVO.setNidFuncionalidad(disponibilidad.getNidFuncionalidad());
            disponibilidadVO.setModulo(enumFuncionalidad == null ? "" : enumFuncionalidad.getTxtFuncionalidad());
            disponibilidadVO.setTiempoMedioFalla(enumFuncionalidad == null ? BigInteger.ZERO : enumFuncionalidad.getTiempoMedioParaLaFallaBigInteger());
            disponibilidadVO.setTiempoMedioReparacion(null);
            disponibilidadVO.setPorcentajeDisponibilidad(null);

            disponibilidadVOList.add(disponibilidadVO);
        });

        if (Util.esListaVacia(disponibilidadVOList)) {
            return null;
        }

        disponibilidadList = disponibilidadVOList.stream().collect(Collectors.toList());

        disponibilidadList.forEach((disponibilidadVO) -> {

            disponibilidadVO.setTiempoMedioReparacion(
                    obtenerTiempoMedioParaLaReparacion(
                            disponibilidadVO.getNidFuncionalidad()
                    )
            );

            disponibilidadVO.setPorcentajeDisponibilidad(
                    SiscapWebUtil.calculoFormulaDisponibilidad(
                            disponibilidadVO.getTiempoMedioFalla(),
                            disponibilidadVO.getTiempoMedioReparacion()
                    )
            );
        });

        return disponibilidadList;
    }

    private List<Disponibilidad> obtenerListaDisponibilidadFromBD() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", SiscapWebConstantes.FLG_ACTIVO);
        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidDisponibilidad");

        return disponibilidadCallService.loadDisponibilidadList(findByParamBean);
    }

    private EnumFuncionalidad obtenerEnumFuncionalidad(BigInteger nidFuncionalidad) {

        List<EnumFuncionalidad> enumFuncionalidadList = Arrays.asList(EnumFuncionalidad.values());

        AtomicReference<EnumFuncionalidad> nombreModulo = new AtomicReference<>();

        enumFuncionalidadList
                .stream()
                .filter((enumFuncionalidad)
                        -> (enumFuncionalidad.getNidFuncionalidadBigInteger().equals(nidFuncionalidad)))
                .forEachOrdered((enumFuncionalidad) -> {
                    nombreModulo.set(enumFuncionalidad);
                });

        return nombreModulo.get();
    }

    private BigInteger obtenerTiempoMedioParaLaReparacion(BigInteger nidFuncionalidad) {

        BigDecimal promedioTiempoMedioParaLaReparacion = BigDecimal.ZERO;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flgActivo", SiscapWebConstantes.FLG_INACTIVO);
        parameters.put("nidFuncionalidad", nidFuncionalidad);
        FindByParamBean findByParamBean = new FindByParamBean();
        findByParamBean.setParameters(parameters);
        findByParamBean.setOrderBy("nidDisponibilidad");

        List<Disponibilidad> disponibilidadFromBDList = disponibilidadCallService.loadDisponibilidadList(findByParamBean);

        if (Util.esListaVacia(disponibilidadFromBDList)) {
            return BigInteger.ZERO;
        }

        BigDecimal sumaNumTiempoMedio = BigDecimal.ZERO;

        for (Disponibilidad disponibilidad : disponibilidadFromBDList) {

            sumaNumTiempoMedio = sumaNumTiempoMedio.add(new BigDecimal(disponibilidad.getNumTiempoMedio()));
        }

        promedioTiempoMedioParaLaReparacion = sumaNumTiempoMedio
                .divide(new BigDecimal(disponibilidadFromBDList.size()), 0, RoundingMode.HALF_UP);

        return promedioTiempoMedioParaLaReparacion.toBigInteger();
    }

}
