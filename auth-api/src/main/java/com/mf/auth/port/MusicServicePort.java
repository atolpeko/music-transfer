package com.mf.auth.port;

import com.mf.auth.port.exception.AuthException;
import com.mf.auth.port.exception.MusicServiceException;
import com.mf.auth.domain.entity.OAuth2Token;

/**
 * Music service interface.
 */
public interface MusicServicePort {

	/**
	 * Exchange authorization code for OAuth2 access token.
	 *
	 * @param authCode  authorization code to be exchanged for an OAuth2 token
	 *
	 * @return OAuth2 access token
	 *
	 * @throws AuthException          if OAuth2 authorization fails
	 * @throws MusicServiceException  if any other music service error occurs
	 */
	OAuth2Token oauth2ExchangeCode(String authCode);
}
