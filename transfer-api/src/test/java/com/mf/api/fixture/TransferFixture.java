package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;

import java.time.LocalDateTime;
import java.util.Map;

public class TransferFixture {

    public static final String URL = "/api/transfer";

    public static Map<String, OAuth2Token> tokens(String source, String target) {
        return Map.of(
            source, new OAuth2Token("fdfddf", "mklfnl", LocalDateTime.MAX),
            target, new OAuth2Token("kmoere", "lkmeff", LocalDateTime.MAX)
        );
    }
}
