package com.mf.auth.port;

import com.mf.auth.domain.entity.Token;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;

import java.util.Optional;

/**
 * Interface for managing UUIDs in a repository.
 */
public interface UUIDRepositoryPort {

    /**
     * Find a valid UUID by its value.
     *
     * @param value  UUID value
     *
     * @return found UUID or Optional.empty() if none found
     *
     * @throws DataAccessException in repository is unavailable
     */
    Optional<Token> findValidByValue(String value);

    /**
     * Save the specified UUID.
     *
     * @param uuid  UUID to save
     *
     * @throws DataModificationException  if a UUID cannot be saved
     * @throws DataAccessException        in repository is unavailable
     */
    void save(Token uuid);
}
