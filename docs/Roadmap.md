
## Future

* Concept for i18n error messages with MException
* Calendar for christian holidays (moon phase calculation), link https://github.com/flightonary/jholiday
* Link https://github.com/apilayer/restcountries to get Country and Language information
* Link https://github.com/googlei18n/libphonenumber to work with phone numbers
* Reimplement cron scheduling
* Add central maven parent pom for all mhu projects and reorg maven poms
* Add JSqlParser support for generic xdb queries, generate Adb query model out of a sql statement https://github.com/JSQLParser/JSqlParser

## 3.5.0

* dependencies for karaf 4.2.*
* Java 11 compatibility

## 3.4.0

* Switch to vaadin 8
* Java 10 compatibility

## 3.3.7

* Extend MForm to support Actions
* Vaadin AbstractSubSpaceTree - Generic component showing in a tree a list of sub spaces


## 3.3.6

* Use https://github.com/danielmiessler/SecLists (copy) to validate passwords
* Implement DB Encapsulation and divide from Locking

## 3.3.4

* move mhu-lib-karaf to mhus-osgi-tools, split into mhu-osgi-services and mhu-karaf-commands
* disable mhu-lib-liferay - use previous version if needed
* add MSendMail tool to send mime mails
* fix lambda analyse tool
* extend MPojo model to user @Public annotation by default
