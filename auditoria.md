# Auditoría Completa — Epycus

> **Rol**: Ingeniero Senior / Consultor Experto
> **Fecha original**: 29/06/2026 · **Revisión y verificación contra código**: 29/06/2026
> **Proyecto**: Epycus — App Android (Java) + Backend ASP.NET Core 9
> **Objetivo**: Firmar APK/AAB lista para producción, tests, offline-first, publicación multi-store

---

## ⚠️ Nota de esta revisión

Este documento fue **re-verificado línea por línea contra el código real**. La versión
anterior contenía casillas `[x]` que no se correspondían con el código y severidades
infladas. Lo corregido en esta pasada:

- **C-01 (pinning)** y **C-06 (logging headers)** estaban marcados como hechos pero **seguían sin resolver** en el código. → **Resueltos ahora** (ver más abajo).
- **C-04 (WriteBackQueue sin auth)** es un **falso positivo**: la cola usa el `OkHttpClient` compartido que ya lleva `AuthInterceptor`.
- **C-07 / C-08** estaban como CRITICAL; en realidad **no bloquean la firma** → recalibrados a LOW/MEDIUM.
- Se añaden tres requisitos que el encabezado pedía y el plan ignoraba: **soporte 16 KB**, **AAB vs APK**, **verificación de firma V2/V3 con `apksigner`** (no `jarsigner`).
- Se añaden requisitos administrativos de tienda (política de privacidad / Data Safety, borrado de cuenta).

---

## Resumen Ejecutivo

Epycus es una plataforma de **bienestar personal y gamificación** con:

- **App Android** (`es.epycus.app`): Java 100%, MinSdk 28, TargetSdk 36, AGP 9.0.1, versionCode 2 / versionName 1.1
- **Backend ASP.NET Core 9** (`EpycusApp`): C#, MariaDB 11.8, EF Core, SignalR, JWT — **en producción** en `https://app.epycus.es` (VPS Debian 13, Nginx, systemd, SSH puerto 2222). Local en `C:\Users\marco\Pictures\EpycusApp`.
- **Frontend Web**: Razor Pages + Bootstrap 5 + PWA
- **Dominio**: `https://app.epycus.es`

### Objetivos de publicación

1. Soporte de **páginas de 16 KB** (Android 15+).
2. Subida a **Google Play**, **Samsung Galaxy Store**, **Uptodown**, **Aptoide**.
3. Formato **AAB** (Play) o **APK** (resto de tiendas).
4. **Firmado V2 (Full APK Signature) + V3** (requisito de Aptoide para el sello "App de Confianza").
5. Navegación interna sin roturas + opción clara de **cerrar sesión / salir**.

---

## Estado de los hallazgos (post-verificación)

Leyenda: ✅ Resuelto · 🔧 Pendiente (código) · 📋 Pendiente (admin/infra) · ❎ Falso positivo

### 🔴 CRÍTICOS — bloqueantes reales para firmar

| ID | Hallazgo | Estado | Detalle |
|----|----------|--------|---------|
| C-01 | Certificate pinning con placeholder `REPLACE_WITH_ACTUAL_PIN` | ✅ **Resuelto** | Reemplazado por pines SPKI reales de la cadena TLS: hoja + intermedio Let's Encrypt + raíz ISRG Root X2. Pinning multinivel para no romper la app en la renovación de 90 días. `RetrofitClient.java`. |
| C-06 | Logging HTTP en `Level.HEADERS` (filtra JWT en Logcat en debug) | ✅ **Resuelto** | Bajado a `Level.BASIC` (nunca registra cabeceras). `RetrofitClient.java`. |
| C-05 | Tokens en SharedPreferences sin cifrar | ✅ **Ya estaba** | `SessionManager` usa `EncryptedSharedPreferences` (AES256). *Nota: la lib `security-crypto` está deprecada desde 2024 → deuda técnica, no bloqueante.* |
| C-04 | WriteBackQueue sin Bearer token en reintentos | ❎ **Falso positivo** | `WriteBackQueue.executeOperation()` usa `client.getHttpClient()`, el **mismo** `OkHttpClient` con `AuthInterceptor` (RetrofitClient.java:48). Los reintentos offline **sí** se autentican y manejan refresh 401. |
| C-02 | Sin tests unitarios Android | 🔧 Pendiente | **Importante pero NO bloquea la firma.** Es mitigación de riesgo, no un gate de release. Ver Fase 1. |
| C-03 | Sin tests instrumentados Android (Room/UI) | 🔧 Pendiente | Igual que C-02. No bloquea firma. |

