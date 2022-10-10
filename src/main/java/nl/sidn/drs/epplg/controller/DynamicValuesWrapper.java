package nl.sidn.drs.epplg.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DynamicValuesWrapper {
  Integer index = 0;    // to keep track of the variable we are reading/modifying. Every occurrence in every epp command increases the index.
  List<Integer> dynamicParameters = new ArrayList<>();    // the variables, actually counters that are used to add to variables
}
