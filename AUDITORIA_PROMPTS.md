# Auditoría Epycus Android — Prompts por Especialidad

> Proyecto: Epycus App Móvil (Java + Android SDK 36 + Material 3 + Retrofit + Room)
> Propósito: App de gamificación de hábitos profesionales con IA integrada (Edy)
> Repositorio: https://github.com/Chester0802/EpycusApp
> API: https://app.epycus.es/ | Swagger: https://app.epycus.es/swagger
> Última actualización: 2026-06-19

---

## Cómo usar estos prompts

Cada prompt está diseñado para un **auditor especializado Android**. Copia el prompt en una sesión de IA con contexto del proyecto o pásalo a un revisor humano. Cada uno incluye:

1. **Contexto del proyecto** (resumido para esa especialidad)
2. **Alcance de la auditoría** (archivos/directorios a revisar)
3. **Checklist de revisión** (qué buscar)
4. **Entregables esperados**

---

## Índice de Especialidades

| # | Especialidad | Código | Enfoque principal |
|---|-------------|--------|-------------------|
| 1 | UI/UX & Temas (Material 3) | UX | Sistema de temas claro/oscuro, persistencia, toggle, consistencia visual, estados de carga/vacío/error |
| 2 | Networking & API | API | Retrofit, OkHttp, interceptors, manejo de errores HTTP, refresh token, timeouts, logging |
| 3 | Arquitectura & Patrones | ARQ | MVVM, Repository, separación de capas, ViewBinding, ciclo de vida, navegación |
| 4 | Datos & Offline (Room) | DATA | Room entities, DAOs, caching strategy, migraciones, main-thread queries, fallback offline |
| 5 | Autenticación & Seguridad | SEC | JWT, refresh token, SessionManager, AuthInterceptor, logout, SharedPreferences, Google Auth |
| 6 | Rendimiento & Memoria | PERF | Memory leaks (Fragments, binding, timers), leaking Android handlers, ViewBinding nulling, listas grandes |
| 7 | Calidad de Código & Testing | DEV | Clean Code, nombres, duplicación, cobertura de tests, convenciones Java, linting |
| 8 | Gamificación & Lógica | GAME | XP, niveles, rachas, progreso, adapters, listeners, misiones, pomodoro, lógica local |

---

## 1. UX — UI/UX & Temas (Material 3)

### Contexto
App Android con Material 3 (tema claro/oscuro). Actualmente usa `Theme.Material3.DayNight.NoActionBar` que sigue automáticamente la configuración del sistema. El usuario quiere un **toggle manual** en LoginActivity para cambiar tema y que la preferencia persista. La app debe tener estados de carga (loading spinners), estados vacíos (empty state), y manejo de errores visual (Snackbar). Falta implementar `isLightTheme` persistente en SharedPreferences.

### Archivos a revisar
- `res/values/themes.xml` — Tema claro (light)
- `res/values-night/themes.xml` — Tema oscuro (dark)
- `res/values/colors.xml`, `res/values/styles.xml` — Recursos de tema
- `app/src/main/java/es/epycus/app/ui/auth/LoginActivity.java` — Toggle de tema
- `app/src/main/java/es/epycus/app/ui/MainContainerActivity.java` — Tema en fragments
- `app/src/main/java/es/epycus/app/ui/splash/SplashActivity.java` — Tema inicial
- `res/layout/*.xml` — 15 layouts (todos usan `?attr/ep*` correctamente?)
- `res/drawable/*.xml` — 13 drawables

### Checklist
- [ ] ¿El toggle de tema existe en LoginActivity? (no, debe implementarse)
- [ ] ¿La preferencia de tema persiste en SharedPreferences? (no, debe implementarse)
- [ ] ¿La app recuerda la elección del usuario al reiniciar? (no, sigue al sistema)
- [ ] ¿Todos los layouts usan atributos temáticos (`?attr/ep*`) en vez de colores hardcodeados?
- [ ] ¿Existe `values-night/themes.xml`? (verificar si está creado)
- [ ] ¿Los temas claro/oscuro tienen contraste WCAG suficiente?
- [ ] ¿Los estados de carga (loadingView) existen en todas las pantallas?
- [ ] ¿Los estados vacíos (empty state) existen en fragments con listas?
- [ ] ¿Los Snackbar/Toast usan colores temáticos?
- [ ] ¿Los mood selectors tienen feedback visual correcto?
- [ ] ¿El sistema de temas usa `AppCompatDelegate.setDefaultNightMode()`?
- [ ] ¿El icono "E" con círculo funciona en ambos temas?
- [ ] ¿BottomNavigationView se adapta al tema actual?

