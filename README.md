![logo](https://raw.githubusercontent.com/ezScrum/ezScrum/master/WebContent/images/readme/ezscrum_log_big.png)ezScrum
=======

ezScrum is a process management tool for Scrum.

- Easy to use
- Easy to manage
- Web based
- Open source


Version
------------
1.8.0 Alpha2


License
------------
GPL V2

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

6. OS: Windows
    - Set User Account Control (UAC) to lowest level.
    - Double click ```InstallApp-NT.bat``` to install service.
    - Double click ```ServiceStart.bat``` to start ezScrum service.

7. OS: Ubuntu
    - Change directory to ezScrum_Ubuntu

    ```
        cd ezScrum_Ubuntu/
    ```
    - Change script's mode for ```ezScrum```
    
    ```
        chmod +x ezScrum
    ```
    - Change wrapper's mode for ``wrapper``
    
    ```
        chmod +x wrapper
    ```
    - Add ezScrum to system service.
    
    ```
        sudo ./install
    ```
    - Start ezScrum service
    
    ```
        ./start
    ```
8. Open the browser and go to ``http://127.0.0.1:8080/ezScrum`` or ``http://<<IP_ADDRESS>>:8080/ezScrum``


Readme
----------
You can also check out online version <a href="https://github.com/ezScrum/ezScrum/blob/master/README.md">HERE</a>.
