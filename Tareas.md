# Epycus App Móvil — Tareas, Auditoría y Documentación para IA

> **Propósito**: Este archivo es el punto de entrada para cualquier IA que trabaje en este proyecto.  
> **Regla de oro**: Cada vez que una IA modifique código, deberá registrar aquí sus cambios, hallazgos, errores y pendientes.

---

## 📁 Estructura del Proyecto Android

```
Epycus/                          # Raíz del proyecto Android (Gradle)
├── .git/
├── .gitignore
├── .gradle/
├── .idea/
├── build.gradle.kts             # Gradle raíz (plugins android application)
├── gradle.properties
├── gradle/
│   └── libs.versions.toml       # Version catalog (dependencias centralizadas)
├── gradlew / gradlew.bat
├── local.properties
├── settings.gradle.kts          # Incluye :app, nombre "Epycus"
├── Tareas.md                    # ← ESTE ARCHIVO
└── app/
    ├── build.gradle.kts         # SDK 36, minSdk 28, Retrofit, OkHttp, Nav, Lifecycle
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/es/epycus/app/
            │   ├── MainActivity.java
            │   ├── api/                    # Capa de red (Retrofit)
            │   │   ├── RetrofitClient.java
            │   │   ├── AuthInterceptor.java
            │   │   ├── ApiAdminService.java
            │   │   ├── ApiAuthService.java
            │   │   ├── ApiBienestarService.java
            │   │   ├── ApiDashboardService.java
            │   │   ├── ApiDiarioService.java
            │   │   ├── ApiEstadoAnimoService.java
            │   │   ├── ApiGamificacionService.java
            │   │   ├── ApiHabitosService.java
            │   │   ├── ApiIaService.java
            │   │   ├── ApiMisionesService.java
            │   │   ├── ApiPerfilService.java
            │   │   ├── ApiPomodoroService.java
            │   │   └── ApiProgresoService.java
            │   ├── model/                  # Modelos de datos
            │   │   ├── RespuestaApi.java
            │   │   ├── dto/
            │   │   │   ├── ChatRequest.java
            │   │   │   ├── ChatResponse.java
            │   │   │   ├── CompletarRegistroGoogleDto.java
            │   │   │   ├── DashboardResponse.java
            │   │   │   ├── GamificacionResponse.java
            │   │   │   ├── GoogleAuthDto.java
            │   │   │   ├── HabitoHoyDto.java
            │   │   │   ├── LoginDto.java
            │   │   │   ├── MisionDto.java
            │   │   │   ├── PausaActivaDto.java
            │   │   │   ├── PerfilResponse.java
            │   │   │   ├── RecomendacionPausaDto.java
            │   │   │   ├── RecuperarContrasenaDto.java
            │   │   │   ├── RefreshDto.java
            │   │   │   ├── RegistroRequestDto.java
            │   │   │   └── RestablecerContrasenaDto.java
            │   │   └── entidades/
            │   │       ├── AuthResponse.java
            │   │       ├── Carrera.java
            │   │       ├── Habito.java
            │   │       ├── Nivel.java
            │   │       ├── ProgresoUsuario.java
            │   │       └── Usuario.java
            │   ├── repository/             # Repositorios (lógica de datos)
            │   │   ├── AuthRepository.java
            │   │   ├── DiarioRepository.java
            │   │   ├── HabitosRepository.java
            │   │   ├── MisionesRepository.java
            │   │   └── PomodoroRepository.java
            │   ├── ui/                     # Capa de UI (Activities + Fragments)
            │   │   ├── MainContainerActivity.java
            │   │   ├── adapters/
            │   │   │   ├── HabitoHoyAdapter.java
            │   │   │   ├── MensajeChatAdapter.java
            │   │   │   └── MisionAdapter.java
            │   │   ├── auth/
            │   │   │   ├── LoginActivity.java
            │   │   │   └── RegistroActivity.java
            │   │   ├── diario/
            │   │   │   └── DiarioFragment.java
            │   │   ├── habitos/
            │   │   │   └── HabitosFragment.java
            │   │   ├── home/
            │   │   │   ├── DashboardActivity.java
            │   │   │   └── InicioFragment.java
            │   │   ├── ia/
            │   │   │   └── IaChatActivity.java
            │   │   ├── perfil/
            │   │   │   └── PerfilFragment.java
            │   │   └── pomodoro/
            │   │       └── PomodoroFragment.java
            │   └── util/
            │       └── SessionManager.java  # Tokens, sesión local
            ├── res/
            │   ├── drawable/               # Iconos, shapes, backgrounds
            │   ├── layout/                 # 15 archivos XML (activities + fragments + items)
            │   ├── menu/                   # Menús de navegación
            │   ├── mipmap-*/               # Launcher icons (varias densidades)
            │   ├── values/                 # strings.xml, colors.xml, themes.xml, styles.xml
            │   ├── values-night/           # Temas oscuros
            │   └── xml/                    # network_security_config, backup_rules
            ├── test/                       # Tests unitarios (JUnit)
            └── androidTest/                # Tests instrumentados (Espresso)
```

