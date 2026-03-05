# ez-booth

Successor to [tschuba/ez-basar](https://github.com/tschuba/ez-basar).

## How-Tos

### Build the application JAR

```shell
./mvnw install -pl .,test,core && 
  ./mvnw package -Pproduction -pl server,vaadin-ui
```

This command will create the following JAR files:

```text
.
├── server/
│   └── target/
│       └── ez-booth-1.0.0-server-{version}.jar
├── vaadin-ui/
│   └── target/
│       └── ez-booth-1.0.0-vaadin-ui-{version}.jar
```

### JPackage application image

```shell
mvn clean package -Pdist -DskipTests -pl server,vaadin-ui -am
```

### Create application distribution archives

Execute the command from [Build the application](#build-the-application) and then execute:

```shell
# Tests are skipped here for efficiency, as they have already been run in the build step above.
./mvnw verify -DskipTests=true -pl .
```

This command will create the following distribution files:

```text
.
└── target/
│   └── ez-booth-{version}-linux.tar.gz
│   └── ez-booth-{version}-windows.zip
```
