package com.example.ismael.trackgpstokml;

import android.graphics.Color;
import android.widget.Toast;

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
public class SaxHandler extends DefaultHandler {
    //==============================================================================================
    // VARIABLES
    //==============================================================================================
    private GoogleMap mapa; // Mapa de Google.
    private LatLng punto; // Coordenadas de un punto.
    private List<LatLng> listaPuntos; // Lista de puntos.
    private PolylineOptions ruta; // Ruta.

    // Variables que necesitaremos en SAX.
    private boolean dentroEtiqueta;
    private String textoLeido;

    //==============================================================================================
    // CONSTRUCTOR
    //==============================================================================================
    public SaxHandler(GoogleMap mapa) {
        this.mapa = mapa;
        this.dentroEtiqueta = false;
        this.listaPuntos = new ArrayList<>();
        this.ruta = new PolylineOptions();
    }

    //==============================================================================================
    // MÉTODOS SOBREESCRITOS
    //==============================================================================================

    /**
     * Contenido de la etiqueta.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        // Recoge el contenido de la etiqueta.
        if (dentroEtiqueta) {
            textoLeido = new String(ch, start, length);
        }
    }

    /**
     * Comienzo del elemento.
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        dentroEtiqueta = true; // Para comprobación de seguridad.
    }

    /**
     * Final del elemento.
     * Aquí programamos las acciones a realizar al finalizar una etiqueta.
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);

        // Comprobaciones de seguridad que nos convengan según ejercicio.
        if (dentroEtiqueta) {
            if (localName.equals("coordinates")) {
                // Entraremos aquí cada vez que SAX lea un </coordinates>.

                try {
                    // Cogemos un punto mediante los indexOf de las comas.
                    // Para saber si hay que sumar o restar al índice haz pruebas con SYSO.
                    double latitud = Double.parseDouble(textoLeido.substring(0, textoLeido.indexOf(',')));
                    double longitud = Double.parseDouble(textoLeido.substring(textoLeido.indexOf(',') + 1, textoLeido.lastIndexOf(',')));
                    //double altura = Double.parseDouble(textoLeido.substring(textoLeido.lastIndexOf(',')+1, textoLeido.length()));

                    punto = new LatLng(latitud, longitud);

                    listaPuntos.add(punto); // Se añade un punto a la lista, para crear una ruta después.

                } catch (Exception e) {
                    System.out.println("Saltando punto errróneas: " + textoLeido);
                }
            }

            // Se resetean variables.
            textoLeido = "";
            dentroEtiqueta = false;
        }
    }

    /**
     * Comienzo del documento.
     */
    @Override
    public void startDocument() throws SAXException {
        // Iniciamos variables que hagan falta (lo hicimos en constructor)
    }

    /**
     * Final del documento.
     * Aquí creamos la ruta en el mapa y movemos la cámara aplicando un zoom
     */
    @Override
    public void endDocument() throws SAXException {
        // Añadimos la lista de puntos (el array de puntos que hemos ido guardando).
        if (!listaPuntos.isEmpty()) {
            ruta.addAll(listaPuntos).color(Color.BLUE);

        } else
            System.out.println("Error, no hay puntos o no hay fichero.");
    }

    //==============================================================================================
    // GETTERS
    //==============================================================================================

    /**
     * Obtener ruta.
     */
    public PolylineOptions getRuta() {
        return ruta;
    }

    /**
     * Obtener última coordenada.
     */
    public LatLng getLastCoordenadas() {
        return punto;
    }
}