## 📁 Estructura del Backend (EpycusApp — ASP.NET Core 9)

```
EpycusApp/                       # Backend (raíz del repositorio GitHub)
├── EpycusApp.csproj            # .NET 9, EF Core, Pomelo MySQL, JWT, BCrypt, Swagger
├── Program.cs                   # DI, middleware, auth, rate limiting, health checks
├── appsettings.json             # Config (EN GITIGNORE — usar variables de entorno)
├── appsettings.Example.json     # Template con placeholders
├── appsettings.Development.json
├── Controllers/                 # 12 MVC + 8 API + 2 Base
│   ├── BaseController.cs
│   ├── BaseApiController.cs
│   ├── AutenticacionController.cs
│   ├── HabitosController.cs
│   ├── PomodoroController.cs
│   ├── MisionesController.cs
│   ├── ProgresoController.cs
│   ├── PerfilController.cs
│   ├── BienestarController.cs
│   ├── DiarioAnimoController.cs
│   ├── IaController.cs
│   ├── AjustesController.cs
│   ├── AdminController.cs
│   ├── HomeController.cs
│   └── Api/                    # 8 API controllers
├── Servicios/
│   ├── Interfaces/              # 12 interfaces
│   └── Implementaciones/        # 13 implementaciones
├── Models/
│   ├── Entidades/               # 29 entidades EF
│   └── Enums/
├── ViewModels/                  # ViewModels con validación
├── DTOs/                        # Data Transfer Objects
├── Datos/
│   ├── ContextoAplicacion.cs    # DbContext + 29 DbSets
│   └── Semilla/
│       └── DatosSemilla.cs      # Seed data
├── Middleware/                   # TelemetriaMiddleware, CargarPersonajeFilter
├── Ayudantes/                   # CalculadorXP, ConstantesGamificacion, RespuestaApi
├── Views/                       # ~25+ Razor views + layouts + partials
├── wwwroot/                     # CSS, JS, imágenes, lib (Bootstrap 5, Chart.js)
├── Migrations/                  # Migraciones EF Core
├── deploy/                      # Scripts VPS, Nginx, systemd, monitoreo
├── EpycusApp.Tests/             # Tests unitarios (xUnit + Moq + FluentAssertions)
├── EpycusApp.AcceptanceTests/   # Tests de aceptación (Playwright)
└── .github/workflows/           # CI/CD pipeline
```

---

## 🧠 Instrucciones para IAs

### 1. Cada vez que modifiques código

Agrega una entrada en **# Historial de Cambios** (al final de este archivo) con:
- Fecha y hora
- Archivos modificados
- Qué se cambió y por qué
- Riesgo de la modificación (Alto/Medio/Bajo)

### 2. Cada vez que hagas una auditoría

Agrega una entrada en **# Auditorías** con:
- Fecha y alcance
- Hallazgos encontrados (bugs, code smells, vulnerabilidades)
- Errores corregidos durante la auditoría
- Pendientes que quedan

### 3. Convenciones del proyecto Android

| Aspecto | Convención |
|---------|-----------|
| Idioma | Código y comentarios en **inglés** |
| Patrón | MVVM (Model-View-ViewModel) con Repository |
| HTTP | Retrofit + OkHttp + Gson |
| Auth | JWT via `AuthInterceptor` + `SessionManager` |
| Navegación | Activities + Fragments (Nav Components) |
| Layouts | XML con Material 3 |
| API Base | `https://app.epycus.es/` (definido en build.gradle.kts) |
| Tests | JUnit (unit) + Espresso (instrumented) |
| Java | Java 11, target SDK 36, min SDK 28 |

### 4. Convenciones del proyecto Backend

