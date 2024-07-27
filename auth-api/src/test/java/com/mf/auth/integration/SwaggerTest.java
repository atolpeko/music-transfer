package com.mf.auth.integration;

import static com.mf.auth.fixture.SwaggerFixture.REDIRECT_URL;
import static com.mf.auth.fixture.SwaggerFixture.URL;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mf.auth.config.IntegrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
class SwaggerTest {

	@Autowired
	MockMvc mvc;

	@Test
	void testRedirectionToSwagger() throws Exception {
		mvc.perform(get(URL))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(REDIRECT_URL));
	}
}
