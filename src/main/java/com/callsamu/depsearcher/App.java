package com.callsamu.depsearcher;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "mvndsearch",
    version = "0.0.1",
    mixinStandardHelpOptions = true,
    description = "Searchs for dependencies in Maven Central")
public class App implements Callable<Integer> {
  @Parameters(index = "0", description = "A query parameter to be used")
  private String query;

  @Option(
      names = {"-l", "--latest"},
      description = "A query parameter to be used")
  private boolean latest;

  private Optional<DependencyData> find(List<DependencyData> deps, Optional<String> version) {
    final DependencyData first = deps.getFirst();

    if (this.latest) {
      return Optional.of(first);
    }

    if (version.isEmpty()) {
      Optional<DependencyData> chosen = Optional.empty();

      String message =
          Ansi.AUTO.string(
              "@|bold "
                  + "Choose a version of "
                  + first.groupId()
                  + ":"
                  + first.artifactId()
                  + "|@");
      try (Scanner scanner = new Scanner(System.in)) {
        chosen = choose(message, deps, scanner);
        while (chosen.isEmpty()) {
          message =
              Ansi.AUTO.string(
                  "@|bold,red \nInvalid version, please choose one of the following: |@");
          chosen = choose(message, deps, scanner);
        }
        return chosen;
      }
    }

    for (final DependencyData dep : deps) {
      if (dep.version().equals(version.get())) {
        return Optional.of(dep);
      }
    }

    return Optional.empty();
  }

  private static Optional<DependencyData> choose(
      String message, List<DependencyData> deps, Scanner scanner) {
    int index = 0;
    System.out.println(message);
    for (final DependencyData dep : deps) {
      System.out.println("[" + index + "]    " + dep.version());
      index++;
    }

    System.out.print(Ansi.AUTO.string("@|bold " + "OPTION: " + "|@"));
    try {
      final int choice = scanner.nextInt(10);
      if (choice < 0 || choice >= deps.size()) {
        return Optional.empty();
      }
      System.out.println("\n");
      return Optional.of(deps.get(choice));
    } catch (InputMismatchException e) {
      scanner.nextLine();
      return Optional.empty();
    }
  }

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
      final List<DependencyData> deps = client.get(q);

      if (deps.isEmpty()) {
        System.out.println("No results found for " + query);
        return 1;
      }

      final Optional<String> version = q.getVersion();
      final Optional<DependencyData> dep = find(deps, version);
      if (dep == null) {
        System.out.println("No results found for " + query);
        return 1;
      }

      final String r = mapper.writeValueAsString(dep.get());
      System.out.println(Ansi.AUTO.string("@|green " + r + "|@"));

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
