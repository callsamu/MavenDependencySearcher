package com.callsamu.depsearcher;

public record DependencyData(
	String groupId, 
	String artifactId, 
	String version
){};
