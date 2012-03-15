package com.ecs.android.sample.oauth2.store;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.api.client.auth.oauth2.TokenResponse;

public class SharedPreferencesCredentialStore implements CredentialStore {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES_IN = "expires_in";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE = "scope";

	private SharedPreferences prefs;

	public SharedPreferencesCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	public TokenResponse read() {
		TokenResponse tokenResponse = new TokenResponse();
		tokenResponse.setAccessToken(prefs.getString(ACCESS_TOKEN, ""));
		tokenResponse.setExpiresInSeconds(prefs.getLong(EXPIRES_IN, 0));;
		tokenResponse.setRefreshToken(prefs.getString(REFRESH_TOKEN, ""));
		tokenResponse.setScope(prefs.getString(SCOPE, ""));
		return tokenResponse;
	}

	
	public void write(TokenResponse tokenResponse) {
		Editor editor = prefs.edit();
		if (tokenResponse.getAccessToken()!=null) editor.putString(ACCESS_TOKEN,tokenResponse.getAccessToken());
		if (tokenResponse.getExpiresInSeconds()!=null) editor.putLong(EXPIRES_IN,tokenResponse.getExpiresInSeconds());
		if (tokenResponse.getRefreshToken()!=null) editor.putString(REFRESH_TOKEN,tokenResponse.getRefreshToken());
		if (tokenResponse.getScope()!=null) editor.putString(SCOPE,tokenResponse.getScope());
		editor.commit();
	}

	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(EXPIRES_IN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(SCOPE);
		editor.commit();
	}
}