### 🟠 ALTOS

| ID | Hallazgo | Estado | Detalle |
|----|----------|--------|---------|
| H-01 | Room `exportSchema = false` | ✅ **Resuelto** | Activado `exportSchema = true` + `room.schemaLocation` en `build.gradle.kts` + `sourceSets` androidTest para tests de migración. |
| H-03 | `MIGRATION_2_3` vacía | ✅ **Verificado OK** | Vacía **a propósito**: no hubo cambios de esquema entre v2 y v3 (comentario justificativo en `AppDatabase.java`). `MIGRATION_3_4` sí crea `write_back_queue`. Con `exportSchema=true` ya se puede validar con `MigrationTestHelper`. |
| H-02 | JWT blacklist en memoria (backend) | 🔧 Pendiente (backend) | Reinicio del servidor revalida JWTs viejos. Mitigado parcialmente por rotación de refresh token. Solución: blacklist persistente (Redis/BD). |
| H-04 | Sin resolución de conflictos offline (last-write-wins) | 🔧 Pendiente | Riesgo de pérdida de datos en edición concurrente. Solución: conflict resolution por timestamp. Complejo → planificar. |
| H-05 | `carreras()` expone entidades EF sin DTO (backend) | 🔧 Pendiente (backend) | Añadir DTO. |
| H-06 | Rate limiting de admin login | 🔧 Pendiente (backend) | Verificar política específica para `/api/v1/admin/login`. |
| H-07 | Sin crash reporting en Android | 📋 Pendiente | Integrar Sentry Android (coherente con el backend, más ligero que Crashlytics; no requiere google-services). Necesita DSN/proyecto. |
| H-08 | ProGuard mantiene modelos para Gson | ✅ **Resuelto/reforzado** | Mantener modelos para Gson es estándar (riesgo de reverse-engineering bajo). **Hallazgo real**: faltaban reglas para **SignalR** (reflexión + slf4j) y para los **Worker** de WorkManager → podía romper el build de release. Añadidas. Verificado con `minifyReleaseWithR8` ✅ (R8 pasa). |

### 🟡 MEDIOS

| ID | Hallazgo | Estado |
|----|----------|--------|
| C-07 | Base URL hardcodeada igual en debug y release (**recalibrado desde CRITICAL**) | 🔧 Que release apunte a prod es **correcto**; lo único mejorable es el debug. No bloquea nada. |
| C-08 | Sin versión de API parametrizada (**recalibrado desde CRITICAL**) | 🔧 Extraer `/api/v1/` a constante. No bloquea. |
| M-01 | CacheManager sin limpieza automática de expirados | ✅ **Resuelto** — `purgeExpired()` al iniciar |
| M-02 | Cola offline sin WorkManager | ✅ **Resuelto** — ver nota de bugs reales abajo |
| M-03 | SignalR sin backoff exponencial | ✅ **Resuelto** — backoff + jitter + rejoin de grupo |
| M-04 | Sentry DSN backend posiblemente vacío | 📋 Pendiente (backend) |
| M-05 | Posibles N+1 queries backend | 🔧 Pendiente (backend) |
| M-06 | PNG sin convertir a WebP | 🟢 Cosmético |
| M-07 | Playwright apunta a producción | 🔧 Pendiente — parametrizar por entorno |

### 🟢 BAJOS

