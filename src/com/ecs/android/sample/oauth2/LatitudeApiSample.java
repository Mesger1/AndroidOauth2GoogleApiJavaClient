package com.ecs.android.sample.oauth2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.ecs.android.sample.oauth2.asynctasks.GetLatitudeLocationTask;
import com.ecs.android.sample.oauth2.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class LatitudeApiSample extends Activity {

	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
		Button clearCredentials = (Button) findViewById(R.id.btn_clear_credentials);

		// Launch the OAuth flow to get an access token required to do
		// authorized API calls.
		// When the OAuth flow finishes, we redirect to this Activity to perform
		// the API call.
		launchOauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(v.getContext(),
						OAuthAccessTokenActivity.class));
			}
		});

		// Clearing the credentials and performing an API call to see the
		// unauthorized message.
		clearCredentials.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearCredentials();
				performApiCall();
			}

		});

		// Performs an authorized API call.
		performApiCall();

	}

	/**
	 * Clears our credentials (token and token secret) from the shared
	 * preferences. We also setup the authorizer (without the token). After
	 * this, no more authorized API calls will be possible.
	 */
	private void clearCredentials() {
		new SharedPreferencesCredentialStore(prefs).clearCredentials();
	}

	/**
	 * Performs an authorized API call.
	 */
	private void performApiCall() {
		try {
			new GetLatitudeLocationTask(this).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HttpRequestFactory createRequestFactoryWithAccessTokenOnly(
			HttpTransport transport, JsonFactory jsonFactory,
			TokenResponse tokenResponse) {
		return transport.createRequestFactory(new Credential(BearerToken
				.authorizationHeaderAccessMethod())
				.setFromTokenResponse(tokenResponse));
	}



}