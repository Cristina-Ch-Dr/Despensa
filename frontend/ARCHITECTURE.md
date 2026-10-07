# Arquitectura del frontend — La Despensa

> Documento vivo. Cualquier persona (o IA) que entre al proyecto debe poder leer esto y entender cómo está organizado el frontend y por qué. Se actualiza cada vez que cambia una decisión importante.

## Descripción breve

La Despensa es una aplicación para gestionar compras compartidas entre grupos familiares: crear grupos, invitar miembros, mantener listas de la compra comunes y marcar productos como comprados. Backend en Spring Boot, frontend en **Angular 22**.

## Objetivos y alcance inicial

- **Usuarios**: registro, login, perfil, gestión de amigos.
- **Grupos**: crear, editar, invitar, listar miembros.
- **Listas**: CRUD de listas por grupo, marcar ítems como comprados.
- **Productos**: catálogo, añadir a listas, editar, buscar.
- **Autenticación**: JWT con Spring Security, interceptor que añade el token.
- **UX mínima**: responsive, componentes reutilizables, rutas con lazy loading.

## Stack y decisiones técnicas (Angular 22)

Angular 22 (junio 2026) consolida el modelo *signal-first*. Estas son las convenciones que seguimos porque son ya el estándar recomendado del framework, no una preferencia nuestra:

| Área | Decisión |
|---|---|
| Componentes | Standalone (es el default, no hace falta declararlo ni usar `--standalone`) |
| Detección de cambios | `OnPush` (default en v22) |
| Estado local/UI | `signal()`, `computed()`, `linkedSignal()` — evitar propiedades mutables sueltas en el componente |
| Control de flujo en plantillas | `@if`, `@for`, `@switch` (nunca `*ngIf` / `*ngFor`) |
| Datos remotos (HTTP) | `httpResource()` / `resource()` como primera opción; RxJS solo si el caso lo pide (streams complejos, combinaciones) |
| Formularios | Signal Forms; Reactive Forms solo si algo muy concreto lo necesita |
| Inyección de dependencias | `inject()` en vez de inyección por constructor |
| Manejo de errores en plantilla | Bloque `@boundary` para aislar errores de renderizado por sección |
| Testing | Vitest (Karma/Jasmine quedan descartados para código nuevo) |
| Accesibilidad | `@angular/aria` para patrones de componentes accesibles (menús, combobox, etc.) en vez de reinventarlos |

Si en algún momento se usa una API o patrón antiguo (por ejemplo, un componente no-standalone o `*ngIf`), debe ser una excepción justificada y documentada en el changelog, no la norma.

## Estructura de carpetas

```
src/
└── app/
    ├── core/
    │   ├── models/           # interfaces que reflejan los DTOs del backend
    │   ├── services/         # un servicio por recurso, usa httpResource internamente
    │   ├── layout/           # shell de /app: banner, navegación y RouterOutlet de páginas hijas
    │   ├── interceptors/
    │   │   └── auth.interceptor.ts
    │   ├── guards/
    │   │   └── auth.guard.ts
    │   └── layout/           # shell de la app: header, sidebar, footer
    │
    ├── shared/
    │   ├── components/       # ui-button, modal, product-card...
    │   ├── pipes/
    │   └── directives/
    │
    ├── features/
    │   ├── auth/
    │   ├── usuarios/
    │   ├── grupos/
    │   ├── listas/
    │   └── productos/
    │
    ├── app.routes.ts
    ├── app.config.ts         # providers globales: router, httpClient, etc.
    ├── main.ts
    └── app.component.ts
```


## Convenciones y buenas prácticas