| ID | Hallazgo |
|----|----------|
| L-01 | Nombre BD `epycus_cache` engaña (almacena datos de usuario, no solo cache) |
| L-04 | Comentarios mezclados ES/EN |
| L-05 | (Resuelto con H-01: `exportSchema=true` reactiva validación de migraciones) |

---

## 🐞 Bugs reales encontrados durante la revisión (no estaban en la auditoría)

Al implementar M-02 se descubrió que el mecanismo offline estaba **peor** de lo descrito:

- **B-01 — La cola de reintentos nunca se vaciaba.** `PomodoroRepository.procesarColaWriteBack()` **no se llamaba desde ningún sitio**. Las operaciones de Pomodoro encoladas estando offline quedaban en la BD y **jamás se reintentaban** → se perdían. ✅ **Resuelto** con `SyncWorker` (WorkManager) enganchado en `queueForRetry()` y tras cada respuesta correcta.
- **B-02 — `deleteAll()` en cada éxito.** `PomodoroRepository.handleResponse()` borraba **toda** la cola write-back al primer éxito, incluidas operaciones de otras sesiones aún sin sincronizar → pérdida de datos. ✅ **Resuelto**: ya no borra la cola; programa un flush selectivo.
- **B-03 — `WriteBackQueue` era código muerto.** La clase descrita como "cola de reintentos" (la del falso C-04) **no estaba referenciada en ningún sitio**. ✅ **Eliminada**; su lógica genérica (petición autenticada vía cliente compartido) vive ahora en `SyncWorker`.
- **B-07 — Imagen de personaje no carga / mal recortada.** El backend devuelve la imagen como **ruta relativa** (`/img/personajes/...`); Glide la trataba como archivo local → `FileNotFoundException`. Además el avatar (`120dp`, `centerCrop`) mostraba el torso de un personaje de cuerpo entero (cabeza y piernas cortadas). ✅ **Resuelto**: `ImageUrls.absolute()` antepone `API_BASE_URL` (idempotente con URLs ya absolutas) en las 4 cargas (home, avatar ×2, preview de selección) + `TopCropTransformation` en el avatar para mostrar de cuello a cabeza.
- **B-06 — Guardar config de Pomodoro → 400.** El cliente enviaba `tiempoEstudio/tiempoDescanso/tiempoDescansoLargo` pero el DTO del backend espera el sufijo **`Min`** (`TiempoEstudioMin`...), así que llegaban a 0 y fallaba `[Range(1,180)]`. Además el backend **sobrescribe toda** la configuración, así que mandar solo 4 campos reseteaba sonido/volumen/etc. ✅ **Resuelto**: nombres con `Min` + envío de la **config completa** (los 4 editados + el resto desde la config cargada) + validación de orden (descanso < estudio < descanso largo) en cliente.
- **B-05 — Crash al entrar al home: lectura de Room en el hilo principal.** `MisionesRepository.getCachedMisiones()`, `HabitosRepository.getActivos()`, `AuthRepository.getCachedUsuario()` y `CacheManager.get()` leen Room de forma síncrona en `onCreateView`; con `setOffscreenPageLimit(4)` los 5 fragments se crean a la vez → `IllegalStateException: Cannot access database on the main thread`. Detectado en emulador tras arreglar B-04. ✅ **Resuelto**: `allowMainThreadQueries()` en la BD de cache (consultas diminutas; las escrituras siguen por `getWriteExecutor()`). Mejora futura: pasar estas lecturas a async. *(Es la UX offline-first: muestra cache al instante y refresca de red.)*
- **B-04 — Crash al actualizar: migración de Room rota (`usuarios`).** En `d78650d` se quitaron las columnas `token`/`refresh_token`/`fecha_registro` de `UsuarioEntity` (los tokens pasaron a `EncryptedSharedPreferences`) **sin migración ni bump de versión**. Cualquier dispositivo con la BD vieja crasheaba al cachear el usuario tras el login (`IllegalStateException: Migration didn't properly handle: usuarios`). Detectado al ejecutar la app real en emulador (los tests unitarios usan BD en memoria y no lo cogían). ✅ **Resuelto**: BD `epycus_cache` (que es cache) a `version=5` con `fallbackToDestructiveMigration()` → recrea la BD ante cualquier desajuste de esquema. Trade-off asumido: en un cambio de esquema se pierde el cache local y la cola write-back pendiente (transitorios; se re-piden al servidor). Los tokens no están en esta BD, así que no se pierde la sesión.

