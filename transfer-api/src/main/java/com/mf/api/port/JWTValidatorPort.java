package com.mf.api.port;

import com.mf.api.port.exception.RestException;

public interface JWTValidatorPort {

	/**
	 * Validates if the specified JWT is valid.
	 *
	 * @param jwt  JWT to validate
	 *
	 * @return true if this JWT is valid, false otherwise
	 *
	 * @throws RestException  if fails to access validation service
	 */
	boolean isValid(String jwt);
}
