# Pendientes App Android Epycus

Fecha: 2026-06-22
Base: `REVISION_APIS_Y_MOVIL.md`
Completado hasta ahora:
- Tipar Retrofit (13 DTOs, 6 servicios, 3 fragments, 2 repos)
- Google Sign-In (ya implementado)
- Estados de misiones (corregido ServicioIA.cs backend)
- Pomodoro (conectar UI con backend: config, iniciar, finalizar)
- Perfil (editar nombre/carrera)

---

## 1. Offline cache con Room

### Estado actual
- Habitos: `HabitosRepository` ya usa Room (`HabitoEntity`, `habitoDao`). Tiene `cacheHabitos()` y `getCachedHabitos()`.
- Perfil: `PerfilFragment.cargarPerfil()` guarda en `CacheEntity` con clave `"perfil"`. `cargarPerfilDesdeCache()` recupera.
- Diario: solo `preguntaGuia` se cachea con `CacheManager` (JSON). No hay Room para entradas.
- Misiones: solo `CacheManager` JSON. No hay Room.
- Pomodoro: `CacheManager` JSON. No hay Room.

### Prompt
```text
Agrega cache offline con Room para Misiones y Diario en la app Android Epycus.

Archivos a modificar:
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\data\local\AppDatabase.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\data\local\entity\
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\repository\MisionesRepository.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\repository\DiarioRepository.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\misiones\MisionesFragment.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\diario\DiarioFragment.java

Requisitos:
1. Crear entidades Room para MisionEntity (id, nombre, descripcion, nombreCurso, prioridad, estado, fechaLimite, xpOtorgado, fechaCreacion, categoriaId).
2. Crear entidad DiarioEntradaEntity (fecha, estado, nota).
3. Agregar DAOs: MisionDao (insertAll, deleteAll, getAll, getActivas), DiarioEntradaDao (insertOrUpdate, getByFecha, getUltima).
4. Registrar ambos DAOs en AppDatabase.
5. MisionesRepository: en listar(), guardar en Room al recibir respuesta exitosa. En getCached(), devolver de Room si no hay red.
6. DiarioRepository: en hoy(), guardar entrada en Room. En getCached(key), devolver ultima entrada de Room.
7. MisionesFragment y DiarioFragment: mostrar cache inmediatamente si existe, refrescar en background si hay red, mostrar error solo si falla refresh y no hay datos.

Verificar con: .\gradlew.bat assembleDebug
```

---

## 2. Estados de carga, vacío, error y reintento

### Estado actual
- HabitosFragment: tiene `cargarHabitos()` con loading/empty. Al fallar usa cache.
- MisionesFragment: tiene `cargarMisiones()` con empty state y swipe refresh.
- DiarioFragment: tiene loading en pregunta guia, entradas con empty state.
- PerfilFragment: tiene loading y swipe refresh.
- PomodoroFragment: no tiene loading state.

### Prompt
```text
Estandariza los estados de UI en todos los fragments de la app Android Epycus.

Revisar cada fragment en C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\ y asegurar que cada llamada a API tenga:

1. **Loading**: mostrar ProgressBar/invisible mientras carga (ya existe en la mayoria).
2. **Empty**: mostrar layout vacio con mensaje y boton de accion cuando no hay datos.
3. **Error**: Snackbar con mensaje de error y opcion de reintentar (ya existe en algunos).
4. **Pull-to-refresh**: SwipeRefreshLayout en todas las pantallas con datos.

Fragments a revisar:
- habitos/HabitosFragment.java
- misiones/MisionesFragment.java
- diario/DiarioFragment.java
- pomodoro/PomodoroFragment.java
- inicio/DashboardFragment.java (si existe)
- perfil/PerfilFragment.java

Verificar con: .\gradlew.bat assembleDebug
```

---

## 3. Endpoints que siguen devolviendo Object

### Estado actual
Estos endpoints aún devuelven `RespuestaApi<Object>` porque el backend retorna datos anónimos:

