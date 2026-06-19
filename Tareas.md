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
| Dashboard | ✅ Parcial — fragmentos creados, datos desde API |
| Hábitos | ✅ Fragmento + adaptador |
| Pomodoro | ✅ Fragmento funcional |
| IA Chat | ✅ Activity de chat implementada |
| Perfil | ✅ Fragmento funcional |
| Diario | ✅ Fragmento implementado |
| Misiones | ✅ Adaptador + repositorio |
| Testing | ❌ Sin tests implementados |
| Offline | ❌ No implementado |
| Push notifications | ❌ No implementado |

---

## 📋 Pendientes

### Android App

- [x] **P-001**: Implementar `cargarCarreras()` en `RegistroActivity.java`
- [x] **P-002 a P-007**: Agregar feedback al usuario en callbacks `onFailure` y catch blocks
- [x] **P-008**: Strings hardcodeadas movidas a `strings.xml`
- [x] **P-009**: Migración a ViewBinding completada
- [x] **P-010**: Logging agregado en todos los catch blocks
- [x] **P-011**: `MainActivity.java` marcada como código muerto con nota
- [ ] Agregar manejo de errores de red consistente (Snackbar/Toast en todos los fragments)
- [ ] Implementar refresh token automático cuando expire el JWT
- [ ] Agregar pantalla de splash
- [ ] Agregar soporte offline (Room database local)
- [ ] Agregar pull-to-refresh en fragments
- [ ] Implementar estado de carga (loading spinners) en todas las pantallas

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

---

## 🔗 Referencias

- **Backend API**: `https://app.epycus.es/`
- **Swagger**: `https://app.epycus.es/swagger`
- **Health**: `https://app.epycus.es/health`
- **GitHub**: `https://github.com/Chester0802/EpycusApp`
- **Backend PENDIENTES**: `C:\Users\marco\Pictures\EpycusApp\PENDIENTES.md`
- **Backend AUDITORIA_PROMPTS**: `C:\Users\marco\Pictures\EpycusApp\AUDITORIA_PROMPTS.md`
- **Backend NUEVOS_MODULOS**: `C:\Users\marco\Pictures\EpycusApp\NUEVOS_MODULOS.md`
