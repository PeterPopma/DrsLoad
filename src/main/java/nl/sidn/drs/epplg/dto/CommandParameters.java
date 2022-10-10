package nl.sidn.drs.epplg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandParameters {
  private String domainName = "jaap.nl";;
  private String hostName = "ns1.peter.nl";

  @Override
  public String toString() {
    return "EppCommandParameters{" +
        "domainName='" + domainName + '\'' +
        ", hostName='" + hostName + '\'' +
        ", hostName2='" + hostName2 + '\'' +
        ", domainToken='" + domainToken + '\'' +
        ", registrant='" + registrant + '\'' +
        ", adminC='" + adminC + '\'' +
        ", techC='" + techC + '\'' +
        ", ipAddress='" + ipAddress + '\'' +
        ", renewPeriod='" + renewPeriod + '\'' +
        ", contactInfo=" + contactInfo +
        '}';
  }

  private String hostName2 = "ns2.peter.nl";
  private String domainToken = "jaap.nl";
  private String registrant = "PET000002-ACONN";
  private String adminC = "PET000002-ACONN";
  private String techC = "PET000002-ACONN";
  private String ipAddress = "62.1.2.3";
  private String renewPeriod = "3";
  private ContactInfo contactInfo = new ContactInfo();
}
