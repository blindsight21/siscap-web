///////////////////////////////////////// TRADUCCION DEL CALENDARIO

PrimeFaces.locales['es'] = {
    closeText: 'Cerrar',
    prevText: 'Anterior',
    nextText: 'Siguiente',
    monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
    dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'],
    dayNamesMin: ['D', 'L', 'M', 'X', 'J', 'V', 'S'],
    weekHeader: 'Semana',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Sólo hora',
    timeText: 'Tiempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Fecha actual',
    ampm: false,
    month: 'Mes',
    week: 'Semana',
    day: 'Día',
    allDayText: 'Todo el día'
};

///////////////////////////////////////// FUNCIONES DEL LOGIN

function handleLoginRequest(xhr, status, args) {
    if (args.validationFailed || !args.loggedIn) {
    }
    else {
        setTimeout(function () {
            window.location = '/siscap-web/faces/index.xhtml';
        }, 500);
    }
}

function logout(xhr, status, args) {
    setTimeout(function () {
        window.location = '/siscap-web/faces/login.xhtml';
    }, 500);
}

///////////////////////////////////////// COOKIE

function createCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else
        var expires = "";
    document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ')
            c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0)
            return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name, "", -1);
}

///////////////////////////////////////// AYUDA

function doc_keyUp(e) {
    if (e.ctrlKey && e.keyCode == 38) {
        $(".contenedorAnimacion").show();
        $(".contenedorTitulo").show();
        activar();
    }
    if (e.ctrlKey && e.keyCode == 40) {
        $(".contenedorAnimacion").hide();
        $(".contenedorTitulo").hide();
        desactivar();
    }
}
document.addEventListener('keyup', doc_keyUp, false);

///////////////////////////////////////// DRAG AND DROP

function drag_start(event) {
    var style = window.getComputedStyle(event.target, null);
    event.dataTransfer.setData("text/plain",
            (parseInt(style.getPropertyValue("left"), 10) - event.clientX) + ',' + (parseInt(style.getPropertyValue("top"), 10) - event.clientY));
}
function drag_over(event) {
    event.preventDefault();
    return false;
}
function drop(event) {
    var offset = event.dataTransfer.getData("text/plain").split(',');
    var dm = document.getElementById('dragme');
    dm.style.left = (event.clientX + parseInt(offset[0], 10)) + 'px';
    dm.style.top = (event.clientY + parseInt(offset[1], 10)) + 'px';
    event.preventDefault();
    return false;
}

document.addEventListener('dragover', drag_over, false);
document.addEventListener('drop', drop, false);

///////////////////////////////////////// DOCUMENT READY

document.ready = function inicio() {
    var dm = document.getElementById('dragme');
    dm.addEventListener('dragstart', drag_start, false);
};

function noBackButton() {
    window.location.hash = "";
    window.location.hash = "/";//chrome
    window.onhashchange = function(){
         window.location.hash = "";
    };
}

//Ver para llamar a esta funcion con retorno booleano
function keyPressed() {
    if (event.keyCode === 13) {
        return false;

    }
}

function valideNumericAlphanumeric(e, parametro) {
    /*  
     rangos:
     8           : tecla retroceso (para utilizarlo al borrar datos)
     48 - 57     : numeros del 0 al 9
     65 - 90     : letras mayusculas
     97 - 122    : letras minusculas
     */
    var nidTipoDocumento = parseInt(parametro);
    
    var key = window.Event ? e.which : e.keyCode;
    if (nidTipoDocumento === 1) {//numeros
        return ((key === 8) ||(key === 13) ||(key >= 48 && key <= 57));
    } else {//alfanumericos
        return ((key === 8) ||(key === 13) ||(key >= 48 && key <= 57) || (key >= 65 && key <= 90) || (key >= 97 && key <= 122));
    }
}