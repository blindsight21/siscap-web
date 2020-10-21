var usandoVideoLocal = false;
var deshabilitarBarraProgreso = true;
var mostrarDescripcion = true;
var titulovideo = "#tiuloVideo";
var descripcionvideo = "#descripcionVideo";
var preguntarParaContinuar = true;
var autoplay = true;

$(document).ready(function () {
    IniciarVideo();
});

function IniciarVideo() {

    var player = videojs('video', {
        techOrder: ['youtube', 'html5', 'flash'],
        fluid: true,
        plugins: {
            videoJsResolutionSwitcher: {
                default: 'high', // Default resolution [{Number}, 'low', 'high'],
                dynamicLabel: true
            }
        }
    }, function () {
        var player = this;
        if (deshabilitarBarraProgreso) {
            player.disableProgress();
            this.disableProgress.disable();
        }
        player.on('loadstart', function (e) {
            if (mostrarDescripcion) {
                descripcion();
            }
        });
        player.on('resolutionchange', function () {

        });
        player.on('playlistchange', function () {

        });
        player.on('timeupdate', function (e) {
            var posicion = player.playlist.currentItem();
            var tiempo = player.currentTime();
            var fixtiempo = tiempo.toFixed(1);
            console.log(fixtiempo);
            if(posicion > 0 && fixtiempo >= 0.1 && fixtiempo <= 0.2){
                if (preguntarParaContinuar) {
                    if (autoplay) {
                        player.pause();
                        cotinue();
                    }
                }
            }
        });
        player.on('ended', function (e) {
            var posicion = player.playlist.currentItem() + 1;
            if (posicion < videos.length) {
                liactivo();
            }
            if (posicion == videos.length) {
                unlock();
            }
        });

        player.playlist(videos);

        player.playlist.autoadvance(0);

        var btnCompartir = addNewButton({
            player: player,
            icon: "btnCompartir",
            id: "BotonCompartir"
        });
        btnCompartir.onclick = function () {
            player.pause();
            compartir();
        };

        var btnReiniciar = addNewButton({
            player: player,
            icon: "btnReiniciar",
            id: "BotonReiniciar"
        });
        btnReiniciar.onclick = function () {
            player.currentTime(0);
        };

        var btnRetroceder = addNewButton({
            player: player,
            icon: "btnRetroceder",
            id: "BotonRetroceder"
        });
        btnRetroceder.onclick = function () {
            var tiempo = player.currentTime();
            player.currentTime(tiempo - 10);
        };

        for (var i = 0; i < videos.length; i++) {
            var titlevisible_;
            var textvisible_;

            if (videos[i].obligatorio == 0) {
                if (videos[i].titlevisible == 0) {
                    titlevisible_ = "hidden";
                }
                else {
                    titlevisible_ = "";
                }
                if (videos[i].textvisible == 0) {
                    textvisible_ = "hidden";
                }
                else {
                    textvisible_ = "";
                }
                var appending =
                        '<li data-videoplaylist="' + i + '">' +
                        '<div class="lock">' +
                        '<span class="number">' + (i + 1) + '</span>' +
                        '<div class="poster"><img src="' + videos[i].poster + '"></div>' +
                        '<div class="title">' +
                        '<div class="title_text ' + titlevisible_ + '">' + videos[i].title + '</div>' +
                        '<div class="text ' + textvisible_ + '">' + videos[i].text + '</div>' +
                        '</div>' +
                        '</div>' +
                        '</li>';
            }
            else
            {
                if (videos[i].titlevisible == 0) {
                    titlevisible_ = "hidden";
                }
                else {
                    titlevisible_ = "";
                }
                if (videos[i].textvisible == 0) {
                    textvisible_ = "hidden";
                }
                else {
                    textvisible_ = "";
                }
                var appending =
                        '<li data-videoplaylist="' + i + '">' +
                        '<div class="lock">' +
                        '<div class="icon-lock"><div class="lock-top-1"></div><div class="lock-top-2"></div><div class="lock-body"></div><div class="lock-hole"></div></div>' +
                        '<span class="number">' + (i + 1) + '</span>' +
                        '<div class="poster"><img src="' + videos[i].poster + '"></div>' +
                        '<div class="title">' +
                        '<div class="title_text ' + titlevisible_ + '">' + videos[i].title + '</div>' +
                        '<div class="text ' + textvisible_ + '">' + videos[i].text + '</div>' +
                        '</div>' +
                        '</div>' +
                        '</li>';
            }
            $("#playlist").append(appending);
        }

        $('ul').find('li:first').addClass('active');

        function descripcion() {
            var activeIndex = player.playlist.currentItem();
            $(titulovideo).html(videos[activeIndex].title);
            $(descripcionvideo).html(videos[activeIndex].text);
        }

        function liactivo() {
            var activeIndex = player.playlist.currentItem() + 1;
            $('li').removeClass('active');
            $('li[data-videoplaylist="' + activeIndex + '"]').addClass('active');
        }

        function unlock() {
            /*var contador = 0;
             for (var i = 0; i < videos.length; i++) {
             if (videos[i].obligatorio == 1)
             {
             contador++;
             }
             }
             if (player.playlist.currentItem() + 1 == contador) {
             for (var dex = 0; dex < videos.length; dex++) {
             $('[data-videoplaylist="' + dex + '"]').on("click", function (e) {
             var clicked = e.target.nodeName === 'LI' ? $(e.target) : $(e.target).closest('li');
             var videoIndex = clicked.data('videoplaylist');
             var player_ = videojs('video');
             player_.playlist(videos)
             player_.playList.currentItem(dex++);
             liactivo();
             });
             }
             termino = true;
             funcionAlTerminar();
             }*/
            termino = true;
            funcionAlTerminar();
        }

    });

}

