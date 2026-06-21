# Revision de APIs y app movil Epycus

Fecha: 2026-06-21  
Proyectos revisados:

- Backend/API: `C:\Users\marco\Pictures\EpycusApp`
- App movil Android: `C:\Users\marco\Pictures\Epycus`

## Resumen ejecutivo

El backend de Epycus ya tiene una API REST bastante completa bajo `/api/*`, con autenticacion JWT, refresh tokens, rate limiting, health checks, Swagger en desarrollo, servicios por modulo y controladores para autenticacion, dashboard, habitos, misiones, pomodoro, perfil, gamificacion, bienestar, diario, IA y administracion.

La app movil ya no esta solo en planificacion: existe una app Android nativa en Java, con Retrofit, OkHttp, Gson, Room, EncryptedSharedPreferences, Glide, Google Sign-In, tema claro/oscuro y pantallas funcionales para login, registro, inicio, habitos, misiones, pomodoro, diario, perfil e IA.

El estado real es bueno para una primera version, pero aun falta cerrar contratos tipados entre backend y Android, completar flujos incompletos de UI, mejorar manejo offline, terminar Google Sign-In para usuarios nuevos, y agregar pruebas de integracion API-app.

## Verificacion ejecutada

- Backend: `dotnet test --no-restore` en `C:\Users\marco\Pictures\EpycusApp`
  - Resultado: correcto, sin errores visibles.
- Android: `.\gradlew.bat testDebugUnitTest` en `C:\Users\marco\Pictures\Epycus`
  - Resultado: correcto.
  - Avisos: uso de APIs deprecadas y operaciones unchecked; conviene limpiar genericos y revisar `-Xlint`.

## Backend/API revisado

Stack detectado:

- ASP.NET Core MVC 9
- Entity Framework Core 9 con Pomelo MySQL/MariaDB
- JWT Bearer con tokens en cookie para web y `Authorization: Bearer` para movil
- Google OAuth en web y endpoint de Google para movil
- Rate limiting: `Api`, `Auth`, `Mobile`, `Gemini`, `DeepSeek`
- Health checks: base de datos, Gemini, DeepSeek y disco
- Swagger configurado en desarrollo

APIs principales:

- `api/auth`: login, refresh, logout, registro, verificar correo, recuperar/restablecer contrasena, Google, completar registro Google, carreras.
- `api/dashboard`: resumen y frase del dia.
- `api/habitos`: listar, hoy, crear, editar, eliminar, completar, fallar, semana, dashboard, categorias.
- `api/misiones`: listar, obtener, crear, editar, eliminar, completar, cambiar estado, categorias.
- `api/pomodoro`: iniciar, ciclo completado, finalizar, cancelar, configuracion, tip, sesion activa, historial, racha, estadisticas.
- `api/perfil`: obtener, actualizar, cambiar contrasena, personaje, tema, personajes, logros.
- `api/gamificacion`: mi progreso, logros.
- `api/progreso`: progreso, logros, historial animo.
- `api/bienestar`: resumen, alertas, frase, estado hoy, historial animo, pendientes, pausa activa.
- `api/diario`: hoy, fecha, mes, registrar, actualizar, racha, promedio mes, pregunta guia.
- `api/ia`: chat, historial, conversaciones, sugerencias, contexto bienestar, feedback, mensajes hoy.
- `api/admin`: login, usuarios, suscripcion, frases.

## App movil revisada

Stack detectado:

- Android nativo Java
- Gradle Kotlin DSL
- Retrofit + Gson + OkHttp
- AuthInterceptor con refresh token automatico ante 401
- EncryptedSharedPreferences para token y refresh token
- Room para cache local
- Google Sign-In
- Material Components, AppCompat, Navigation, ViewBinding
- Glide para imagenes de personaje

Base URL actual:

```text
https://app.epycus.es/
```

Pantallas y modulos presentes:

- Splash
- Login
- Registro
- Contenedor principal con navegacion inferior
- Inicio/dashboard
- Habitos
- Misiones
- Pomodoro
- Diario
- Perfil
- Chat IA

## Compatibilidad API vs Android

Lo que esta bien alineado:

- La base URL y rutas Retrofit coinciden con la mayoria de controladores API.
- `RespuestaApi<T>` de Android coincide con `RespuestaApi<T>` del backend: `exito`, `mensaje`, `datos`, `errores`.
- El backend acepta `Authorization: Bearer <token>`, aunque tambien soporte cookies para web.
- El refresh token movil esta contemplado y se renueva desde `AuthInterceptor`.
- Dashboard, gamificacion, IA, habitos de hoy y perfil tienen modelos Android parcialmente tipados.

Riesgos o desajustes:

