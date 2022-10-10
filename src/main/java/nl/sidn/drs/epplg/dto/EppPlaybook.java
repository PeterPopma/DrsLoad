package nl.sidn.drs.epplg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EppPlaybook {
  private String user = "300106";
  private String password = "Geheim_123";
  private Integer repeatCount = 100;
  private Integer callsPerMinute = 30;
  private Integer numberOfThreads = 1;
  private String commands = "";
  private CommandParameters commandParameters = new CommandParameters();

  @Override
  public String toString() {
    return "EppPlaybook{" +
        "user='" + user + '\'' +
        ", password='" + password + '\'' +
        ", repeatCount=" + repeatCount +
        ", callsPerMinute=" + callsPerMinute +
        ", numberOfThreads=" + numberOfThreads +
        ", commands='" + commands + '\'' +
        ", commandParameters=" + commandParameters +
        '}';
  }
}
