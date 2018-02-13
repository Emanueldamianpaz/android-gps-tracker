package com.example.ismael.trackgpstokml;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

/**
 * Clase de copia y pega a full. Los métodos interesantes son:
 * enableLocationUpdates() - Configura el refresco de gps
 * updateUI() - Se lanza en cada refresco (pesicola >.<)
 */
public class GPSTracker extends AppCompatActivity
		implements GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks,
		LocationListener {

	private static final String LOGTAG = "android-localizacion";

	private static final int PETICION_PERMISO_LOCALIZACION = 101;
	private static final int PETICION_CONFIG_UBICACION = 201;

	public GoogleApiClient apiClient;
	private Activity contexto;
	private RegistradorKML registrador;

	private LocationRequest locRequest;

	/* ============================ Constructor ============================ */

	public GPSTracker(Activity contexto, RegistradorKML registrador){
		this.contexto = contexto;
		this.registrador = registrador;
		//Construcción cliente API Google
		apiClient = new GoogleApiClient.Builder(this.contexto)
				.enableAutoManage(this, this)
				.addConnectionCallbacks(this)
				.addApi(LocationServices.API)
				.build();
		apiClient.connect();
	}

	/* ============================ Métodos ============================ */

	public void toggleLocationUpdates(boolean enable) {
		if (enable) {
			enableLocationUpdates();
		} else {
			disableLocationUpdates();
		}
	}

	/**
	 * TODO método importante
	 * Se configuran los updates entre otras cosas
	 */
	private void enableLocationUpdates() {

		locRequest = new LocationRequest();
		locRequest.setInterval(1000);
		locRequest.setFastestInterval(600);
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
							Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
						}

						break;
					// No se pueede pedir cambio de configuracion
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
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

		if (ActivityCompat.checkSelfPermission(this.contexto,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this.contexto,
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
	 * TODO método importante
	 * Se lanza periódicamente si hemos activado las actualizaciones
	 * @param loc
	 */
	private void updateUI(Location loc) {
		if (loc != null) {
			registrador.anhadirPunto(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
		} else {
			// Si entramos aquí es porque las coordenadas son desconocidas
		}
	}

	/**
	 * TODO Método interesante: gestiona lo que pasa cuando aceptamos/rechazamos permisos
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
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
				//Permiso denegado, desactivamos botón que hemos encendido

				Log.e(LOGTAG, "Permiso denegado");
				MainActivity.botonGPS.setChecked(false);
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
						break;
				}
				break;
		}
	}

	/**
	 * TODO Método que se lanza cuando recién actualiza
	 * @param location
	 */
	@Override
	public void onLocationChanged(Location location) {

		Log.i(LOGTAG, "Recibida nueva ubicación!");

		//Mostramos la nueva ubicación recibida
		updateUI(location);
	}
}
