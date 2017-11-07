# GDA-DOCER
WebApp per browsing DOCER e REST Api

## DOCUMENTAZIONE

- https://comunerivadelgarda.github.io/gda-docer-ui/
- https://comunerivadelgarda.github.io/gda-docer-ui/api-doc/

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
  - [x] Download di file
  - [x] Nuova versione
  - [x] Download versione
  - [x] Gestione policy crud
  - [x] Documentazione API REST (Swagger)
  - [x] Documentazione API Helper (Java Doc)
  - [x] Refactor Helper x semplificazione chiamate dopo prima versione (incapsulato "oggetti\metodi" SOAP ed esposto Liste\Mappe)
  - [x] Gestione ACL
    - [x] API Helper
    - [x] Supporto parametro acls via url
  - [x] Gestione watermark tramite chiamata iWas (B3)
    - [x] parametro da passare a GDA a WebView (TIZIANO e MICHELA). Il parametro è di tipo [{descrizione:nome, righe:[lista righe]}]
    - [x] codice server che prende valori e genera (MIRCO). Modifica UI in funzione del parametro: combo con le scelte + pulsante (se una solo selezione predefinita)
    - [ ] da testare    
  - [ ] Upload massivo (C)
    - [ ] Finestra UPLOAD divisa in due parti PRINCIPALE (upload singolo) e ALLEGATI (upload multiplo). Principale compare solo se per external_id non presente (MIRCO)
  - [x] Download massivo (C)
    - [x] basato su external_id, crea lo zip e propone download dello zip
    - [x] nome dello zip da dare viene passato come parametro f= (se manca uso DATA e ORA generazione zip)
  - [ ] Valutare Icone ed Ottimizzazione UI con Michela (C) 
  
> *Attualmente si apre solo la finestra salva file

- [x] WEBAPP BRIDGE
  - [x] compatibilità Explorer con WebKit 
  - [x] Apertura Explorer (compatibilità)
  - [x] Cartella di download preimpostabile
  - [x] Salvataggio contenuti non gestiti da browser
  - [x] Inettare codice JS, cookie jar, finestre in popup (compatibilità App come jEnte)
  - [x] Apri file direttamente da pulsante (scelta tra download e apri)* (B3)
    - [x] Modifica WebVIEW (TIZIANO)
    - [ ] da testare

- [x] USO HELPER IN GDA
  - [x] Listing di documenti per ext_id, con relativi metadati
  - [x] Lettura contenuto documento (stream e byte[])
  - [x] Inserimento documenti con ext_id
  - [x] Test su caso avanzato (pubblicazione protocollo)
  - [x] Inserimento nuovo ufficio = gruppo
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
    - [x] usato in GDA
  - [x] Inserimento/cancellazione relazione utente-ufficio=gruppo
    - [x] API Helper paragrafo Gestione associazioni utente-ufficio e autenticazione
    - [x] usato in GDA
  - [x] Gestione ACL (assegnazione e modifica per ogni modifica delle attribuzioni) (B5)
    - [x] API Helper
    - [x] usato in GDA
    - [ ] Ticket #72 (MIRCO)
  - [x] uso Helper nel contesto di scripting Groovy (determine)
    - [x] usato in GDA
  - [x] Modifica GDA-DOCUMENTALE (B5)
  - [x] GDA-PEC Adeguamento 
    - [x] chiamate GDA-PEC verso GDA-DOCUMENTALE per parametro "ANNESSO" (verificare categoria giusto)
    - [x] archiviazione PEC in ingresso con EML PRINCIPALE e ALLEGATI
    - [x] Verificare il CONTENT-TYPE se esiste come metadato su DOCER (PEC) - dove usato e se indispensabile. Il problema potrebbe essere su quelle in uscite (perche' prende allegato da DOCER)
    - [x] e' GDAPEC che prende da DOCER l'allegato?
    - [x] GDAPEC dopo protocollo ed archiviazione in DOCER chiamata a microservizio x allineare ACL su Protocollo
  - [ ] Operazione di consolida (paragrafo "Protocollazione ed archiviazione" -> cambio stato di archiviazione)
    - [x] API Helper
    - [ ] usato in GDA
  - [ ] Supporto per l’introduzione dell’helper nei contesti rimanenti
    - [ ] Albo
    - [x] GDA-PEC
      - [ ] da Testare
    - [x] Protocollazione Esterna (gda-ws gda-documentale)
      - [x] da Testare (si testa con PEC)

- [ ] VARIE
  - [x] Supporto per adeguamento script Mauro (probabilmente chiamate rest dirette)
    - [x] adeguato servizio che Mauro utilizza (gda-ws) (Tiziano)
  - [x] Sistemazione codice
  - [x] Creazione Utenti e Gruppi via API SOAP
  - [x] Uso dell'Helper come istanza di Classe che da errore (vedi email Tiziano) (B3)
    - [x] sembra stranezza progetto GDA (perche' su nuovo progetto ok)
  - [x] ACLs da GDA dare le attribuzioni a DOCER (B3)
   - [x] REST pronti da usare
  - [x] protocollazione (mai provata) (B5)
    - [x] testato
  - [x] aggiungi i metadati documento per Registrazione, Conservazione e Pubblicazione (MIRCO metto in B)
    - [x] Registrazione
    - [x] Conservazione
    - [x] Pubblicazione
  - [x] verificare ENUM Metadati con Allegato XLS metadati (MIRCO metto in B)
  - [x] metadato originale cartaceo/digitale (B3)
    - [x] Si esite già il metadato "ARCHIVE_TYPE" e deve esse settato a " PAPER"
  - [ ] invio in conservazione (C)
  - [ ] Test e validazione
  - [ ] chiamate rest da Python
  - [ ] Uso del metadato “Originale cartaceo" (permessi mix tra originali cartacei e digitali in stesso protocollo??)
  - [ ] Gestione hash x stampa registro protocollo
    - [x] Calcolo DOC_HASH automatico Helper
    - [ ] stampa del protocollato il giorno tot (elenco HASH da external_id)
    - [ ] stampa registro delle modifiche
      - [ ] docer sa cosa è modificato
      - [ ] versionamento del file
    - [x] Helper nuovo metodo ricerca documenti modificati in data X (MIRCO) --> EXTERNAL_ID NOME_FILE HASH
      - [x]  verifica errore ricerca per Data ed intervallo EXTERNAL_ID
    - [ ] 27/11/2017 estrarre 2 registri: registro di protocollo giornaliero e registro delle modifiche
      - [x] tutte le registrazioni di protocollo N.|Data|Oggetto|HASH
      - [x] registro delle modifiche giornaliere (dal giorno prima indietro) modifiche ai dati e/o files
      - [ ] TIZIANO 07/11/2017: jasper con fonte JSON registri ci sono ad esclusione degli hash NOME FILE|HASH
      - [ ] TIZIANO: modifica script attuale che deve aggiungere EXTERNAL_ID come colonna
      - [ ] TIZIANO: legge da PS + EXTERNAL_ID, crea PDF, file inviato a DOCER e non da ALFRESCO
      - [x] MIRCO: RG param IN data (GG singolo) EXT=PROTOCOLLO_X,PROTOCOLLO_Y prendi tutti tutti i DOC con quegli EXTERNAL_ID (da - a) creati quel giorno --> EXTERNAL_ID|NOME|HASH
      - [x] MIRCO: RM param IN PROTOCOLLO IN tutti i documenti aggiunti o modificati (OGGI, giorno che si sta concludendo) con EXTERNAL_ID precedente a PROTOCOLLO_X
