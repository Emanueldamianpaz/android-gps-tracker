package com.example.ismael.trackgpstokml;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class GPSTracker extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks,
		LocationListener {

	private static final String LOGTAG = "android-localizacion";

	private static final int PETICION_PERMISO_LOCALIZACION = 101;
	private static final int PETICION_CONFIG_UBICACION = 201;

	private GoogleApiClient apiClient;
	private Activity contexto;
	private RegistradorKML registrador;

	private TextView lblLatitud;
	private TextView lblLongitud;
	private ToggleButton btnActualizar;

	private LocationRequest locRequest;

	public GPSTracker(Activity contexto, RegistradorKML registrador){
		//Construcción cliente API Google
		apiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addConnectionCallbacks(this)
				.addApi(LocationServices.API)
				.build();
		this.contexto = contexto;
		this.registrador = registrador;
	}

	public void toggleLocationUpdates(boolean enable) {
		if (enable) {
			enableLocationUpdates();
		} else {
			disableLocationUpdates();
		}
	}

	private void enableLocationUpdates() {

		locRequest = new LocationRequest();
		locRequest.setInterval(2000);
		locRequest.setFastestInterval(1000);
		locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		LocationSettingsRequest locSettingsRequest =
				new LocationSettingsRequest.Builder()
						.addLocationRequest(locRequest)
						.build();

		PendingResult<LocationSettingsResult> result =
				LocationServices.SettingsApi.checkLocationSettings(
						apiClient, locSettingsRequest);

		result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
			@Override
			public void onResult(LocationSettingsResult locationSettingsResult) {
				final Status status = locationSettingsResult.getStatus();
				switch (status.getStatusCode()) {
					case LocationSettingsStatusCodes.SUCCESS:

						Log.i(LOGTAG, "Configuración correcta");
						startLocationUpdates();

						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						try {
							Log.i(LOGTAG, "Se requiere actuación del usuario");
							status.startResolutionForResult(contexto, PETICION_CONFIG_UBICACION);
						} catch (IntentSender.SendIntentException e) {
							// Pasa algo raro al pedir configurar ubicacion
							btnActualizar.setChecked(false);
							Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
						}

						break;
					// No se pueede pedir cambio de configuracion
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
						btnActualizar.setChecked(false);
						break;
				}
			}
		});
	}

	private void disableLocationUpdates() {

		LocationServices.FusedLocationApi.removeLocationUpdates(
				apiClient, this);

	}

	private void startLocationUpdates() {
		if (ActivityCompat.checkSelfPermission(contexto,
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			//Ojo: estamos suponiendo que ya tenemos concedido el permiso.
			//Sería recomendable implementar la posible petición en caso de no tenerlo.

			Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

			LocationServices.FusedLocationApi.requestLocationUpdates(
					apiClient, locRequest, this);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		//Se ha producido un error que no se puede resolver automáticamente
		//y la conexión con los Google Play Services no se ha establecido.

		Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		//Conectado correctamente a Google Play Services

		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PETICION_PERMISO_LOCALIZACION);
		} else {

			Location lastLocation =
					LocationServices.FusedLocationApi.getLastLocation(apiClient);

			updateUI(lastLocation);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		//Se ha interrumpido la conexión con Google Play Services

		Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
	}

	/**
	 * Al actualizar locations
	 * @param loc
	 */
	private void updateUI(Location loc) {
		if (loc != null) {
			registrador.addPoint(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
		} else {
			// Coordenadas desconocidas
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PETICION_PERMISO_LOCALIZACION) {
			if (grantResults.length == 1
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				//Permiso concedido

				@SuppressWarnings("MissingPermission")
				Location lastLocation =
						LocationServices.FusedLocationApi.getLastLocation(apiClient);

				updateUI(lastLocation);

			} else {
				//Permiso denegado:
				//Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

				Log.e(LOGTAG, "Permiso denegado");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PETICION_CONFIG_UBICACION:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startLocationUpdates();
						break;
					case Activity.RESULT_CANCELED:
						Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");
						btnActualizar.setChecked(false);
						break;
				}
				break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i(LOGTAG, "Recibida nueva ubicación!");

		//Mostramos la nueva ubicación recibida
		updateUI(location);
	}
}


/*
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double altitud; //altitud

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("OPERADOR DE RED", "OPERADOR DE RED");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS HABILITADO", "GPS HABILITADO");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * * /
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}		
	}
	
	/**
	 * Function to get latitude
	 * * /
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * * /
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();


		}
		
		// return longitude
		return longitude;
	}

	/**
	 * Funcion para obtener la velocidad
	 * en metros/seg
	 * * /
	public float getSpeed(){
		float speed=0;
		if(location != null){
			 speed= location.getSpeed();


		}

		return speed;
	}

	/**
	 * Funcion para obtener la distancia a otro punto
	 * habria que pasarle lat1 , lon1
	 * distancia en metros
	 * * /
	public float getDistance(double lat1,double long1){

		float metros=0;
        float [] dist = new float[1];

		if(location != null){
			location.distanceBetween(getLatitude(),lat1,getLongitude(),long1, dist);
            metros= dist[0];

		}

		// return longitude
		return metros;
	}



	/**
	 * Function to get altitud
	 * * /
	public double getAltitud(){
		if(location != null){
			latitude = location.getAltitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function para chequear si el GPS/wifi esta habilitado
	 * @return boolean
	 * * /
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * * /
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS configuración");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS no esta habilitado. ¿Ir al menú de configuración?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
	    Location nueva;
	    nueva = location; // aqui tenemos los nuevos datos de localizacion


	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

        switch (status) {
            case LocationProvider.AVAILABLE:

                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
        }
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
*/