- Muchos servicios Retrofit devuelven `RespuestaApi<Object>`. Esto compila, pero deja los contratos fragiles y obliga a parseos manuales o Gson dinamico.
- `ApiMisionesController.ObtenerMisiones` devuelve solo `id`, `nombre`, `prioridad`, `fechaLimite`, `completada`, pero Android tiene `MisionDto` con `descripcion`, `nombreCurso`, `estado`, `xpOtorgado`, `fechaCreacion`, `categoriaId`. La lista movil puede mostrar datos incompletos o valores default.
- `MisionDto.isCompletada()` compara `"Completada"`, pero el backend usa `"Completado"` en varios lugares. Esto puede romper estados visuales.
- `ApiPomodoroService` no expone en Android todos los endpoints existentes del backend, por ejemplo `sesion-activa`, `historial`, `racha`, `estadisticas`.
- Google Sign-In movil llama `api/auth/google`; si el backend responde `completar_registro`, la app solo muestra error. Falta flujo para completar registro Google desde Android.
- El registro normal recibe token/refreshToken, pero la pantalla actual termina con `finish()` y no guarda sesion automaticamente. Puede ser intencional, pero para UX movil conviene iniciar sesion directo.
- `SessionManager.saveSession` guarda `userId = -1` porque el backend no devuelve usuario; se extrae nombre del JWT, pero no el ID. Si luego se necesita ID local, conviene parsearlo del claim `NameIdentifier` o devolverlo en login.
- La app tiene cache Room para dashboard/progreso, pero otros modulos siguen muy dependientes de red.
- Hay acciones de perfil marcadas como `proximamente`; falta terminar seleccion de personaje, tema, logros y configuracion completa.

## Prioridades para terminar la version movil

1. Congelar contrato API movil

Crear DTOs C# de respuesta por endpoint movil y evitar respuestas anonimas grandes. Luego replicar esos DTOs en Java. Prioridad: auth, dashboard, gamificacion, habitos, misiones, pomodoro, perfil, diario, IA.

2. Tipar Retrofit

Reemplazar `RespuestaApi<Object>` por modelos concretos. Esto reducira errores silenciosos y warnings unchecked.

3. Terminar Google Sign-In movil

Cuando `api/auth/google` devuelva mensaje `completar_registro`, abrir una pantalla de completar registro con nombre, correo, fecha nacimiento, genero, carrera y terminos; luego llamar `api/auth/completar-registro-google`.

4. Corregir Misiones

Alinear estados (`Completado` vs `Completada`) y ampliar la respuesta de lista para incluir los campos que Android espera, o ajustar Android a una version resumida y pedir detalle al abrir una mision.

5. Completar Pomodoro movil

Agregar consumo de `sesion-activa`, `historial`, `racha`, `estadisticas`; persistir sesion local ante cierre de app; manejar notificaciones locales y vibracion si estan activadas.

6. Completar perfil

Implementar editar perfil, cambiar contrasena, cambiar personaje, cambiar tema y logros reales sin mensajes `proximamente`.

7. Offline-first basico

Cachear listas de habitos, misiones, perfil y ultimos datos de diario. Agregar estados de carga, vacio, error y reintento por pantalla.

8. Pruebas de contrato

Agregar tests backend que validen JSON real de endpoints moviles y tests Android de parseo de fixtures JSON.

## Prompts recomendados para terminar la app movil

### Prompt 1: auditar contratos API contra Retrofit

```text
Revisa `C:\Users\marco\Pictures\EpycusApp\Controllers\Api` y `C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\api`.

Objetivo: crear una matriz endpoint por endpoint con metodo, ruta, body esperado, respuesta real del backend y modelo Retrofit actual. Detecta desajustes de nombres, tipos, estados enum, campos faltantes y endpoints no usados. Luego implementa los cambios minimos para que Android use modelos tipados en vez de `Object`, sin romper el backend web.

Verifica con:
- `dotnet test --no-restore`
- `.\gradlew.bat testDebugUnitTest`
```

### Prompt 2: cerrar flujo Google Sign-In movil

```text
Implementa el flujo completo de Google Sign-In en Android.

Contexto:
- Android llama `api/auth/google`.
- Si el backend devuelve error/mensaje `completar_registro`, debe abrirse una pantalla `CompletarRegistroGoogleActivity`.
- La pantalla debe pedir fecha de nacimiento, genero, carrera y aceptar terminos, reutilizando `api/auth/carreras`.
- Al enviar, debe llamar `api/auth/completar-registro-google`, guardar token y refreshToken en `SessionManager`, y navegar a `MainContainerActivity`.

Cuida estados loading/error, rotacion de pantalla basica y cancelacion de calls en `onDestroy`.
```

### Prompt 3: arreglar Misiones movil

