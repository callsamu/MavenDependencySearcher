package com.callsamu.depsearcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "dependency")
public record DependencyData(
	@JsonProperty("g") String groupId,
	@JsonProperty("a") String artifactId,
	@JsonProperty("version") String version
){};