| Aspecto | Convención |
|---------|-----------|
| Idioma | Código en **español** (variables, clases, métodos) |
| Patrón | MVC + Service + Repository (EF Core) |
| API | RESTful en `/api/*` + Swagger |
| Auth | JWT en cookies HttpOnly + Google OAuth |
| BD | EF Core + Pomelo MySQL + MariaDB 11.8 |
| Tests | xUnit + Moq + FluentAssertions (unitarios), Playwright (aceptación) |
| Deploy | VPS Debian 13 + Nginx + systemd |
| CI/CD | GitHub Actions (build → quality → deploy → security) |

### 5. Reglas para todas las IAs

1. **No modifiques `appsettings.json`** con credenciales reales — usa variables de entorno
2. **No comitas secretos** — revisa `git diff` antes de commitear
3. **No borres código comentado** — mejor preguntar si se necesita
4. **No agregues dependencias** sin verificar que no existan ya en el version catalog
5. **No uses `alert()` nativo** — usa el sistema de toasts/notificaciones existente
6. **No hardcodees colores** — usa variables CSS/recursos de tema
7. **Siempre UTF-8 sin BOM** — el proyecto tuvo problemas de mojibake en el pasado
8. **Documenta en este archivo** cualquier cambio estructural

---

## 🔍 Auditorías

### Auditoría 001 — 2026-06-19 (Inicial)

**Alcance**: Estructura completa del proyecto Android (`C:\Users\marco\Pictures\Epycus`)

#### ✅ Errores de build corregidos previamente
- `boxCornerRadius` no existe en Material3 `TextInputLayout` → eliminado
- Atributos XML `android:topLeft` → `topLeftRadius`, etc.

#### ✅ Warnings de lint/IDE corregidos
- Lambdas simplificadas (3 archivos)
- `@Nullable` en `onCreate` (6 Activities)
- `@NonNull` en parámetros Callback (6 archivos)
- Diamond operator `<>` (5 archivos)
- `@SuppressLint("SetTextI18n")` añadido (9 archivos)

#### 🔴 Hallazgos Pendientes (Corregidos)

| ID | Prioridad | Archivo | Problema | Estado |
|----|-----------|---------|----------|--------|
| P-001 | **Alta** | `RegistroActivity.java:56` | `cargarCarreras()` no implementado | ✅ Corregido |
| P-002 | **Alta** | `DiarioFragment.java:128` | `onFailure` vacío | ✅ Corregido |
| P-003 | **Alta** | `HabitosFragment.java:146` | `onFailure` vacío | ✅ Corregido |
| P-004 | **Media** | `InicioFragment.java:90-92` | `onFailure` sin mensaje | ✅ Corregido |
| P-005 | **Media** | `InicioFragment.java:118` | `onFailure` vacío | ✅ Corregido |
| P-006 | **Media** | `DiarioFragment.java:123` | `catch` silencioso | ✅ Corregido |
| P-007 | **Media** | `InicioFragment.java:113` | `catch` silencioso | ✅ Corregido |
| P-008 | **Baja** | 9 archivos | Strings hardcodeadas | ✅ Corregido |
| P-009 | **Baja** | Múltiples archivos | Migrar a ViewBinding | ✅ Corregido |
| P-010 | **Baja** | Múltiples archivos | Logging en catch blocks | ✅ Corregido |
| P-011 | **Baja** | `MainActivity.java` | Código muerto | ✅ Documentado |

#### 📊 Estado del proyecto Android

| Aspecto | Estado |
|---------|--------|
| Compila | ✅ Sí |
| Autenticación | ✅ Login/Registro implementados |
| API conectada | ✅ 14 servicios API vía Retrofit |
| Dashboard | ✅ Fragmento con caching offline |
| Hábitos | ✅ Fragmento + adaptador + pull-to-refresh |
| Pomodoro | ✅ Fragmento funcional (timer local) |
| IA Chat | ✅ Activity de chat implementada |
| Perfil | ✅ Fragmento con caching offline |
| Diario | ✅ Fragmento con caching offline |
| Misiones | ✅ Adaptador + repositorio |
| Testing | ❌ Sin tests implementados |
| Offline | ✅ Room implementado (4 entidades, cache JSON) |
| Pull-to-refresh | ✅ 4 fragments con SwipeRefreshLayout |
| Loading spinners | ✅ Todas las pantallas con ProgressBar |
| Tema claro/oscuro | ✅ Toggle manual + persistencia |
| Refresh token | ✅ Automático con retry y force logout |
| Manejo errores red | ✅ Snackbar consistente en todos los onFailure |
| Push notifications | ❌ No implementado |
| Google OAuth | ⚠️ Endpoints definidos, UI no implementada |