- **Nombres**: carpetas en kebab-case, componentes en PascalCase con sufijo `Component`.
- **Sin barrel files** (`index.ts` que reexportan todo): Angular los desaconseja porque rompen el tree-shaking y ralentizan el build. Importar siempre desde la ruta real del archivo.
- **Servicios**: un servicio por recurso en `core/services`, con métodos que reflejan los endpoints del backend. Exponen `resource`/`httpResource`, no Observables sueltos que cada componente tiene que gestionar a mano.
- **Modelos**: interfaces en `core/models` que reflejan los DTOs de Spring Boot. Si un DTO cambia, este es el primer sitio a tocar.
- **Rutas**: lazy loading con `loadComponent` para todo lo que no sea crítico en el primer render.
- **Estado**: empezar con signals a nivel de componente/servicio; considerar una librería de estado global (NgRx Signals, por ejemplo) solo si aparece estado compartido complejo entre features no relacionadas.
- **Notificaciones/errores**: un servicio compartido (`shared/services/notification.service.ts`) para toasts/errores en vez de que cada componente maneje su propio mensaje.
- **Tests**: unitarios junto a cada archivo (`*.spec.ts`), con Vitest.
- **Environments**: `src/environments` para URLs de API; nada de claves sensibles en el frontend.
- **Lint y formato**: ESLint + Prettier desde el inicio.

## Autenticación y seguridad

- JWT emitido por Spring Security; el interceptor de `core/interceptors` añade `Authorization: Bearer <token>` a las peticiones protegidas.
- **Guardar el token en memoria (signal) + `sessionStorage`** como respaldo entre recargas, no en `localStorage` puro si se puede evitar, para reducir superficie ante XSS. Si el backend soporta cookies `httpOnly`, es preferible a cualquier storage accesible por JS — pendiente de decidir con el backend.
- Pendiente de definir: expiración/refresh del token. Anotar aquí la decisión en cuanto se tome.

## Contratos API

Los endpoints reales viven en el backend (Spring Boot). En vez de duplicar aquí cada campo y mantenerlo sincronizado a mano, este documento solo lista los **recursos principales**; el detalle de cada contrato (payloads, códigos de error) debe consultarse en el Swagger/OpenAPI del backend cuando esté publicado.

- `POST /api/auth/register`, `POST /api/auth/login`
- `GET /api/users/me`, `GET /api/users/{id}/friends`
- `GET /api/groups`, `POST /api/groups`, `GET /api/groups/{id}`
- `GET /api/groups/{groupId}/lists`, `POST /api/groups/{groupId}/lists`
- `GET /api/lists/{listId}/items`, `POST /api/lists/{listId}/items`
- `GET /api/products`, `POST /api/products`

Todas las rutas protegidas validan el JWT en el header `Authorization`.

> Cuando el backend tenga Swagger/OpenAPI expuesto, sustituir esta lista por un enlace directo — es la única fuente que no se desincroniza sola.

## Primeros pasos para arrancar el frontend

1. Scaffold del proyecto Angular 22.
2. Crear `core/models` con las interfaces básicas.
3. Implementar `auth.service` (con `httpResource`) y `auth.interceptor`.
4. Crear `features/auth` con login y register.
5. Crear `features/grupos` con listado de grupos.
6. Añadir `shared/product-card` y `shared/modal`.
7. Conectar con los endpoints reales de Spring Boot y validar contratos.

## Comandos útiles

```
ng g c features/auth/login
ng g s core/services/auth
ng test
ng build
```

## Documentación viva y mantenimiento

- Este archivo se guarda en el repo como `ARCHITECTURE.md`.
- Cada vez que cambie un DTO del backend o una ruta, actualizar `core/models` **y** este documento en el mismo commit/PR.
- Decisiones de arquitectura no triviales (por ejemplo, "por qué NgRx y no solo signals") se documentan como una entrada corta en el Changelog, con la fecha y el motivo — no hace falta un ADR formal para un proyecto personal, pero sí dejar el "por qué", no solo el "qué".
- **Checklist de PR**: antes de mergear, verificar que modelos y servicios coinciden con el backend, y que no se han colado patrones antiguos (`*ngIf`, inyección por constructor, componentes no-standalone) salvo excepción documentada.

### Changelog

| Fecha | Cambio |
|---|---|
| 2026-09-24 | Se sustituye la plantilla inicial de Angular por un shell raíz reutilizable con identidad visual común; el login queda como primera ruta lazy de la aplicación. |
| 2026-10-07 | Se añade `core/layout/app-layout` para la zona `/app`, con sidebar y outlet de rutas hijas; login y registro siguen fuera del shell. |

## Tareas recomendadas para el siguiente sprint

- Autenticación completa + guard de rutas.
- CRUD básico de grupos y listas con datos reales (vía `httpResource`).
- UI mínima para añadir productos a una lista y marcarlos como comprados.
- Pipeline de CI que ejecute lint y tests.
