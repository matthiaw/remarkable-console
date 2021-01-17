# remarkable-console
I looked for a paperless solution and bought a [remarkable](https://remarkable.com/). But when i 
want to share some drawings a was frustrated about the quality of the SVG files and PDF files. 

The linux based remarkable saves the pages in zip-file and a binary format.
Detals could be found at [https://remarkablewiki.com/start](https://remarkablewiki.com/start).

**Requirements** 
 * Download zip-files of every notebook which is synchronized to the web application
 * Export notebook and page to single-page SVG, PNG and PDF 
 * Export notebook to multi-page PDF 
 * customize any color for exports (highlight color, black first color, gray secondary color, background color, grid color of template)
 * antialiasing svg images
 
## Install
Install the console with maven

```sh
$ mvn install
```

## First Startup
When the application is started with 

```sh
$ java -jar remarkable-console-0.1-SNAPSHOT-jar-with-dependencies.jar
```

then is starts registering to the web application [https://my.remarkable.com/](https://my.remarkable.com/)

### Register client to WEB application
To register copy the one-time-code into the terminal. This could be found at [https://my.remarkable.com/connect/desktop](https://my.remarkable.com/connect/desktop) by registering a 'Desktop App'

```
RM > Device not registered yet. Please input OneTimeCode from https://my.remarkable.com/connect/desktop:
RM > [Copy to this line One-Time-Code]
```

### Download svg templates through SSH
Add into application.properties the remarkable-ip and the  remarkable-password. This could be found in the remarkable at Settings > About > Copyrights and licenses > General information (scroll down)

```
ssh.host = xxx.xxx.xxx.xx
ssh.password = xxxxxxxxxx
```

You need to restart the client and must ensure that the remarkable is on.

## Usage
To excecute the download, the in-memory-mapping (reading) and the export call all the commands inside the console.

```
RM > download -n
RM > read -n
RM > export -n
```

The console could be closed by

```
RM > exit
```
