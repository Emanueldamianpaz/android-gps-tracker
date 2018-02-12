package com.example.ismael.trackgpstokml.threads;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.ismael.trackgpstokml.R;
import com.example.ismael.trackgpstokml.util.RegistradorKML;
import com.example.ismael.trackgpstokml.util.SaxHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Activity de maps generada con: click derecho + new, Google, Maps activity
 * Se genera esta clase y la que está en res -> values -> google_maps_api.xml. Tienes que mirarlo y hacer lo que pone
 * Como habrá que recorrer un fichero, tenemos que hacerlo con asynctask por si fuera muy largo.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Esto lo hemos creado para poder usarlo en el asynctask
    private SAXParser parser;
    private SaxHandler handler;

    /**
     * Esto se deja tal cual se ha generado
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Ejecuta las acciones que le programemos antes de abrir el mapa (leer el kml con los puntos)
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        /* Aquí se lee el kml y se ponen los puntos */

        mMap = googleMap;

        /* Lo que hacemos aquí es leer el kml con sax y llenar el mapa de puntos.
         * Hay que hacerlo con asynctask porque si hay muchos puntos peta. */
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            parser = factory.newSAXParser();
            // Manejador SAX programado por nosotros. Le pasamos nuestro mapa para que ponga los puntos.
            handler = new SaxHandler(mMap);

            // AsyncTask. Le pasamos el directorio de ficheros como string
            ProcesarKML procesador = new ProcesarKML();
            procesador.execute(this.getFilesDir().getAbsolutePath());

        } catch (SAXException e) { System.out.println(e.getMessage());
        } catch (ParserConfigurationException e) { System.out.println(e.getMessage()); }

    }

    /* =============================== AsyncTask =============================== */

    private class ProcesarKML extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                parser.parse(new FileInputStream(new File(strings[0], RegistradorKML.FICHERO)), handler);

            } catch (FileNotFoundException e) { e.printStackTrace();
            } catch (SAXException e) { e.printStackTrace();
            } catch (IOException e) { e.printStackTrace(); }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mMap.addPolyline(handler.getRuta());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( handler.getLastCoordenadas(), 15));
        }
    }
}
