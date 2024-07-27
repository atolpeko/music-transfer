package com.mf.auth.port;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;

import java.util.Optional;

/**
 * Interface for managing JWTs in a repository.
 */
public interface JWTRepositoryPort {

    /**
     * Find a JWT by its value.
     *
     * @param value  JWT value
     *
     * @return found JWT or Optional.empty() if none found
     *
     * @throws DataAccessException in repository is unavailable
     */
    Optional<JWT> findByValue(String value);

    /**
     * Find a JWT by its access token.
     *
     * @param token  access token
     *
     * @return found JWT or Optional.empty() if none found
     *
     * @throws DataAccessException in repository is unavailable
     */
    Optional<JWT> findByAccessToken(String token);

    /**
     * Save the specified JWT.
     *
     * @param jwt  JWT to save
     *
     * @throws DataModificationException  if a JWT cannot be saved
     * @throws DataAccessException        in repository is unavailable
     */
    void save(JWT jwt);

    /**
     * Delete a JWT bt its ID.
     *
     * @param id  JWT ID
     *
     * @throws DataModificationException  if a JWT cannot be deleted
     * @throws DataAccessException        in repository is unavailable
     */
    void deleteById(String id);
}
