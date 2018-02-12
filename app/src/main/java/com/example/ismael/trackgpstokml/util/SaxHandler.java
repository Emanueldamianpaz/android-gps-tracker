package com.example.ismael.trackgpstokml.util;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de SAX que especifíca que se tiene que hacer mientras se lee el fichero KML
 */
@Deprecated
public class SaxHandler extends DefaultHandler {

    private GoogleMap mapa;

    // Variables que necesitemos en SAX
    private boolean dentroEtiqueta;
    private String textoLeido;

    LatLng coordenadas; // Coordenadas de 1 punto

    List<LatLng> linea; // Lista de puntos para hacer una ruta (si quieres hacerla)
    PolylineOptions ruta; // Esto será la ruta

    /* -------------------- Constructor -------------------- */

    public SaxHandler(GoogleMap mapa){
        this.mapa = mapa;
        dentroEtiqueta = false;
        linea = new ArrayList<>();
        ruta = new PolylineOptions();
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

                try {
                    // Cogemos coordenadas mediante los indexOf de las comas. Para saber si hay que sumar o restar al índice haz pruebas con SYSO
                    Double latitud = Double.parseDouble(textoLeido.substring(0, textoLeido.indexOf(',')));
                    Double longitud = Double.parseDouble(textoLeido.substring(textoLeido.indexOf(',') + 1, textoLeido.lastIndexOf(',')));
                    //Double altura = Double.parseDouble(textoLeido.substring(textoLeido.lastIndexOf(',')+1, textoLeido.length()));

                    coordenadas = new LatLng(latitud, longitud);

                    // Si queremos añadir los puntos por marcadores
                    // mapa.addMarker(new MarkerOptions().position(coordenadas));

                    // Si queremos crear una ruta
                    linea.add(coordenadas);
                }catch(Exception e){ System.out.println("Saltando coordenadas errróneas: " + textoLeido); }
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
        if(!linea.isEmpty()) {
            ruta.addAll(linea).color(Color.BLUE);
        }else
            System.out.println("Error, no hay puntos o no hay fichero.");


    }


    public PolylineOptions getRuta(){
        return ruta;
    }

    public LatLng getLastCoordenadas(){
        return coordenadas;
    }

}