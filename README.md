# trainingsplaner-resource-server

Diese Anwendung implementiert den Resource Server für die Trainingsplaner App und befindet sich aktuell in Entwicklung. 

Dieser REST Service läuft momentan lokal auf Port ``8081`` 
(konfiguriert in ``application.yaml``).

## Dokumentation
Bei gestarteter Anwendung:
* Aufrufen der swagger-ui: <localhost:8081/resource-server/swagger-ui.html>
* Abrufen der OpenAPI Beschreibung: <localhost:8081/resource-server/tp-resource-server-api-docs.yaml>

## REST Request
Alle Requests können aus dem Ordner postman entnommen und als eigene Collection importiert werden. 
Die Resource Server Collection enthält alle erlaubten CRUD-Operationen.
Die Resource Server Test Collection enthält Endpunkte, die nur zum Test gedacht sind: Einfügen von mehreren 
Testdatensätzen und Abruf einer Liste aller Testdaten.

### User ID
Jeder User hat eine von der E-Mail unabhängige User ID. Diese ist in Requests für User Items mitzugeben. 

### Authorization Header
Jeder Request erwartet einen Authorization HEADER. Im Tab "Authorization" unter Postman im zugehörigen Request kann ein 
Token eingegeben werden.

Alternativ kann der Authorization Header manuell unter dem "Header" Tab eingegeben werden. Dann muss 
``Bearer {accessToken}`` eingetragen werden.
