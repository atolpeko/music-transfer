package com.mf.auth.usecase;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;
import com.mf.auth.usecase.exception.UseCaseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
@RequiredArgsConstructor
public class JWTUseCaseImpl implements JWTUseCase {
    private final JWTService jwtService;
    private final JWTRepositoryPort jwtRepository;
    private final CircuitBreaker breaker;

    @Override
    public JWT obtain(String accessToken) {
        try {
            var jwt = breaker.executeCallable(
                () -> jwtRepository.findValidByAccessToken(accessToken)
            ).orElseThrow(
                () -> new AuthorizationException("Invalid access token provided")
            );

            jwt.revokeAccess();
            breaker.executeRunnable(
                () -> jwtRepository.updateAccessTokenByJwtId(jwt.getId(), true)
            );

            return jwt;
        } catch (UseCaseException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new RepositoryAccessException("Failed to access DB", e);
        } catch (Exception e) {
            throw new RepositoryAccessException("Failed to delete JWT", e);
        }
    }

    @Override
    public boolean isValid(String jwt) {
        try {
            var token = breaker.executeCallable(() -> jwtRepository.findValidByValue(jwt));
            return token
                .map(t -> t.isValid() && jwtService.isValid(t.getValue()))
                .orElse(false);
        } catch (Exception e) {
            throw new RepositoryAccessException("Failed to access DB", e);
        }
    }
}
