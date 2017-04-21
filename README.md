# GDA-DOCER
WebApp per browsing DOCER e REST Api

## RUN

	mvn clean tomcat7:run
	
## REQUISITI

Viene utilizzato angular 1.4 altrimenti abbiamo un problema con WebView ()

## TODO

- [ ] EXPLORER
  - [-] Upload di file su Folder (+ gestione folder e browsing) (eliminato in favore di ext_id)
  - [-] Controlli Upload un solo PRINCIPALE su Cartella (eliminato in favore di ext_id)
  - [x] Upload di file con ext_id, titolo, descrizione, tipo
  - [x] Gestione Related con ext_id
  - [x] Eliminazione File
  - [ ] Upload massivo
  - [x] Download di file
  - [ ] Download massivo
  - [x] Nuova versione
  - [x] Download versione
  - [x] Gestione policy crud
  - [ ] Apri file direttamente da pulsante (scelta tra download e apri)*
  - [ ] Gestione watermark tramite chiamata iWas
  - [x] Documentazione API REST (Swagger)
  - [x] Documentazione API Helper (Java Doc)
  - [x] Refactor Helper x semplificazione chiamate dopo prima versione (incapsulato "oggetti\metodi" SOAP ed esposto Liste\Mappe)
  - [ ] Valutare Icone ed Ottimizzazione UI con Michela
  - [x] Gestione ACL
    - [x] API Helper
    - [x] Supporto parametro acls via url

> *Attualmente si apre solo la finestra salva file

- [*] WEBAPP BRIDGE
  - [*] compatibilità Explorer con WebKit 
  - [*] Apertura Explorer (compatibilità)
  - [*] Cartella di download preimpostabile
  - [*] Salvataggio contenuti non gestiti da browser
  - [*] Inettare codice JS, cookie jar, finestre in popup (compatibilità App come jEnte)

- [*] USO HELPER IN GDA
  - [*] Listing di documenti per ext_id, con relativi metadati
  - [*] Lettura contenuto documento (stream e byte[])
  - [*] Inserimento documenti con ext_id
  - [*] Test su caso avanzato (pubblicazione protocollo)
  - [ ] Inserimento nuovo ufficio
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
  - [ ] Inserimento/cancellazione relazione utente-ufficio
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
  - [ ] Gestione ACL (assegnazione e modifica per ogni modifica delle attribuzioni)
    - [x] API Helper
  - [ ] Operazione di consolida (paragrafo "Protocollazione ed archiviazione" -> cambio stato di archiviazione)
    - [x] API Helper
  - [ ] Helper nel contesto di scripting Groovy
  - [ ] Supporto per l’introduzione dell’helper nei contesti rimanenti

- [ ] VARIE
  - [ ] Uso del metadato “Originale cartaceo" (permessi mix tra originali cartacei e digitali in stesso protocollo??)
  - [ ] Supporto per adeguamento script Mauro (probabilmente chiamate rest dirette)
  - [ ] Sistemazione codice
  - [ ] Gestione hash x stampa registro protocollo
    - [x] Calcolo DOC_HASH_KEY automatico Helper
  - [ ] Test e validazione
  - [x] Creazione Utenti e Gruppi via API SOAP - ERRORE IN ATTESA DI RISP. KDM