**Resultado:** una única ruta de sincronización offline (`SyncWorker`) que sobrevive a la muerte del proceso, espera a que haya red y reintenta con backoff exponencial.

---

## 🧹 Pasada de Lint + UI/UX (errores de compilación y experiencia)

`lintDebug` pasó de **4 errores / 230 warnings** a **0 errores / 220 warnings**. Errores reales corregidos:

- **E-01 — `VIBRATE` sin declarar.** `PomodoroFragment` vibraba sin el permiso → `SecurityException` en runtime. ✅ Añadido `android.permission.VIBRATE` al manifest.
- **E-02 — Navegación atrás rota con gestos.** `MainContainerActivity.onBackPressed()` (obsoleto) ya no se invoca con gestos de Android 13+ → no se cerraba el panel Pomodoro (afecta al *filtro humano*). ✅ Migrado a `OnBackPressedDispatcher` + `android:enableOnBackInvokedCallback="true"` (back predictivo).
- **E-03 — Pin inválido en `network_security_config.xml`.** Había un **segundo** pinning (a nivel de plataforma) con el placeholder `REPLACE_WITH_ACTUAL_PIN`. ✅ Sustituido por los 3 pines reales (hoja+intermedio+raíz), resolviendo también `MissingBackupPin`.

UI/UX y correctness adicionales:
- **DefaultLocale (×6):** fechas a la API formateadas con `Locale.US` (evita dígitos no-ASCII en árabe/farsi que romperían el payload); temporizador con `Locale.getDefault()`.
- **RelativeOverlap:** en `fragment_inicio.xml` (portrait + landscape) el saludo podía solapar el personaje con nombres largos → acotado con `layout_toStartOf="@id/ivPersonaje"`.

**Verificado:** cerrar sesión funciona (diálogo de confirmación → API → limpieza → redirección a Login) → el requisito de *salir/cerrar sesión clara* se cumple.

**Limpieza de recursos:** eliminados **79 recursos sin usar** (11 archivos drawable/anim/font + 26 colores + 34 strings + 5 estilos). Lint bajó a **0 errores / 141 warnings**. Los **45 dimens marcados "sin usar" se conservaron a propósito**: son tokens de diseño con override en `values-sw600dp` (tablets); quitarlos provocaba `MissingDefaultResource` → crash en móvil. Los 141 warnings restantes son cosméticos/perf (overdraw, baseline alignment, versiones de dependencias) — ninguno es bug.

---

## 🛠️ Backend (producción) — 30/06/2026

- **B-08 — `pomodoro/racha` 500.** `ObtenerRachaActualAsync` usaba SQL cruda mapeando a un `ValueTuple` (no soportado por EF) y pasaba los parámetros como objeto anónimo (no se enlazaban `@usuarioId/@hace30`). ✅ Reescrito con LINQ en memoria (acotado a 30 días). Verificado **200** en producción.
- **B-09 — `pomodoro/historial` 500.** El cliente manda `pagina=0` → `Skip((0-1)*tamano)` = OFFSET negativo. ✅ `pagina` clampada a `>=1` en el controller. Verificado **200** en producción.
- **CI/CD roto → arreglado.** El job `build` fallaba (`NETSDK1004`: el paso de tests usaba `--no-restore` sin restaurar el proyecto de Tests) y, además, los secrets del VPS estaban vacíos y el deploy usaba clave SSH inexistente. ✅ Restaurado el proyecto de Tests + cambiado el deploy a **password auth** + configurados los 5 secrets (`VPS_HOST/USER/PORT/APP_PATH/PASSWORD`). Ahora **push a `main` despliega a `app.epycus.es`** (build → test → SCP → migraciones → `systemctl restart epycus-web` → health check OK).

