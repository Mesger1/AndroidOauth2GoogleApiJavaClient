package com.ecs.android.sample.oauth2.asynctasks;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.ecs.android.sample.oauth2.OAuth2ClientCredentials;
import com.ecs.android.sample.oauth2.store.CredentialStore;
import com.ecs.android.sample.oauth2.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GetAccessTokenFromCodeTask extends
		AsyncTask<String, Void, Boolean> {
	private Context context;

	public GetAccessTokenFromCodeTask(Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String code = params[0];
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		TokenResponse response = null;
		try {
			response = new GoogleAuthorizationCodeTokenRequest(
					new NetHttpTransport(), new JacksonFactory(),
					OAuth2ClientCredentials.CLIENT_ID,
					OAuth2ClientCredentials.CLIENT_SECRET, code,
					OAuth2ClientCredentials.REDIRECT_URI).setGrantType(
					"authorization_code").execute();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (response != null) {
			CredentialStore credentialStore = new SharedPreferencesCredentialStore(
					prefs);
			credentialStore.write(response);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
}