---

### Auditoría 002 — 2026-06-19 (Extrema - Post-implementación)

**Alcance**: Auditoría completa del código Android tras implementar los 6 pendientes (red, refresh, splash, Room, pull-to-refresh, loading). También se creó `AUDITORIA_PROMPTS.md` con 8 especialidades y prompt unificado para futuras auditorías.

#### ✅ Implementado durante esta sesión
1. **Manejo errores red** — Todos los `onFailure` → Snackbar con `R.string.error_conexion` (7 archivos)
2. **Refresh token** — AuthInterceptor intercepta 401, llama refresh sin bucle (X-Retry), actualiza token, force logout si falla
3. **Splash** — SplashActivity con logo + delay 1.5s, redirección según sesión, LAUNCHER en manifest
4. **Room offline** — AppDatabase con 4 entidades, DAOs, cache en repositorios y fragments, fallback en onFailure
5. **Pull-to-refresh** — 4 fragments envueltos en SwipeRefreshLayout con listener
6. **Loading spinners** — ProgressBar en todas las pantallas (incluye DiarioFragment, LoginActivity, RegistroActivity)
7. **Theme toggle** — ThemeManager persistente, botón "Modo oscuro/claro" en LoginActivity, applyTheme en SplashActivity
8. **AUDITORIA_PROMPTS.md** — Documento de prompts por especialidad (8 áreas + prompt unificado extremo)

#### 🟡 Hallazgos de Seguridad

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| S-001 | **Alta** | `RetrofitClient.java:35` | `HttpLoggingInterceptor.Level.BODY` expone tokens JWT en logs. Debe desactivarse o usar `HEADERS` en release |
| S-002 | **Media** | `util/SessionManager.java` | Tokens almacenados en SharedPreferences planas. Usar `EncryptedSharedPreferences` |
| S-003 | **Media** | `ui/auth/LoginActivity.java` | Sin Google OAuth UI aunque los endpoints están definidos |

#### 🟡 Hallazgos de Arquitectura

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| A-001 | **Media** | `data/local/AppDatabase.java` | `allowMainThreadQueries()` es rápido para cache pequeña pero debería migrarse a background executor |
| A-002 | **Media** | `ui/MainContainerActivity.java` | FragmentTransaction manual sin Navigation Component — difícil de mantener |
| A-003 | **Baja** | `ui/MainContainerActivity.java` | Fragmentos se recrean en cada click del bottom nav (sin `FragmentManager` reuse) |
| A-004 | **Baja** | `AndroidManifest.xml:40` | `MainActivity.java` es código muerto pero sigue declarada en manifest |
| A-005 | **Baja** | `data/local/AppDatabase.java` | `fallbackToDestructiveMigration()` puede borrar cache local al actualizar DB |

#### 🟡 Hallazgos de Rendimiento

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| P-001 | **Media** | Todos los Fragments | Callbacks de Retrofit no se cancelan al destruir la View (pueden ejecutarse después de onDestroyView) |
| P-002 | **Media** | `ui/pomodoro/PomodoroFragment.java` | Timer no persiste al rotar (se pierde el estado) |
| P-003 | **Baja** | `ui/splash/SplashActivity.java` | `Handler.postDelayed` podría ejecutarse después de `finish()` si la Activity se destruye antes |

#### 🟡 Hallazgos de Calidad de Código

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| C-001 | **Media** | `gradle/libs.versions.toml` | Sin reglas ProGuard para Room/Retrofit/Gson |
| C-002 | **Baja** | Todos los Fragments | Sin DiffUtil en los 3 adapters |
| C-003 | **Baja** | Todos los Fragments | Sin tests unitarios ni instrumentados |
| C-004 | **Baja** | `data/local/entity/CacheEntity.java` | Cache key-value con String es typedébil; propenso a errores tipográficos |
| C-005 | **Baja** | `app/build.gradle.kts` | `lintOptions` no configurado; no hay `abortOnError` |

#### 🟢 Auditoría UX (Especialidad 1) — Hallazgos y Fixes

