# Corrections et tâches restantes

État au 30/09/2026. MS-Location est fonctionnel en local (REST + client gRPC) ; MS-User est fonctionnel après correction des imports gRPC.

---

## 1. MS-User — corrections à apporter

### 1.1 Imports gRPC manquants (bloquant — corrigé en local, à pousser)

`UserGrpcService.java` ne compilait pas : les imports des classes générées à partir de `user.proto` étaient absents.

```java
import r5a08.finduser.grpc.user.UserReply;
import r5a08.finduser.grpc.user.UserRequest;
import r5a08.finduser.grpc.user.UserServiceGrpc;
```

**À faire** : commiter la correction et la fusionner dans `main`.

### 1.2 Routes REST non conformes au sujet

Le sujet demande `POST /users`. En REST, l'action est portée par la méthode HTTP, pas par le chemin.

| Actuel | Route obtenue | Correction |
|---|---|---|
| `@PostMapping("/create")` | `POST /users/create` | `@PostMapping` |
| `@PutMapping("path/{id}")` | `PUT /users/path/{id}` (bug : texte de modèle VS Code resté en place) | `@PutMapping("/{id}")` |
| `@DeleteMapping("/delete/{id}")` | `DELETE /users/delete/{id}` | `@DeleteMapping("/{id}")` |

### 1.3 Code de retour de la création

`POST` renvoie **200** au lieu de **201 Created**.

**Correction** : ajouter `@ResponseStatus(HttpStatus.CREATED)` sur la méthode de création.

### 1.4 Port HTTP non défini

`application.properties` ne définit pas `server.port` : MS-User démarre sur **8080**, le même port que l'API Gateway. En local, les deux ne peuvent pas tourner en même temps.

**Correction** : ajouter `server.port=8081`.

### 1.5 Configuration non paramétrable pour Docker

L'URL de la base est écrite en dur (`localhost:5432`). En Docker Compose, la base sera joignable sur `user-db:5432`.

**Correction** : utiliser des variables d'environnement avec valeur par défaut, par exemple :

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/userdb}
spring.datasource.username=${DB_USERNAME:user}
spring.datasource.password=${DB_PASSWORD:user}
```

---

## 2. MS-Location — derniers points

- [ ] **Configuration paramétrable** : appliquer les variables d'environnement dans `application.yml` (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `USER_SERVICE_GRPC_TARGET`), puis vérifier que l'application démarre comme avant en local.
- [ ] **Import manquant** dans `LocationService` : `org.springframework.data.domain.Sort` (utilisé par `findAll(Sort.by("id"))`).
- [ ] **Données de test** : supprimer la localisation d'id 1 (`userId` `"0"`, créée avec l'ancien bouchon) avant les captures.
- [ ] **Tests PUT et DELETE** à rejouer avec le vrai appel gRPC et à consigner dans le README.
- [ ] **OpenAPI (optionnel)** : ajouter `@Schema(example = ...)` sur les champs des DTO et documenter le schéma `ProblemDetail` des réponses d'erreur.

---

## 3. À faire en binôme

### 3.1 API Gateway (Partie 3)

- [ ] Projet Spring Boot avec `spring-cloud-starter-gateway`.
- [ ] Routes dans `application.yml` : `/users/**` → MS-User, `/locations/**` → MS-Location.
- [ ] Aucune logique métier dans la Gateway (routage uniquement).
- [ ] Vérifier que `http://localhost:8080/users` est transmis à MS-User.

### 3.2 Dockerfiles

- [ ] Un `Dockerfile` dans chacun des dossiers `ms-user`, `ms-location` et `api-gateway`.
- [ ] Construire avec le Maven Wrapper et **sans les tests** (`./mvnw package -DskipTests`) : le test `contextLoads()` a besoin de PostgreSQL, absent au moment du build.

### 3.3 docker-compose.yml (Partie 4)

À placer **à la racine du dépôt** (les chemins `build: ./ms-user`, etc. sont relatifs à ce fichier).

- [ ] Cinq services : `api-gateway`, `ms-user`, `ms-location`, `user-db`, `location-db` (le sujet annonce « 6 services » mais n'en liste que cinq).
- [ ] Quatre réseaux : `gateway-net`, `internal-net`, `user-db-net`, `location-db-net`.
- [ ] Seul le port **8080** de la Gateway est publié.
- [ ] Variables d'environnement :
  - bases : `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` ;
  - MS-User : `DB_URL=jdbc:postgresql://user-db:5432/userdb`, identifiants ;
  - MS-Location : `DB_URL=jdbc:postgresql://location-db:5432/locationdb`, identifiants, `USER_SERVICE_GRPC_TARGET=ms-user:9090`.

### 3.4 Tests de bout en bout (Partie 5)

- [ ] Via la Gateway (Postman) : créer un utilisateur, puis une localisation pour cet utilisateur.
- [ ] grpcurl directement sur MS-User.
- [ ] Localisation pour un utilisateur inexistant : 404 (et non 500).

---

## 4. Livrables

- [ ] Code source des trois projets, avec `user.proto`.
- [ ] `docker-compose.yml` et les Dockerfile.
- [ ] Capture Postman (scénario de succès via la Gateway).
- [ ] Capture grpcurl (`GetUser` avec un id existant).
- [ ] Réponses aux questions de la section 7 (voir le README de MS-Location, section « Éléments pour la discussion »).