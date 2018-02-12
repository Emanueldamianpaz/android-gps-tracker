package com.example.ismael.trackgpstokml;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ismael.trackgpstokml.threads.GPSTracker;
import com.example.ismael.trackgpstokml.threads.MapsActivity;
import com.example.ismael.trackgpstokml.util.RegistradorKML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static ToggleButton botonGPS;
    private static Button botonMaps;
    private static GPSTracker gps;
    private static RegistradorKML registro;

    private static TextView texto;

    /* ============================ OnCreate ============================ */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ---------------------------- Inicializamos ----------------------------*/

        botonGPS = findViewById(R.id.botonGPS);
        botonMaps = findViewById(R.id.botonMaps);
        texto = findViewById(R.id.texto);

        registro = new RegistradorKML(this);
        gps = new GPSTracker(this, registro);

        /* ---------------------------- Botones ----------------------------*/

        botonGPS.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gestiona las acciones a tomar por el programa
                alternarAccion(botonGPS.isChecked());

            }
        });

        botonMaps.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Aquí se lanza el intent de maps

                Intent i = new Intent(getApplication(), MapsActivity.class);
                startActivity(i);
            }
        });
    }

    /* ============================ Métodos ============================ */

    private void alternarAccion(boolean activado){
        // En el caso de encender el botón
        if(activado) {

            // Con esto evitamos que el usuario abra maps antes de guardar el fichero
            botonMaps.setVisibility(Button.INVISIBLE);

            // Abrimos el fichero (sobreescribe) y comienza los updates
            registro.abrirFichero();
            gps.toggleLocationUpdates(true);
        }

        // En el caso de parar el botón
        else {
            botonMaps.setVisibility(Button.VISIBLE);

            gps.toggleLocationUpdates(false);
            registro.cerrarFichero();

            // Muestra el fichero en pantalla (debug)
            mostrarFichero();
        }
    }

    public void mostrarFichero(){
        // Esto es para mostrar el KML en pantalla y ver fallos
        try {
            InputStreamReader flujo = new FileReader(new File(this.getFilesDir(), RegistradorKML.FICHERO));
            BufferedReader filtroLectura = new BufferedReader(flujo);
            texto.setText("\n");
            do {
                String linea = filtroLectura.readLine();
                if (linea != null)
                    texto.append(linea);
                else
                    break;
            } while (true);
            filtroLectura.close();
            flujo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
