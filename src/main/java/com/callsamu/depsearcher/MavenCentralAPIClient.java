package com.callsamu.depsearcher;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.UriBuilder;


public class MavenCentralAPIClient {
	private final String origin;
	private final String path;
	private final HttpClient client;
	private final ObjectMapper mapper;

	MavenCentralAPIClient(
		String origin, 
		String path,
		HttpClient http
	) {
		this.origin = origin;
		this.path = path;
		this.client = http;
		this.mapper = new ObjectMapper();
	}

	public DependencyData parse(String json) throws Exception {
		JsonNode root = this.mapper.readTree(json);
		JsonNode object = root.get("response").get("docs");

		List<DependencyData> results = this.mapper.treeToValue(
			object, 
			new TypeReference<List<DependencyData>>() {}
		);

		return results.get(0);
	}
	
	private String fetch(URI uri) throws Exception {
		final BodyHandler<String> handler = BodyHandlers.ofString();
		final HttpRequest request = HttpRequest.newBuilder(uri)
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

	public DependencyData get(String g, String a) throws Exception {
		URI uri = UriBuilder
			.fromPath(this.origin)
			.path(this.path)
			.queryParam("q", "g:" + g + " AND a:" + a)
			.queryParam("rows", "1")
			.queryParam("wt", "json")
			.build();

		try {
			return this.parse(this.fetch(uri));
		}
		catch (Exception e) {
			throw e;
		}
	}
}
