/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//Iconos bicicletas
var biciLibre = L.icon({
    iconUrl: 'img/mapa/bicicleta_libre.png',

    iconSize: [57, 33], // size of the icon
    iconAnchor: [22, 94], // point of the icon which will correspond to marker's location
    popupAnchor: [-3, -76] // point from which the popup should open relative to the iconAnchor
});
var biciSeleccionada = L.icon({
    iconUrl: 'img/mapa/bicicleta_seleccionada.png',

    iconSize: [57, 33], // size of the icon
    iconAnchor: [22, 94], // point of the icon which will correspond to marker's location
    popupAnchor: [-3, -76] // point from which the popup should open relative to the iconAnchor
});
//Creacion del mapa
const PROVEEDOR = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
const VALLADOLID = [41.6520, -4.7286];
const ZOOM = 13;

var mapa = L.map('mapaglobal').setView(VALLADOLID, ZOOM);
L.tileLayer(PROVEEDOR, {
    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(mapa);

console.log("Mapa creado");
//Obtencion de las coordenadas de las bicis a traves de peticion al servlet
var ids = [];
$.get('BicicletasCoordenadas', function (data) {
    console.log(data);
    var ubicaciones = JSON.parse(data);
    for (i = 0; i < ubicaciones.length; i++) {
        var id = ubicaciones[i].id;
        var lat = ubicaciones[i].lat;
        var lon = ubicaciones[i].lon;
        ids.push({lat: lat, lon: lon, id: id});
        var marcador = L.marker([lat, lon], {icon: biciLibre}).addTo(mapa).on('click', function (e) {
            $("#bicicletaUbicacion").text(e.latlng);
            for (j = 0; j < ids.length; j++) {
                if (Math.abs(e.latlng.lat - ids[j].lat) < 0.001 && Math.abs(e.latlng.lng - ids[j].lon) < 0.001) {
                    $("#bicicletaId").val(ids[j].id);
                    break;
                }
            }

        });
        ;
    }
});