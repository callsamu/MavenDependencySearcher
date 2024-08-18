package com.callsamu.depsearcher;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "mvndsearch",
    version = "0.0.1",
    mixinStandardHelpOptions = true,
    description = "Searchs for dependencies in Maven Central")
public class App implements Callable<Integer> {
  @Parameters(index = "0", description = "A query parameter to be used")
  private String query;

  @Override
  public Integer call() throws Exception {
    MavenCentralAPIClient client =
        new MavenCentralAPIClient(
            UriBuilder.fromUri("https://search.maven.org/solrsearch/select").build(),
            HttpClient.newHttpClient());

    final XmlMapper mapper = new XmlMapper();
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    try {
      final UserQuery q = UserQuery.fromString(query);
      final DependencyData data = client.get(q);
      final String r = mapper.writeValueAsString(data);
      System.out.println(r);

      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
