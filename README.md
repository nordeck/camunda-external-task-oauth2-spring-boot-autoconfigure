<h1 align="center">
  <br>
  <a href="https://nordeck.net/"><img src="https://nordeck.net/wp-content/uploads/2020/05/NIC_logo_Nordeck-300x101.png" alt="Markdownify" width="300"></a>
  <br>
  Camunda-External-Task-OAuth2-Spring-Boot-Autoconfigure
  <br>
</h1>
<h4 align="center">A library to use the OAuth2 Flow for for <a href="https://camunda.com/" target="_blank">Camunda</a>.</h4>


# Camunda external task OAuth2 extension
Extension to connect the Camunda external task client to an OAuth2 secured Camunda REST API.

## Maven coordinates

```xml
<dependency>
    <groupId>net.nordeck.camunda</groupId>
    <artifactId>camunda-external-task-oauth2-spring-boot-autoconfigure</artifactId>
    <version>$VERSION</version>
</dependency>
```

## Configuration
The extension makes use of Spring Boot autoconfiguration. Properties to set:
```yml
oauth2:
    issuer-uri: # e.g. https://my.keycloak.somewhere/auth/realms/camunda
    token-uri:  # e.g. https://my.keycloak.somewhere/auth/realms/camunda/protocol/openid-connect/token
    client-id:  # The realm's client id to use
    client-secret: # The realm's client secret
    scope: # The scopes to request
```

## Logging
To enable HTTP request / response logging via Logbook:
```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>logbook-spring-boot-starter</artifactId>
    <version>${logbook.version}</version>
</dependency>
```

See [Logbook](https://github.com/zalando/logbook).

### License

This project is licensed under [APACHE 2.0](./LICENSE).

> [nordeck.net](https://nordeck.net/) &nbsp;&middot;&nbsp;
> GitHub [Nordeck](https://github.com/nordeck/) 