| ID | Prioridad | Archivo | Problema | Estado |
|----|-----------|---------|----------|--------|
| UX-001 | **Alta** | `res/layout/activity_dashboard.xml` | Sin `android:background` ni `textColor` — invisible en dark mode | ✅ Corregido |
| UX-002 | **Media** | `res/values/styles_epycus.xml:23` | `@color/white` hardcodeado en `Widget.Epycus.Button` | ✅ Corregido |
| UX-003 | **Baja** | `res/layout/fragment_diario.xml` | Mood selectors sin indicador de color | ✅ Corregido |
| UX-004 | **Baja** | `res/layout/fragment_pomodoro.xml` | Sin loading state | ✅ Corregido |
| UX-005 | **Baja** | `res/drawable/ic_launcher_foreground.xml` | Colores hardcodeados (launcher icon — no aplica tema) | ⏸️ No aplica |
| UX-006 | **Baja** | `res/drawable/ic_launcher_background.xml` | `#3DDC84` hardcodeado (launcher icon — estático por diseño) | ⏸️ No aplica |

#### 📊 Resumen Final

| Categoría | ✅ Buenos | 🟡 Mejorables | ❌ Críticos |
|-----------|----------|---------------|-------------|
| Networking | Refresh token, interceptor, 14 servicios, logging condicional, calls cancelados, errores diferenciados | — | — |
| UI/UX | ViewBinding, loading, pull-to-refresh, tema toggle | Sin DiffUtil (C-002) | — |
| Offline | Room implementado, cache en repos | Main-thread queries (A-001), destructive migration (A-005) | — |
| Seguridad | AuthInterceptor, force logout on main thread | SharedPreferences planas (S-002) | — |
| Testing | — | Sin tests (C-003) | — |

---: Implementar `cargarCarreras()` en `RegistroActivity.java`
- [x] **P-002 a P-007**: Agregar feedback al usuario en callbacks `onFailure` y catch blocks
- [x] **P-008**: Strings hardcodeadas movidas a `strings.xml`
- [x] **P-009**: Migración a ViewBinding completada
- [x] **P-010**: Logging agregado en todos los catch blocks
- [x] **P-011**: `MainActivity.java` marcada como código muerto con nota
- [x] Agregar manejo de errores de red consistente (Snackbar/Toast en todos los fragments)
- [x] Implementar refresh token automático cuando expire el JWT
- [x] Agregar pantalla de splash
- [x] Agregar soporte offline (Room database local)
- [x] Agregar pull-to-refresh en fragments
- [x] Implementar estado de carga (loading spinners) en todas las pantallas
- [x] **UX-001**: Corregir `activity_dashboard.xml` — agregar `background` y `textColor` temáticos
- [x] **UX-002**: Reemplazar `@color/white` hardcodeado por `?attr/epTextOnPrimary` en `styles_epycus.xml`
- [x] **UX-003**: Agregar indicadores de color a mood selectors en DiarioFragment
- [x] **UX-004**: Agregar loading state a PomodoroFragment

### Backend (EpycusApp)

> Ver `C:\Users\marco\Pictures\EpycusApp\PENDIENTES.md` para la lista completa.

- [ ] HTTPS con Let's Encrypt (UX-002, IMP-015)
- [ ] Rate limiter "Gemini" aplicado en IaController (ARQ-003, IA-CRIT-01)
- [ ] Anti-CSRF en endpoint `/api/ia/chat` (ARQ-006, IA-CRIT-02)
- [ ] Backups automatizados de BD (BD-007)
- [ ] Migraciones de BD en pipeline CI/CD (CI-007)
- [ ] Política de contraseñas + CAPTCHA + bloqueo de cuenta (SEC-015 a SEC-017)
- [ ] Colores hardcodeados en JS (UX-060 a UX-068)
- [ ] Accesibilidad aria-label (UX-069)
- [ ] Contraste WCAG en tema Sakura (UX-071 a UX-073)
- [ ] Señales de bienestar en system prompt de IA (IA-IMP-04)

---

## 📝 Historial de Cambios

