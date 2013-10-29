Pre-Condition:
1.installed and setup mysql
2.unzip ezScrum.zip, we call ${ezScrum} as the unzipped folder.

Usage:
1.modify the JettyServer.xml in the ${ezScrum},
We modify the line
<SystemProperty name="jetty.host" default="localhost"/> as
<SystemProperty name="jetty.host" default="IP"/> ex. IP:192.168.1.2
2.double click the InstallApp-NT.bat in the ${ezScrum} to install the ezScrum as Windows service.
3.double click the ServiceStart.bat to launch the ezScrum service.
4.access http://192.168.1.2:8080/ezScrum to experiment ezScrum.

By the way
1. Please clear bowser cache in client side, after updating the ezScrum server.