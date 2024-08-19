package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;

import java.time.LocalDateTime;
import java.util.Map;

public class TransferFixture {

    public static final String URL = "/api/transfer";
    public static final String VALID_JWT = "Bearer vdffelkjnelpdkhdiofplkd";
    public static final String INVALID_JWT = "gdgd";
    public static final String MALFORMED_JWT = "grtrgr";

    public static Map<String, OAuth2Token> tokens(String source, String target) {
        return Map.of(
            source, new OAuth2Token("fdfddf", "mklfnl", LocalDateTime.MAX),
            target, new OAuth2Token("kmoere", "lkmeff", LocalDateTime.MAX)
        );
    }
}
