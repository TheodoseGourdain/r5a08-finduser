# FindUser — application User/Location

BUT 3 — R5.A.08 Qualité de développement — TP « Implémentation de microservices : REST, gRPC et API Gateway ».

> Ce README documente pour l'instant **MS-Location**. Les sections MS-User, API Gateway et Docker Compose seront ajoutées ensuite.

---

## MS-Location

Microservice de gestion des localisations GPS des utilisateurs. Il possède sa propre base de données (**Location-DB**, pattern *Database per Service*) et vérifie l'existence des utilisateurs auprès de **MS-User** par un appel **gRPC** interne.

### Stack technique

| Élément | Version / choix |
|---|---|
| Spring Boot | 4.1.1 |
| Java | 17 (compilation), exécuté en 21 |
| Base de données | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate 7 |
| Client gRPC | `spring-boot-starter-grpc-client` (génération des stubs via `protobuf-maven-plugin`) |
| Documentation | springdoc-openapi 3.1.0 (Swagger UI) |
| Build | Maven Wrapper (`./mvnw`) |

### Organisation du code

```
r5a08.finduser.ms_location
├── config/        GrpcClientConfig          stub client gRPC vers MS-User
├── controller/    LocationController        routes REST + documentation OpenAPI
├── dto/           LocationRequest           corps du POST
│                  LocationPutRequest        corps du PUT
│                  LocationPatchRequest      corps du PATCH
│                  LocationResponse          réponse de toutes les routes
├── exception/     LocationNotFoundException
│                  UserNotFoundException
│                  UserServiceUnavailableException
│                  GlobalExceptionHandler    exceptions → réponses HTTP (RFC 9457)
├── model/         Location                  entité JPA
├── repository/    LocationRepository        Spring Data JPA
├── service/       LocationService           logique métier
└── user/          UserVerifier              contrat « l'utilisateur existe-t-il ? »
                   GrpcUserVerifier          implémentation gRPC
src/main/proto/    user.proto                contrat gRPC partagé avec MS-User
```

### API REST

Préfixe : `/locations`. Port local : **8082**.

| Méthode | Route | Rôle | Succès | Erreurs |
|---|---|---|---|---|
| `GET` | `/locations` | Lister toutes les localisations (triées par id) | 200 | — |
| `GET` | `/locations?userId=…` | Lister les localisations d'un utilisateur | 200 | 404 utilisateur, 503 |
| `GET` | `/locations/{id}` | Lire une localisation | 200 | 404 |
| `POST` | `/locations` | Enregistrer une localisation | 201 | 400, 404 utilisateur, 503 |
| `PUT` | `/locations/{id}` | Remplacer une localisation | 200 | 400, 404 |
| `PATCH` | `/locations/{id}` | Modifier partiellement une localisation | 200 | 400, 404 |
| `DELETE` | `/locations/{id}` | Supprimer une localisation | 204 | 404 |

Les routes `PUT`, `PATCH` et `DELETE` vont au-delà du sujet (qui demande `POST`, `GET /{id}` et `GET ?userId=`).

Exemple de corps pour `POST` :

```json
{
  "userId": "1",
  "latitude": 47.6582,
  "longitude": -2.7608,
  "dateHeure": "2026-09-30T10:00:00",
  "nom": "IUT de Vannes"
}
```

Documentation interactive : `http://localhost:8082/swagger-ui.html` — spécification : `http://localhost:8082/v3/api-docs` (importable dans Postman).

### Choix de conception

