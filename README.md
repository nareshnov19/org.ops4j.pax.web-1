
Building Pax Web
================

mvn clean install

NB: if you want to avoid test execution:
mvn clean install -DskipTests

Releasing Pax Web
=================

mvn -Prelease -Darguments="-Prelease" release:prepare -DautoVersionSubmodules=true

mvn -Prelease -Darguments="-Prelease" -Dgoals=deploy release:perform

Go to oss.sonatype.org and push pax-web to central.

If you want more information about releasing, please take a look on:

http://team.ops4j.org/wiki/display/ops4j/Releasing
