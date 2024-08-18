package com.callsamu.depsearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserQueryTest {
	@Test
	public void shouldParseClassQuery() throws Exception {
		final UserQuery q = UserQuery.fromString("com.google.inject.Guice");
		assertEquals(q.toAPIQueryParam(), "c:com.google.inject.Guice");
		assertTrue(q.getVersion().isEmpty());
	}

	@Test
	public void shouldParseGroupAndArtifactQuery() throws Exception {
		final UserQuery q = UserQuery.fromString("com.google.inject:guice");
		assertEquals(q.toAPIQueryParam(), "g:com.google.inject AND a:guice");
		assertTrue(q.getVersion().isEmpty());
	}

	@Test
	public void shouldParseFullTextQuery() throws Exception {
		final UserQuery q = UserQuery.fromString("google guice");
		assertEquals(q.toAPIQueryParam(), "google guice");
		assertTrue(q.getVersion().isEmpty());
	}

	@Test
	public void shouldParseClassWithVersionQuery() throws Exception {
		final UserQuery q = UserQuery.fromString("com.google.inject:guice:3.0");
		assertEquals(q.toAPIQueryParam(), "g:com.google.inject AND a:guice");
		assertFalse(q.getVersion().isEmpty());
		assertEquals(q.getVersion().get(), "3.0");
	}

	@Test
	public void shouldParseGroupAndArtifactWithVersionQuery() throws Exception {
		final UserQuery q = UserQuery.fromString("com.google.inject:guice:3.0");
		assertEquals(q.toAPIQueryParam(), "g:com.google.inject AND a:guice");
		assertFalse(q.getVersion().isEmpty());
		assertEquals(q.getVersion().get(), "3.0");
	}
}