| Fecha | Archivos | Cambio | Riesgo |
|-------|----------|--------|--------|
| 2026-06-19 | `Tareas.md` | Creación inicial del archivo de documentación y auditoría para IA | Bajo |
| 2026-06-19 | `AuthRepository.java`, `RegistroActivity.java` | **P-001**: Implementado `cargarCarreras()` — llama a API `obtenerCarreras()` y popula spinner | Medio |
| 2026-06-19 | `DiarioFragment.java`, `HabitosFragment.java`, `InicioFragment.java` | **P-002 a P-005**: Agregado Snackbar/Toast en `onFailure` de todas las llamadas API | Bajo |
| 2026-06-19 | `DiarioFragment.java`, `InicioFragment.java` | **P-006/P-007**: Reemplazados `catch (Exception ignored)` con `Log.e()` | Bajo |
| 2026-06-19 | `strings.xml`, +9 archivos .java | **P-008**: Extraídos todos los strings literales a `strings.xml`, eliminados `@SuppressLint("SetTextI18n")` | Medio |
| 2026-06-19 | `app/build.gradle.kts`, +7 archivos .java | **P-009**: Activado `viewBinding = true`, migrados Activities y Fragments a ViewBinding | Medio |
| 2026-06-19 | `IaChatActivity.java`, `PerfilFragment.java`, `HabitosFragment.java` | **P-010**: Agregado `Log.e` con TAG en todos los catch blocks sin logging | Bajo |
| 2026-06-19 | `MainActivity.java` | **P-011**: Documentada como código muerto con nota para eliminación futura | Bajo |
| 2026-06-19 | `AppDatabase.java` | **Hotfix**: Agregado `.allowMainThreadQueries()` para evitar crash `Cannot access database on the main thread` en cache local | Alto |
| 2026-06-19 | `LoginActivity.java`, `RegistroActivity.java`, `HabitosFragment.java`, `PerfilFragment.java`, `IaChatActivity.java` | **1 - Red consistente**: Todos los `onFailure` ahora usan `Snackbar` con `R.string.error_conexion`. Se eliminaron literales y strings específicos | Bajo |
| 2026-06-19 | `AuthInterceptor.java`, `RetrofitClient.java` | **2 - Refresh token**: Intercepta 401, llama a `/auth/refresh` con Retrofit sin auth, actualiza token y reintenta. Si falla, fuerza logout. `X-Retry` header evita bucles | Alto |
| 2026-06-19 | `ui/splash/SplashActivity.java` (nuevo), `res/layout/activity_splash.xml` (nuevo), `AndroidManifest.xml` | **3 - Splash**: Activity con logo "E" + nombre. Delay 1.5s. Redirige a `MainContainerActivity` o `LoginActivity` según sesión. Declarada como LAUNCHER | Medio |
| 2026-06-19 | `gradle/libs.versions.toml`, `app/build.gradle.kts` | **4 - Room/SwipeRefresh deps**: Agregadas room-runtime 2.6.1, room-compiler, swiperefreshlayout 1.1.0 | Medio |
| 2026-06-19 | `data/local/AppDatabase.java`, `data/local/dao/*`, `data/local/entity/*` (7 archivos nuevos) | **4 - Room DB**: Entidades Usuario/Habito/Progreso/Cache, DAOs CRUD, AppDatabase singleton | Medio |
| 2026-06-19 | `AuthRepository.java`, `HabitosRepository.java` | **4 - Offline repos**: Repositorios cachean en Room tras éxito; `getCachedXxx()` para fallback offline | Medio |
| 2026-06-19 | `InicioFragment.java`, `DiarioFragment.java`, `PerfilFragment.java` | **4 - Offline cache**: Fragmentos cachean JSON (dashboard, progreso, perfil, pregunta guía) en Room y lo cargan en `onFailure` | Medio |
| 2026-06-19 | `fragment_inicio.xml`, `fragment_habitos.xml`, `fragment_diario.xml`, `fragment_perfil.xml` | **5 - Pull-to-refresh**: Layouts envueltos en `SwipeRefreshLayout` con id `swipeRefresh` | Bajo |
| 2026-06-19 | `InicioFragment.java`, `HabitosFragment.java`, `DiarioFragment.java`, `PerfilFragment.java` | **5 - Pull-to-refresh**: `setOnRefreshListener` conectado a `cargarXxx()`. `setRefreshing(false)` en éxito y error | Bajo |
| 2026-06-19 | `fragment_diario.xml`, `activity_login.xml`, `activity_registro.xml` | **6 - Loading spinners**: Agregado `loadingView` (ProgressBar) a layouts que no lo tenían | Bajo |
| 2026-06-19 | `DiarioFragment.java`, `LoginActivity.java`, `RegistroActivity.java` | **6 - Loading spinners**: `loadingView` se muestra/oculta durante llamadas API. Login/Registro usan ProgressBar en vez de cambiar texto del botón | Bajo |
| 2026-06-19 | `activity_dashboard.xml` | **UX-001**: Agregado `android:background="?attr/epBgPrimary"` y `android:textColor="?attr/epTextPrimary"` para visibilidad en ambos temas | Bajo |
| 2026-06-19 | `styles_epycus.xml` | **UX-002**: Reemplazado `@color/white` por `?attr/epTextOnPrimary` en `Widget.Epycus.Button` | Bajo |
| 2026-06-19 | `fragment_diario.xml`, `shape_dot.xml` (nuevo) | **UX-003**: Agregados indicadores de color (dots circulares) a cada mood selector usando `mood_*` colors | Bajo |
| 2026-06-19 | `fragment_pomodoro.xml`, `PomodoroFragment.java` | **UX-004**: Agregado `loadingView` (ProgressBar) con show/hide durante inicialización | Bajo |
| 2026-06-19 | `AUDITORIA_PROMPTS.md` | **UX Audit docs**: Agregada sección de resultados de auditoría UX con checklist, issues y fixes | Bajo |
| 2026-06-19 | `RetrofitClient.java` | **API-S-001**: Logging interceptor cambiado de `Level.BODY` a condicional `HEADERS`/`NONE` según `BuildConfig.DEBUG` | Alto |
| 2026-06-19 | `AuthInterceptor.java` | **API-S-002**: `forceLogout()` envuelto en `Handler(Looper.getMainLooper()).post()` para ejecutar en main thread | Medio |
| 2026-06-19 | `LoginActivity.java` | **API-A-002**: `saveSession` cambia `userId=0` a `-1`; añadido `activeCall` + cancelación en `onDestroy()`; errores de red diferenciados | Bajo |
| 2026-06-19 | `PerfilFragment.java` | **API-A-002**: Añadido `database.usuarioDao().insert()` al cargar perfil exitosamente; `cargarDatosLocales()` prueba Room cache primero | Bajo |
| 2026-06-19 | `InicioFragment.java` | **API-A-003 + UX-001**: Añadido `activeCalls` list + cancelación en `onDestroyView()`; errores de red diferenciados por tipo | Medio |
| 2026-06-19 | `HabitosFragment.java` | **API-A-003 + UX-001 + A-004**: Añadido `activeCalls`, cancelación, errores diferenciados, cache Room de hábitos | Medio |
| 2026-06-19 | `DiarioFragment.java` | **API-A-003 + UX-001**: Añadido `activeCalls`, cancelación, errores diferenciados | Medio |
| 2026-06-19 | `IaChatActivity.java` | **API-A-003 + UX-001**: Añadido `activeCall`, cancelación en `onDestroy()`, errores diferenciados | Medio |
| 2026-06-19 | `RegistroActivity.java` | **API-A-003 + UX-001**: Añadido `activeCall`, cancelación en `onDestroy()`, errores diferenciados | Medio |
| 2026-06-19 | `strings.xml` | **UX-001**: Añadidos `error_timeout` y `error_sin_conexion` para errores de red diferenciados | Bajo |
| 2026-06-19 | `AUDITORIA_PROMPTS.md` | **API Audit docs**: Agregada sección de resultados de auditoría API con checklist, issues y fixes | Bajo |

