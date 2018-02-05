package com.example.ismael.trackgpstokml;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static Button botonGPS;
    private static Button botonMaps;
    private static GPSTracker gps;
    private static RegistradorKML registro;
    private static boolean registrando;

    private static TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonGPS = findViewById(R.id.botonGPS);
        botonMaps = findViewById(R.id.botonMaps);
        texto = findViewById(R.id.texto);
        gps = new GPSTracker(this);
        registro = new RegistradorKML();
        gps.stopUsingGPS();

        botonGPS.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                alternarAccion();

            }
        });
    }

    private static void alternarAccion(){
        // En el caso de que estemos esperando a iniciar
        if(botonGPS.getText().toString().equals(R.string.texto_iniciar)) {
            botonGPS.setText(R.string.texto_parar);
            registrando = true;
            do {
                if (gps.canGetLocation()) {
                    double latitud = gps.getLatitude();
                    double longitud = gps.getLongitude();
                    double altitud = gps.getAltitud();

                    registro.addPoint(latitud, longitud, altitud);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                    gps.showSettingsAlert();
            }while(registrando == true);
        }

        // En el caso que estamos esperando a parar
        else {
            botonGPS.setText(R.string.texto_iniciar);
            botonMaps.setVisibility(Button.VISIBLE);
            registro.cerrarFichero();
            registrando = false;

            // Vemos qué hemos registrado
            try {
                FileReader flujo = new FileReader(registro.fichero);
                BufferedReader filtroLectura = new BufferedReader(flujo);
                try {
                    do {
                        texto.append(filtroLectura.readLine());
                    } while (true);
                }catch (EOFException e){}
                filtroLectura.close();
                flujo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
