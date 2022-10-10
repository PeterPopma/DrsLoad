package nl.sidn.drs.epplg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactInfo {
  private String handle = "PET000002-ACONN";
  private String name = "Handige Harry";
  private String street = "IJsselkade 112";
  private String city = "Amsterdam";
  private String postalcode = "6823 LE";
  private String telephone = "+31.621718293";
  private String email = "peter.popma@sidn.nl";

  @Override
  public String toString() {
    return "EppContactInfo{" +
        "handle='" + handle + '\'' +
        ", name='" + name + '\'' +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", postalcode='" + postalcode + '\'' +
        ", telephone='" + telephone + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
