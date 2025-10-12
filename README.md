# ez-booth

Successor to [tschuba/ez-basar](https://github.com/tschuba/ez-basar).

## How-Tos

### Package the application

```shell
./mvnw clean compile &&
  ./mvnw install -pl .,core,test && 
  ./mvnw -Pproduction package -pl server,vaadin-ui
```
