# martini-api

Microservicio REST en Spring Boot para **Martini Conecta**, la app Android de clientes B2B de Martini Professional (Chile).

Guarda las solicitudes del formulario **"Quiero que me contacten"**: un cliente puede crear, listar, editar y eliminar una solicitud (CRUD completo). Los campos coinciden con el modelo `SolicitudContacto` de la app (nombre, teléfono, división, comuna, producto y `creadaEn`), más un `estado` (`PENDIENTE` o `CONTACTADO`). Los datos se guardan en una base H2 en archivo, así que no se pierden al reiniciar el servidor.

Este proyecto es un apoyo académico: está pensado para que la app lo consuma con Retrofit.

## Requisitos

- Java 17 o superior (JDK 17+)
- No hace falta instalar Maven: el proyecto incluye el Maven Wrapper (`mvnw` / `mvnw.cmd`)

Para comprobar Java:

```bash
java -version
```

## Cómo ejecutar

En la carpeta del proyecto:

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

**Windows** (PowerShell o CMD)

```bat
mvnw.cmd spring-boot:run
```

Si el wrapper aún no tiene permisos de ejecución en Linux o macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Cuando arranque verás algo como `Tomcat started on port 8080`. La API queda en:

- En el mismo computador: `http://localhost:8080/api/solicitudes`

La primera vez se crean 3 solicitudes de ejemplo (Ana Soto, Carlos Muñoz y Rosa Pérez). Si la tabla ya tiene datos, no se vuelven a insertar.

Para ejecutar los tests:

```bash
./mvnw test
```

En Windows: `mvnw.cmd test`.

## Endpoints

Todos cuelgan de `/api/solicitudes`.

| Método | Ruta | Descripción | Respuesta |
|--------|------|-------------|-----------|
| `GET` | `/api/solicitudes` | Lista todas las solicitudes, de la más reciente a la más antigua (`creadaEn` desc) | `200` y un arreglo JSON |
| `GET` | `/api/solicitudes?division=GELATERIA` | Lista filtrada por división, también ordenada por fecha | `200` |
| `GET` | `/api/solicitudes/{id}` | Obtiene una solicitud | `200` o `404` |
| `POST` | `/api/solicitudes` | Crea una solicitud | `201` o `400` |
| `PUT` | `/api/solicitudes/{id}` | Actualiza nombre, teléfono, división, comuna, producto y estado (conserva `creadaEn`) | `200`, `400` o `404` |
| `DELETE` | `/api/solicitudes/{id}` | Elimina una solicitud | `204` o `404` |

Valores válidos de `division` (iguales a los de la app):

- `PANADERIA_PASTELERIA`
- `GELATERIA`
- `FOOD_SERVICE`
- `RETAIL`

Valores válidos de `producto` (iguales a los de la app):

- `MANTECA_UNIGRAL`
- `MARGARINA_GRAN_MASTER_HOJA`
- `BRAVO_CREM`
- `MONNALISA`
- `CHOCOLATE_CARAVELLA`
- `CHOCOLATE_ARIBA`
- `LINEA_VEGANA_ORASI`

Valores válidos de `estado`: `PENDIENTE` (por defecto al crear, si no se envía) y `CONTACTADO`.

`creadaEn` es un número con la fecha en milisegundos (epoch), igual que en la app. Si no se envía o va en `0`, el servidor pone la hora actual.

El teléfono acepta el formato normalizado de la app (`+56 9 1234 5678`) y también variantes solo con dígitos (`912345678`, `+56912345678`).

### Ejemplo de `POST` / `PUT`

El cuerpo sigue el modelo Android (`SolicitudContacto`), más `estado`:

```http
POST /api/solicitudes
Content-Type: application/json
```

```json
{
  "nombre": "Ana Soto",
  "telefono": "+56 9 8765 4321",
  "division": "PANADERIA_PASTELERIA",
  "comuna": "Ñuñoa",
  "producto": "MANTECA_UNIGRAL"
}
```

`nombre`, `telefono`, `division`, `comuna` y `producto` son obligatorios. `nombre` y `comuna` deben tener al menos 2 caracteres (sin contar espacios de más). `estado` y `creadaEn` son opcionales en el `POST`.

### Ejemplo de respuesta `201`

```json
{
  "id": 4,
  "nombre": "Ana Soto",
  "telefono": "+56 9 8765 4321",
  "division": "PANADERIA_PASTELERIA",
  "comuna": "Ñuñoa",
  "producto": "MANTECA_UNIGRAL",
  "creadaEn": 1795058223282,
  "estado": "PENDIENTE"
}
```

`id` lo asigna el servidor. No hace falta (ni sirve) enviarlo.

### Ejemplo de error `400` (validación)

