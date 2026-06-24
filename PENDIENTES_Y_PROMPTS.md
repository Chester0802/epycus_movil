# Pendientes App Android Epycus

Fecha: 2026-06-24
Base: `REVISION_APIS_Y_MOVIL.md`
Completado hasta ahora:
- Tipar Retrofit (DTOs, servicios, fragments, repos)
- Google Sign-In con flujo completar registro
- Estados de misiones (corregido)
- Pomodoro completo (conectar UI con backend)
- Perfil (editar nombre/carrera, cambiar contrasena, personaje, tema, logros)

---

## 1. Offline cache con Room

### Estado actual
- Habitos: `HabitosRepository` ya usa Room (`HabitoEntity`, `habitoDao`). Tiene `cacheHabitos()` y `getCachedHabitos()`.
- Perfil: `PerfilFragment.cargarPerfil()` guarda en `CacheEntity` con clave `"perfil"`. `cargarPerfilDesdeCache()` recupera.
- Diario: `preguntaGuia` se cachea con `CacheManager` (JSON).
- Misiones: `CacheManager` JSON.
- Pomodoro: `CacheManager` JSON (configuracion y tip).

**Estado: COMPLETADO** ✅

---

## 2. Estados de carga, vacio, error y reintento

### Estado actual
- HabitosFragment: loading/empty, cache offline, swipe refresh
- MisionesFragment: loading/empty, swipe refresh
- DiarioFragment: loading en pregunta guia, empty state
- PerfilFragment: loading, swipe refresh
- PomodoroFragment: loading en recargarTodo, swipe refresh

**Estado: COMPLETADO** ✅

---

## 3. Endpoints tipados

### Estado actual
- La mayoria de endpoints usan DTOs tipados
- Pendientes de tipar (bajo impacto porque no se usan o usan parseo generico):
  - `ApiHabitosService.listar()` - no usado por el fragment (usa `hoy()`)
  - `ApiHabitosService.dashboard()` - no usado
  - `ApiHabitosService.categorias()` - usa parseo manual de JSON
  - `ApiDashboardService.fraseDelDia()` - no usado
  - `ApiIaService` varios endpoints de historial/conversaciones/sugerencias

**Estado: COMPLETADO** (tipado donde es necesario) ✅

---

## 4. Pruebas de contrato JSON

### Estado actual
- Backend: `ContratoJsonTests.cs` con tests de serializacion para todos los DTOs
- Android: tests unitarios de parseo Gson con fixtures JSON

**Estado: COMPLETADO** ✅

---

## 5. Auth movil

### Estado actual
- `SessionManager`: usa EncryptedSharedPreferences, extrae userId del JWT (`nameid`, `sub`, `nameidentifier`)
- `AuthInterceptor`: refresh automatico ante 401, limitado a 1 intento, forceLogout si falla
- `AuthRepository.logout()`: llama `api/auth/logout` y limpia sesion
- `LoginActivity`: valida expiracion de token al restaurar sesion
- `HttpLoggingInterceptor`: solo headers en debug, NONE en release
- `SplashActivity`: valida sesion antes de navegar al home

**Estado: COMPLETADO** ✅

---

## 6. Misiones: dialogo crear y editar tipado

### Estado actual
- Dialogo crear mision con nombre, descripcion, prioridad, fecha, categoria
- DatePickerDialog para fechaLimite
- Editar mision con dialogo precargado
- Body tipado correctamente

**Estado: COMPLETADO** ✅

---

## 7. Habitos: editar habito

### Estado actual
- `mostrarDialogoEditarHabito(int id)` funcional
- Layout `dialog_nuevo_habito.xml` reutilizado para editar
- PUT `api/v1/habitos/{id}` con campos editados
- Ya no muestra "Funcionalidad pronto disponible"

**Estado: COMPLETADO** ✅

---

## 8. Diario: crear entrada de texto

### Estado actual
- `DiarioFragment` usa `DiarioRepository` para crear/actualizar entrada
- TextInputLayout para escribir la entrada del dia
- Carga `api/diario/hoy` al abrir el fragmento
- Muestra placeholder si no hay entrada
- POST `api/diario` (crear) o PUT `api/diario/{fecha}` (actualizar)
- Pregunta guia desde `api/diario/pregunta-guia`

**Estado: COMPLETADO** ✅

---

## 9. Mejoras backend (2026-06-24)

### DTOs tipados
- Creado `DiarioEntradaResponseDto` para reemplazar `object?` en respuestas de diario
- El controlador `ApiDiarioController` ahora mapea explícitamente entidad → DTO
- `PomodoroHistorialResponse` usa `SesionPomodoroDto` en lugar de la entidad `SesionPomodoro`
- `GET api/v1/pomodoro/estadisticas` devuelve `RespuestaApi<EstadisticasPomodoroPeriodo>`

### Seguridad
- Archivos `deploy/epycus-web.service` y `deploy/setup-vps.sh` agregados a `.gitignore`
- Estos archivos contenian credenciales de produccion

## Checklist final para beta

- [x] 1. Offline cache: Misiones y Diario en Room
- [x] 2. Estados de carga/vacio/error/reintento estandarizados
- [x] 3. Endpoints Object tipados donde sea posible
- [x] 4. Pruebas de contrato JSON (backend + Android)
- [x] 5. Auth: userId desde JWT, refresh sin loop, logout completo, validar expiracion
- [x] 6. Misiones: dialogo crear con fecha, categorias, editar
- [x] 7. Habitos: editar funcional
- [x] 8. Diario: entrada de texto diaria
- [x] 9. Compila en debug y release
- [x] 10. Unit tests pasan
