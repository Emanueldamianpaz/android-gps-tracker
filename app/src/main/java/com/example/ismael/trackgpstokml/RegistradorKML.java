package com.example.ismael.trackgpstokml;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ismael on 05/02/2018.
 */

public class RegistradorKML {

    private static final String KML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
            "  <Placemark>\n";

    private static final String KML_FOOTER =
            "</Placemark>\n</kml>";

    public File fichero;
    private FileWriter flujoSalida;
    private PrintWriter filtroSalida;

    /* ============================ Constructores ============================ */

    public RegistradorKML(Context context){
        fichero = new File(context.getFilesDir(), "ruta.kml");
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
            flujoSalida = new FileWriter(fichero, true);
            filtroSalida = new PrintWriter(flujoSalida);

            filtroSalida.append("<point>\n    <coordinates> ");
            filtroSalida.append(latitud + ", "+ longitud+", "+ altura);
            filtroSalida.append(" </coordinates> \n</point>\n");

            filtroSalida.close();
            flujoSalida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
