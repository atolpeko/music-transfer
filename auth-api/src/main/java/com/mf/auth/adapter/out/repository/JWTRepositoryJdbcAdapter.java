package com.mf.auth.adapter.out.repository;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;
import com.mf.auth.util.DateTimeUtils;

import java.sql.Timestamp;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

@RequiredArgsConstructor
public class JWTRepositoryJdbcAdapter implements JWTRepositoryPort {
    private static final String SELECT_BY_VALUE =
        "SELECT j.id AS jwt_id, j.token AS jwt_value, "
            + "j.expires_at AS jwt_expires_at, at.token AS at_value, "
            + "at.expires_at AS at_expires_at "
            + "FROM jwt j "
            + "INNER JOIN access_token at ON at.id = j.access_token_id "
            + "WHERE j.token = ? AND j.expires_at > CURRENT_TIMESTAMP";

    private static final String SELECT_BY_ACCESS_TOKEN =
        "SELECT j.id AS jwt_id, j.token AS jwt_value, "
            + "j.expires_at AS jwt_expires_at, at.token AS at_value, "
            + "at.expires_at AS at_expires_at "
            + "FROM access_token at "
            + "INNER JOIN jwt j ON at.id = j.access_token_id "
            + "WHERE at.token = ? AND at.expires_at > CURRENT_TIMESTAMP";

    private static final String INSERT_INTO_JWT =
        "INSERT INTO jwt(token, expires_at, access_token_id) VALUES (?, ?, ?)";

    private static final String INSERT_INTO_ACCESS_TOKEN =
        "INSERT INTO access_token(token, expires_at) VALUES (?, ?)";

    private static final String DELETE = "DELETE FROM jwt WHERE id = ?";

    private final JdbcTemplate jdbc;

    private final RowMapper<JWT> mapper = (resultSet, rowNum) ->
        new JWT(
            resultSet.getString("jwt_id"),
            resultSet.getString("jwt_value"),
            DateTimeUtils.fromString(resultSet.getString("jwt_expires_at")),
            new Token(
                resultSet.getString("at_value"),
                DateTimeUtils.fromString(resultSet.getString("at_expires_at"))
            )
        );

    @Override
    public Optional<JWT> findValidByValue(String value) {
        return findBy(SELECT_BY_VALUE, value);
    }

    public Optional<JWT> findBy(String query, String param) {
        try {
            var jwt = jdbc.queryForObject(query, mapper, param);
            return Optional.ofNullable(jwt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        }
    }

    @Override
    public Optional<JWT> findValidByAccessToken(String token) {
        return findBy(SELECT_BY_ACCESS_TOKEN, token);
    }

    @Override
    public void save(JWT jwt) {
        var id = saveAccessToken(jwt.getAccessToken());
        saveJwt(jwt, id);
    }

    private int saveAccessToken(Token token) {
        try {
            var keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                var ps = connection.prepareStatement(
                    INSERT_INTO_ACCESS_TOKEN,
                    new String[] {"id"}
                );
                ps.setString(1, token.getValue());
                ps.setTimestamp(2, Timestamp.valueOf(token.getExpiresAt()));
                return ps;
            }, keyHolder);

            return Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(RuntimeException::new)
                .intValue();
        } catch (DataAccessResourceFailureException e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        } catch (Exception e) {
            var msg = "Failed to save access token: " + e.getMessage();
            throw new DataModificationException(msg, e);
        }
    }

    private void saveJwt(JWT jwt, int accessTokenId) {
        try {
            Object[] params = { jwt.getValue(), jwt.getExpiresAt(), accessTokenId };
            jdbc.update(INSERT_INTO_JWT, params);
        } catch (DataAccessResourceFailureException e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        } catch (Exception e) {
            var msg = "Failed to save JWT: " + e.getMessage();
            throw new DataModificationException(msg, e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            jdbc.update(DELETE, id);
        } catch (DataAccessResourceFailureException e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        } catch (Exception e) {
            var msg = "Failed to delete JWT: " + e.getMessage();
            throw new DataModificationException(msg, e);
        }
    }
}
