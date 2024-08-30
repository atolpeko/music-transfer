package com.mf.auth.usecase.impl;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;
import com.mf.auth.usecase.JWTUseCase;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;
import com.mf.auth.usecase.exception.UseCaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
@RequiredArgsConstructor
public class JWTUseCaseImpl implements JWTUseCase {

    private final JWTService jwtService;
    private final JWTRepositoryPort jwtRepository;

    @Override
    public JWT obtain(String accessToken) {
        try {
            var jwt = jwtRepository.findValidByAccessToken(accessToken)
                .orElseThrow(() -> new AuthorizationException("Invalid access token provided"));

            log.debug("Revoking JWT access token");
            jwt.revokeAccess();
            jwtRepository.updateAccessTokenByJwtId(jwt.getId(), true);

            return jwt;
        } catch (UseCaseException e) {
            throw e;
        } catch (DataAccessException | DataModificationException e) {
            var msg = "Failed to access DB: %s".formatted(e.getMessage());
            throw new RepositoryAccessException(msg, e);
        } catch (Exception e) {
            var msg = "Service unavailable: %s".formatted(e.getMessage());
            throw new UseCaseException(msg, e);
        }
    }

    @Override
    public boolean isValid(String jwt) {
        try {
            var token = jwtRepository.findValidByValue(jwt);
            return token
                .map(t -> t.isValid() && jwtService.isValid(t.getValue()))
                .orElse(false);
        } catch (DataAccessException | DataModificationException e) {
            throw new RepositoryAccessException("Failed to access DB", e);
        } catch (Exception e) {
            var msg = "Service unavailable: %s".formatted(e.getMessage());
            throw new UseCaseException(msg, e);
        }
    }
}
