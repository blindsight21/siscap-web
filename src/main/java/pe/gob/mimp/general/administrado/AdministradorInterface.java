/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.mimp.general.administrado;

import pe.gob.mimp.siscap.administrado.*;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author desarrollador
 */
public interface AdministradorInterface 
{
    public void almacenarEntidadSeleccionadaAjax(final AjaxBehaviorEvent event) throws Exception;
}
