package com.ecs.android.sample.oauth2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ecs.android.sample.oauth2.asynctasks.GetAccessTokenFromCodeTask;
import com.ecs.android.sample.oauth2.store.SharedPreferencesCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;

/**
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the
 * request. After the request is authorized by the user, the callback URL will
 * be intercepted here.
 * 
 */
public class OAuthAccessTokenActivity extends Activity {

	final String TAG = getClass().getName();

	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Starting task to retrieve request token.");
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// new OAuthRequestTokenTask(this).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(false);
		webview.setVisibility(View.VISIBLE);
		setContentView(webview);

		GoogleClientSecrets secrets = new GoogleClientSecrets();

		Details details = new Details();
		details.setClientId(OAuth2ClientCredentials.CLIENT_ID);
		details.setClientSecret(OAuth2ClientCredentials.CLIENT_SECRET);

		secrets.setInstalled(details);

		List<String> scopes = new ArrayList<String>();
		scopes.add(OAuth2ClientCredentials.SCOPE);

		
		String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(
				secrets, OAuth2ClientCredentials.REDIRECT_URI, scopes)
				.setAccessType("offline").setApprovalPrompt("force").build();

		/* WebViewClient must be set BEFORE calling loadUrl! */
		webview.setWebViewClient(new WebViewClient() {
			Boolean isTokenSaved = false;
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap bitmap) {
				System.out.println("AccessToken: "
						+ new SharedPreferencesCredentialStore(prefs).read()
								.getAccessToken());

				System.out.println("onPageStarted : " + url);

			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if(!isTokenSaved)
				if (url.startsWith(OAuth2ClientCredentials.REDIRECT_URI)) {

					if (url.indexOf("code=") != -1) {
						String code = extractCodeFromUrl(url);

						AsyncTask<String,Void,Boolean> task = new GetAccessTokenFromCodeTask(getApplicationContext()).execute(code);
						
						
						try {
							isTokenSaved = task.get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						view.setVisibility(View.INVISIBLE);
						startActivity(new Intent(OAuthAccessTokenActivity.this,
								LatitudeApiSample.class));
					} else if (url.indexOf("error=") != -1) {
						view.setVisibility(View.INVISIBLE);
						new SharedPreferencesCredentialStore(prefs)
								.clearCredentials();
						startActivity(new Intent(OAuthAccessTokenActivity.this,
								LatitudeApiSample.class));
					}

				}
				System.out.println("onPageFinished : " + url);
			}

			private String extractCodeFromUrl(String url) {
				return url.substring(
						OAuth2ClientCredentials.REDIRECT_URI.length() + 7,
						url.length());
			}
		});

		webview.loadUrl(authorizationUrl);
	}

}
