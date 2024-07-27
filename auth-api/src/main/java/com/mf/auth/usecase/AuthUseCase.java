package com.mf.auth.usecase;

import com.mf.auth.domain.entity.Token;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;

public interface AuthUseCase {

    /**
     * Obtain UUID code. Validates provided UUID.
     * Generates a new UUID if none uuid passed.
     *
     * @param uuid  optional UUID
     *
     * @return UUID
     *
     * @throws AuthorizationException     if provided UUID is invalid
     * @throws RepositoryAccessException  if fails to save generated UUID
     *                                    because of some repository error
     */
    Token obtainUuid(String uuid);

    /**
     * Check if a UUID is valid.
     *
     * @param uuidValue  UUID to check
     *
     * @return true if the UUID is valid, false otherwise
     *
     * @throws RepositoryAccessException  if some repository error occurs
     */
    boolean isUuidValid(String uuidValue);

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
    Token auth(String uuid, String service, String authCode);

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
     * @throws AuthorizationException  if such a JWT doesn't exist
     */
    Token auth(String uuid, String jwt, String service, String authCode);
}
