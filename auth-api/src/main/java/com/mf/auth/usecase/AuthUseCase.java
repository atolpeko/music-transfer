package com.mf.auth.usecase;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;

public interface AuthUseCase {

    /**
     * Generate a new UUID.
     *
     *
     * @return UUID
     *
     * @throws RepositoryAccessException  if fails to save generated UUID
     *                                    because of some repository error
     */
    Token generateUuid();

    /**
     * Encrypt a string using a secret.
     *
     * @param toEncrypt  string to encrypt
     * @param secret     secret
     *
     * @return encrypted token
     */
    String encryptWithSecret(String toEncrypt, String secret);

    /**
     * Decrypt a string using a secret.
     *
     * @param toDecrypt  string to decrypt
     * @param secret     secret
     *
     * @return decrypted token
     */
    String decryptWithSecret(String toDecrypt, String secret);

    /**
     * Obtain OAuth2 token using authorization code and return
     * new JWT single-use access token.
     *
     * @param uuid      UUID
     * @param service   music service name
     * @param authCode  authorization code to be exchanged for an OAuth2 token
     *
     * @return JWT single-use access token
     *
     * @throws AuthorizationException  if OAuth2 authorization fails
     * @throws AuthorizationException  if the provided UUID is invalid
     */
    AccessToken auth(String uuid, String service, String authCode);

    /**
     * Obtain OAuth2 token using authorization code and return
     * updated JWT single-use access token.
     *
     * @param uuid      UUID
     * @param jwt       existing JWT
     * @param service   music service name
     * @param authCode  authorization code to be exchanged for an OAuth2 token
     *
     * @return JWT single-use access token
     *
     * @throws AuthorizationException  if OAuth2 authorization fails
     * @throws AuthorizationException  if the provided UUID is invalid
     * @throws AuthorizationException  if the provided JWT is invalid
     */
    AccessToken auth(String uuid, String jwt, String service, String authCode);
}
