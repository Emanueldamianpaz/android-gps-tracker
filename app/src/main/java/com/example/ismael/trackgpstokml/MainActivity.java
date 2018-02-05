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

    private static TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonGPS = findViewById(R.id.botonGPS);
        botonMaps = findViewById(R.id.botonMaps);
        texto = findViewById(R.id.texto);
        registro = new RegistradorKML();
        gps = new GPSTracker(this, registro);

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
            
            registro.abrirFichero();
            gps.toggleLocationUpdates(true);
        }

        // En el caso que estamos esperando a parar
        else {
            botonGPS.setText(R.string.texto_iniciar);
            botonMaps.setVisibility(Button.VISIBLE);

            gps.toggleLocationUpdates(false);
            registro.cerrarFichero();
        }
    }
}