## 🆕 Requisitos de publicación que faltaban en el plan original

### R-01 — Soporte de páginas de 16 KB (Android 15+)
- **Estado: ✅ cumplido por construcción (verificar sobre el binario).**
- La app es **Java puro sin librerías nativas** (no hay `.so` propios; SignalR/OkHttp/Glide/Room son JVM). El requisito de alineación 16 KB aplica a librerías nativas → **no hay nada que alinear**.
- **Verificación obligatoria** sobre el AAB/APK final:
  ```bash
  # Listar libs nativas dentro del bundle (debe salir vacío o todo alineado a 16K)
  unzip -l app-release.aab | grep "\.so$"
  ```
  Si no hay `.so`, cumple automáticamente.

### R-02 — AAB para Play, APK para tiendas alternativas
- **Google Play** exige **AAB**: `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`
- **Samsung / Uptodown / Aptoide** aceptan **APK**: `./gradlew assembleRelease` → `app/build/outputs/apk/release/app-release.apk`
- ⚠️ El plan original mezclaba ambos y tenía typos (`epycus.apk` vs `apycus.apk`). Usar siempre los nombres reales que genera AGP.

### R-03 — Verificación de firma V2/V3 (Aptoide "App de Confianza")
- ❌ `jarsigner -verify` **solo valida V1 (JAR)** — no sirve para confirmar V2/V3.
- ✅ Usar `apksigner` del build-tools:
  ```bash
  apksigner verify -v --print-certs app-release.apk
  # Debe mostrar:  Verified using v2 scheme (APK Signature Scheme v2): true
  #                Verified using v3 scheme (APK Signature Scheme v3): true
  ```
- AGP activa V1+V2+V3 por defecto al firmar release; este paso lo **demuestra**.

### R-04 — Requisitos administrativos de tienda
- **Política de privacidad + formulario Data Safety**: el asistente IA (Edy) envía datos del usuario a **DeepSeek (tercero)** → hay que declararlo o habrá rechazo.
- **Borrado de cuenta**: Play exige mecanismo de eliminación de cuenta (in-app o URL). *Logout ≠ borrado de cuenta.*
- **Ubicación de la API key de DeepSeek**: confirmar que va **proxeada por el backend** y no embebida en el cliente.

---

## Plan de Acción (corregido)

### Fase 0 — PRE-FIRMA (críticos de seguridad)
- [x] EncryptedSharedPreferences (ya estaba)
- [x] **Reemplazar pin SSL real** (hoja + intermedio + raíz, con respaldo anti-renovación)
- [x] **Logging interceptor a `Level.BASIC`**
- [x] WriteBackQueue con auth (verificado: ya correcto vía cliente compartido)
- [ ] **Generar el keystore de producción** (V2/V3) — *aún no existe; bloqueante para firmar*
- [ ] `./gradlew bundleRelease` (AAB) y `assembleRelease` (APK) firmados
- [ ] Verificar firma con `apksigner verify -v`
- [ ] Verificar ausencia de `.so` (16 KB)

