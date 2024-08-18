package com.callsamu.depsearcher;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.http.HttpClient;
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
    final DependencyData dep =
        new MavenCentralAPIClient(url.uri(), client).get("com.google.inject", "guice");

    final RecordedRequest req = server.takeRequest();
    final HttpUrl reqUrl = req.getRequestUrl();

    final String wantQuery = "g:" + wantGroup + " AND " + "a:" + wantArtifact;
    assertTrue(reqUrl.queryParameter("q").equals(wantQuery));
    System.out.println(dep);

    assertTrue(dep.groupId().equals(wantGroup));
    assertTrue(dep.artifactId().equals(wantArtifact));
    assertTrue(dep.version().equals("7.0.0"));

    server.shutdown();
    server.close();
  }
}
