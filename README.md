# GDA-DOCER
WebApp per browsing DOCER e REST Api

## DOCUMENTAZIONE

https://comunerivadelgarda.github.io/gda-docer-ui/
https://comunerivadelgarda.github.io/gda-docer-ui/api-doc/

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
  - [ ] Upload massivo (C)
    - [ ] Finestra UPLOAD divisa in due parti PRINCIPALE (upload singolo) e ALLEGATI (upload multiplo). Principale compare solo se per external_id non presente
  - [x] Download di file
  - [ ] Download massivo (C)
    - [ ] basato su external_id, crea lo zip e propone download dello zip
  - [x] Nuova versione
  - [x] Download versione
  - [x] Gestione policy crud
  - [ ] Apri file direttamente da pulsante (scelta tra download e apri)* (B3)
  - [ ] Gestione watermark tramite chiamata iWas (B3)
  - [x] Documentazione API REST (Swagger)
  - [x] Documentazione API Helper (Java Doc)
  - [x] Refactor Helper x semplificazione chiamate dopo prima versione (incapsulato "oggetti\metodi" SOAP ed esposto Liste\Mappe)
  - [ ] Valutare Icone ed Ottimizzazione UI con Michela (C)
  - [x] Gestione ACL
    - [x] API Helper
    - [x] Supporto parametro acls via url

> *Attualmente si apre solo la finestra salva file

- [x] WEBAPP BRIDGE
  - [x] compatibilità Explorer con WebKit 
  - [x] Apertura Explorer (compatibilità)
  - [x] Cartella di download preimpostabile
  - [x] Salvataggio contenuti non gestiti da browser
  - [x] Inettare codice JS, cookie jar, finestre in popup (compatibilità App come jEnte)

- [x] USO HELPER IN GDA
  - [x] Listing di documenti per ext_id, con relativi metadati
  - [x] Lettura contenuto documento (stream e byte[])
  - [x] Inserimento documenti con ext_id
  - [x] Test su caso avanzato (pubblicazione protocollo)
  - [ ] Inserimento nuovo ufficio
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
  - [ ] Inserimento/cancellazione relazione utente-ufficio
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
  - [ ] Gestione ACL (assegnazione e modifica per ogni modifica delle attribuzioni) (B5)
    - [x] API Helper
  - [ ] Operazione di consolida (paragrafo "Protocollazione ed archiviazione" -> cambio stato di archiviazione)
    - [x] API Helper
  - [ ] Helper nel contesto di scripting Groovy
  - [ ] Supporto per l’introduzione dell’helper nei contesti rimanenti
  - [ ] Modifica GDA-DOCUMENTALE (B5)
    - [ ] GDA-PEC Adeguamento chiamate GDA-PEC verso GDA-DOCUMENTALE per parametro "ANNESSO" (verificare categoria giusto)
    - [ ] Verificare il CONTENT-TYPE se esiste come metadato su DOCER (PEC) - dove usato e se indispensabile

- [ ] VARIE
  - [ ] Uso del metadato “Originale cartaceo" (permessi mix tra originali cartacei e digitali in stesso protocollo??)
  - [ ] Supporto per adeguamento script Mauro (probabilmente chiamate rest dirette)
  - [ ] Sistemazione codice
  - [ ] Gestione hash x stampa registro protocollo
    - [x] Calcolo DOC_HASH_KEY automatico Helper
  - [ ] Test e validazione
  - [x] Creazione Utenti e Gruppi via API SOAP
  - [ ] Uso dell'Helper come istanza di Classe che da errore (vedi email Tiziano) (B3)
  - [ ] ACLs da GDA dare le attribuzioni a DOCER (B3)
  - [ ] chiamate rest da Python
  - [ ] protocollazione (mai provata) (B5)
  - [ ] invio in conservazione (C)
  - [ ] aggiungi i metadati documento per Registrazione, Conservazione e Pubblicazione
  - [ ] metadato originale cartaceo/digitale (B3)