### Auditoría 003 — 2026-06-19 (Networking & API)

**Alcance**: Auditoría especializada de la capa de red: RetrofitClient, AuthInterceptor, 14 servicios API, RespuestaApi, 5 repositorios, llamadas API en fragments.

| # | Checklist | Estado |
|---|-----------|--------|
| 1 | ¿Todos los endpoints usan `RespuestaApi<T>` como envoltorio? | ✅ Sí |
| 2 | ¿El AuthInterceptor maneja correctamente 401? | ✅ Sí |
| 3 | ¿El refresh token se llama con client sin auth interceptor? | ✅ Sí (authlessRetrofit) |
| 4 | ¿El header X-Retry previene reintentos infinitos? | ✅ Sí |
| 5 | ¿Los timeouts son adecuados? (30s) | ✅ Aceptable |
| 6 | ¿El logging interceptor muestra credenciales en logs? | **❌ Sí — RIESGO** |
| 7 | ¿Hay manejo de errores HTTP específicos (403, 404, 422, 500)? | **❌ No** |
| 8 | ¿Las llamadas en paralelo podrían causar race conditions? | ✅ No (main thread) |
| 9 | ¿El `onFailure` distingue entre timeout, DNS y otros errores? | **❌ No** |
| 10 | ¿Hay llamadas en `onCreate` que deberían estar en `onResume`? | ✅ No |
| 11 | ¿La base URL cambia entre debug/release? | ⚠️ No, pero aceptable |
| 12 | ¿Se usa `enqueue` (async) consistentemente? | ✅ Sí |

