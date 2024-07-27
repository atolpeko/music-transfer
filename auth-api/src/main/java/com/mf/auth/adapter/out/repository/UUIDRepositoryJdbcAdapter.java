package com.mf.auth.adapter.out.repository;

import com.mf.auth.domain.entity.Token;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;
import com.mf.auth.util.DateTimeUtils;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@RequiredArgsConstructor
public class UUIDRepositoryJdbcAdapter implements UUIDRepositoryPort {
    private static final String SELECT_BY_ID =
        "SELECT u.token as value, u.expires_at "
            + "FROM uuid u "
            + "WHERE u.token = ? AND u.expires_at > CURRENT_TIMESTAMP";

    private static final String INSERT =
        "INSERT INTO uuid(token, expires_at) VALUES (?, ?)";

    private final JdbcTemplate jdbc;

    private final RowMapper<Token> mapper = (resultSet, rowNum) ->
        new Token(
            resultSet.getString("value"),
            DateTimeUtils.fromString(resultSet.getString("expires_at"))
        );

    @Override
    public Optional<Token> findValidByValue(String value) {
        try {
            var jwt = jdbc.queryForObject(SELECT_BY_ID, mapper, value);
            return Optional.ofNullable(jwt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        }
    }

    @Override
    public void save(Token uuid) {
        try {
            Object[] params = { uuid.getValue(), uuid.getExpiresAt() };
            jdbc.update(INSERT, params);
        } catch (DataAccessResourceFailureException e) {
            var msg = "Failed to access DB: " + e.getMessage();
            throw new DataAccessException(msg, e);
        } catch (Exception e) {
            var msg = "Failed to save UUID: " + e.getMessage();
            throw new DataModificationException(msg, e);
        }
    }
}