| Servicio | Endpoint | Uri |
|----------|----------|-----|
| ApiHabitosService | listar() | GET api/habitos |
| ApiHabitosService | obtener(id) | GET api/habitos/{id} |
| ApiHabitosService | dashboard() | GET api/habitos/dashboard |
| ApiHabitosService | categorias() | GET api/habitos/categorias |
| ApiMisionesService | categorias() | GET api/misiones/categorias |
| ApiBienestarService | frase() | GET api/bienestar/frase |
| ApiEstadoAnimoService | registrar() | POST api/estado-animo |
| ApiEstadoAnimoService | historial() | GET api/estado-animo/historial |
| ApiIaService | contextoBienestar() | GET api/ia/contexto-bienestar |
| ApiAdminService | usuarios(), usuario(id), frases() | varios |
| ApiPomodoroService | estadisticas() | GET api/pomodoro/estadisticas |
| ApiProgresoService | logros() | GET api/progreso/logros |
| ApiProgresoService | historialAnimo() | GET api/progreso/historial-animo |

### Prompt
```text
Revisa los endpoints que aun devuelven RespuestaApi<Object> en la app Android Epycus y decide para cada uno si conviene crear un DTO tipado o mantener Object.

Para cada endpoint en C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\api\ que use Object:
1. Revisa el controlador correspondiente en C:\Users\marco\Pictures\EpycusApp\Controllers\Api\ para ver el JSON real.
2. Si el backend devuelve un DTO conocido (aunque anonimo), crea el DTO Java en model/dto/.
3. Si es un objeto verdaderamente dinamico, manten Object.
4. Actualiza el service interface, repository y fragment que lo consume.

Prioridad alta: ApiHabitosService.listar(), ApiHabitosService.dashboard(), ApiMisionesService.categorias().
Prioridad media: ApiEstadoAnimoService.historial() (devuelve array de {fecha, estado, nota}).

Verificar con: .\gradlew.bat assembleDebug
```

---

## 4. Pruebas de contrato JSON

### Estado actual
No hay tests de contrato entre backend y Android.

### Prompt
```text
Crea pruebas de contrato JSON entre el backend ASP.NET Core y la app Android Epycus.

Backend (C:\Users\marco\Pictures\EpycusApp):
1. Revisa si existe EpycusApp.Tests. Si no, crear proyecto de test.
2. Agrega tests de integracion con WebApplicationFactory que verifiquen la estructura JSON de endpoints moviles principales:
   - api/auth/login (POST)
   - api/dashboard/resumen (GET)
   - api/habitos/hoy (GET)
   - api/misiones (GET)
   - api/perfil (GET)
   - api/gamificacion/mi-progreso (GET)
3. Cada test debe verificar: exito (bool), mensaje (string?), datos (object), errores (string[]?).
4. Usar JsonDocument.Parse para validar estructura sin deserializar.

Android (C:\Users\marco\Pictures\Epycus):
1. Crear fixtures JSON en app/src/test/resources/fixtures/ con respuestas reales del backend.
2. Crear tests de parseo Gson en app/src/test/java/ para:
   - AuthResponse (login, refresh)
   - DashboardResponse
   - GamificacionResponse
   - HabitoHoyDto (lista)
   - MisionDto (lista)
   - PerfilResponse
   - ChatResponse
3. Verificar que Gson deserializa sin errores y campos mapean correctamente.

Objetivo: que cualquier cambio de contrato rompa tests antes de romper la app.

Verificar con:
- dotnet test en C:\Users\marco\Pictures\EpycusApp
- .\gradlew.bat testDebugUnitTest en C:\Users\marco\Pictures\Epycus
```

---

## 5. Endurecer auth movil

### Estado actual
- AuthInterceptor maneja refresh automatico ante 401.
- SessionManager guarda token/refreshToken en EncryptedSharedPreferences.
- userId se guarda como -1 (no se extrae del JWT).

### Prompt
```text
Revisa y endurece el flujo de autenticacion movil de Epycus.

Archivos a modificar:
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\util\SessionManager.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\api\AuthInterceptor.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\repository\AuthRepository.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\auth\LoginActivity.java

Requisitos:
1. SessionManager: extraer userId del claim "nameid" del JWT al guardar token. No usar -1.
2. AuthInterceptor: si refresh falla, no hacer forceLogout() en bucle. Limitar a 1 intento de refresh.
3. AuthRepository.logout(): llamar api/auth/logout y limpiar Room + SharedPreferences.
4. LoginActivity: al restaurar sesion (isLoggedIn true en onCreate), validar que el token no haya expirado parseando la fecha "exp" del JWT. Si expiro, ir a login.
5. Remover logs sensibles en release: HttpLoggingInterceptor solo mostrar headers, nunca body en release.
6. SplashActivity (si existe): validar sesion antes de navegar al home.

Verificar con: .\gradlew.bat assembleDebug
```

