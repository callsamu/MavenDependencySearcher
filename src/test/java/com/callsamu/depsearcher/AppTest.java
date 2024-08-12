package com.callsamu.depsearcher;

import static org.junit.Assert.assertTrue;

import java.net.http.HttpClient;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
		final HttpClient client = HttpClient.newHttpClient();
		final MavenCentralAPIClient mvn = new MavenCentralAPIClient(
			"https://repo1.maven.org", 
			"/maven2", 
			client
		);

		final String data = """
			"response": {
				"docs": [
					{
						"a": "lombok",
						"ec": [
							".pom.sha256",
							".jar.sha256"
						],
						"g": "name.remal.gradle-plugins.lombok",
						"id": "name.remal.gradle-plugins.lombok:lombok",
						"latestVersion": "2.2.7",
						"p": "jar",
						"repositoryId": "central",
						"text": [
							"name.remal.gradle-plugins.lombok",
							"lombok",
							".jar.sha256"
						],
						"timestamp": 1719939783583,
						"versionCount": 20
					},
					{
						"a": "lombok",
						"ec": [
							"-sources.jar.sha256",
							".pom.sha256",
							".jar.sha256"
						],
						"g": "org.projectlombok",
						"id": "org.projectlombok:lombok",
						"latestVersion": "1.18.34",
						"p": "jar",
						"repositoryId": "central",
						"text": [
							"org.projectlombok",
							"-sources.jar.sha512",
							".pom.sha256",
							".jar.sha256"
						],
						"timestamp": 1719535553024,
						"versionCount": 53
					},
				],
				"numFound": 83,
				"start": 0
			},
			"responseHeader": {
				"QTime": 3,
				"params": {
					"core": "",
					"defType": "dismax",
					"fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
					"indent": "off",
					"q": "lombok",
					"qf": "text^20 g^5 a^10",
					"rows": "5",
					"sort": "score desc,timestamp desc,g asc,a asc",
					"spellcheck": "true",
					"spellcheck.count": "5",
					"start": "",
					"version": "2.2",
					"wt": "json"
				},
				"status": 0
			},
			"spellcheck": {
				"suggestions": []
			}
		}
		""";

		try {
			final DependencyData dep = mvn.parse(data);
			assertTrue(dep.groupId().equals("org.projectlombok"));
			assertTrue(dep.artifactId().equals("lombok"));
			assertTrue(dep.version().equals("1.18.34"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
