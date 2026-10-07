# Guía de desarrollo

## Propósito

La Despensa es el frontend de una aplicación para organizar listas de la compra compartidas por una familia o por las personas que comparten piso. La colaboración se articula alrededor de grupos, listas y productos.

La aplicación está construida con Angular 22 y componentes standalone. Actualmente están implementadas las pantallas de inicio de sesión y registro, junto con la comunicación básica de autenticación. Los modelos de usuario, grupo, lista y producto ya existen, pero no implican que esas funcionalidades estén implementadas.

Esta guía describe la estructura existente y las convenciones que se deben seguir al incorporar funcionalidades, manteniendo el código sencillo y fácil de ampliar.

## Estructura actual

```text
src/
├── app/
│   ├── app.ts                 # Componente raíz
│   ├── app.html               # Punto de entrada del router
│   ├── app.css
│   ├── app.config.ts          # Proveedores globales de Angular
│   ├── app.routes.ts          # Rutas y carga diferida
│   ├── core/
│   │   ├── layout/
│   │   │   └── app-layout/    # Banner, navegación lateral y outlet de la app
│   │   ├── models/            # Tipos y modelos del dominio
│   │   └── services/          # Servicios transversales, como AuthService
│   └── features/
│       ├── auth/
│       │   ├── login/
│       │   └── register/
│       ├── home/              # Página inicial dentro del layout
│       ├── groups/            # Página vacía de grupos
│       └── friends/           # Página vacía de amigos
├── assets/                    # Recursos estáticos
├── environments/              # Configuración de entorno
├── main.ts                    # Arranque de la aplicación
└── styles.css                 # Estilos y variables globales
```

Los componentes de login y registro viven juntos bajo `features/auth` y se cargan de forma diferida desde `app.routes.ts`; permanecen fuera del layout de la aplicación. La zona `/app` carga `core/layout/app-layout`, cuyo `RouterOutlet` muestra sus páginas hijas. La ruta inicial carga `features/home`. Cada pantalla mantiene su TypeScript, plantilla y estilos junto a su funcionalidad. `core` contiene elementos compartidos a nivel de aplicación, no componentes pertenecientes a una pantalla concreta.

## Estructura al añadir funcionalidades

Organiza el código por funcionalidad. Mantén cada componente, plantilla, estilos, tipos y lógica específica cerca de la funcionalidad que los utiliza.

```text
src/app/
├── core/
│   ├── guards/
│   ├── interceptors/
│   ├── layout/
│   ├── models/
│   └── services/
├── features/
│   ├── auth/
│   ├── groups/
│   ├── lists/
│   └── products/
├── shared/                    # Solo piezas reutilizadas por varias features
└── app.routes.ts
```

Esta estructura es una guía de crecimiento, no una invitación a crear carpetas vacías por adelantado. Crea una feature cuando su funcionalidad empiece a existir. Si una pieza solo sirve a una feature, déjala allí; muévela a `shared` o `core` únicamente cuando tenga un uso realmente transversal.

## Responsabilidades

- **Componentes de pantalla:** coordinan la interacción, el estado de presentación y los formularios. Evita poner en ellos detalles de transporte HTTP o reglas de negocio reutilizables.
- **Servicios:** encapsulan las llamadas HTTP y las operaciones compartidas de una funcionalidad. Inyecta dependencias con `inject()` y sigue el patrón observable de `HttpClient` ya usado por `AuthService`.
- **Modelos:** define tipos explícitos para las entidades, los resúmenes y los contratos de API. Usa formas de resumen para relaciones cuando no se necesite cargar la entidad completa, evitando referencias circulares y respuestas innecesariamente grandes.
- **Rutas:** registra las pantallas en `app.routes.ts` y conserva la carga diferida con `loadComponent` para las nuevas páginas standalone.
- **Estilos:** deja tokens, tipografía y reglas globales en `src/styles.css`; mantén los estilos de una pantalla junto a su componente. Reutiliza las variables CSS existentes antes de introducir colores o escalas duplicadas.

## Añadir páginas al layout

El shell de `/app` está en `core/layout/app-layout`; su `RouterOutlet` es el área principal y la navegación se define en `navigationItems` de `app-layout.ts`. Para añadir una página:

