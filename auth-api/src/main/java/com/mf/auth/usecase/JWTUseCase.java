package com.mf.auth.usecase;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.usecase.exception.AuthorizationException;

public interface JWTUseCase {

    /**
     * Obtain a JWT using a single-use access token.
     *
     * @param accessToken  access token
     *
     * @throws AuthorizationException  if the provided access token is invalid
     *
     * @return JWT
     */
    JWT obtain(String accessToken);

    /**
     * Validates if the specified JWT is valid.
     *
     * @param jwt  JWT to validate
     *
     * @return true if this JWT is valid, false otherwise
     */
    boolean isValid(String jwt);
}
