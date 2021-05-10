# trainingsplaner-resource-server

Resource Server für die Trainingsplaner App. Die Anwendung läuft lokal auf Port ``8081`` (konfiguriert in ``application.properties``).

## Dokumenation
Bei gestarteter Anwendung:
* Aufrufen der swagger-ui: <localhost:8081/resource-server/swagger-ui.html>
* Abrufen der OpenAPI Beschreibung: <localhost:8081/resource-server/tp-resource-server-api-docs.yaml>

## Request URLs
### Auslesen der User Informationen
* ``GET localhost:8081/trainingsplaner/resource-server/users/{userId}``
    * Header: ``Authorization: Bearer {accessToken}``
    * Path: ``{userId}`` ist momentan die Email Adresse

### Anlegen eines neuen Users in der Datenbank (temporärer Endpunkt)
* ``POST localhost:8081/trainingsplaner/resource-server/users``
    * Header: ``Authorization: Bearer {accessToken}``
    * Body: ``UserRequest`` Objekt (model/request/UserRequest.java)

## Request authorization in Postman
Jeder Request erwartet einen Authorization HEADER. Im Tab "Authorization" unter Postman im zugehörigen Request kann ein Token eingegeben werden.

Alternativ kann der Authorization Header manuell unter dem "Header" Tab eingegeben werden. Dann muss ``Bearer {accessToken}`` eingetragen werden 

### Mögliche Werte
* ``Bearer unauthorized``: gibt einen Fehler 401 UNAUTHORIZED zurück. Testet, was passiert, wenn ein ungültiger Token angegeben wird
* Andere Werte werden momentan als gültig anerkannt.
