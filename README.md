# 🛒 La Despensa

**La Despensa** es una aplicación web para hacer la compra de forma compartida. Está pensada para que las personas con las que vives, o tu familia, tengáis las listas de la compra en un mismo sitio. Así, quien vaya al súper sabe qué hace falta y no se olvida nada.

> 🚧 **Proyecto en desarrollo.** La app todavía no está terminada. Más abajo tienes [qué funciona ya y qué falta](#-estado-del-proyecto).

---

## 💡 ¿Para qué sirve?

- **Compartir la compra de casa.** Compañeros de piso, parejas o familias apuntan lo que falta en una lista común. Quien vaya a comprar lo ve todo y va marcando lo que coge.
- **Organizar por tiendas.** Puedes tener una lista para el supermercado, otra para la frutería, otra para la droguería... Cada producto va a la tienda donde lo compras.
- **Llevar varias compras a la vez.** Por ejemplo, una madre que hace la compra de su casa y también la de su hijo universitario, que vive fuera. Cada casa tiene su grupo y sus listas, y nada se mezcla.
- **Evitar duplicados y olvidos.** Todos ven la misma lista actualizada, así que nadie compra dos veces la leche ni se olvida del papel higiénico.

### Conceptos principales

| Concepto | Qué es |
|---|---|
| **Usuario** | La persona registrada en la app. |
| **Amigos** | Usuarios con los que has conectado mediante una solicitud de amistad. Puedes añadirlos a tus grupos. |
| **Grupo** | Un hogar o un conjunto de personas que comparten compras (por ejemplo: *Casa*, *Piso de Madrid*, *Viaje de verano*). |
| **Lista** | Una lista de la compra dentro de un grupo (por ejemplo: *Compra semanal*, *Mercadona*, *Limpieza*). |
| **Producto** | Un artículo con nombre, descripción, categoría y precio orientativo que se añade a las listas. |

---

## 🧱 Tecnologías

| Parte | Tecnología |
|---|---|
| Backend (`api/`) | Java 21 · Spring Boot 4.1 · Spring Web · Spring Data JPA · Spring Security · Bean Validation · Lombok |
| Base de datos | PostgreSQL |
| Frontend (`frontend/despensa/`) | Angular 22 (componentes standalone, signals, lazy loading) |
| Tests | JUnit (backend) · Vitest (frontend) |

---

## 📁 Estructura del repositorio

```
Despensa/
├── api/                 # Backend Spring Boot (arquitectura hexagonal)
│   └── src/main/java/com/pabei/Despensa/
│       ├── application/     # Servicios con la lógica de negocio
│       ├── domain/          # Modelos, DTOs, puertos, repositorios y excepciones
│       └── infrastructure/  # Controladores REST, configuración (CORS, seguridad) y manejo de errores
├── frontend/
│   ├── ARCHITECTURE.md  # Decisiones y convenciones del frontend
│   └── despensa/        # Aplicación Angular
│       └── src/app/
│           ├── core/        # Layout, modelos y servicios compartidos
│           └── features/    # Pantallas: auth, home, grupos, amigos...
└── BBDD-data/
    └── data.sql         # Datos de ejemplo para la base de datos
```

---

## 🚀 Cómo arrancarlo en local

### Requisitos

- Java 21
- Node.js y npm (versión compatible con Angular 22)
- PostgreSQL

### 1. Base de datos

Crea una base de datos llamada `Despensa` en PostgreSQL:

```sql
CREATE DATABASE "Despensa";
```

Las tablas se crean solas al arrancar el backend (`spring.jpa.hibernate.ddl-auto=update`). Si quieres datos de ejemplo, ejecuta después `BBDD-data/data.sql`.

### 2. Backend

Revisa en `api/src/main/resources/application.properties` el usuario y la contraseña de tu PostgreSQL y arranca la API:

```bash
cd api
./mvnw spring-boot:run
```

La API queda escuchando en `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend/despensa
npm install
npm start
```

La app se abre en `http://localhost:4200`. Las llamadas a `/api/**` se redirigen al backend mediante `proxy.conf.json`.

### Tests

```bash
cd api && ./mvnw test
```

```bash
cd frontend/despensa && npm test
```

---

## 🔌 Endpoints disponibles

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/register` | Registrar un usuario nuevo |
| `POST` | `/api/auth/login` | Iniciar sesión |
| `GET` | `/api/amigos` | Ver mis amigos |
| `GET` | `/api/amigos/solicitudes` | Ver solicitudes de amistad |
| `POST` | `/api/amigos/solicitudes` | Enviar una solicitud de amistad |
| `POST` | `/api/amigos/solicitudes/{id}/aceptar` | Aceptar una solicitud |
| `POST` | `/api/amigos/solicitudes/{id}/rechazar` | Rechazar una solicitud |
| `DELETE` | `/api/amigos/solicitudes/{id}` | Cancelar o eliminar una solicitud |

---

## 📌 Estado del proyecto

### ✅ Hecho

- Registro e inicio de sesión (backend, frontend y tests).
- Layout principal con barra lateral y cabecera.
- Amigos: enviar, aceptar, rechazar y ver solicitudes, y ver la lista de amigos.
- Modelo de datos de grupos, listas y productos.

### 🛠️ Pendiente

- [ ] Grupos: crear, editar, invitar amigos y ver miembros (la pantalla existe, pero está vacía).
- [ ] Listas: crear listas dentro de un grupo y organizarlas por tienda.
- [ ] Productos: catálogo, añadir y quitar de una lista, cantidades.
- [ ] Marcar productos como comprados durante la compra.
- [ ] Autenticación con JWT y proteger los endpoints (ahora mismo la seguridad está abierta).
- [ ] Perfil de usuario.
- [ ] Diseño responsive para usarla cómodamente desde el móvil en el súper.

---

## 👥 Autoría

Proyecto desarrollado por Cristina-Ch-Dr.
