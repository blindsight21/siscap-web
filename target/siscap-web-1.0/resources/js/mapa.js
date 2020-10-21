var currentMarker = null;
function handlePointClick(event) {
    if (currentMarker === null) {
        document.getElementById('lat').value = event.latLng.lat();
        document.getElementById('lng').value = event.latLng.lng();
        currentMarker = new google.maps.Marker({
            position: new google.maps.LatLng(event.latLng.lat(), event.latLng.lng())
        });
        PF('map').addOverlay(currentMarker);
        PF('dlg').show();
    }
}

function completed() {
    currentMarker.setMap(null);
    currentMarker = null;
    PF('dlg').hide();
}

function cancel() {
    PF('dlg').hide();
    currentMarker.setMap(null);
    currentMarker = null;
    return false;
}

function geocode() {
    PF('map').geocode(document.getElementById('buscarDireccion').value);
}