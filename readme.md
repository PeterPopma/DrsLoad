With this tool you can generate EPP commands.
It can be used to create testsets, L/P tests, stresstests, etc.

Usage:

POST /run-job -> Runs a loadtest (with scenario's in the JSON body)
POST /stop-job -> Stops a loadtest (with job name in the body)
GET /[command] -> Show the template for the epp command (CONTACTCREATE, DOMAINDELETE, etc.)

You specify a job as json. For example:

{
    "name": "initial fill",
	  "hostName": "localhost",
	  "port": 700,
	  "runTimeSeconds": 30,
	  "scenarioNames": [ "domaincreate_domain_0_to_300" ]
}

The scenario's must be specified as JSON and must be put in a filename
with exactly the same name, appended with ".json" (so in the example "domaincreate_domain_0_to_300.json")
The location of the scenario files is determined in application.yml:

eppload:
  scenarios:
    location: C:\Projects\EppLoad\scenarios

Some example scenarios are specified as .json files in the "scenarios" folder in the source repository.
The scenarios are run in parallel, but the commands of one scenario are waiting for response before
executing the next command.
If you don't specify any scenario's, the job default.json is used (but with hostname and port specified in http POST)

EPP User accounts:
The loadtest assumes you have defined the following 1000 EPP-users:
800000 800001 800002 ... 800999
All these users will user the password "Secret_123!"

Scenario syntax:

- A value of -1 for "repeatCount" or "runTimeSeconds" means forever.
- ++[value] means an increasing value, starting at [value] and increased by 1 on each call.
- ??[value] means an random value, from 0 to [value] (value excluded).
- If you leave out a domainname, contacthandle or hostname in an UPDATE, INFO, DELETE, or TRANSFER, the last created object from the scenario is used.
You can use that to create a chain like CONTACT: CREATE > INFO > UPDATE > DELETE > INFO
- When you execute a DOMAININFO command, the token will be used in a subsequent DOMAINTRANSFER call. This can be used to transfer a domain.
- When a domain is created, you can specify the techc, adminc, or registrant handle. 
If you leave out some of this values, the {contactInfo} section will be used to create a new contact and couple it instead.


commands: 

CONTACTCREATE
CONTACTINFO
CONTACTUPDATE
CONTACTDELETE
DOMAINCREATE
DOMAININFO
DOMAINUPDATE
DOMAINDELETE
DOMAINCANCELDELETE
DOMAINTRANSFER
DOMAINTRANSFERQUERY
DOMAINRENEW
HOSTCREATE
HOSTINFO
HOSTUPDATE
HOSTDELETE
POLL