```text
Revisa `ApiMisionesController.cs`, `MisionDto.java`, `MisionesRepository.java`, `ApiMisionesService.java`, `MisionesFragment.java` y `MisionAdapter.java`.

Objetivo:
- Alinear estados de mision entre backend y Android: usar exactamente `Pendiente`, `EnProgreso`, `Completado`, `Fallido`.
- Decidir si la lista devuelve DTO resumido o completo. Implementa una opcion consistente.
- Corregir `isCompletada()`.
- Tipar respuestas de crear, editar, completar, eliminar y categorias.
- Mejorar UI de vacio/error/loading y refresco.

Verifica con build Android y tests backend.
```

### Prompt 4: completar Pomodoro movil

```text
Termina el modulo Pomodoro Android contra `ApiPomodoroController.cs`.

Debes:
- Agregar metodos Retrofit para `sesion-activa`, `historial`, `racha`, `estadisticas`.
- Crear DTOs Java para configuracion, sesion activa, ciclo completado, historial y estadisticas.
- Persistir sesion en curso localmente para sobrevivir a cierre/rotacion.
- Mostrar racha, ciclos del dia, meta diaria y tip aleatorio.
- Respetar configuracion de vibracion, auto-iniciar descanso/enfoque y tiempos personalizados.
- Manejar 401 con refresh automatico existente.
```

### Prompt 5: perfil y configuracion real

```text
Completa `PerfilFragment.java` usando `ApiPerfilService`.

Implementa:
- Editar nombre y carrera.
- Cambiar contrasena.
- Cambiar personaje con lista real de `api/perfil/personajes`.
- Cambiar tema con persistencia local y backend.
- Mostrar logros reales de `api/perfil/logros`.
- Eliminar todos los `Toast`/mensajes `proximamente` relacionados con funcionalidades ya disponibles por API.

Mantener diseño consistente con los XML actuales y estados de carga/error.
```

### Prompt 6: offline y cache local

```text
Mejora la app Android para funcionar mejor sin conexion.

Usa Room ya existente para cachear:
- Dashboard/progreso
- Habitos de hoy
- Misiones
- Perfil
- Ultima entrada de diario

Cada pantalla debe:
- Mostrar cache inmediatamente si existe.
- Refrescar en background si hay red.
- Mostrar snackbar solo si falla refresh y no hay datos.
- Tener pull-to-refresh donde aplique.

No cambies la arquitectura completa; hazlo incremental con los DAOs existentes o agrega DAOs simples.
```

### Prompt 7: pruebas de contrato JSON

```text
Crea pruebas de contrato entre backend y Android.

Backend:
- Agrega tests de integracion para endpoints moviles principales usando WebApplicationFactory e InMemory.
- Verifica forma JSON: `exito`, `mensaje`, `datos`, `errores`.

Android:
- Agrega fixtures JSON en `app/src/test/resources`.
- Prueba deserializacion Gson para AuthResponse, DashboardResponse, GamificacionResponse, HabitoHoyDto, MisionDto, PerfilResponse y ChatResponse.

Objetivo: que cualquier cambio de contrato rompa tests antes de romper la app.
```

### Prompt 8: endurecer auth movil

```text
Revisa autenticacion movil completa.

Objetivo:
- Parsear y guardar userId desde JWT o devolver `usuario` en login/registro/refresh.
- Evitar logs sensibles en release y revisar `HttpLoggingInterceptor`.
- Manejar refresh token fallido sin loops ni multiples pantallas login.
- Agregar logout robusto llamando `api/auth/logout` y limpiando sesion local.
- Validar expiracion de token en Splash antes de entrar al home.
```

## Checklist final antes de publicar una beta

- Login correo/contrasena funciona en dispositivo real.
- Registro normal crea usuario y decide si entra directo o vuelve a login.
- Login Google funciona para usuario existente.
- Login Google funciona para usuario nuevo con completar registro.
- Refresh token se ejecuta correctamente tras expirar el JWT.
- Logout revoca refresh tokens y limpia sesion local.
- Dashboard muestra datos reales y cache si no hay red.
- Habitos: listar, crear, completar, fallar, eliminar.
- Misiones: listar, crear, completar, cambiar estado, eliminar.
- Pomodoro: iniciar, completar ciclo, finalizar/cancelar, ver configuracion.
- Diario: pregunta guia, registrar entrada, ver entrada de hoy.
- IA: enviar mensaje, mantener conversacion, mostrar errores de limite/conexion.
- Perfil: ver datos, editar, cambiar tema/personaje, cambiar contrasena.
- App compila en debug y release.
- Backend tests pasan.
- Android unit tests pasan.
- No hay secretos nuevos en repositorio.

## Conclusion

La base ya esta mucho mas avanzada de lo que indica el README del backend: existe una app Android funcional y conectada a APIs reales. El siguiente salto no es crear mas modulos, sino cerrar contratos, tipar respuestas, terminar flujos incompletos y probar integracion. Con eso, Epycus puede llegar a una beta movil estable sin reescribir la arquitectura.
