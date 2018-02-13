package com.example.ismael.trackgpstokml;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase principal del GPS Tracker.
 */
public class MainActivity extends AppCompatActivity {
    //==============================================================================================
    // VARIABLES
    //==============================================================================================
    private GPSTracker gps;
    private RegistradorKML registro;

    // Elementos visuales.
    public static ToggleButton botonGPS; // TODO: Quitar la variable estática y hacerle un get.
    private Button botonMaps;
    private TextView texto;

    //==============================================================================================
    // MÉTODOS SOBREESCRITOS
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de variables.
        botonGPS = findViewById(R.id.botonGPS);
        botonMaps = findViewById(R.id.botonMaps);
        texto = findViewById(R.id.texto);
        registro = new RegistradorKML(this);
        gps = new GPSTracker(this, registro);

        // Función botón GPS.
        botonGPS.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gestiona las acciones a tomar por el programa.
                alternarAccion(botonGPS.isChecked());
            }
        });

        // Función botón Maps.
        botonMaps.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí se lanza el intent de maps.
                Intent intent = new Intent(getApplication(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //==============================================================================================
    // MÉTODOS
    //==============================================================================================
    private void alternarAccion(boolean activado) {
        if (activado) { // En el caso de encender el botón.
            // Con esto evitamos que el usuario abra maps antes de guardar el fichero.
            botonMaps.setVisibility(Button.INVISIBLE);
            texto.setVisibility(TextView.INVISIBLE);

            // Abrimos el fichero (sobreescribe) y comienza los updates.
            registro.abrirFichero();
            gps.toggleLocationUpdates(true);
        }

        else { // En el caso de parar el botón.
            botonMaps.setVisibility(Button.VISIBLE);
            texto.setVisibility(TextView.VISIBLE);

            gps.toggleLocationUpdates(false);
            registro.cerrarFichero();

            // Muestra el fichero en pantalla (debug).
            mostrarFichero();
        }
    }

    /**
     * Para mostrar el KML en pantalla y ver fallos.
     */
    public void mostrarFichero() {
        InputStreamReader flujoLectura;
        BufferedReader filtroLectura;
        String linea;

        try {
            flujoLectura = new FileReader(new File(this.getFilesDir(), RegistradorKML.KML_NOMBRE_FICHERO));
            filtroLectura = new BufferedReader(flujoLectura);
            texto.setText("\n");

            linea = filtroLectura.readLine();

            while (linea != null) {
                texto.append(linea + "\n");

                linea = filtroLectura.readLine();
            }

            filtroLectura.close();
            flujoLectura.close();

        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}