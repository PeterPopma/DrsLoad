package nl.sidn.eppload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactInfo {
  private String handle;
  private String name;
  private String street;
  private String city;
  private String postalcode;
  private String telephone;
  private String email;

  @Override
  public String toString() {
    return "EppContactInfo {" +
        " handle='" + handle + '\'' +
        ", name='" + name + '\'' +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", postalcode='" + postalcode + '\'' +
        ", telephone='" + telephone + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
