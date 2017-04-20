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

> *Attualmente viene chiesto all’utente (apri o salva dopo download)

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
  - [ ] Inserimento/cancellazione relazione utente-ufficio
  - [ ] Gestione ACL (assegnazione e modifica per ogni modifica delle attribuzioni)
  - [ ] Operazione di consolida (paragrafo "Protocollazione ed archiviazione" -> cambio stato di archiviazione)
  - [ ] Helper nel contesto di scripting Groovy
  - [ ] Supporto per l’introduzione dell’helper nei contesti rimanenti

- [ ] VARIE
  - [ ] Uso del metadato “Originale cartaceo" (permessi mix tra originali cartacei e digitali in stesso protocollo??)
  - [ ] Supporto per adeguamento script Mauro (probabilmente chiamate rest dirette)
  - [ ] Sistemazione codice
  - [ ] Gestione hash x stampa registro protocollo *
  - [ ] Test e validazione

> *non so se possa essere utile ma ho calcolato ed inserito Hash MD5 file ad ogni upload in docer (helper lo fa in autonomia sapendo che poteva esserci utile e lo mette in metadato DOC_HASH_KEY)