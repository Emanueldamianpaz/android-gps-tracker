package com.example.ismael.trackgpstokml;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static ToggleButton botonGPS;
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
        gps.apiClient.connect();

        botonGPS.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                alternarAccion(botonGPS.isChecked());

            }
        });

        botonMaps.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // te imaginas que se ve el mapa?

            }
        });
    }

    /* ============================ Métodos ============================ */

    private static void alternarAccion(boolean activado){
        // En el caso de que estemos esperando a iniciar
        if(activado) {

            registro.abrirFichero();
            gps.toggleLocationUpdates(true);
        }

        // En el caso que estamos esperando a parar
        else {
            botonMaps.setVisibility(Button.VISIBLE);

            gps.toggleLocationUpdates(false);
            registro.cerrarFichero();
            mostrarFichero();
        }
    }

    public static void mostrarFichero(){
        // Vemos qué hemos registrado
        try {
            InputStreamReader flujo = new FileReader(registro.fichero);
            BufferedReader filtroLectura = new BufferedReader(flujo);
            texto.setText("");
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
