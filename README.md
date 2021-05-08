# trainingsplaner-resource-server

Resource Server für die Trainingsplaner App.

## Port
Die Anwendung läuft lokal auf Port ``8081``.

## Request URLs
### Auslesen der User Informationen
* ``GET localhost:8081//trainingsplaner/resource-server/users?uid={user-id}``
    * Header: ``Authorization: Bearer {accessToken}``
    * Query:``uid`` ist momentan die Email Adresse

### Anlegen eines neuen Users in der Datenbank (temporärer Endpunkt)
* ``POST localhost:8081//trainingsplaner/resource-server/users``
    * Header: ``Authorization: Bearer {accessToken}``
    * Body: ``UserRequest`` Objekt (model/request/UserRequest.java)

## Request authorization in Postman
Jeder Request erwartet einen Authorization HEADER. Im Tab "Authorization" unter Postman im zugehörigen Request kann ein Token eingegeben werden.

Alternativ kann der Authorization Header manuell unter dem "Header" Tab eingegeben werden. Dann muss ``Bearer {accessToken}`` eingetragen werden 

### Mögliche Werte
* ``Bearer unauthorized``: gibt einen Fehler 401 UNAUTHORIZED zurück. Testet, was passiert, wenn ein ungültiger Token angegeben wird
* Andere Werte werden momentan als gültig anerkannt.
