![logo](https://raw.githubusercontent.com/ezScrum/ezScrum/master/WebContent/images/readme/ezscrum_log_big.png)ezScrum
=======

ezScrum is a process management tool for Scrum.

- Easy to use
- Easy to manage
- Web based
- Open source


Version
------------
1.8.1


License
------------
GPL V2


Upgrade ezScrum
------------
In order to upgrade ezScrum and save all old project data for you, we need your assistance to fill out a questionnaire.
[https://ezscrum.typeform.com/to/iPXgBP
](https://ezscrum.typeform.com/to/iPXgBP "https://ezscrum.typeform.com/to/iPXgBP")

Snapshot
------------
![Snapshot](https://raw.githubusercontent.com/ezScrum/ezScrum/master/WebContent/images/readme/snapshot.png)


How to start
------------
1. Install JAVA 1.7
2. Install MySQL server
3. Download the newest ezScrum from <a href="https://sourceforge.net/projects/ezscrum/">HERE</a>.
4. Set database configurations in ```ezScrum.ini```.

    ```
    ServerUrl = <<DB_IP_ADDRESS>>
    Account = <<DB_ACCOUNT>>
    Password = <<DB_PASSWORD>>
    DatabaseName = <<DB_NAME>>
    ```

5. Set IP address in ```JettyServer.xml```. Replace ``localhost`` to your IP address.

    ```
    <Set name="host">
        <SystemProperty name="jetty.host" default="localhost"/>
    </Set>
    ```

    to

    ```
    <Set name="host">
        <SystemProperty name="jetty.host" default="<<IP_ADDRESS>>"/>
    </Set>
    ```

	####OS: Windows
    1. Set User Account Control (UAC) to lowest level.
    2. Double click ```InstallApp-NT.bat``` to install service.
    3. Double click ```ServiceStart.bat``` to start ezScrum service.

 ####OS: Ubuntu

	- Change directory to ezScrum_Ubuntu : 
	
    ``` bash
      cd ezScrum_Ubuntu/
    ```
	
	- Install dos2unix : 

	```bash
	sudo apt-get install tofrodos
	```

	```bash
	sudo ln -s /usr/bin/fromdos /usr/bin/dos2unix
	```
	- change ./setup.sh mod to execute : 
	```bash 
    chmod 755 ./setup.sh
    ```
	- Use dos2unix command only once in the first time.

	 Set up
	
	```bash
	sudo dos2unix ./setup.sh
	```
	
	```bash
	sudo ./setup.sh
	```
	##### start

	```bash
	sudo ./launch
	```
	##### Stop ezScrum service

	```bash
	sudo ./shutDown.sh
	```
	##### remove ezScrum

	```bash
	sudo ./remove.sh
	```
8. Open the browser and go to ``http://127.0.0.1:8080/ezScrum`` or ``http://<<IP_ADDRESS>>:8080/ezScrum``


Readme
----------
You can also check out online version <a href="https://github.com/ezScrum/ezScrum/blob/master/README.md">HERE</a>.