```json
{
  "estado": 400,
  "error": "Validación fallida",
  "errores": {
    "nombre": "El nombre debe tener al menos 2 caracteres",
    "telefono": "Ingresa un celular chileno de 9 dígitos",
    "comuna": "La comuna debe tener al menos 2 caracteres"
  }
}
```

### Ejemplo de error `404`

```json
{
  "estado": 404,
  "error": "No encontrado",
  "mensaje": "No existe una solicitud con id 99"
}
```

### Pruebas rápidas con curl

```bash
# Listar
curl http://localhost:8080/api/solicitudes

# Crear
curl -s -X POST http://localhost:8080/api/solicitudes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Luis Pérez","telefono":"+56 9 1234 5678","division":"RETAIL","comuna":"Maipú","producto":"BRAVO_CREM"}'

# Obtener
curl http://localhost:8080/api/solicitudes/1

# Actualizar (marcar como contactado)
curl -s -X PUT http://localhost:8080/api/solicitudes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Soto","telefono":"+56 9 8765 4321","division":"PANADERIA_PASTELERIA","comuna":"Ñuñoa","producto":"MANTECA_UNIGRAL","estado":"CONTACTADO"}'

# Eliminar
curl -i -X DELETE http://localhost:8080/api/solicitudes/1
```

## Cómo exponerlo con Port Forwarding de VS Code

El servidor escucha en `0.0.0.0:8080`. En el computador donde corre Spring Boot eso es `localhost:8080`. El emulador de Android y un celular físico no siempre pueden llegar a esa dirección, así que hay que publicarla.

Pasos (están tomados del anexo del curso):

1. Deja el microservicio corriendo (`./mvnw spring-boot:run` o `mvnw.cmd spring-boot:run`).
2. En Visual Studio Code abre la pestaña **Puertos** (Ports). Si no la ves: *Ver → Puerto* o *View → Ports*.
3. Pulsa **Reenviar puerto** / **Forward a Port** y escribe `8080`.
4. Cambia la visibilidad del puerto a **Pública** / **Public**. VS Code mostrará una URL, por ejemplo `https://algo-8080.region.devtunnels.ms`.
5. Prueba esa URL en el navegador: `https://TU-URL/api/solicitudes` debería devolver el JSON.

### URL base para Retrofit

| Cliente | URL base |
|---------|----------|
| Emulador de Android (el servidor corre en el mismo PC, sin túnel) | `http://10.0.2.2:8080/` |
| Emulador o dispositivo usando Port Forwarding | `https://TU-URL-PUBLICA/` (la que muestra VS Code, con la barra final) |
| App en el mismo computador (pruebas con curl o Postman) | `http://localhost:8080/` |

Interfaz Retrofit de ejemplo (`SolicitudApi`):

```kotlin
interface SolicitudApi {
    @GET("api/solicitudes")
    suspend fun listar(@Query("division") division: String? = null): List<SolicitudContacto>

    @GET("api/solicitudes/{id}")
    suspend fun obtener(@Path("id") id: Long): SolicitudContacto

    @POST("api/solicitudes")
    suspend fun crear(@Body solicitud: SolicitudContacto): SolicitudContacto

    @PUT("api/solicitudes/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body solicitud: SolicitudContacto
    ): SolicitudContacto

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}

val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

Los endpoints quedan como `api/solicitudes` sobre esa base. Si usas HTTPS del túnel, Android lo acepta sin configuración extra. Si en algún momento apuntas a `http://` en un dispositivo físico, puede hacer falta permitir tráfico cleartext en el manifest.

`estado` es un campo extra de la API. Gson lo ignora al leer `SolicitudContacto` hasta que se agregue al data class.

## Consola H2

Para ver o editar los datos a mano:

1. Con el servidor en marcha, abre [http://localhost:8080/h2-console](http://localhost:8080/h2-console).
2. Completa:
   - **JDBC URL:** `jdbc:h2:file:./data/martinidb`
   - **User Name:** `sa`
   - **Password:** (déjalo vacío)
3. Pulsa **Connect**. La tabla se llama `SOLICITUDES`.

Los archivos de la base quedan en la carpeta `data/` del proyecto. Esa carpeta no se sube a Git (está en `.gitignore`).

## Estructura del código

La petición recorre estas capas:

`Controller` → `Service` → `Repository` (Spring Data JPA) → entidad `Solicitud` → base H2

| Paquete | Rol |
|---------|-----|
| `controller` | Recibe el HTTP y devuelve JSON |
| `service` | Reglas de negocio (crear, actualizar, 404) |
| `repository` | Acceso a la tabla |
| `model` | Entidad `Solicitud` y los enums `Division`, `ProductoInteres` y `EstadoSolicitud` |
| `exception` | 404 y 400 en JSON |
| `config` | Datos de ejemplo al primer arranque |

## Autores

Proyecto académico de Diego Pazos y Matías Urrejola para Martini Conecta (Apps Móviles, DSY1105).
