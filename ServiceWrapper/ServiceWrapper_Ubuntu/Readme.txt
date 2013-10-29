Pre-Condition:
1.installed and setup mysql
2.unzip ezScrum_Ubuntu.tar.gz, we call ${ezScrum} as the unzipped folder.
3.change directory to ${ezScrum}

Usage:
1.modify the JettyServer.xml in the ${ezScrum},
We modify the line
<SystemProperty name="jetty.host" default="localhost"/> as
<SystemProperty name="jetty.host" default="IP"/> ex. IP:192.168.1.2
2.change mode for a script named ezScrum. $chmod +x ezScrum
3.change mode for a executable named wrapper. $chmod +x wrapper
4.check the two files has executable permission.
5.make the ezScrum as system service. $sudo ./ezScrum install
6.start ezScrum service. $sudo ./ezScrum start
7.access http://192.168.1.2:8080/ezScrum.

By The Way
1.make ezScrum stop $sudo ./ezScrum stop
2.uninstall ezScrum service $sudo ./ezScrum remove
3.Please clear bowser cache in client side, after updating the ezScrum server.