1. Crea su componente standalone dentro de `features/<funcionalidad>/` y cárgalo de forma diferida como ruta hija de `app` en `app.routes.ts`:

   ```ts
   {
     path: 'grupos',
     loadComponent: () => import('./features/groups/groups').then((module) => module.Groups),
   }
   ```

2. Añade el enlace correspondiente a `navigationItems` en `app-layout.ts`:

   ```ts
   { label: 'Grupos', route: '/app/grupos', exact: false }
   ```

   Usa `exact: true` para una página con una única ruta y `false` cuando el enlace deba seguir activo en rutas descendientes. El enlace solo debe incorporarse cuando su ruta exista.

El banner, el sidebar responsive, el enlace para saltar al contenido y la zona principal se mantienen en el layout. Login y registro continúan fuera de `/app` para no mostrar navegación de la zona de compras en los formularios de autenticación. Las rutas `/app/grupos` y `/app/amigos` están creadas como páginas vacías para completar más adelante; su presencia no implica que esas funcionalidades ya estén implementadas.

## Convenciones de implementación

1. Usa componentes standalone, tipos estrictos y nombres claros en español para los conceptos de dominio existentes (por ejemplo, `Usuario`, `Grupo`, `Lista` y `Producto`).
2. Mantén el estado local de interfaz cerca de la pantalla; para estado reactivo local sigue el uso de `signal` ya presente en autenticación.
3. Mantén las responsabilidades separadas: la pantalla recoge y valida la entrada, el servicio coordina la petición y los modelos describen los datos. No dupliques reglas entre pantallas sin una razón clara.
4. Tipifica las peticiones y respuestas con contratos explícitos. No uses `unknown` como contrato definitivo cuando se conozca la forma real de la respuesta del backend.
5. Para navegación interna, usa el router de Angular (`RouterLink`/`Router`) en vez de provocar una navegación completa con enlaces HTML. Al añadir rutas privadas, centraliza la comprobación de sesión en un guard; evita repetirla en cada pantalla.
6. Usa `environment.apiUrl` como base de la API. En desarrollo, `proxy.conf.json` reenvía `/api/**` a `http://localhost:8080`; no fijes esa URL en servicios o componentes.
7. Conserva estados de carga y mensajes de error explícitos para las peticiones. No ocultes errores ni conviertas fallos HTTP en respuestas aparentemente correctas.
8. Mantén la accesibilidad existente: etiquetas asociadas a controles, nombres accesibles para botones, errores perceptibles y estados anunciados cuando sea necesario.

## API y autenticación actuales

`AuthService` ofrece `login` y `register` en `/api/auth/login` y `/api/auth/register`. Si la respuesta contiene un token, lo guarda en `sessionStorage` y lo expone mediante un `signal`; `logout()` elimina ese token. Login y registro gestionan actualmente su propio estado de envío y sus mensajes.

Antes de ampliar la autenticación, confirma con el contrato real del backend la forma de la respuesta y las necesidades de sesión. No asumas que existe una sesión de usuario completa porque haya un token almacenado. Si se incorporan peticiones autenticadas, concentra la adición del token en un interceptor y decide el comportamiento de rutas protegidas de forma coherente. No guardes secretos ni credenciales en el código fuente.

## Funcionalidades futuras

Como orientación, una ampliación gradual puede seguir este orden:

1. Completar el estado de sesión, la salida y la navegación hacia la zona privada.
2. Incorporar grupos y membresías, que definen con quién se comparten recursos.
3. Incorporar listas asociadas a un grupo.
4. Incorporar productos dentro de listas y las acciones necesarias para coordinarlos entre miembros.

Cada paso debe apoyarse en los contratos y endpoints disponibles en el backend. No se debe representar como funcionalidad terminada aquello que solo tenga un modelo o una ruta prevista.

## Ejecución y validación

Desde `frontend/despensa`:

```bash
npm start
npm run build
```

El servidor de desarrollo queda disponible normalmente en `http://localhost:4200`; las peticiones `/api` usan el proxy local configurado en `proxy.conf.json`. Usa la compilación para comprobar errores de integración y tipos.

**No crear ni ejecutar tests para este proyecto.** El repositorio puede contener archivos `.spec.ts` o configuración generada por Angular; esta preferencia no requiere borrarlos ni añadir nuevos. No incluyas pasos de testing en el flujo de desarrollo.