#### 🟠 Hallazgos de Seguridad

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| API-S-001 | **Crítica** | `RetrofitClient.java:36` | `HttpLoggingInterceptor.Level.BODY` expone tokens JWT + credenciales en logs | ✅ Corregido — `HEADERS` en debug, `NONE` en release |
| API-S-002 | **Alta** | `AuthInterceptor.java:96-99` | `forceLogout()` llama `context.startActivity()` desde background thread | ✅ Corregido — `Handler(Looper.getMainLooper()).post()` |

#### 🟡 Hallazgos de Arquitectura

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| API-A-001 | **Alta** | 10 servicios API | 12 de 14 servicios usan `RespuestaApi<Object>` en lugar de DTOs tipados | ⏸️ Requiere refactor mayor |
| API-A-002 | **Alta** | `LoginActivity.java:77` | `saveSession(authData, 0, correo, correo)` pasa `userId=0` | ✅ Corregido — cambiado a `-1`, PerfilFragment ahora cachea en Room |
| API-A-003 | **Media** | Todos los Fragments + Activities con API calls | Los Retrofit `Call` no se cancelaban al destruir la vista | ✅ Corregido — `activeCalls` + cancelación en `onDestroyView()`/`onDestroy()` |
| API-A-004 | **Media** | `DiarioRepository.java`, `MisionesRepository.java`, `PomodoroRepository.java` | 3 de 5 repositorios sin cache Room | ⏸️ Funcional via `database.cacheDao()` directo en fragments |
| API-A-005 | **Baja** | `app/build.gradle.kts:20` | `API_BASE_URL` en `defaultConfig` — no varía entre debug/release | ⏸️ Aceptable, no crítico |

#### 🟢 Hallazgos de Experiencia de Usuario (API errors)

| ID | Prioridad | Archivo | Problema |
|----|-----------|---------|----------|
| API-UX-001 | **Media** | Todos los onFailure | Todos los errores de red mostraban mensaje genérico | ✅ Corregido — `SocketTimeoutException`, `UnknownHostException`, `ConnectException` diferenciados |
| API-UX-002 | **Baja** | Todos los onResponse | Sin manejo de errores HTTP específicos (403, 422, 500) | ⏸️ Pendiente — requiere refactor de `onResponse` en todos los fragments |

#### ✅ Aspectos Correctos

- AuthInterceptor: detección de 401, refresh con authless client, X-Retry anti-bucle, force logout en fallo
- Timeouts de 30s configurados en 3 dimensiones (connect, read, write)
- Todos los servicios envueltos en `RespuestaApi<T>` con exito/mensaje/datos/errores
- Consistencia en uso de `enqueue()` async desde UI
- Logging en catch blocks con TAG consistente
- Singleton thread-safe en RetrofitClient
- Manejo de nulos en `sessionManager.getToken()` antes de agregar header
- Refresh token almacenado separadamente del access token
- Logging condicional según BuildConfig.DEBUG (arreglado)
- `forceLogout()` ahora en main thread (arreglado)
- Todos los Retrofit `Call` se cancelan en `onDestroyView()`/`onDestroy()` (arreglado)
- Errores de red diferenciados: timeout, DNS, conexión (arreglado)
- HabitosFragment con cache Room para fallback offline (arreglado)

---

## 🔗 Referencias

- **Backend API**: `https://app.epycus.es/`
- **Swagger**: `https://app.epycus.es/swagger`
- **Health**: `https://app.epycus.es/health`
- **GitHub**: `https://github.com/Chester0802/EpycusApp`
- **Backend PENDIENTES**: `C:\Users\marco\Pictures\EpycusApp\PENDIENTES.md`
- **Backend AUDITORIA_PROMPTS**: `C:\Users\marco\Pictures\EpycusApp\AUDITORIA_PROMPTS.md`
- **Backend NUEVOS_MODULOS**: `C:\Users\marco\Pictures\EpycusApp\NUEVOS_MODULOS.md`
