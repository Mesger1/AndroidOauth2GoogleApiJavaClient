package com.ecs.android.sample.oauth2.store;

import com.google.api.client.auth.oauth2.TokenResponse;

public interface CredentialStore {

  TokenResponse read();
  void write(TokenResponse response);
  void clearCredentials();
}
