package com.example.ismael.trackgpstokml;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  Clase que manipula el fichero KML de texto.
 */
public class RegistradorKML {
    //==============================================================================================
    // CONSTANTES
    //==============================================================================================
    // KML.
    public static final String KML_NOMBRE_FICHERO = "ruta.kml";
    private static final String KML_CABECERA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
            "  <Placemark>";
    private static final String KML_PIE = "\n  </Placemark>\n</kml>";
    // GPX.
    public static final String GPX_NOMBRE_FICHERO = "ruta.gpx";
    private static final String GPX_CABECERA = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n\n" +
            "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" version=\"1.1\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
    private static final String GPX_PIE = "\n</gpx>";

    //==============================================================================================
    // ATRIBUTOS
    //==============================================================================================
    private File fichero;
    private static FileWriter flujoSalida;
    private static PrintWriter filtroSalida;
    private Context contexto;

    //==============================================================================================
    // CONSTRUCTOR
    //==============================================================================================
    public RegistradorKML(Context contexto){
        this.contexto = contexto;
        this.fichero = new File(contexto.getFilesDir(), KML_NOMBRE_FICHERO);
    }

    //==============================================================================================
    // MÉTODOS
    //==============================================================================================

    /**
     * Método que abre o crea el fichero, y escribe la cabecera.
     */
    public void abrirFichero(){
        try {
            flujoSalida = new FileWriter(fichero);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.write(KML_CABECERA);

            filtroSalida.close();
            flujoSalida.close();

        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método que añade un punto al fichero.
     */
    public void anhadirPunto(double latitud, double longitud, double altitud){
        try {
            flujoSalida = new FileWriter(fichero, true);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.append("\n    <point>\n      <coordinates> ");
            filtroSalida.append(latitud + ","+ longitud+","+ altitud);
            filtroSalida.append(" </coordinates> \n    </point>");

            filtroSalida.close();
            flujoSalida.close();

        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método que cierra el fichero, y es escribe el pie.
     */
    public void cerrarFichero(){
        try {
            flujoSalida = new FileWriter(fichero, true);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.append(KML_PIE);

            filtroSalida.close();
            flujoSalida.close();

        } catch (IOException e) {
            Toast.makeText(contexto, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}