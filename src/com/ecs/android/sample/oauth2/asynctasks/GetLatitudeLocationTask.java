package com.ecs.android.sample.oauth2.asynctasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.ecs.android.sample.oauth2.R;
import com.ecs.android.sample.oauth2.store.CredentialStore;
import com.ecs.android.sample.oauth2.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.latitude.Latitude;
import com.google.api.services.latitude.LatitudeRequest;
import com.google.api.services.latitude.model.Location;

public class GetLatitudeLocationTask extends AsyncTask<Void, Void, String> {

	private SharedPreferences prefs;
	private TextView textView;

	public GetLatitudeLocationTask(Activity context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.textView = (TextView) context.findViewById(R.id.response_code);
	}

	@Override
	protected String doInBackground(Void... params) {
		return performApiCall();
	}

	private String performApiCall() {
		try {

			CredentialStore credentialStore = new SharedPreferencesCredentialStore(
					prefs);
			TokenResponse tokenResponse = credentialStore.read();

			// set up Latitude
			NetHttpTransport transport = new NetHttpTransport();
			JacksonFactory factory = new JacksonFactory();

			Latitude latitude = Latitude
					.builder(transport, factory)
					.setApplicationName("Google-LatitudeSample/1.0")
					.setHttpRequestInitializer(
							createRequestFactoryWithAccessTokenOnly(transport,
									factory, tokenResponse).getInitializer())
					.setJsonHttpRequestInitializer(
							new JsonHttpRequestInitializer() {
								public void initialize(JsonHttpRequest request) {
									LatitudeRequest latitudeRequest = (LatitudeRequest) request;
									latitudeRequest.setPrettyPrint(true);
								}
							}).build();

			String locationAsString = convertLocationToString(latitude
					.currentLocation().get().execute());
			return locationAsString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error occured : " + ex.getMessage();
		}
	}

	public static HttpRequestFactory createRequestFactoryWithAccessTokenOnly(
			HttpTransport transport, JsonFactory jsonFactory,
			TokenResponse tokenResponse) {
		return transport.createRequestFactory(new Credential(BearerToken
				.authorizationHeaderAccessMethod())
				.setFromTokenResponse(tokenResponse));
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		textView.setText(result);
	}

	private String convertLocationToString(Location currentLocation) {
		String timestampMs = (String) currentLocation.get("timestampMs");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date d = new Date(Long.valueOf(timestampMs));
		String locationAsString = "Current location : "
				+ currentLocation.get("latitude") + " - "
				+ currentLocation.get("longitude") + " at " + df.format(d);
		return locationAsString;
	}

}
