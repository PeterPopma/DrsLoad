CREATE OR REPLACE
PROCEDURE drpsys.create_loadtest_users
AS
 n_counter NUMBER;
BEGIN
	n_counter := 0;
LOOP
	INSERT
	INTO
	DRPSYS.DRP_DEELNEMER (DNR_ID,
	RGY_ID_AANGESLOTEN_BIJ,
	RGY_ID_VERTEGENWOORDIGT,
	NAAM,
	NAAM_DEELNEMERLIJST,
	DEELNEMERCODE,
	DEELNEMERNUMMER,
	VAN_REGISTRY_JN,
	TAL_CODE,
	TFE_CODE,
	FACTUURBIJLAGE_VIA_EMAIL_JN,
	RVM_CODE,
	REGISTRATIENUMMER,
	MAX_AANTAL_WHOIS,
	URL,
	BUITENLANDSE_DEELNEMER_JN,
	CONTRACT_GETEKEND_JN,
	BERICHTEN_VIA_POLL_JN,
	BERICHTEN_VIA_EMAIL_JN,
	TMS_CODE,
	RDS_CODE,
	TELEFOONNUMMER,
	FAXNUMMER,
	NOTIFYEMAILADRES,
	USER_ID,
	CATEGORIE,
	VERWIJDEREN_JN,
	REKENINGNUMMER_INCASSO,
	TICKETNUMMER,
	BEHOUDEN_DNS_KEY_JN,
	PBLCR_ONDERSTEUNING_DNSSEC_JN,
	INSCHRIJFDATUM,
	ACTIVERINGSDATUM,
	DATUM_OPZEGGING,
	INGANGSDATUM_FAILLISSEMENT,
	ONTVANGSTDATUM_BRIEF_CURATOR,
	ONTVANGSTDATUM_VRZK_OPHEFFING,
	IANA_ID,
	WHOIS_URL,
	VERHUIZING_EMAILADRES,
	TOON_RESELLER_IN_DRS_JN,
	LID_VAN_VVR_JN,
	SID,
	ABUSE_TELEFOONNUMMER,
	ABUSE_EMAILADRES)
VALUES (800000 + n_counter,
1,
1,
CONCAT('Registrar', n_counter),
CONCAT('Registrar', n_counter),
CONCAT('EP', LPAD(n_counter, 3, '0')),
800000 + n_counter,
'N',
'dut',
'kwartaal',
'N',
'ANDERS',
'99999999',
5000,
NULL,
'N',
'N',
'N',
'J',
'active',
NULL,
'+31.621718293',
NULL,
'p_popma@hotmail.com',
'epp loadtest',
'1',
'N',
NULL,
NULL,
'J',
'N',
TIMESTAMP '1997-01-10 14:07:15.000000',
TIMESTAMP '1997-01-10 14:07:15.000000',
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
NULL,
'N',
'J',
NULL,
NULL,
NULL);

INSERT
	INTO
	DRPSYS.DRP_GEBRUIKER (GBR_ID,
	DNR_ID,
	TGT_CODE,
	NAAM,
	WACHTWOORD,
	ACTIEF_JN,
	ACTIVERINGSREDEN,
	USER_ID,
	VERWIJDEREN_JN,
	TICKETNUMMER,
	VERLOOPDATUM_WACHTWOORD,
	VRIJGEGEVEN_JN,
	MOBIEL_NUMMER,
	INLOGGEN_JN)
VALUES (800000 + n_counter,
800000 + n_counter,
'epp',
800000 + n_counter,
'e1NTSEE1MTJ9K3pIWnRXZXA3VEMxQU5YdUxZUWdkaWdFM21vaGttZXRjWTNFS2c2LytpT2x4bExrSkhlbStmekxGbkp1ME44S1Zib2pYZmZBUW9QSDFEeHpLRWRGSnpoa01EWmpOVEZt',
'J',
NULL,
'epp loadtest',
'N',
0,
NULL,
'J',
NULL,
'J');

INSERT
	INTO
	DRPSYS.DRP_GEBRUIKER_GEBRUIKERSROL (GGL_ID,
	GBR_ID,
	GRL_ID,
	USER_ID,
	VERWIJDEREN_JN)
VALUES (DRP_GEBRUIKER_GEBRUIKERSRO_SEQ.NEXTVAL,
800000 + n_counter,
22,
'epp loadtest',
'N');

n_counter := n_counter + 1;

IF
	n_counter = 1000 THEN
      EXIT;
END
IF;
END
LOOP;
END;

DECLARE
BEGIN
  drpsys.create_loadtest_users();
END;