- **`userId` sans relation JPA.** Les utilisateurs vivent dans une autre base : l'entité ne stocke que l'identifiant. L'intégrité est assurée par l'appel gRPC à MS-User lors de la création.
- **`userId` non modifiable.** Une localisation enregistre un fait (« tel utilisateur était à tel endroit ») : la réattribuer n'a pas de sens et compliquerait l'autorisation (TP5). Le setter est privé et le champ est absent des DTO `PUT` et `PATCH`. Conséquence : ces deux routes n'appellent pas MS-User.
- **`PUT` ≠ `PATCH`.** `PUT` remplace la ressource entière (tous les champs obligatoires) ; `PATCH` ne modifie que les champs fournis (un champ absent n'est pas modifié).
- **DTO séparés de l'entité.** Le contrat JSON est indépendant du modèle JPA ; le client ne peut ni fixer ni modifier l'`id`.
- **Double validation.** Les DTO valident l'entrée (`@Valid`, erreurs 400 détaillées) ; les setters de l'entité garantissent qu'elle reste toujours valide.
- **`GET ?userId=` vérifie l'utilisateur.** Un utilisateur inexistant donne une 404, ce qui le distingue d'un utilisateur existant sans localisation (liste vide).
- **Abstraction `UserVerifier`.** Le service ne dépend que de l'interface, pas de gRPC. Un bouchon a permis de développer MS-Location avant que MS-User soit prêt ; il a été supprimé à l'arrivée de `GrpcUserVerifier`.

### Gestion des erreurs

Les erreurs sont renvoyées au format `application/problem+json` (RFC 9457, classe `ProblemDetail`).

| Cause | Code | Exemple de `detail` |
|---|---|---|
| Localisation inexistante | 404 | « La localisation 114 n'existe pas. » |
| Utilisateur inexistant (`exists = false`) | 404 | « L'utilisateur 9999 n'existe pas. » |
| Validation `@Valid` échouée | 400 | « Données invalides » + objet `errors` par champ |
| Valeur refusée par un setter | 400 | message du setter |
| MS-User injoignable ou trop lent | 503 | « Le service utilisateurs est indisponible (gRPC : UNAVAILABLE). » |

### Communication gRPC avec MS-User

- **Contrat** : `user.proto` (`UserService.GetUser`), identique dans MS-User et MS-Location, `java_package = "r5a08.finduser.grpc.user"`.
- **Canal** : `spring.grpc.client.channel.user-service.target` (propriété Spring Boot 4.1).
- **Délai** : chaque appel est limité à **2 secondes** (`withDeadlineAfter`).
- **Traduction des statuts gRPC** :
  - `exists = false` ou statut `NOT_FOUND` → 404 ;
  - tout autre statut (`UNAVAILABLE`, `DEADLINE_EXCEEDED`, …) → 503.

### Configuration

Les adresses sont paramétrables par variables d'environnement ; les valeurs par défaut correspondent à l'environnement local.

| Variable | Défaut (local) | Valeur prévue en Docker |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/locationdb` | `jdbc:postgresql://location-db:5432/locationdb` |
| `DB_USERNAME` | `location` | identique |
| `DB_PASSWORD` | `location` | identique |
| `USER_SERVICE_GRPC_TARGET` | `localhost:9090` | `ms-user:9090` |

### Lancer en local

```bash
# Base Location-DB (première fois)
docker run -d --name location-db-dev -p 5433:5432 \
  -e POSTGRES_DB=locationdb -e POSTGRES_USER=location -e POSTGRES_PASSWORD=location \
  postgres:16

# Les fois suivantes
docker start location-db-dev

# Application (depuis ms-location/)
./mvnw spring-boot:run
```

MS-User doit tourner (gRPC sur `localhost:9090`) pour les routes qui vérifient l'utilisateur.

> Toujours utiliser `./mvnw` et non `mvn` : le plugin protobuf exige Maven ≥ 3.9.6.
> `./mvnw package` lance le test `contextLoads()`, qui nécessite la base démarrée.

---

## Tests réalisés

Environnement : Location-DB (port 5433), User-DB (port 5432), MS-User (REST 8080, gRPC 9090), MS-Location (8082). Tests effectués le 30/09/2026.

### Préalable : MS-User

| Test | Commande | Résultat |
|---|---|---|
| Création d'un utilisateur | `POST /users/create` (Alice Martin) | 200, id `1`, mot de passe absent de la réponse ✅ |

### gRPC seul (grpcurl sur MS-User)

| Test | Commande | Résultat |
|---|---|---|
| Services exposés | `grpcurl -plaintext localhost:9090 list` | `UserService`, `grpc.health.v1.Health`, `grpc.reflection.v1.ServerReflection` ✅ |
| Utilisateur existant | `grpcurl -plaintext -d '{"id": "1"}' localhost:9090 UserService/GetUser` | `exists: true`, nom et prénom renvoyés ✅ |
| Utilisateur inexistant | `grpcurl -plaintext -d '{"id": "9999"}' localhost:9090 UserService/GetUser` | seul l'`id` est renvoyé ✅ |

En proto3, les champs à leur valeur par défaut (`false`) ne sont pas affichés : l'absence de `exists` signifie `exists: false`.

### MS-Location avec MS-User démarré

| Test | Requête | Attendu | Obtenu |
|---|---|---|---|
| Création pour un utilisateur existant | `POST /locations` (`userId` `1`) | 201 | 201 ✅ |
| Création pour un utilisateur inexistant | `POST /locations` (`userId` `9999`) | 404 | 404 « Utilisateur introuvable » ✅ |
| Filtre sur un utilisateur existant | `GET /locations?userId=1` | 200 | 200, liste de ses localisations ✅ |
| Filtre sur un utilisateur inexistant | `GET /locations?userId=9999` | 404 | 404 ✅ |
| Localisation inexistante | `GET /locations/114` | 404 | 404 « Localisation introuvable » ✅ |
| Données invalides | `POST` avec `latitude: 200` et sans `nom` | 400 | 400 avec les deux messages dans `errors` ✅ |
| Modification partielle | `PATCH /locations/2` (`nom` seul) | 200 | 200, seul le nom modifié ✅ |
| Remplacement | `PUT /locations/{id}` | 200 | à rejouer avec gRPC |
| Suppression | `DELETE /locations/{id}` | 204 | à rejouer avec gRPC |

### MS-Location avec MS-User arrêté

| Test | Requête | Attendu | Obtenu |
|---|---|---|---|
| Création | `POST /locations` | 503 | 503 « Service utilisateurs indisponible (gRPC : UNAVAILABLE) » ✅ |
| Liste complète | `GET /locations` | 200 | 200 ✅ |
| Modification partielle | `PATCH /locations/2` | 200 | 200 ✅ |

---

## Éléments pour la discussion (section 7 du sujet)

**Couplage temporel.** L'appel gRPC étant synchrone, MS-Location ne peut plus créer de localisations (ni filtrer par utilisateur) quand MS-User est arrêté : le test ci-dessus renvoie une 503. Le délai de 2 secondes et la traduction en 503 évitent un blocage indéfini ou une 500, mais n'isolent pas la panne. Par ailleurs, avec `@Transactional`, une connexion à la base est réservée pendant l'appel gRPC : si MS-User répond lentement, les connexions s'accumulent. Les routes qui n'appellent pas MS-User (`GET`, `PUT`, `PATCH`, `DELETE`) restent disponibles. Le TP3 (Kafka) permettra de réduire ce couplage.

**Sécurité.** Aucun contrôle d'accès pour l'instant. Proposition : l'**authentification** (vérification d'un jeton émis par Keycloak) peut se faire dès la Gateway ; l'**autorisation** (« cette localisation appartient-elle à l'appelant ? ») ne peut se faire que dans MS-Location, seul à connaître les données. Le `userId` non modifiable simplifie cette règle. Envoyer un mot de passe à chaque requête est à exclure : MS-Location manipulerait des identifiants qui ne le concernent pas.

**Évolution du contrat gRPC.** *(à rédiger)* Piste : protobuf ignore les champs inconnus. Si un champ est ajouté à `UserReply` sans régénérer les stubs de MS-Location, celui-ci continue de fonctionner et ignore simplement le nouveau champ — illustration du lecteur tolérant (loi de Postel). En revanche, renuméroter ou changer le type d'un champ existant casserait la compatibilité.