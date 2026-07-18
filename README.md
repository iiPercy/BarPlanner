# 🍸☕ BarPlanner (Client-Server Menu Management System)

BarPlanner è un'applicazione Client-Server sviluppata in **Java** che permette ai proprietari di bar e locali di gestire in modo semplice, veloce e accattivante i propri menù digitali, con la possibilità di esportare automaticamente listini impaginati in **PDF**.

Il sistema supporta la gestione multi-turno (es. *Caffetteria/Brunch* di giorno, *Cocktail Bar* di sera), adattando dinamicamente sia l'interfaccia utente che lo stile dei documenti generati.

## ✨ Features Principali

- **Architettura Client-Server:** Comunicazione fluida tra un'interfaccia desktop (JavaFX) e un backend RESTful (Spring Boot).
- **Popolamento Automatico DB:** In fase di inizializzazione, il server scarica in automatico dati reali di cibi e drink interrogando le API pubbliche di *TheCocktailDB* e *Spoonacular*.
- **Gestione Multi-Turno:** L'interfaccia utente cambia tema (Giorno ☀️ / Sera 🌙) in base al turno selezionato.
- **Generazione PDF:** Esportazione del menù in PDF con formattazione e palette di colori dinamica in base al turno di riferimento (tramite libreria OpenPDF/iText).
- **UI Asincrona:** L'interfaccia grafica rimane sempre reattiva grazie all'uso di `Task` e Thread separati per tutte le chiamate di rete HTTP verso il server.
- **CRUD e Filtri in tempo reale:** Ricerca istantanea nel menù per nome, categoria o ingrediente.

## 🛠️ Tecnologie Utilizzate

### Backend (Server)
* **Java 17+**
* **Spring Boot / Spring Data JPA** per la gestione del server e persistenza.
* **MySQL** come database relazionale (configurato tramite *application.properties*).
* **Lombok** per la riduzione del codice boilerplate (DTO ed Entity).

### Frontend (Client)
* **JavaFX** per l'interfaccia grafica desktop.
* **CSS** per lo styling dinamico dei temi giorno/sera.
* **OpenPDF (com.lowagie.text)** per l'impaginazione e stampa dei documenti PDF.

### Architettura e Scelte Tecniche (Developer Notes)
* Il progetto integra strumenti moderni ma mantiene volontariamente un approccio nativo su alcune specifiche funzionalità per dimostrare la padronanza dei fondamenti Java. 
* Le chiamate HTTP verso le API esterne (nel Server) e verso il Backend (nel Client) sono implementate nativamente tramite `HttpURLConnection`, evitando astrazioni ad alto livello (come `WebClient` o `RestTemplate`). 
* Il parsing dei dati da e verso JSON è gestito tramite la libreria `Gson` di Google per mappare agilmente strutture complesse sui DTO dell'applicazione.
* Durante lo sviluppo, sono stati impiegati strumenti di **Generative AI (Gemini Pro)** per velocizzare la scrittura del codice ripetitivo (come il parsing JSON -> DTO e la prototipazione dei file `.fxml`), ottimizzando il workflow e le tempistiche di rilascio.

---

## 📸 Screenshots
*(Aggiungi qui 3-4 screenshot della tua applicazione! Inseriscili trascinando le immagini direttamente su GitHub)*
* `![Schermata iniziale](link_immagine_1)`
* `![Gestione Menu Tema Giorno](link_immagine_2)`
* `![Esempio di PDF Generato](link_immagine_3)`

---

## 🚀 Come avviare il progetto

Il progetto è diviso in due moduli principali: `server` e `client`. 

### 1. Avvio del Server
1. Assicurati di avere un'istanza **MySQL** in esecuzione sulla porta `3306`.
2. Crea le variabili d'ambiente necessarie (o modificale nel file `application.properties`):
   * `DB_PASSWORD` (La password del tuo database root)
   * `spoonacular.api.key` (La tua API key di Spoonacular)
3. Il server creerà automaticamente il database `barplanner` se non esiste.
4. Avvia il server Spring Boot. Il servizio risponderà su `http://localhost:8080/BarPlanner`.

### 2. Avvio del Client
1. Una volta che il server è *UP*, apri il modulo Client.
2. Avvia la classe `App.java` per far partire l'interfaccia JavaFX.
3. Se è il primissimo avvio o il database è vuoto, clicca sul bottone rosso **"Inizializza Applicazione"** nella UI per avviare il fetch massivo dei dati dalle API esterne.
4. Seleziona il tuo turno e inizia a personalizzare il tuo menù!