### Entregables
- Implementación de toggle con persistencia
- Mapa de todos los colores hardcodeados en layouts
- Recomendaciones de accesibilidad (contraste, tamaños de fuente)
- Screenshots antes/después del fix de temas

---

### Resultados de Auditoría UX — 2026-06-19

**Estado**: 11/13 checklist items cumplidos

#### ✅ Implementado correctamente
- Toggle de tema en LoginActivity con `ThemeManager.toggle()` + `recreate()`
- Persistencia en SharedPreferences (`epycus_theme`, key `is_light_theme`)
- `applyTheme()` en SplashActivity, LoginActivity y MainContainerActivity
- `values-night/themes.xml` con `Theme.Material3.Dark.NoActionBar`
- Todos los 16 layouts usan `?attr/ep*` — 0 colores hardcodeados
- Drawables funcionales (`bg_card_rounded`, `bg_accent_circle`, `bg_chat_message_*`, `bg_accent_gradient`) usan `?attr/`
- Iconos de navegación tienen `android:tint="?attr/epTextPrimary"`
- Loading states en Login, Inicio, Hábitos, Diario, Perfil, Chat IA
- Empty states en InicioFragment y HabitosFragment
- Mood selectors con `bg_card_rounded` + `selectableItemBackground`
- BottomNavigationView con `Widget.Epycus.BottomNavigation` usando `?attr/ep*`

#### 🔴 Issues corregidos
| # | Archivo | Problema | Fix |
|---|---------|----------|-----|
| 1 | `activity_dashboard.xml` | Sin `android:background` ni `textColor` — invisible en dark mode | Agregado `epBgPrimary` y `?attr/epTextPrimary` |
| 2 | `styles_epycus.xml:23` | `@color/white` hardcodeado en `Widget.Epycus.Button` | Reemplazado por `?attr/epTextOnPrimary` |
| 3 | `fragment_diario.xml` | Mood selectors sin indicador de color | Agregados dots de colores (`shape_dot` + `backgroundTint` con `mood_*`) |
| 4 | `fragment_pomodoro.xml` | Sin loading state | Agregado `loadingView` (ProgressBar) |
| 5 | `PomodoroFragment.java` | Sin manejo de estado de carga | Agregado show/hide de `loadingView` en `onCreateView` |

