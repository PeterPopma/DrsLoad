With this tool you can generate EPP commands.
It can be used to create testsets, L/P tests, stresstests, etc.

Usage:

POST /run-job -> Runs a loadtest (with scenario's in the JSON body)
POST /stop-job -> Stops a loadtest (with job name in the body)

The scenarios are specified as .json files in the "scenarios" folder.
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
- if you leave out a domainname, contacthandle or hostname, the last created object from the scenario is used.
- When you execute a DOMAININFO command, the token will be used in a subsequent DOMAINTRANSFER call. This can be used to transfer a domain.
- when a domain is created, the {contactInfo} section provides a new contact that will be used as techc, adminc and registrant


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
DOMAINRENEW
HOSTCREATE
HOSTINFO
HOSTUPDATE
HOSTDELETE
POLL