---

## 6. Misiones: dialogo crear y editar tipado

### Estado actual
- `MisionesFragment.crearMision()` usa `JsonObject` manual.
- No hay dialogo editar.
- `categorias()` devuelve `Object` y se parsea manualmente.
- FechaLimite no se configura (no hay date picker).

### Prompt
```text
Mejora el dialogo de crear/editar misiones en la app Android Epycus.

Archivo:
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\misiones\MisionesFragment.java

Requisitos:
1. Crear layout dialog_nueva_mision.xml con: EditText nombre, EditText descripcion, Spinner prioridad (Alta/Media/Baja), DatePicker fechaLimite, Spinner categoria.
2. Reemplazar mostrarDialogoNuevaMision() actual con el nuevo layout.
3. Agregar DatePickerDialog para fechaLimite.
4. Enviar body con los campos correctos al backend: nombre, descripcion (opcional), prioridad, fechaLimite (yyyy-MM-dd), categoriaId.
5. Agregar swipe to edit (o boton editar) que abra el mismo dialogo precargado.
6. Usar RespuestaApi tipada en lugar de Object.

Verificar con: .\gradlew.bat assembleDebug
```

---

## 7. Habitos: editar habito

### Estado actual
- `mostrarDialogoEditarHabito()` muestra "Funcionalidad pronto disponible" (R.string.funcionalidad_pronto).

### Prompt
```text
Implementa editar habito en la app Android Epycus.

Archivos:
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\habitos\HabitosFragment.java
- app/src/main/res/layout/dialog_editar_habito.xml (crear)

Requisitos:
1. Crear layout dialog_editar_habito.xml similar a dialog_nuevo_habito pero precargado con datos.
2. Implementar mostrarDialogoEditarHabito(int id) que cargue los datos del habito desde api/habitos/{id} (o desde la lista local).
3. Al guardar, llamar PUT api/habitos/{id} con los campos editados.
4. Reemplazar el Snackbar de "pronto disponible" con el dialogo funcional.
5. Usar RespuestaApi tipada.

Verificar con: .\gradlew.bat assembleDebug
```

---

## 8. Diario: crear entrada de texto

### Estado actual
- `DiarioFragment` permite seleccionar animo y escribir nota, guarda via `ApiEstadoAnimoService`. Esto es estado de animo, no entrada de diario.
- El diario textual (api/diario) no tiene UI para crear/ver entradas de texto diarias.

### Prompt
```text
Completa la funcionalidad de diario textual en la app Android Epycus.

Archivos:
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\ui\diario\DiarioFragment.java
- C:\Users\marco\Pictures\Epycus\app\src\main\java\es\epycus\app\repository\DiarioRepository.java
- C:\Users\marco\Pictures\Epycus\app\src\main\res\layout\fragment_diario.xml (revisar)

Requisitos:
1. Usar DiarioRepository.crear() o DiarioRepository.actualizar() para guardar entrada de texto.
2. Agregar en el layout un TextInputLayout para escribir la entrada del dia.
3. Al cargar DiarioFragment, llamar api/diario/hoy. Si existe entrada, mostrar el texto.
4. Si no existe entrada, mostrar placeholder "Escribe algo sobre tu dia...".
5. Boton guardar que llame POST api/diario (crear) o PUT api/diario/{fecha} (actualizar).
6. Cargar pregunta guia desde api/diario/pregunta-guia y mostrarla como sugerencia.
7. Usar RespuestaApi tipada (DiarioEntradaResponse).

Verificar con: .\gradlew.bat assembleDebug
```

---

## Checklist final para beta

- [ ] 1. Offline cache: Misiones y Diario en Room
- [ ] 2. Estados de carga/vacio/error/reintento estandarizados
- [ ] 3. Endpoints Object tipados donde sea posible (listar habitos, dashboard, categorias)
- [ ] 4. Pruebas de contrato JSON (backend + Android)
- [ ] 5. Auth: userId desde JWT, refresh sin loop, logout completo, validar expiracion
- [ ] 6. Misiones: dialogo crear con fecha, categorias, editar
- [ ] 7. Habitos: editar funcional
- [ ] 8. Diario: entrada de texto diaria
- [ ] 9. Compila en debug y release
- [ ] 10. Unit tests pasan
