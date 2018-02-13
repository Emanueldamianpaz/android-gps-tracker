package com.example.ismael.trackgpstokml;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

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
 * Activity de Maps generada con: Clic derecho > New > Google > Maps activity.
 * Se genera esta clase y la que está en res > values > google_maps_api.xml.
 * Tienes que mirarlo y hacer lo que pone.
 * Como habrá que recorrer un fichero, tenemos que hacerlo con AsyncTask por si fuera muy largo.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //==============================================================================================
    // ATRIBUTOS
    //==============================================================================================
    private GoogleMap mapa;

    // Esto lo hemos creado para poder usarlo en el AsyncTask.
    private SAXParser parser;
    private SaxHandler handler;

    //==============================================================================================
    // MÉTODOS SOBREESCRITOS
    //==============================================================================================
    /**
     * Este se deja tal cual se ha generado.
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
     * Este método ejecuta las acciones que le programemos antes de abrir el mapa(leer el KML con los puntos).
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
        // Aquí se lee el KML y se ponen los puntos.

        mapa = googleMap;

        // Lo que hacemos aquí es leer el KML con SAX y llenar el mapa de puntos.
        // Hay que hacerlo con AsyncTask porque si hay muchos puntos peta.
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            parser = factory.newSAXParser();

            // Manejador SAX programado por nosotros. Le pasamos nuestro mapa para que ponga los puntos.
            handler = new SaxHandler(mapa);

            // AsyncTask. Le pasamos el directorio de ficheros como string.
            ProcesarKML procesador = new ProcesarKML();
            procesador.execute(this.getFilesDir().getAbsolutePath());

        } catch (SAXException e) { System.out.println(e.getMessage());
        } catch (ParserConfigurationException e) { System.out.println(e.getMessage()); }
    }

    //==============================================================================================
    // ASYNCTASK - TAREA ASÍNCRONA
    //==============================================================================================
    private class ProcesarKML extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                parser.parse(new FileInputStream(new File(strings[0], RegistradorKML.KML_NOMBRE_FICHERO)), handler);

            } catch (FileNotFoundException e) {
                Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } catch (SAXException e) {
                Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mapa.addPolyline(handler.getRuta()); // Se añade una ruta.

            // Se añade un punto en el mapa.
            //mapa.addMarker(new MarkerOptions().position(handler.coordenadas).title("hola"));

            // Se mueve la cámara a la última posición.
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom( handler.getLastCoordenadas(), 15));
        }
    }
}