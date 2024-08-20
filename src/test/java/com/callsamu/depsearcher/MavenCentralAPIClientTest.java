package com.callsamu.depsearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

public class MavenCentralAPIClientTest {
  @Test
  public void shouldGetByGroupAndArtifact() throws Exception {
    final String dataPath = "/group-artifact-query.json";
    final var thisClass = MavenCentralAPIClientTest.class;

    final String data;

    try (final InputStream stream = thisClass.getResourceAsStream(dataPath)) {
      data = new String(stream.readAllBytes());
    }

    final MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody(data));

    server.start();

    final HttpUrl url = server.url("/");
    System.out.println(url.host());

    final String wantGroup = "com.google.inject";
    final String wantArtifact = "guice";

    final HttpClient client = HttpClient.newHttpClient();
    final UserQuery q = UserQuery.fromString(wantGroup + ":" + wantArtifact);
    final List<DependencyData> deps = new MavenCentralAPIClient(url.uri(), client).get(q);

    final RecordedRequest req = server.takeRequest();
    final HttpUrl reqUrl = req.getRequestUrl();

    final String wantQuery = q.toAPIQueryParam(); 
    assertTrue(reqUrl.queryParameter("q").equals(wantQuery));


	final Optional<DependencyData> nonMatchingDep = deps.stream()
		.filter(dep -> 
			dep.groupId().equals(wantGroup) && 
			dep.artifactId().equals(wantArtifact)
		).findFirst();

	final String message = nonMatchingDep.isEmpty() ? 
		"Non matching dependency found: " + nonMatchingDep.toString() :
		null;
	assertFalse(message, nonMatchingDep.isEmpty());

    server.shutdown();
    server.close();
  }
}