function addNewButton(data) {
    var myPlayer = data.player,
            controlBar,
            newElement = document.createElement('div'),
            newLink = document.createElement('div');

    newElement.id = data.id;
    newElement.className = 'btnStyle vjs-control';

    newLink.innerHTML = "<div class='" + data.icon + "'></div>";
    newElement.appendChild(newLink);
    controlBar = document.getElementsByClassName('vjs-control-bar')[0];
    insertBeforeNode = document.getElementsByClassName('vjs-fullscreen-control')[0];
    controlBar.insertBefore(newElement, insertBeforeNode);
    return newElement;
}

function resume() {
    var player_ = videojs('video');
    player_.play();
}

function next() {
    autoplay = false;
    var player_ = videojs('video');
    player_.playlist.next();
    var activeIndex;
    if (usandoVideoLocal) {
        if (player_.playlist.currentItem() >= 0) {
            if (player_.playlist.currentItem() + 1 < videos.length) {
                activeIndex = player_.playlist.currentItem() + 1;
            }
            else {
                activeIndex = player_.playlist.currentItem();
            }
        }
    } else {
        activeIndex = player_.playlist.currentItem();
    }
    $('li').removeClass('active');
    $('li[data-videoplaylist="' + activeIndex + '"]').addClass('active');
}

function prev() {
    autoplay = false;
    var player_ = videojs('video');
    player_.playlist.previous();
    var activeIndex;
    if (usandoVideoLocal) {
        if (player_.playlist.currentItem() > 0) {
            activeIndex = player_.playlist.currentItem() -1;
        }
        else {
            activeIndex = player_.playlist.currentItem();
        }
    } else {
        activeIndex = player_.playlist.currentItem();
    }
    $('li').removeClass('active');
    $('li[data-videoplaylist="' + activeIndex + '"]').addClass('active');
}

function first() {
    autoplay = false;
    var player_ = videojs('video');
    player_.playlist.first();
    var activeIndex;
    if (usandoVideoLocal) {
        activeIndex = 0;
    } else {
        activeIndex = player_.playlist.currentItem();
    }
    $('li').removeClass('active');
    $('li[data-videoplaylist="' + activeIndex + '"]').addClass('active');
}

function last() {
    autoplay = false;
    var player_ = videojs('video');
    player_.playlist.last();
    var activeIndex;
    if (usandoVideoLocal) {
        activeIndex = videos.length - 1;
    } else {
        activeIndex = player_.playlist.currentItem();
    }
    $('li').removeClass('active');
    $('li[data-videoplaylist="' + activeIndex + '"]').addClass('active');
}
