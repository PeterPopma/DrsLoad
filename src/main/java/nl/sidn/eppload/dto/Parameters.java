package nl.sidn.eppload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parameters {
  private String domainName;
  private String hostName;

  @Override
  public String toString() {
    return "EppCommandParameters{" +
        "domainName='" + domainName + '\'' +
        ", hostName='" + hostName + '\'' +
        ", hostName2='" + hostName2 + '\'' +
        ", hostNameNew='" + hostNameNew + '\'' +
        ", domainToken='" + domainToken + '\'' +
        ", registrant='" + registrant + '\'' +
        ", adminC='" + adminC + '\'' +
        ", techC='" + techC + '\'' +
        ", ipAddress='" + ipAddress + '\'' +
        ", ipAddressAdd='" + ipAddressAdd + '\'' +
        ", ipAddressRemove='" + ipAddressRemove + '\'' +
        ", renewPeriod='" + renewPeriod + '\'' +
        ", contactInfo=" + contactInfo +
        '}';
  }

  private String hostName2;
  private String hostNameNew;
  private String domainToken;
  private String registrant;
  private String adminC;
  private String techC;
  private String ipAddress;
  private String ipAddressAdd;
  private String ipAddressRemove;
  private String renewPeriod;

  private String loginUser;

  private String loginPassword;
  private ContactInfo contactInfo = new ContactInfo();
}