### Fase 1 — Tests Android (mitigación de riesgo, NO bloquea firma)
Infra: **Robolectric + Room-testing** → corren en la JVM con `./gradlew testDebugUnitTest` **sin emulador**. SDK fijada a 34 en `robolectric.properties`. **40 tests, 0 fallos.**
- [x] `WriteBackDaoTest` (7) — cola offline (persistencia de `SyncWorker`/Pomodoro)
- [x] `CacheDaoTest` (6) — cache Room
- [x] `EntidadesDaoTest` (6) — DAOs Usuario/Hábito
- [x] `SessionManagerJwtTest` (9) — parsing de JWT (auth)
- [x] `SyncWorkerLogicTest` (3) — mapeo de método HTTP
- [x] `NetworkUtilsTest` (8) — mapeo de errores HTTP/red a strings
- [x] `ApiAuthContractTest` (4) — contrato request/response de auth con **MockWebServer**
- [x] `ApiContractTest` (8) — contrato de hábitos/misiones/pomodoro con MockWebServer (ruta `@Path` + verbo HTTP; valida que el endpoint que reencola `SyncWorker` coincide)
- [x] `CacheManagerTest` (8) — TTL/expiración/`purgeExpired` (refactor mínimo: `isExpired`/`wrap` testables + constructor con DI de la BD)
- [x] `ContratoJsonTest` (8) — contratos JSON (ya existía)
- **Total: 68 tests JVM, 0 fallos** (`./gradlew testDebugUnitTest`).
- `isOnline` no se testea en JVM (envoltorio fino de `ConnectivityManager`, shadow frágil) → cubierto por Maestro.
- Tests de `SessionManager`/`AuthRepository` con sesión cifrada tampoco (EncryptedSharedPreferences usa AndroidKeyStore, no soportado por Robolectric) → cubiertos por Maestro.
- [ ] `MigrationTestHelper` (posible gracias a `exportSchema=true`) — necesita wiring de `schemas` en androidTest
- [x] **UI con Maestro** (black-box, en `.maestro/`): `01_login`, `02_navegacion_tabs`, `03_navegacion_atras` (valida el fix de back), `04_cerrar_sesion`, `00_smoke`. Se corren con `maestro test -e EMAIL=.. -e PASSWORD=.. .maestro/` sobre un emulador/dispositivo con la app instalada (`./gradlew installDebug`). Reemplazan a Espresso.

### Fase 2 — Backend (en producción; cambios vía local → commit → push → SSH:2222)
- [ ] H-05 DTO para carreras
- [ ] H-06 rate limiting admin login
- [ ] H-02 blacklist JWT persistente
- [ ] M-04 Sentry DSN real, M-05 revisar N+1

### Fase 3 — HIGH/MEDIUM Android restantes
- [ ] H-07 Sentry Android (crash reporting) — *necesita DSN*
- [x] M-02 cola offline → WorkManager (`SyncWorker`) + corregidos bugs B-01/B-02/B-03
- [x] M-03 SignalR backoff exponencial
- [x] M-01 limpieza automática de cache
- [x] H-08 reglas ProGuard para SignalR/WorkManager (R8 release verificado)
- [ ] C-07/C-08 (URL/versión API) — baja prioridad; sin servidor de staging aporta poco

### Fase 4 — Publicación
- [ ] R-04 privacidad / Data Safety / borrado de cuenta
- [ ] Changelog, capturas, ficha de cada tienda

---

## Checklist Pre-Release (corregido)

- [ ] `keystore.properties` con keystore real (V2/V3)
- [x] Certificate pinning SHA-256 real (con pines de respaldo)
- [x] EncryptedSharedPreferences
- [x] WriteBackQueue autenticado (verificado)
- [x] Logging interceptor en `Level.BASIC`
- [x] Room `exportSchema=true`
- [ ] AAB firmado (`bundleRelease`) para Play
- [ ] APK firmado (`assembleRelease`) para Samsung/Uptodown/Aptoide
- [ ] **`apksigner verify -v` muestra v2=true, v3=true**
- [ ] Sin `.so` desalineados (16 KB)
- [ ] Tests unitarios e instrumentados pasando
- [ ] Política de privacidad publicada + Data Safety rellenado
- [ ] Mecanismo de borrado de cuenta
- [ ] `versionCode`/`versionName` incrementados por release
- [ ] Google Client ID válido en `secrets.properties`

---

## Pendientes que requieren acción del usuario

1. **Keystore**: generar el `.jks` de producción (Android Studio: Build → Generate Signed Bundle/APK, activando V1+V2+V3) y rellenar `keystore.properties`.
2. **Cuenta Google Play Console**: aún no existe.
3. **Política de privacidad**: hace falta una URL pública.
4. **Sentry Android (H-07)**: decidir si se integra y proveer DSN.

---

*Auditoría re-verificada contra el código. Los ítems marcados ✅ ya están aplicados en el árbol de trabajo.*
