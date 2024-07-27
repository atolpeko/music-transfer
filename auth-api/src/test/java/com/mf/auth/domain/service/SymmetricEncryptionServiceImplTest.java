package com.mf.auth.domain.service;

import static com.mf.auth.fixture.SymmetricEncryptionServiceFixture.ALG_NAME;
import static com.mf.auth.fixture.SymmetricEncryptionServiceFixture.KEY;
import static com.mf.auth.fixture.SymmetricEncryptionServiceFixture.KEY_SIZE;
import static com.mf.auth.fixture.SymmetricEncryptionServiceFixture.TRANSFORMATION;
import static com.mf.auth.fixture.SymmetricEncryptionServiceFixture.VALUE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.when;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.ServiceProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class SymmetricEncryptionServiceImplTest {

	@InjectMocks
	SymmetricEncryptionServiceImpl target;

	@Mock
	ServiceProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.encryptionAlgName()).thenReturn(ALG_NAME);
		when(properties.encryptionKeySize()).thenReturn(KEY_SIZE);
		when(properties.encryptionTransformation()).thenReturn(TRANSFORMATION);
	}

	@Test
	void testEncryption() {
		var result = target.encrypt(VALUE, KEY);
		assertFalse(result.isEmpty());
	}

	@Test
	void testDecryption() {
		var enc = target.encrypt(VALUE, KEY);
		var dec = target.decrypt(enc, KEY);

		assertEquals(VALUE, dec);
	}
}