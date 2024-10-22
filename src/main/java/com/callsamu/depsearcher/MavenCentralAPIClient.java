package com.callsamu.depsearcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class MavenCentralAPIClient {
  private final URI endpoint;
  private final HttpClient client;
  private final ObjectMapper mapper;

  MavenCentralAPIClient(URI endpoint, HttpClient http) {
    this.endpoint = endpoint;
    this.client = http;
    this.mapper = new ObjectMapper();
  }

  public List<DependencyData> parse(String json) throws Exception {
    JsonNode root = this.mapper.readTree(json);
    JsonNode object = root.get("response").get("docs");

    List<DependencyData> results =
        this.mapper.treeToValue(object, new TypeReference<List<DependencyData>>() {});

    return results;
  }

  private String fetch(URI uri) throws Exception {
    final BodyHandler<String> handler = BodyHandlers.ofString();
    final HttpRequest request =
        HttpRequest.newBuilder(uri)
            .GET()
            .header("Accept", "application/json")
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    final HttpResponse<String> response;

    try {
      response = this.client.send(request, handler);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw e;
    }

    return response.body();
  }

  public List<DependencyData> get(UserQuery q) throws Exception {
    final String queryParam = q.toAPIQueryParam();

    if (!queryParam.contains("g:") && !queryParam.contains("a:")) {
      throw new IllegalArgumentException("Query type not yet supported:" + queryParam);
    }

    URI uri =
        UriBuilder.fromUri(this.endpoint)
            .queryParam("q", queryParam)
            .queryParam("rows", "10")
            .queryParam("core", "gav")
            .queryParam("wt", "json")
            .build();

    try {
      return this.parse(this.fetch(uri));
    } catch (Exception e) {
      throw e;
    }
  }
}
