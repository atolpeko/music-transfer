package com.mf.auth.usecase;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;

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
    private final UUIDRepositoryPort uuidRepository;
    private final JWTRepositoryPort jwtRepository;
    private final CircuitBreaker breaker;

    @Override
    public JWT obtain(String requestUuid, String accessToken) {
        try {
            var uuid = breaker.executeCallable(
                () -> uuidRepository.findValidByValue(requestUuid));
            if (uuid.isEmpty() || !uuid.get().isValid()) {
                throw new AuthorizationException("Invalid UUID provided");
            }

            var jwt = jwtRepository.findValidByAccessToken(accessToken);
            return jwt.orElseThrow(() ->
                new AuthorizationException("Invalid access token provided"));
        } catch (UseCaseException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new RepositoryAccessException("Failed to access DB", e);
        } catch (Exception e) {
            throw new RepositoryAccessException("Failed to delete JWT", e);
        }
    }

    @Override
    public boolean isValid(JWT jwt) {
        return jwtService.isValid(jwt);
    }
}
