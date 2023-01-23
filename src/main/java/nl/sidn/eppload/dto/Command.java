package nl.sidn.eppload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Command {
  String command;
  Parameters parameters;
}
