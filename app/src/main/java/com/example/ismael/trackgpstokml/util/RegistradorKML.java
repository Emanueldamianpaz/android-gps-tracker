package com.example.ismael.trackgpstokml.util;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  Clase que manipula el fichero kml de texto
 * Created by Ismael on 05/02/2018.
 */
@Deprecated
public class RegistradorKML {

    public static String FICHERO = "ruta.kml";

    private static final String KML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
            "  <Placemark>\n";

    private static final String KML_FOOTER =
            "</Placemark>\n</kml>";

    private File fichero;
    private FileWriter flujoSalida;
    private PrintWriter filtroSalida;

    /* ============================ Constructores ============================ */

    public RegistradorKML(Context context){
        fichero = new File(context.getFilesDir(), FICHERO);
    }

    /* ============================ Métodos ============================ */

    public void abrirFichero(){
        try {
            flujoSalida = new FileWriter(fichero);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.write(KML_HEADER);

            filtroSalida.close();
            flujoSalida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarFichero(){
        try {
            flujoSalida = new FileWriter(fichero, true);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.append(KML_FOOTER);

            filtroSalida.close();
            flujoSalida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPoint(double latitud, double longitud, double altura){
        try {
            if(latitud == 0 || longitud == 0)
                throw new IOException("Coordenadas nulas.");

            flujoSalida = new FileWriter(fichero, true);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.append("<point>\n    <coordinates> ");
            filtroSalida.append(latitud + ","+ longitud+","+ altura);
            filtroSalida.append(" </coordinates> \n</point>\n");

            filtroSalida.close();
            flujoSalida.close();
        } catch (IOException e) { System.out.println("Se han saltado coordenadas por ser nulas.");}
    }


}
