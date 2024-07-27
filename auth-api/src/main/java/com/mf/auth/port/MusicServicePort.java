package com.mf.auth.port;

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
	 * @throws MusicServiceException   if OAuth2 authorization fails
	 */
	OAuth2Token oauth2ExchangeCode(String authCode);
}
