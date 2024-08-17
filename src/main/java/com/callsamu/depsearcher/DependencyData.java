package com.callsamu.depsearcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "dependency")
public record DependencyData(
	@JsonProperty("g") 
	@JacksonXmlProperty(localName = "groupId") 
	String groupId,

	@JsonProperty("a") 
	@JacksonXmlProperty(localName = "artifactId") 
	String artifactId,

	@JsonProperty("v") 
	@JacksonXmlProperty(localName = "version") 
	String version
){};
