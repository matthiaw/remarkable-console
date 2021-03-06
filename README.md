# remarkable-console

```
__________               _________                
\______   \ ____   _____ \_   ___ \  ____   ____  
 |       _// __ \ /     \/    \  \/ /  _ \ /    \ 
 |    |   \  ___/|  Y Y  \     \___(  <_> )   |  \
 |____|_  /\___  >__|_|  /\______  /\____/|___|  /
        \/     \/      \/        \/            \/ 
RemCom - Remarkable Console (0.1)
```

I looked for a paperless solution and bought a [remarkable](https://remarkable.com/). But when i 
want to share some drawings i was frustrated about the quality of the SVG files and PDF files.  So this project started with the goal to get better exports through a java terminal based application.

Needs [remarkable-api](https://github.com/matthiaw/remarkable-api).

The linux based remarkable saves the pages in zip-file and a binary format.
Details could be found at [https://remarkablewiki.com/start](https://remarkablewiki.com/start).

**Requirements** 
 * Download zip-files of every notebook which is synchronized to the web application
 * Export notebook and page to single-page SVG, PNG and PDF 
 * Export notebook to multi-page PDF 
 * customize any color for exports (highlight color, black first color, gray secondary color, background color, grid color of template)
 * antialiasing svg images
 
## Install
Install the console with maven

```sh
$ mvn clean install
```
Check with `mvn --version` has `JAVA_HOME` greater Java 1.8 (see [Details](https://subscription.packtpub.com/book/application_development/9781785286124/1/ch01lvl1sec13/changing-the-jdk-used-by-maven)). If you not use a high java version with maven build, the error `class file has wrong version 55.0, should be 52.0` occurs.

## First Startup
When the application is started with 

```sh
$ java -jar remarkable-console.jar
```

then is starts registering to the web application [https://my.remarkable.com/](https://my.remarkable.com/)

### Register client to WEB application
To register copy the one-time-code into the terminal. This could be found at [https://my.remarkable.com/connect/desktop](https://my.remarkable.com/connect/desktop) by registering a 'Desktop App'

```
RM > Device not registered yet. Please input OneTimeCode from https://my.remarkable.com/connect/desktop:
RM > [Copy to this line One-Time-Code]
```

### Download svg templates through SSH
Add into `application.properties` the remarkable-ip and the remarkable-password. This could be found in the remarkable at *Settings > About > Copyrights and licenses > General information (scroll down)*

```
ssh.host = xxx.xxx.xxx.xx
ssh.password = xxxxxxxxxx
```

You need to restart the client and must ensure that the remarkable is on.

## Usage
To excecute the download, the in-memory-mapping (reading) and the export call all the commands inside the console.

```
RM > notebooks -d -r -e
```

to start the embedded webserver at http://localhost:8090/ use

```
RM > server --start
```

The console could be closed by

```
RM > exit
```
