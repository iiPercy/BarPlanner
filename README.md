# 🍸☕ BarPlanner (Client-Server Menu Management System)

(Project for the "Advanced Coding" exam at the University of Pisa - Score: 30/30)

BarPlanner is a Client-Server application developed in **Java** that allows bar and club owners to easily, quickly, and attractively manage their digital menus, with the ability to automatically export paginated price lists as **PDFs**.

The system supports multi-shift management (*Cafe/Brunch* during the day, *Cocktail Bar* at night), dynamically adapting both the user interface and the style of the generated documents.

## ✨ Main Features

* **Client-Server Architecture:** Fluid communication between a desktop interface (JavaFX) and a RESTful backend (Spring Boot).
* **Automatic DB Population:** During initialization, the server automatically downloads real food and drink data by querying the public APIs of *TheCocktailDB* and *Spoonacular*.
* **Multi-Shift Management:** The user interface changes theme (Day ☀️ / Night 🌙) based on the selected shift.
* **PDF Generation:** Exporting the menu to PDF with formatting and dynamic color palettes based on the reference shift (via OpenPDF/iText library).
* **Asynchronous UI:** The graphical interface always remains responsive thanks to the use of `Task` and separate Threads for all HTTP network calls to the server.
* **CRUD and Real-time Filters:** Instant search in the menu by name, category, or ingredient.

## 🛠️ Technologies Used

### Backend (Server)

* **Java 17+**
* **Spring Boot / Spring Data JPA** for server management and persistence.
* **MySQL** as the relational database (configured via *application.properties*).
* **Lombok** to reduce boilerplate code (DTOs and Entities).

### Frontend (Client)

* **JavaFX** for the desktop graphical interface.
* **CSS** for the dynamic styling of day/night themes.
* **OpenPDF (com.lowagie.text)** for pagination and printing of PDF documents.

### Architecture and Technical Choices (Developer Notes)

* The project integrates modern tools but deliberately maintains a native approach on some specific functionalities to demonstrate mastery of Java fundamentals, as required by the provided specifications.
* HTTP calls to external APIs (in the Server) and to the Backend (in the Client) are natively implemented via `HttpURLConnection`, avoiding high-level abstractions (such as `WebClient` or `RestTemplate`).
* Data parsing to and from JSON is handled via Google's `Gson` library to easily map complex structures onto the application's DTOs.
* During development, **Generative AI (Gemini Pro)** tools were used to speed up the writing of repetitive code (such as JSON -> DTO parsing and prototyping `.fxml` files), optimizing the workflow and release timelines.

---

## 📸 Screenshots

* **Initial Screen**
<br>
  <img width="894" height="589" alt="Schermata iniziale" src="https://github.com/user-attachments/assets/92208a80-3ae2-43bc-9b7f-31abf52ff292" />
  
* **Menu Management Day Theme**
<br>
  <img width="894" height="591" alt="Gestione Menu Tema Giorno" src="https://github.com/user-attachments/assets/a7d186ba-4383-468c-a706-db1df265dc61" />
  
* **Generated PDF Example**
<br>
  <img width="893" height="1226" alt="Esempio di PDF Generato" src="https://github.com/user-attachments/assets/f8f999ed-e40d-4348-b383-3d48d814d943" />
  
---

## 🚀 How to run the project

The project is divided into two main modules: `server` and `client`.

### 1. Starting the Server

1. Make sure you have a **MySQL** instance running on port `3306`.
2. Create the necessary environment variables (or modify them in the `application.properties` file):
* `DB_PASSWORD` (The password for your root database)
* `spoonacular.api.key` (Your Spoonacular API key)


3. The server will automatically create the `barplanner` database if it does not exist.
4. Start the Spring Boot server. The service will respond on `http://localhost:8080/BarPlanner`.

### 2. Starting the Client

1. Once the server is *UP*, open the Client module.
2. Run the `App.java` class to start the JavaFX interface.
3. Upon the first launch, the database is automatically generated and populated, but in case of corrupted files or if you want to download all the data from the external APIs again, there is a red **"Inizializza Applicazione"** (Initialize Application) button in the UI that manually starts the massive data fetch from the external APIs.
4. Select your shift and start customizing your menu!
