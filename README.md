<h1 align="center">
  <br>
  <a href="https://nordeck.net/"><img src="https://nordeck.net/wp-content/uploads/2020/05/NIC_logo_Nordeck-300x101.png" alt="Markdownify" width="300"></a>
  <br>
  Camunda-External-Task-OAuth2-Spring-Boot-Autoconfigure
  <br>
</h1>
<h4 align="center">A library to use the OAuth2 Flow for for <a href="https://camunda.com/" target="_blank">Camunda</a>.</h4>

### Key Features

Extension to connect the Camunda external task client to an OAuth2 secured Camunda REST API.

### Maven coordinates

TBD

### How to use

The extension makes use of Spring Boot autoconfiguration. Properties to set:

```yml
oauth2:
  issuer-uri: # e.g. https://someUrl:8082/oauth2/default
  token-uri:  # e.g.  http://localhost:8080/auth/oauth/token
  client-id:  # The client id to use
  client-secret: # The client secret
  scope: # The scopes to request
```

### Download

You can download the latest version of our library from this GitHub
repo (https://github.com/nordeck/camunda-external-task-oauth2-spring-boot-autoconfigure).

### Logging

To enable HTTP request / response logging via Logbook:

```xml

<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>logbook-spring-boot-starter</artifactId>
    <version>${logbook.version}</version>
</dependency>
```

### How to Contribute

Please take a look at our [Contribution Guidelines](https://github.com/nordeck/.github/blob/main/docs/CONTRIBUTING.md).

### License

This project is licensed under [APACHE 2.0](./LICENSE).

> [nordeck.net](https://nordeck.net/) &nbsp;&middot;&nbsp;
> GitHub [Nordeck](https://github.com/nordeck/)
