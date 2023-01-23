package nl.sidn.eppload.controller;

public class EppCommands {

	public String getLogin(String loginName, String loginPwd) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <login>\n"+
					"      <clID>"+loginName+"</clID>\n"+
					"      <pw>"+loginPwd+"</pw>\n"+
					"      <options>\n"+
					"      <version>1.0</version>\n"+
					"      <lang>en</lang>\n"+
					"      </options>\n"+
					"      <svcs>\n"+
					"        <objURI>urn:ietf:params:xml:ns:contact-1.0</objURI>\n"+
					"        <objURI>urn:ietf:params:xml:ns:host-1.0</objURI>\n"+
					"        <objURI>urn:ietf:params:xml:ns:domain-1.0</objURI>\n"+
					"        <svcExtension>\n"+
					"          <extURI>http://rxsd.domain-registry.nl/sidn-ext-epp-1.0</extURI>\n"+
					"        </svcExtension>\n"+
					"      </svcs>\n"+
					"    </login>\n"+
					"    <clTRID>"+loginName+"</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getLogout() { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <logout/>\n"+
					"    <clTRID>300100</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getContactCreate(String name, String street, String city, String postalCode, String telephone, String email) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:urn1=\"http://rxsd.domain-registry.nl/sidn-ext-epp-1.0\">\n"+
					"  <command>\n"+
					"    <create>\n"+
					"      <contact:create xmlns:contact=\"urn:ietf:params:xml:ns:contact-1.0\">\n"+
					"        <contact:id>sh8013</contact:id>\n"+
					"        <contact:postalInfo type=\"loc\">\n"+
					"          <contact:name>"+name+"</contact:name>\n"+
					"          <contact:org>De Klusjeman BV</contact:org>\n"+
					"          <contact:addr>\n"+
					"            <contact:street>"+street+"</contact:street>\n"+
					"            <contact:city>"+city+"</contact:city>\n"+
					"            <contact:sp>Limburg</contact:sp>\n"+
					"            <contact:pc>"+postalCode+"</contact:pc>\n"+
					"            <contact:cc>NL</contact:cc>\n"+
					"          </contact:addr>\n"+
					"        </contact:postalInfo>\n"+
					"        <contact:voice>"+telephone+"</contact:voice>\n"+
					"        <contact:fax>+31.204578274</contact:fax>\n"+
					"        <contact:email>"+email+"</contact:email>\n"+
					"        <contact:authInfo>\n"+
					"        <contact:pw>2fooBAR</contact:pw>\n"+
					"        </contact:authInfo>\n"+
					"        <contact:disclose flag=\"0\">\n"+
					"        <contact:voice />\n"+
					"        <contact:email />\n"+
					"        </contact:disclose>\n"+
					"        </contact:create>\n"+
					"        </create>\n"+
					"        <extension>\n"+
					"        <urn1:ext>\n"+
					"          <urn1:create>\n"+
					"            <urn1:contact>\n"+
					"              <urn1:legalForm>EENMANSZAAK</urn1:legalForm>\n"+
					"              <urn1:legalFormRegNo>8764654.0</urn1:legalFormRegNo>\n"+
					"            </urn1:contact>\n"+
					"          </urn1:create>\n"+
					"        </urn1:ext>\n"+
					"        </extension>\n"+
					"    <clTRID>ABC-12345</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getContactInfo(String contactHandle) { return
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
						"<epp:epp xmlns:contact=\"urn:ietf:params:xml:ns:contact-1.0\" xmlns:epp=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:ns0=\"DAV:\">\n" +
						"   <epp:command>\n" +
						"        <epp:info>\n" +
						"                <contact:info>\n" +
						"                        <contact:id>" + contactHandle + "</contact:id>\n" +
						"                </contact:info>\n" +
						"        </epp:info>\n" +
						"   </epp:command>\n" +
						"</epp:epp>";
	}

	public String getContactUpdate(String name, String street, String city, String postalCode, String telephone, String email) { return
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
		"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:urn1=\"http://rxsd.domain-registry.nl/sidn-ext-epp-1.0\">\n"+
		"  <command>\n"+
		"    <update>\n"+
		"      <contact:update xmlns:contact=\"urn:ietf:params:xml:ns:contact-1.0\">\n"+
		"        <contact:id>PET000001-SIDN0</contact:id>\n"+
		"        <contact:chg>\n"+
		"        <contact:postalInfo type=\"loc\">\n"+
		"          <contact:name>"+name+"</contact:name>\n"+
		"          <contact:org>Peter BV</contact:org>\n"+
		"          <contact:addr>\n"+
		"            <contact:street>"+street+"</contact:street>\n"+
		"            <contact:city>"+city+"</contact:city>\n"+
		"            <contact:pc>"+postalCode+"</contact:pc>\n"+
		"            <contact:cc>NL</contact:cc>\n"+
		"          </contact:addr>\n"+
		"        </contact:postalInfo>\n"+
		"        <contact:voice>"+telephone+"</contact:voice>\n"+
		"        <contact:fax>+31.204578274</contact:fax>\n"+
		"        <contact:email>"+email+"</contact:email>\n"+
		"        </contact:chg>\n"+
		"      </contact:update>\n"+
		"    </update>\n"+
		"        <extension>\n"+
		"        <urn1:ext>\n"+
		"          <urn1:update>\n"+
		"            <urn1:contact>\n"+
		"              <urn1:legalForm>PERSOON</urn1:legalForm>\n"+
		"              <urn1:legalFormRegNo>8764654.0</urn1:legalFormRegNo>\n"+
		"            </urn1:contact>\n"+
		"          </urn1:update>\n"+
		"        </urn1:ext>\n"+
		"        </extension>\n"+
		"    <clTRID>ABC-12345</clTRID>\n"+
		"  </command>\n"+
		"</epp>\n";
	}

	public String getContactDelete(String contactHandle) { return ""; }


	public String getDomainCreate(String domainName, String hostName, String hostName2, String registrant, String adminC, String techC) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
					"<epp xmlns= \"urn:ietf:params:xml:ns:epp-1.0\" xmlns:domain= \"urn:ietf:params:xml:ns:domain-1.0\">\n" +
					"  <command>\n" +
					"    <create>\n" +
					"      <domain:create>\n" +
					"        <domain:name>"+ domainName +"</domain:name>\n" +
					"        <!--domain:ns-->\n" +
					"          <!--domain:hostObj>"+ hostName +"</domain:hostObj-->\n" +
					"          <!--domain:hostObj>"+ hostName2 +"</domain:hostObj-->\n" +
					"        <!--/domain:ns-->\n" +
					"        <domain:registrant>"+ registrant +"</domain:registrant>\n" +
					"        <domain:contact type= \"admin \">"+ adminC +"</domain:contact>\n" +
					"        <domain:contact type= \"tech \">"+ techC +"</domain:contact>\n" +
					"        <domain:authInfo><domain:pw></domain:pw></domain:authInfo>\n" +
					"      </domain:create>\n" +
					"    </create>\n" +
					"  </command>\n" +
					"</epp>\n";
	}

	public String getDomainUpdate(String domainName, String hostname, String hostname2, String registrant, String adminC, String techC) { return
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
		"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
		"     <command>\n" +
		"       <update>\n" +
		"         <domain:update xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n" +
		"           <domain:name>"+ domainName +"</domain:name>\n" +
		"           <domain:add>\n" +
		"             <domain:ns>\n" +               
		"               <domain:hostObj>"+ hostname +"</domain:hostObj>\n" +
		"               <domain:hostObj>"+ hostname2 +"</domain:hostObj>\n" +
		"             </domain:ns>\n" +
		"             <domain:contact type=\"tech\">"+ techC +"</domain:contact>\n" +
		"             <domain:contact type=\"admin\">"+ adminC +"</domain:contact>\n" +
		"           </domain:add>\n" +
		"           <domain:rem>\n" +
		"             <domain:ns>\n" +
		"               <domain:hostObj>"+ hostname +"</domain:hostObj>\n" +
		"               <domain:hostObj>"+ hostname2 +"</domain:hostObj>\n" +
		"             </domain:ns>\n" +
		"             <domain:contact type=\"tech\">"+ techC +"</domain:contact>\n" +
		"             <domain:contact type=\"admin\">"+ adminC +"</domain:contact>\n" +
		"           </domain:rem>\n" +
		"           <domain:chg>\n" +
		"             <domain:registrant>"+ registrant +"</domain:registrant>\n" +
		"             <domain:authInfo>\n" +
		"              <domain:null/>\n" + 
		"             </domain:authInfo>\n" +
		"           </domain:chg>\n" +
		"         </domain:update>\n" +
		"       </update>\n" +
		"       <clTRID>500100-002</clTRID>\n" +
		"     </command>\n" +
		"</epp>\n";
	}

	public String getDomainTransfer(String domainName, String domainToken) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <transfer op=\"request\">\n"+
					"      <domain:transfer xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n"+
					"        <domain:name>"+domainName+"</domain:name>\n" +
					"        <domain:authInfo>\n" +
					"          <domain:pw>"+ domainToken+"</domain:pw>\n"+
					"        </domain:authInfo>\n"+
					"      </domain:transfer>\n"+
					"    </transfer>\n"+
					"    <clTRID>300100</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getDomainTransferQuery(String domainName) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <transfer op=\"query\">\n"+
					"      <domain:transfer xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n"+
					"        <domain:name>"+domainName+"</domain:name>\n" +
					"      </domain:transfer>\n"+
					"    </transfer>\n"+
					"    <clTRID>300100</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getDomainInfo(String domainName) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <info>\n"+
					"      <domain:info xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n"+
					"        <domain:name>"+domainName+"</domain:name>\n" +
					"      </domain:info>\n"+
					"    </info>\n"+
					"    <clTRID>300100</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getDomainDelete(String domainName) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
					"  <command>\n"+
					"    <delete>\n"+
					"      <domain:delete xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n"+
					"        <domain:name>"+domainName+"</domain:name>\n" +
					"      </domain:delete>\n"+
					"    </delete>\n"+
					"    <clTRID>300100</clTRID>\n"+
					"  </command>\n"+
					"</epp>\n";
	}

	public String getDomainCancelDelete(String domainName) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\" >\n"+
					"  <extension>\n"+
					"    <sidn:command xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sidn=\"urn:ietf:params:xml:ns:sidn-ext-app-1.0\">\n"+
					"      <sidn:domainCancelDelete>\n"+
					"        <sidn:name>"+domainName+"</sidn:name>\n" +
					"      </sidn:domainCancelDelete>\n"+
					"      <clTRID>300100</clTRID>\n"+
					"    </sidn:command>\n"+
					"  </extension>\n"+
					"</epp>\n";
	}

	public String getDomainRenew(String domainName, String renewPeriod) { return
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
					"   <command>\n" +
					"     <renew>\n" +
					"        <domain:renew xmlns:domain=\"urn:ietf:params:xml:ns:domain-1.0\">\n" +
					"           <domain:name>"+domainName+"</domain:name>\n" +
					"           <domain:curExpDate>2012-01-01</domain:curExpDate>\n" +
					"           <domain:period unit=\"y\">"+renewPeriod+"</domain:period>\n" +
					"           </domain:renew>\n" +
					"     </renew>\n" +
					"     <clTRID>ABC-12345</clTRID>\n" +
					"   </command>\n" +
					"</epp>";
	}

	public String getHostCreate(String hostname, String ipAddress) {
		return
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
		"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
		"  <command>\n" +
		"    <create>\n" +
		"      <host:create xmlns:host=\"urn:ietf:params:xml:ns:host-1.0\">\n" +
		"        <host:name>" + hostname + "</host:name>\n" +
		"        <host:addr ip=\"v4\">"+ ipAddress +"</host:addr>\n" +
		"      </host:create>\n" +
		"   </create>\n" +
		"   <clTRID>Host create: " + hostname + "</clTRID>\n" +
		"  </command>\n" +
		"</epp>\n";
	}

	public String getHostInfo(String hostname) {
		return
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
						"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
						"  <command>\n" +
						"    <info>\n" +
						"      <host:info xmlns:host=\"urn:ietf:params:xml:ns:host-1.0\">\n" +
						"        <host:name>" + hostname + "</host:name>\n" +
						"      </host:info>\n" +
						"   </info>\n" +
						"   <clTRID>Host info: " + hostname + "</clTRID>\n" +
						"  </command>\n" +
						"</epp>\n";
	}

	public String getHostUpdate(String hostname, String ipAddressAdd, String ipAddressRemove, String hostNameNew ) {
		String command = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
				"  <command>\n" +
				"    <update>\n" +
				"      <host:update xmlns:host=\"urn:ietf:params:xml:ns:host-1.0\">\n" +
				"        <host:name>" + hostname + "</host:name>\n";
		if (ipAddressAdd!=null && !ipAddressAdd.isEmpty()) {
			command += "        <host:add>\n" +
					"          <host:addr ip=\"v4\">"+ ipAddressAdd +"</host:addr>\n" +
					"        </host:add>\n";
		}
		if (ipAddressRemove!=null && !ipAddressRemove.isEmpty()) {
			command += "        <host:rem>\n" +
					"          <host:addr ip=\"v4\">"+ ipAddressRemove +"</host:addr>\n" +
					"        </host:rem>\n";
		}
		if (hostNameNew!=null && !hostNameNew.isEmpty()) {
			command += "        <host:chg>\n" +
					"          <<host:name>"+ hostNameNew +"</host:name>\n" +
					"        </host:chg>\n";
		}
		command += "      </host:update>\n" +
				"   </update>\n" +
				"   <clTRID>Host create: " + hostname + "</clTRID>\n" +
				"  </command>\n" +
				"</epp>\n";

		return command;
	}

	public String getHostDelete(String hostname) {
		return
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
						"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n" +
						"  <command>\n" +
						"    <delete>\n" +
						"      <host:delete xmlns:host=\"urn:ietf:params:xml:ns:host-1.0\">\n" +
						"        <host:name>" + hostname + "</host:name>\n" +
						"      </host:delete>\n" +
						"   </delete>\n" +
						"   <clTRID>Host delete: " + hostname + "</clTRID>\n" +
						"  </command>\n" +
						"</epp>\n";
	}

	public String getPoll() { return
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
		"<epp xmlns=\"urn:ietf:params:xml:ns:epp-1.0\">\n"+
		"  <command>\n"+
		"    <poll op=\"req\"/>\n"+
		"    <clTRID>ABC-12345</clTRID>\n"+
		"  </command>\n"+
		"</epp>\n";
	}


}
