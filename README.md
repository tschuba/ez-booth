# ez-booth

Successor to [tschuba/ez-basar](https://github.com/tschuba/ez-basar).

## Build application JARs

```shell
./mvnw clean package -Pproduction -pl server,vaadin-ui -am -DskipTests
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

## Create JPackage application image

```shell
mvn clean package -Pproduction -Pdist -pl server,vaadin-ui -am -DskipTests
```

## Create application distribution archives

Execute the command from [Build application JARs](#build-application-jars) and then execute:

```shell
# Tests are skipped here for efficiency, as they have already been run in the build step above.
./mvnw verify -DskipTests -pl .
```

This command will create the following distribution files:

```text
.
└── target/
│   └── ez-booth-{version}-linux.tar.gz
│   └── ez-booth-{version}-windows.zip
```