#### 🟢 Pendientes (baja prioridad)
- Launcher icons (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`) — colores estáticos por diseño (adaptive icons)
- Verificación formal de contraste WCAG
- Migración de PomodoroTimer a ViewModel para sobrevivir rotación

---

## 2. API — Networking & API Integration

### Contexto
Retrofit 2 + OkHttp 4 + Gson. 14 servicios API. AuthInterceptor para JWT. Refresh token automático implementado. Endpoint base: `https://app.epycus.es/`. Timeouts de 30s. Logging BODY en debug. El error más común es 401 (token expirado) y falta de conexión.

### Archivos a revisar
- `api/RetrofitClient.java` — Singleton, 14 servicios, authless retrofit para refresh
- `api/AuthInterceptor.java` — JWT header, refresh automático, X-Retry, force logout
- `api/ApiAuthService.java` — Auth endpoints (login, refresh, registro, carreras)
- `api/Api*.java` (14 interfaces) — Endpoints REST
- `model/RespuestaApi.java` — Envoltorio genérico de respuesta
- `model/dto/*.java` — 15 DTOs
- `repository/*.java` — 5 repositorios

### Checklist
- [ ] ¿Todos los endpoints usan `RespuestaApi<T>` como envoltorio?
- [ ] ¿El AuthInterceptor maneja correctamente 401? (implementado)
- [ ] ¿El refresh token se llama con un client sin auth interceptor? (evita bucle)
- [ ] ¿El header X-Retry previene reintentos infinitos?
- [ ] ¿Los timeouts son adecuados? (30s, verificar si es mucho/poco)
- [ ] ¿El logging interceptor muestra credenciales en logs? (riesgo de seguridad)
- [ ] ¿Hay manejo de errores HTTP específicos (403, 404, 422, 500)?
- [ ] ¿Las llamadas en paralelo desde un fragment podrían causar race conditions?
- [ ] ¿El `onFailure` de Retrofit distingue entre timeout, DNS, y otros errores?
- [ ] ¿Hay llamadas en `onCreate` que deberían estar en `onResume`/`onStart`?
- [ ] ¿La base URL cambia entre debug/release? (verificar BuildConfig)
- [ ] ¿Se usa `enqueue` (async) o `execute` (sync) consistentemente?

### Entregables
- Diagrama de flujo de autenticación (login → token → refresh → logout)
- Mapa de errores HTTP no manejados
- Recomendaciones de timeouts y retry policy
- Análisis de posibles race conditions

---

## 3. ARQ — Arquitectura & Patrones

### Contexto
App Android con patrón MVVM simplificado (sin LiveData/ViewModel puro en algunos casos). Repository pattern. ViewBinding activado. Navegación por Activities + FragmentTransactions manuales (sin Navigation Component). Singleton para RetrofitClient y SessionManager. Room para cache offline.

### Archivos a revisar
- `ui/MainContainerActivity.java` — Navegación manual con FragmentManager
- `ui/home/InicioFragment.java` — Dashboard + progreso, caching Room
- `ui/habitos/HabitosFragment.java` — RecyclerView con adapter
- `ui/diario/DiarioFragment.java` — Estados de ánimo + pregunta guía
- `ui/perfil/PerfilFragment.java` — Perfil + logout
- `ui/pomodoro/PomodoroFragment.java` — Timer local (sin API)
- `ui/ia/IaChatActivity.java` — Chat con Edy (RecyclerView)
- `ui/auth/LoginActivity.java`, `RegistroActivity.java` — Login/registro
- `ui/splash/SplashActivity.java` — Splash + redirección
- `repository/*.java` — 5 repositorios

### Checklist
- [ ] ¿Se respeta el patrón MVVM? (Activity/Fragment solo UI, lógica en Repository)
- [ ] ¿ViewBinding se usa en TODAS las Activities y Fragments?
- [ ] ¿Los Fragments nullifican binding en `onDestroyView()`?
- [ ] ¿La navegación manual con FragmentManager es mantenible?
- [ ] ¿Hay fugas de contexto? (Activities retenidas por threads, listeners, callbacks)
- [ ] ¿Se usa `requireContext()` correctamente (vs `getContext()` que puede ser null)?
- [ ] ¿Los singletons (`RetrofitClient`, `SessionManager`, `AppDatabase`) son thread-safe?
- [ ] ¿El splash hace alguna operación pesada en onCreate?
- [ ] ¿Hay código duplicado entre fragments? (patrones de carga, error, caching)
- [ ] ¿El PomodoroFragment podría extraerse a ViewModel para sobrevivir config changes?
- [ ] ¿Las pantallas manejan correctamente los cambios de configuración (rotación)?
- [ ] ¿Los adapters de RecyclerView notifican cambios correctamente?

### Entregables
- Diagrama de arquitectura actual con capas y flujos
- Análisis de deuda técnica (duplicación, acoplamiento)
- Recomendaciones de migración a Navigation Component
- Propuesta de ViewModel/LiveData para estados complejos

---

## 4. DATA — Datos & Offline (Room)

### Contexto
Room 2.6.1 con 4 entidades (UsuarioEntity, HabitoEntity, ProgresoEntity, CacheEntity). AppDatabase con `allowMainThreadQueries()`. Cache de respuestas JSON via CacheEntity (key-value). Repositorios cachean en éxito, fallback offline en onFailure.

### Archivos a revisar
- `data/local/AppDatabase.java` — Database singleton, version 1, fallback destructive
- `data/local/entity/*.java` — 4 entidades Room
- `data/local/dao/*.java` — 4 DAOs (insert, query, delete)
- `repository/AuthRepository.java` — cacheUsuario, getCachedUsuario
- `repository/HabitosRepository.java` — cacheHabitos, getCachedHabitos
- `ui/home/InicioFragment.java` — Cache JSON dashboard + progreso
- `ui/diario/DiarioFragment.java` — Cache JSON pregunta guía
- `ui/perfil/PerfilFragment.java` — Cache JSON perfil

### Checklist
- [ ] ¿`allowMainThreadQueries()` es seguro para el volumen de datos actual?
- [ ] ¿Las entidades tienen @PrimaryKey correcta y @NonNull donde corresponde?
- [ ] ¿Los DAOs usan `OnConflictStrategy.REPLACE` correctamente?
- [ ] ¿Las migraciones destructivas (`fallbackToDestructiveMigration`) borrarán datos en producción?
- [ ] ¿El cacheo en onResponse cubre TODAS las llamadas GET relevantes?
- [ ] ¿El fallback en onFailure cubre TODOS los Fragments?
- [ ] ¿Las escrituras a Room se hacen en background thread? (no, están en main thread)
- [ ] ¿Hay riesgo de datos inconsistentes si falla la escritura a Room?
- [ ] ¿El cache se limpia al hacer logout? (solo UsuarioDao.deleteAll)
- [ ] ¿Las fechas se almacenan correctamente? (String vs Long)
- [ ] ¿Las entidades tienen getters para que Room funcione?
- [ ] ¿Los tipos booleanos se serializan correctamente en Room?

### Entregables
- Recomendación sobre background executor para Room writes
- Plan de migraciones para futuras versiones (evitar destructive)
- Análisis de consistencia de datos entre cache y servidor
- Propuesta de stale-while-revalidate para cache

---

## 5. SEC — Autenticación & Seguridad

### Contexto
JWT con refresh tokens. SessionManager basado en SharedPreferences (MODE_PRIVATE). AuthInterceptor agrega Bearer token. Refresh automático en 401. Logout force a LoginActivity. Google OAuth endpoints definidos pero sin implementación UI. Sin biometría, sin pin local.

### Archivos a revisar
- `util/SessionManager.java` — SharedPreferences, tokens, user data
- `api/AuthInterceptor.java` — Bearer token, refresh, force logout
- `api/RetrofitClient.java` — Authless retrofit, logging interceptor
- `repository/AuthRepository.java` — Login, registro, saveSession, clearSession
- `ui/auth/LoginActivity.java` — Login, validación, setLoading
- `ui/perfil/PerfilFragment.java` — Logout + redirect
- `AndroidManifest.xml` — Permisos, exported activities
- `model/dto/GoogleAuthDto.java`, `CompletarRegistroGoogleDto.java`
- `model/dto/RefreshDto.java`, `LoginDto.java`, `RegistroRequestDto.java`

### Checklist
- [ ] ¿SharedPreferences usa MODE_PRIVATE? (sí, verificado)
- [ ] ¿El token JWT se expone en logs de OkHttp? (logging BODY expone el token)
- [ ] ¿El refresh token se almacena junto al access token? (riesgo si泄漏)
- [ ] ¿El force logout en 401 funciona correctamente desde background thread?
- [ ] ¿Hay validación de entrada en Login/Registro? (campos vacíos, formato email)
- [ ] ¿Las credenciales viajan en texto plano? (HTTPS lo protege)
- [ ] ¿El interceptor de logging debería desactivarse en release?
- [ ] ¿Las Activities exportadas están correctamente configuradas?
- [ ] ¿Hay Google OAuth implementado en UI? (endpoints definidos, UI no)
- [ ] ¿El timeout de 30s es seguro contra ataques de slow loris?
- [ ] ¿Hay rate limiting del lado cliente?
- [ ] ¿El `X-Retry` header podría ser manipulado por un atacante?

### Entregables
- Auditoría de OWASP Mobile Top 10
- Recomendaciones de almacenamiento seguro (EncryptedSharedPreferences)
- Plan para desactivar logging en release (BuildConfig.DEBUG)
- Análisis de riesgo de exposición de tokens en logs

---

## 6. PERF — Rendimiento & Memoria

### Contexto
App con imágenes locales (drawables), RecyclerViews, Chat IA, timer Pomodoro, llamadas Retrofit, Room cache. Sin imágenes de red todavía. Sin paginación en listas. Sin análisis de memoria previo.

### Archivos a revisar
- `ui/pomodoro/PomodoroFragment.java` — CountDownTimer, ciclo de vida
- `ui/ia/IaChatActivity.java` — RecyclerView infinito, adapter
- `ui/habitos/HabitosFragment.java` — RecyclerView con notificaciones
- `ui/adapters/*.java` — 3 adapters
- `ui/home/InicioFragment.java` — Múltiples llamadas API en paralelo
- `ui/MainContainerActivity.java` — Fragment recreation en cada nav
- TODOS los Fragments — Nullificación de binding, timers, listeners

### Checklist
- [ ] ¿Los Fragments nullifican binding en onDestroyView? (sí, verificado)
- [ ] ¿Los CountDownTimer se cancelan en onDestroyView? (sí)
- [ ] ¿Los Callbacks de Retrofit se cancelan al salir de la screen? (no, pueden ejecutarse después de destroy)
- [ ] ¿Hay memoria retenida por listeners anónimos en adapters?
- [ ] ¿Los RecyclerViews tienen `setHasFixedSize` donde aplica?
- [ ] ¿El chat IA mantiene todos los mensajes en memoria? (potencial OOM)
- [ ] ¿Los adapters de chat usan ViewHolder pattern correctamente?
- [ ] ¿La recreación de fragments en bottom nav es eficiente?
- [ ] ¿Hay fugas de Handler en SplashActivity? (postDelayed puede ejecutarse después de destroy)
- [ ] ¿Las imágenes en drawables son del tamaño correcto? (sin upscaling)
- [ ] ¿Room queries son lentas en main thread? (tiny data, pero podría crecer)
- [ ] ¿Hay operaciones en `onCreate` que deberían ser lazy?

### Entregables
- Análisis de leaks con LeakCanary (recomendación)
- Perfil de memoria estimado por pantalla
- Recomendaciones de paginación para listas largas
- Estrategia de cancelación de llamadas Retrofit

---

## 7. DEV — Calidad de Código & Testing

### Contexto
Java 11, target SDK 36, minSdk 28. Sin tests unitarios ni instrumentados. Sin linter CI. Convenciones: código en inglés, strings en español en resources, ViewBinding, Material 3.

### Archivos a revisar
- TODOS los archivos .java (35+ archivos)
- TODOS los archivos XML (15 layouts, 13 drawables, themes, colors)
- `app/build.gradle.kts` — Dependencias, configuración
- `gradle/libs.versions.toml` — Version catalog

### Checklist
- [ ] ¿Los nombres de clases/métodos/variables son descriptivos?
- [ ] ¿Hay código muerto? (MainActivity.java marcado como dead code)
- [ ] ¿Hay imports no utilizados? (pueden compilar pero ensucian)
- [ ] ¿Los strings literales están externalizados a strings.xml?
- [ ] ¿Los colores están en colors.xml y referenciados vía `?attr/`?
- [ ] ¿Hay duplicación de código entre fragments? (patrón carga+error+cache)
- [ ] ¿Las excepciones se loguean con Tag consistente?
- [ ] ¿Los catch blocks tienen logging antes de tragar la excepción?
- [ ] ¿Hay tests unitarios para lógica de negocio? (Repository, SessionManager)
- [ ] ¿Hay tests instrumentados para UI? (Espresso)
- [ ] ¿El `proguard-rules.pro` tiene reglas para Retrofit/Gson/Room?
- [ ] ¿El `lintOptions` está configurado en build.gradle?
- [ ] ¿Los warnings de deprecation se están manejando? (Handler fix aplicado)
- [ ] ¿Las convenciones de formato Java son consistentes? (espacios, llaves)

### Entregables
- Reporte de lint estático
- Lista de código muerto
- Recomendaciones de refactorización
- Plan de testing (unitario + instrumentado)

---

## 8. GAME — Gamificación & Lógica

### Contexto
Sistema de hábitos, XP, niveles, rachas, pomodoro, misiones, chat con IA Edy. La mayoría de la lógica está en el backend; el app consume APIs REST. Lógica local mínima: timer pomodoro, cache de progreso, estado de ánimo.

### Archivos a revisar
- `ui/pomodoro/PomodoroFragment.java` — Timer, ciclos, pausas (lógica local)
- `ui/habitos/HabitosFragment.java` — Lista, completar, fallar
- `ui/adapters/HabitoHoyAdapter.java` — Checkbox, listeners
- `ui/adapters/MisionAdapter.java` — Misiones
- `ui/home/InicioFragment.java` — Dashboard, racha, nivel, XP bar
- `model/dto/GamificacionResponse.java` — Progreso parseado
- `model/dto/DashboardResponse.java` — Dashboard parseado
- `model/entidades/*.java` — 6 modelos de datos

### Checklist
- [ ] ¿El timer pomodoro persiste el estado al rotar? (no, se pierde)
- [ ] ¿Los ciclos de pomodoro se sincronizan con backend? (no parece)
- [ ] ¿El completar/fallar hábito actualiza la UI correctamente?
- [ ] ¿La barra de XP refleja el progreso real?
- [ ] ¿Las rachas se calculan localmente o solo vía API?
- [ ] ¿Los adapters usan DiffUtil para notificaciones eficientes?
- [ ] ¿Los listeners de hábitos evitan doble click?
- [ ] ¿Las misiones tienen estados visuales (completada, activa, bloqueada)?
- [ ] ¿El chat IA maneja correctamente el historial de conversación?
- [ ] ¿Los mensajes de Edy tienen typing indicator?
- [ ] ¿El estado de ánimo se persiste localmente? (cache en Room)
- [ ] ¿Hay retroalimentación háptica/sonora en acciones importantes?

### Entregables
- Diagrama de flujo de gamificación (hábito → XP → nivel → recompensa)
- Análisis de consistencia entre estado local y servidor
- Recomendaciones de gamificación (animaciones, micro-interacciones)
- Propuesta de offline-first para hábitos

---

## 🧠 Auditoría Extrema — Prompt Unificado

### Contexto Completo
Epycus es una aplicación Android (Java, SDK 36, minSdk 28) de gamificación de hábitos profesionales. Usa Material 3, Retrofit + OkHttp para API REST, Room para cache offline, ViewBinding, SharedPreferences para sesión. Se conecta a backend ASP.NET Core 9 en `https://app.epycus.es/`. Repositorio: `https://github.com/Chester0802/EpycusApp`.

### Archivos a revisar (TODOS)
```
app/src/main/java/es/epycus/app/
├── MainActivity.java (dead code)
├── api/ (14 archivos) — Retrofit, interceptors, servicios
├── model/ (21 archivos) — DTOs, entidades, RespuestaApi
├── repository/ (5 archivos) — Auth, Diario, Habitos, Misiones, Pomodoro
├── ui/
│   ├── adapters/ (3) — HabitoHoy, MensajeChat, Mision
│   ├── auth/ (2) — Login, Registro
│   ├── diario/ (1) — DiarioFragment
│   ├── habitos/ (1) — HabitosFragment
│   ├── home/ (2) — DashboardActivity, InicioFragment
│   ├── ia/ (1) — IaChatActivity
│   ├── perfil/ (1) — PerfilFragment
│   ├── pomodoro/ (1) — PomodoroFragment
│   ├── splash/ (1) — SplashActivity
│   └── MainContainerActivity.java
├── util/ (1) — SessionManager
└── data/local/ (9 archivos) — Room DB, DAOs, entities
res/
├── layout/ (15 XMLs)
├── drawable/ (13 XMLs)
├── values/ (themes.xml, colors.xml, strings.xml)
├── values-night/ (themes.xml)
├── menu/ (bottom nav)
└── xml/ (network_security_config)
```

### Checklist Extrema (TODAS las anteriores + adicionales)
- [ ] **1. UX TEMAS**: Implementar toggle manual claro/oscuro en LoginActivity con persistencia en SharedPreferences. No depender solo del modo sistema.
- [ ] **2. API LOGIN**: Verificar que login funcione end-to-end (credenciales → token → navegación → caché Room → dashboard).
- [ ] **3. REFRESH TOKEN**: Probar 401 → refresh → retry → éxito y 401 → refresh → fallo → logout.
- [ ] **4. OFFLINE**: Desconectar red, verificar que Room cache muestre datos, reconectar, verificar sync.
- [ ] **5. ROTACIÓN**: Verificar que PomodoroTimer y estados sobrevivan a cambios de configuración.
- [ ] **6. CICLO DE VIDA**: Verificar null safety en binding, cancelación de callbacks, limpieza de timers.
- [ ] **7. LOGGING**: Verificar que logging interceptor no exponga tokens en release (BuildConfig.DEBUG).
- [ ] **8. GOOGLE AUTH**: Verificar si los endpoints están listos del lado mobile, qué falta.
- [ ] **9. EMPTY STATES**: Probar lista vacía (sin hábitos, sin misiones) → mostrar mensaje correcto.
- [ ] **10. ERRORES HTTP**: Probar 403, 500, timeout → mostrar Snackbar apropiado.
- [ ] **11. NAVEGACIÓN**: Probar bottom nav → cada fragment carga datos frescos? Se mantiene estado?
- [ ] **12. RENDIMIENTO**: Verificar que las llamadas Retrofit no se acumulen al navegar rápidamente.

### Entregables Finales
1. **Reporte de Hallazgos** en `Tareas.md` > Auditoría 002 con tabla de severidad
2. **Issues críticos** priorizados (Alta/Media/Baja)
3. **Recomendaciones** con estimación de esfuerzo
4. **Código corregido** para los issues resueltos durante la auditoría
