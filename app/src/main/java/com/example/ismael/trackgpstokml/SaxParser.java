package com.example.ismael.trackgpstokml;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que lee el fichero kml
 */
public class SaxParser extends DefaultHandler {

    private GoogleMap mapa;

    private boolean dentroEtiqueta;
    private String textoLeido;

    LatLng coordenadas; // coordenadas de 1 punto

    List<LatLng> linea; // lista de puntos para hacer una ruta

    /* -------------------- Constructor -------------------- */

    public SaxParser(GoogleMap mapa){
        this.mapa = mapa;
        dentroEtiqueta = false;
        linea = new ArrayList<>();
    }

    /* -------------------- Contenido etiqueta -------------------- */

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        super.characters(ch, start, length);

        // Recoge el contenido de la etiqueta
        if (dentroEtiqueta)
            textoLeido = new String(ch, start, length);
    }

    /* -------------------- Comienzo elemento -------------------- */

    @Override
    public void startElement(String uri, String localName,
                             String name, Attributes attributes) throws SAXException {
        // Para comprobación de seguridad
        dentroEtiqueta = true;
    }

    /* -------------------- Final de elemento -------------------- */

    /**
     * Aquí programamos las acciones a realizar al finalizar una etiqueta
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        super.endElement(uri, localName, name);

        // Comprobaciones de seguridad que nos convengan según ejercicio
        if (dentroEtiqueta) {

            if (localName.equals("coordinates")) {

                /* Entraremos aquí cada vez que sax lea un </coordinates> */

                // Cogemos coordenadas mediante los indexOf de las comas
                Double latitud = Double.parseDouble(textoLeido.substring(0, textoLeido.indexOf(',')-1));
                Double longitud = Double.parseDouble(textoLeido.substring(textoLeido.indexOf(',')+1, textoLeido.lastIndexOf(',')-1));
                //Double altura = Double.parseDouble(textoLeido.substring(textoLeido.lastIndexOf(',')+1, textoLeido.length()));

                coordenadas = new LatLng(latitud, longitud);

                // Si queremos añadir los marcadores de puntos separados
                //mapa.addMarker(new MarkerOptions().position(coordenadas));

                // Si queremos crear una ruta
                linea.add(coordenadas);
           }

           // Reseteamos variables
           textoLeido = "";
           dentroEtiqueta = false;
        }
    }

    /* -------------------- Comienzo y final documento -------------------- */

    @Override
    public void startDocument() throws SAXException {
        // Iniciamos variables que hagan falta (lo hicimos en constructor)
    }

    /**
     * Aquí creamos la ruta en el mapa y movemos la cámara aplicando un zoom
     */
    @Override
    public void endDocument() throws SAXException {
        // Añadimos linea (el array de puntos que hemos ido guardando)
        mapa.addPolyline(new PolylineOptions().addAll(linea).color(Color.GREEN));

        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15));
    }

}