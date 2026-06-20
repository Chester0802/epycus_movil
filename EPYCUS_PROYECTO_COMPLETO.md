# EPYCUS — Documentación Completa del Proyecto (Alineado al Sílabo)

---

## 1. DATOS GENERALES DEL PROYECTO

| Campo | Valor |
|-------|-------|
| **Nombre del Proyecto** | Epycus — App de Productividad y Bienestar Universitario |
| **Tipo** | Aplicación Móvil Android (nativa Java) + Flutter (Unidad IV) |
| **Lenguaje** | Java (Android), Dart (Flutter) |
| **SDK Mínimo** | 28 (Android 9 Pie) |
| **SDK Target** | 36 (Android 16) |
| **Package Name** | `es.epycus.app` |
| **API Base URL** | `https://app.epycus.es/` |
| **Google Client ID** | `621141066064-vtm8tf4bv7bl3oubq3eesaha0205e6gr.apps.googleusercontent.com` |
| **Curso** | Desarrollo de Aplicaciones Móviles |
| **Institución** | Universidad Privada del Norte (UPN) |
| **Fecha de Inicio** | Lunes 30 de Marzo 2026 |
| **Fecha del Último Sprint** | Viernes 19 de Junio 2026 |
| **Semana Actual** | Semana 13 (al 20 de Junio 2026) |
| **Semana de Presentación Final** | Semana 16 |
| **Evaluaciones** | T1 (10%) Sem 6 · T2 (20%) Sem 10 · T3 (30%) Sem 13 · Final (40%) Sem 16 |

---

## 2. EQUIPO PARTICIPANTE

| Nombre Completo | Código | Rol |
|----------------|--------|-----|
| Castillo Azañero, Marco Antonio | N00348614 | Desarrollador |
| Díaz Collantes, Cristopher | N00345270 | Desarrollador |
| Nacarino Pingo, Gabriela de los Ángeles | N00345057 | Desarrollador |
| Sangay Estrada, Imer | N00330652 | Desarrollador |
| Guevara Ojeda, Jesús Edmundo | N00287873 | Desarrollador |

---

## 3. ALINEACIÓN CON EL SÍLABO — ESTRUCTURA POR UNIDADES

### UNIDAD I (Semanas 1–4): ENTORNO DE DESARROLLO
**Logro:** Documentar casos de campañas de marketing irresponsable, analizando malas prácticas y proponiendo soluciones éticas. Diseñar interfaces de aplicaciones móviles de impacto social aplicando estándares y metodologías.

| Semana | Saber Esencial | Estado en Proyecto |
|--------|---------------|-------------------|
| 1 | Presentación del sílabo. Aplicaciones móviles, prototipos | ✅ Prototipos: 19 layouts XML diseñados |
| 2 | Android Studio, JetPack Compose (intro) | ✅ Android Studio configurado. ❌ JetPack Compose no usado (se usó XML views) |
| 3 | XML: ConstraintLayout, RelativeLayout, LinearLayout, GridLayout | ✅ ConstraintLayout en layouts principales |
| 4 | Controles básicos, Activities, Buttons, TextInputLayout, Material Design, UX | ✅ Activities, Material Design 3, ViewBinding |

**ODS 12 en Unidad I:** Prácticas sostenibles en desarrollo de software. Transparencia y veracidad en publicidad. Marketing ético. ✅ Documentado en Anexo 1.

**Evidencia de Práctica de Campo (Semana 4):**
- [ ] Contexto de la empresa / análisis del problema
- [ ] Evaluación de restricciones realistas
- [ ] Antecedentes que justifiquen la solución
- [ ] Alcance de la propuesta
- [ ] Reglas de negocio
- [ ] Product Backlog
- [ ] **Anexo 1:** Casos de campañas de marketing irresponsable, malas prácticas, soluciones éticas. Transparencia y veracidad en publicidad. Estrategias para optimizar rendimiento y eficiencia energética.

---

### UNIDAD II (Semanas 5–7): PERSISTENCIA DE DATOS EN APLICACIONES MÓVILES
**Logro:** Desarrollar aplicaciones que almacenan datos (archivos, bases de datos locales y remotas) usando estándares, metodologías y tecnologías.

| Semana | Saber Esencial | Estado en Proyecto |
|--------|---------------|-------------------|
| 5 | Gestión de procesos. SharedPreferences. SQLite | ✅ SharedPreferences (encriptadas), SQLite (Room) |
| 6 | **Evaluación T1 (10%)** | ✅ Completada |
| 7 | Gestión de proyectos tradicionales (PMI). **Arquitectura MVVM** | ❌ MVVM no implementado. Arquitectura actual: Capas simple (UI → Repository → API/DB) |

**ODS 12 en Unidad II:** Diseño de aplicaciones con impacto social. ✅

**Evidencia de Práctica de Campo (Semana 7):**
- [ ] Definición de Sprints
- [ ] Historias de usuario y criterios de aceptación
- [ ] Requerimientos no funcionales
- [ ] Arquitectura de la aplicación
- [ ] Desarrollo de prototipos
- [ ] **Anexo 2:** Consideraciones en el diseño de aplicaciones con impacto social. Importancia de las aplicaciones promoviendo prácticas de consumo responsable. Características que incentiven comportamientos sostenibles.

---

### UNIDAD III (Semanas 8–11): SERVICIOS WEB EN APLICACIONES MÓVILES
**Logro:** Desarrollar e implementar aplicaciones que utilicen adecuadamente servicios web y consideraciones de calidad, respondiendo a restricciones realistas y a la producción y consumo responsable.

| Semana | Saber Esencial | Estado en Proyecto |
|--------|---------------|-------------------|
| 8 | Desarrollo e implementación de Web Services | ✅ Backend REST en `https://app.epycus.es/` |
| 9 | Gestión de proyectos ágiles. **Retrofit: CRUD** | ✅ Retrofit 2.11.0 con Gson. CRUD completo en hábitos, misiones, diario, perfil |
| 10 | **Evaluación T2 (20%)** | ✅ Completada |
| 11 | **Geolocalización con API Google Maps** | ❌ **NO implementada** — Sin permisos de ubicación ni lógica de mapas |

**ODS 12 en Unidad III:** Producción y consumo responsable en servicios web. ✅

**Evidencia de Práctica de Campo (Semana 11):**
- [x] Ejecución de Sprints
- [ ] **Conexión con SQLite** (✅ Room/SQLite implementado)
- [ ] **Consumo de servicios SOAP** (❌ No implementado — solo REST)
- [x] Consumo de servicios REST
- [ ] **Firma digital de la aceptación** (❌ No implementada)
- [ ] **Geolocalización** (❌ No implementada)
- [ ] **Plan de despliegue** (⚠️ Pendiente de completar)
- [x] Consideraciones de seguridad (JWT, EncryptedSharedPreferences, Network Security Config)
- [ ] **Términos y condiciones** (⚠️ Pendiente de implementar en UI)
- [ ] **Diagrama de despliegue actualizado**
- [ ] **Consideraciones de despliegue en Google Play**

---

### UNIDAD IV (Semanas 12–16): DESARROLLO CON FLUTTER
**Logro:** Desarrollar aplicaciones móviles en Flutter aplicando Material Design y UX, implementando persistencia de datos y consumiendo web services, integrando buenas prácticas de producción y consumo responsable.

| Semana | Saber Esencial | Estado en Proyecto |
|--------|---------------|-------------------|
| 12 | Introducción a Flutter — Widgets — Material Design | ❌ No iniciado |
| 13 | **Evaluación T3 (30%) — Semana actual** | ⏳ En curso — Práctica de campo |
| 14 | Diseño de interfaz con widgets. Persistencia en SQLite | ❌ Pendiente |
| 15 | WebService en Flutter con HTTP | ❌ Pendiente |
| 16 | **Evaluación Final (40%)** — Presentación y exposición | 📅 Próximo |

**ODS 12 en Unidad IV:** Eficiencia en uso de recursos y sostenibilidad. ✅

**Evidencia de Práctica de Campo (Semana 13):**
- [ ] Ejecución de Sprints finales
- [ ] Entregables según Sprint
- [ ] Informe completo y producto implementado al **100%**
- [ ] **Evidencia de inclusión de ODS 12 en el producto**
- [ ] Registro en Blackboard

---

## 4. ANÁLISIS DE BRECHAS: SÍLABO vs. PROYECTO ACTUAL

| Requisito del Sílabo | Estado | Acción Requerida |
|----------------------|--------|-----------------|
| **JetPack Compose** (Sem 2) | ❌ No usado | Proyecto usó XML views (ConstraintLayout). Documentar como decisión técnica: se priorizó compatibilidad y curva de aprendizaje del equipo |
| **Arquitectura MVVM** (Sem 7) | ❌ No implementada | Arquitectura actual: Capas simple. Pendiente refactorizar con ViewModel + LiveData/StateFlow |
| **Servicios SOAP** (Sem 8-11) | ❌ No implementado | Solo REST. Evaluar si se requiere SOAP para algún módulo |
| **Geolocalización / Google Maps** (Sem 11) | ❌ No implementado | Agregar funcionalidad de mapas para localizar recursos universitarios o puntos de estudio |
| **Firma digital** (Sem 8-11) | ❌ No implementada | Agregar firma digital en aceptación de términos y condiciones |
| **Flutter** (Unidad IV, Sems 12-16) | ❌ No iniciado | Desarrollar versión Flutter del proyecto o módulo específico en Flutter |
| **Términos y Condiciones** | ⚠️ En UI no visible | El `RegistroRequestDto` tiene `aceptoTerminos`. Agregar pantalla de T&C |
| **Plan de Despliegue** | ⚠️ Incompleto | Completar diagrama de despliegue y consideraciones Google Play |
| **Documentación de Práctica de Campo** | ⚠️ Incompleta | Falta documentar Anexo 1, Anexo 2, sprints, HU |
| **Evidencia ODS 12** | ⚠️ Parcial | Reforzar y documentar formalmente |

---

## 5. DESCRIPCIÓN DEL PROYECTO

Epycus es una aplicación móvil de **productividad y bienestar** dirigida a estudiantes universitarios. Su objetivo es ayudar a los estudiantes a organizar su vida académica y personal mediante herramientas integradas que fomentan hábitos saludables, gestión eficiente del tiempo y autoconocimiento emocional.

### Módulos Implementados (Android Nativo)

| Módulo | Funcionalidad |
|--------|--------------|
| **Autenticación** | Registro email y Google, login, recuperación de contraseña, JWT con auto-refresh, sesión encriptada |
| **Dashboard** | KPIs (hábitos/misiones pendientes, racha, nivel, XP), frase del día, personaje animado |
| **Hábitos** | CRUD de hábitos, estado diario (Completado/Fallado/Pendiente), filtro por categoría, rachas, XP |
| **Misiones** | CRUD de tareas, prioridades (Alta/Media/Baja), completar con XP |
| **Pomodoro** | Temporizador 25/5/15 min, pausa/reanudación, ciclo, consejos, sesiones en backend |
| **Diario + Ánimo** | 5 estados de ánimo, notas diarias, historial, pregunta guía, integración con IA |
| **Gamificación** | XP, niveles, rachas, personajes por carrera/género/nivel, logros |
| **IA — Edy** | Chat conversacional con IA, historial por conversación, sugerencias, contexto de bienestar |
| **Perfil** | Datos personales, galería de personajes, logros, estadísticas, tema claro/oscuro |
| **Bienestar** | Resumen, alertas, pausas activas, frase motivacional |
| **Administración** | Login admin, CRUD usuarios, gestión de suscripciones, CRUD frases |
| **Temas** | Claro "Kawaii" (rosado) y Oscuro "Solo Leveling" (púrpura/azul) |

### Módulos Pendientes (para completar según sílabo)

| Módulo | Prioridad | Notas |
|--------|-----------|-------|
| Geolocalización/Mapas | Alta (Sem 11) | Google Maps API |
| Servicios SOAP | Media | Integración con backend SOAP si existe |
| Firma Digital | Media | Para aceptación de términos |
| Flutter (versión o módulo) | Alta (Unidad IV) | Versión del proyecto en Flutter |
| Términos y Condiciones | Alta | Pantalla de T&C con checkbox |

---

## 6. ODS 12 — PRODUCCIÓN Y CONSUMO RESPONSABLES

### Cumplimiento en el Proyecto

| Dimensión ODS 12 | Implementación en Epycus | Evidencia |
|-----------------|-------------------------|-----------|
| **12.2** Uso eficiente de recursos naturales | Caché local (Room) reduce peticiones de red → menor consumo de datos y batería | `AppDatabase.java` — 4 tablas de caché offline |
| **12.5** Reducción de generación de desechos | Digitalización: reemplaza agendas físicas, papel, libretas | Módulos de hábitos, misiones, diario digital |
| **12.6** Prácticas sostenibles en empresas | Arquitectura eficiente: singleton patterns, timeouts de red, logging selectivo | `RetrofitClient.java`, `AuthInterceptor.java` |
| **12.8** Educación para desarrollo sostenible | App educativa que fomenta organización, disciplina y bienestar en estudiantes universitarios | Enfoque en productividad académica |
| **12.a** Fortalecimiento científica y tecnológica | Uso de tecnologías modernas: Room, Retrofit, JWT, Material 3, Glide | `build.gradle.kts` — 22 dependencias |

### Anexo 1 — Marketing Ético y Transparencia
- **No hay** publicidad ni tracking de terceros en la app
- **No hay**收集 de datos innecesarios
- Datos mínimos requeridos para funcionamiento (nombre, email, carrera)
- Consentimiento explícito en registro (`aceptoTerminos`)
- Transparencia en el uso de datos personales

### Anexo 2 — Diseño con Impacto Social
- Fomenta hábitos de estudio y organización → mejora rendimiento académico
- Monitoreo de salud mental (estado de ánimo) → bienestar emocional
- Gamificación como motivación positiva → compromiso a largo plazo
- IA conversacional (Edy) como apoyo emocional y académico → accesibilidad
- Temas visuales adaptativos (claro/oscuro) → accesibilidad visual

---

## 7. ARQUITECTURA

### 7.1 Arquitectura Actual (Android Nativo)

```
UI Layer (Activities/Fragments)
    ↓ Llamadas directas con Callbacks anónimos de Retrofit
Repository Layer (5 repositorios)
    ↓
    ├── Remote: API REST (Retrofit + OkHttp + Gson + AuthInterceptor)
    │             └── https://app.epycus.es/ (13 servicios API)
    └── Local: Room Database (SQLite)
                  ├── usuarios (UsuarioDao)
                  ├── habitos (HabitoDao)
                  ├── progresos (ProgresoDao)
                  └── cache (CacheDao)
↓
Utilities: SessionManager (EncryptedSharedPreferences)
           ThemeManager (SharedPreferences)
           NetworkUtils (Manejo de errores HTTP)
```

**Patrón empleado:** Capas con Repository Pattern
**Patrón requerido por sílabo (Sem 7):** MVVM (Pendiente de refactorizar)

### 7.2 Diagrama de Paquetes

```
es.epycus.app
├── api/                      (14 servicios Retrofit + RetrofitClient + AuthInterceptor)
│   ├── ApiAuthService.java
│   ├── ApiAdminService.java
│   ├── ApiBienestarService.java
│   ├── ApiDashboardService.java
│   ├── ApiDiarioService.java
│   ├── ApiEstadoAnimoService.java
│   ├── ApiGamificacionService.java
│   ├── ApiHabitosService.java
│   ├── ApiIaService.java
│   ├── ApiMisionesService.java
│   ├── ApiPerfilService.java
│   ├── ApiPomodoroService.java
│   ├── ApiProgresoService.java
│   ├── AuthInterceptor.java
│   └── RetrofitClient.java
├── data/local/
│   ├── AppDatabase.java      (Room DB v1: epycus_cache)
│   ├── entity/
│   │   ├── UsuarioEntity.java
│   │   ├── HabitoEntity.java
│   │   ├── ProgresoEntity.java
│   │   └── CacheEntity.java
│   └── dao/
│       ├── UsuarioDao.java
│       ├── HabitoDao.java
│       ├── ProgresoDao.java
│       └── CacheDao.java
├── model/
│   ├── RespuestaApi.java     (Wrapper genérico <T>)
│   ├── entidades/
│   │   ├── AuthResponse.java
│   │   ├── Carrera.java
│   │   ├── Habito.java
│   │   ├── Nivel.java
│   │   ├── ProgresoUsuario.java
│   │   └── Usuario.java
│   └── dto/
│       ├── ChatRequest.java / ChatResponse.java
│       ├── CompletarRegistroGoogleDto.java
│       ├── DashboardResponse.java
│       ├── GamificacionResponse.java
│       ├── GoogleAuthDto.java
│       ├── HabitoHoyDto.java
│       ├── LoginDto.java
│       ├── MisionDto.java
│       ├── PausaActivaDto.java
│       ├── PerfilResponse.java
│       ├── PreguntaGuiaResponse.java
│       ├── RecomendacionPausaDto.java
│       ├── RecuperarContrasenaDto.java
│       ├── RefreshDto.java
│       ├── RegistroRequestDto.java
│       └── RestablecerContrasenaDto.java
├── repository/
│   ├── AuthRepository.java
│   ├── DiarioRepository.java
│   ├── HabitosRepository.java
│   ├── MisionesRepository.java
│   └── PomodoroRepository.java
├── ui/
│   ├── splash/SplashActivity.java
│   ├── auth/LoginActivity.java, RegistroActivity.java
│   ├── home/InicioFragment.java, DashboardActivity.java
│   ├── habitos/HabitosFragment.java
│   ├── misiones/MisionesFragment.java
│   ├── diario/DiarioFragment.java
│   ├── perfil/PerfilFragment.java
│   ├── pomodoro/PomodoroFragment.java
│   ├── ia/IaChatActivity.java
│   ├── adapters/HabitoHoyAdapter.java, MensajeChatAdapter.java, MisionAdapter.java
│   └── MainContainerActivity.java
└── util/
    ├── NetworkUtils.java
    ├── SessionManager.java
    └── ThemeManager.java
```

### 7.3 Tecnologías y Dependencias

| Dependencia | Versión | Propósito | Usada según sílabo |
|------------|---------|-----------|-------------------|
| `appcompat` | 1.7.1 | Compatibilidad | ✅ |
| `material` | 1.14.0 | Material Design 3 | ✅ Sem 4 |
| `constraintlayout` | 2.2.1 | Layouts XML | ✅ Sem 3 |
| `retrofit` + `retrofit-gson` | 2.11.0 | Cliente REST + JSON | ✅ Sem 9 |
| `okhttp` + `logging-interceptor` | 4.12.0 | Cliente HTTP | ✅ Sem 9 |
| `gson` | 2.11.0 | Parseo JSON | ✅ Sem 9 |
| `room-runtime` + `room-compiler` | 2.6.1 | SQLite (Room ORM) | ✅ Sem 5 |
| `security-crypto` | 1.1.0 | SharedPreferences encriptadas | ✅ Sem 5 |
| `play-services-auth` | 21.3.0 | Google Sign-In | ✅ |
| `glide` | 4.16.0 | Carga de imágenes | ✅ |
| `swiperefreshlayout` | 1.1.0 | Pull-to-refresh | ✅ |
| `lifecycle-viewmodel` + `livedata` | 2.8.7 | **NO USADO** — Pendiente MVVM | ❌ Sem 7 |
| `navigation-fragment` + `navigation-ui` | 2.8.8 | **NO USADO** | ❌ |
| `junit` | 4.13.2 | Tests unitarios | ⚠️ Sin implementar |
| `espresso-core` | 3.7.0 | Tests de UI | ⚠️ Sin implementar |

**Dependencias Flutter (pendientes):**
- Flutter SDK
- `http` o `dio` para WebService (Sem 15)
- `sqflite` para SQLite (Sem 14)
- `google_maps_flutter` para geolocalización

---

## 8. SERVICIOS REST — API COMPLETA

Base URL: `https://app.epycus.es/`

### 8.1 ApiAuthService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| POST | `api/auth/login` | `LoginDto` → `RespuestaApi<AuthResponse>` |
| POST | `api/auth/refresh` | `RefreshDto` → `RespuestaApi<AuthResponse>` |
| POST | `api/auth/logout` | — → `RespuestaApi<Void>` |
| POST | `api/auth/registro` | `RegistroRequestDto` → `RespuestaApi<AuthResponse>` |
| GET | `api/auth/verificar-correo` | `@Query("token")` → `RespuestaApi<Object>` |
| POST | `api/auth/recuperar-contrasena` | `RecuperarContrasenaDto` → `RespuestaApi<Object>` |
| POST | `api/auth/restablecer-contrasena` | `RestablecerContrasenaDto` → `RespuestaApi<Object>` |
| POST | `api/auth/google` | `GoogleAuthDto` → `RespuestaApi<AuthResponse>` |
| POST | `api/auth/completar-registro-google` | `CompletarRegistroGoogleDto` → `RespuestaApi<AuthResponse>` |
| GET | `api/auth/carreras` | — → `RespuestaApi<List<Carrera>>` |

### 8.2 ApiAdminService
| Método | Endpoint | Response |
|--------|----------|----------|
| POST | `api/admin/login` | `RespuestaApi<Object>` |
| GET | `api/admin/usuarios` | `RespuestaApi<Object>` |
| GET | `api/admin/usuarios/{id}` | `RespuestaApi<Object>` |
| POST | `api/admin/usuarios/{usuarioId}/suscripcion/activar` | `RespuestaApi<Object>` |
| POST | `api/admin/usuarios/{usuarioId}/suscripcion/desactivar` | `RespuestaApi<Object>` |
| GET | `api/admin/frases` | `RespuestaApi<Object>` |
| POST | `api/admin/frases` | `RespuestaApi<Object>` |
| DELETE | `api/admin/frases/{id}` | `RespuestaApi<Object>` |

### 8.3 ApiBienestarService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| GET | `api/bienestar/resumen` | — → `RespuestaApi<Object>` |
| GET | `api/bienestar/alertas` | — → `RespuestaApi<Object>` |
| GET | `api/bienestar/frase` | — → `RespuestaApi<Object>` |
| GET | `api/bienestar/estado-hoy` | — → `RespuestaApi<Object>` |
| GET | `api/bienestar/historial-animo` | `@Query("dias")` → `RespuestaApi<Object>` |
| GET | `api/bienestar/habitos-pendientes` | — → `RespuestaApi<Object>` |
| GET | `api/bienestar/misiones-pendientes` | — → `RespuestaApi<Object>` |
| POST | `api/bienestar/pausa-activa` | `PausaActivaDto` → `RespuestaApi<RecomendacionPausaDto>` |

### 8.4 ApiDashboardService
| Método | Endpoint | Response |
|--------|----------|----------|
| GET | `api/dashboard/resumen` | `RespuestaApi<DashboardResponse>` |
| GET | `api/dashboard/frase-del-dia` | `RespuestaApi<Object>` |

### 8.5 ApiDiarioService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| GET | `api/diario/hoy` | — → `RespuestaApi<Object>` |
| GET | `api/diario/fecha` | `@Query("fecha")` → `RespuestaApi<Object>` |
| GET | `api/diario/mes` | `@Query("anio,mes")` → `RespuestaApi<Object>` |
| POST | `api/diario` | `Object` → `RespuestaApi<Object>` |
| PUT | `api/diario/{fecha}` | `Object` → `RespuestaApi<Object>` |
| GET | `api/diario/racha` | — → `RespuestaApi<Object>` |
| GET | `api/diario/promedio-mes` | `@Query("anio,mes")` → `RespuestaApi<Object>` |
| GET | `api/diario/pregunta-guia` | — → `RespuestaApi<PreguntaGuiaResponse>` |

### 8.6 ApiEstadoAnimoService
| Método | Endpoint | Response |
|--------|----------|----------|
| POST | `api/estado-animo` | `RespuestaApi<Object>` |
| GET | `api/estado-animo/historial` | `RespuestaApi<Object>` |

### 8.7 ApiGamificacionService
| Método | Endpoint | Response |
|--------|----------|----------|
| GET | `api/gamificacion/mi-progreso` | `RespuestaApi<GamificacionResponse>` |
| GET | `api/gamificacion/logros` | `RespuestaApi<Object>` |

### 8.8 ApiHabitosService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| GET | `api/habitos` | — → `RespuestaApi<Object>` |
| GET | `api/habitos/hoy` | — → `RespuestaApi<List<HabitoHoyDto>>` |
| POST | `api/habitos/{id}/completar` | — → `RespuestaApi<Object>` |
| POST | `api/habitos/{id}/fallar` | — → `RespuestaApi<Object>` |
| GET | `api/habitos/{id}/semana` | — → `RespuestaApi<Object>` |
| GET | `api/habitos/{id}` | — → `RespuestaApi<Object>` |
| POST | `api/habitos` | `Object` → `RespuestaApi<Object>` |
| PUT | `api/habitos/{id}` | `Object` → `RespuestaApi<Object>` |
| DELETE | `api/habitos/{id}` | — → `RespuestaApi<Object>` |
| GET | `api/habitos/dashboard` | — → `RespuestaApi<Object>` |
| GET | `api/habitos/categorias` | — → `RespuestaApi<Object>` |

### 8.9 ApiIaService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| POST | `api/ia/chat` | `ChatRequest` → `RespuestaApi<ChatResponse>` |
| GET | `api/ia/historial` | `@Query("conversacionId")` → `RespuestaApi<Object>` |
| GET | `api/ia/conversaciones` | — → `RespuestaApi<Object>` |
| GET | `api/ia/sugerencias` | — → `RespuestaApi<Object>` |
| GET | `api/ia/contexto-bienestar` | — → `RespuestaApi<Object>` |
| POST | `api/ia/feedback` | `Object` → `RespuestaApi<Object>` |
| GET | `api/ia/mensajes-hoy` | — → `RespuestaApi<Object>` |

### 8.10 ApiMisionesService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| GET | `api/misiones` | — → `RespuestaApi<List<MisionDto>>` |
| GET | `api/misiones/{id}` | — → `RespuestaApi<MisionDto>` |
| POST | `api/misiones` | `Object` → `RespuestaApi<Object>` |
| PUT | `api/misiones/{id}` | `Object` → `RespuestaApi<Object>` |
| DELETE | `api/misiones/{id}` | — → `RespuestaApi<Object>` |
| POST | `api/misiones/{id}/completar` | — → `RespuestaApi<Object>` |
| POST | `api/misiones/{id}/estado` | `Object` → `RespuestaApi<Object>` |
| GET | `api/misiones/categorias` | — → `RespuestaApi<Object>` |

### 8.11 ApiPerfilService
| Método | Endpoint | Response |
|--------|----------|----------|
| GET | `api/perfil` | `RespuestaApi<PerfilResponse>` |
| PUT | `api/perfil` | `RespuestaApi<Object>` |
| PUT | `api/perfil/cambiar-contrasena` | `RespuestaApi<Object>` |
| PUT | `api/perfil/personaje` | `RespuestaApi<Object>` |
| PUT | `api/perfil/tema` | `RespuestaApi<Object>` |
| GET | `api/perfil/personajes` | `RespuestaApi<Object>` |
| GET | `api/perfil/logros` | `RespuestaApi<Object>` |

### 8.12 ApiPomodoroService
| Método | Endpoint | Request → Response |
|--------|----------|-------------------|
| POST | `api/pomodoro/iniciar` | `Object` → `RespuestaApi<Object>` |
| POST | `api/pomodoro/{sesionId}/ciclo-completado` | `Object` → `RespuestaApi<Object>` |
| POST | `api/pomodoro/{sesionId}/finalizar` | `Object` → `RespuestaApi<Object>` |
| POST | `api/pomodoro/{sesionId}/cancelar` | — → `RespuestaApi<Object>` |
| GET | `api/pomodoro/configuracion` | — → `RespuestaApi<Object>` |
| PUT | `api/pomodoro/configuracion` | `Object` → `RespuestaApi<Object>` |
| GET | `api/pomodoro/tip-aleatorio` | — → `RespuestaApi<Object>` |

### 8.13 ApiProgresoService
| Método | Endpoint | Response |
|--------|----------|----------|
| GET | `api/progreso` | `RespuestaApi<ProgresoUsuario>` |
| GET | `api/progreso/logros` | `RespuestaApi<Object>` |
| GET | `api/progreso/historial-animo` | `RespuestaApi<Object>` |

---

## 9. MODELOS DE DOMINIO (ENTIDADES)

### AuthResponse
| Campo | Tipo | Descripción |
|-------|------|-------------|
| token | String | JWT token |
| refreshToken | String | Token de refresco |
| mensaje | String | Mensaje de respuesta |

### Carrera (Carrera universitaria)
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | int | ID |
| nombre | String | Nombre |
| area | String | Área académica |
| codigo | String | Código |
| estaActiva | boolean | Activo |

### Habito
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | int | ID |
| nombre | String | Nombre |
| descripcion | String | Descripción |
| frecuencia | String | Frecuencia |
| conPomodoro | boolean | Vinculado con Pomodoro |
| rachaActual | int | Racha actual |
| rachaMaxima | int | Racha máxima |
| estaActivo | boolean | Activo |
| fechaCreacion | String | Fecha de creación |
| categoriaId | int | ID de categoría |
| recordatorioHora | String | Hora de recordatorio |

### Nivel
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | int | ID |
| numero | int | Número de nivel |
| titulo | String | Título |
| xpRequerido | int | XP requerido |
| descripcion | String | Descripción |

### ProgresoUsuario
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | int | ID |
| xpTotal | int | XP total acumulado |
| rachaActual | int | Racha actual |
| rachaMaxima | int | Racha máxima |
| fechaUltimaActividad | String | Última actividad |
| fechaInicioRacha | String | Inicio de racha |
| diaDeGraciaUsado | boolean | Día de gracia usado |
| productividadDiaria | double | Productividad |
| nivelActualId | int | Nivel actual |

### Usuario
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | int | ID |
| codigoUnico | String | Código único |
| nombre | String | Nombre |
| correoElectronico | String | Email |
| fechaNacimiento | String | Fecha de nacimiento |
| genero | String | Género |
| correoVerificado | boolean | Correo verificado |
| estaActivo | boolean | Activo |
| fechaRegistro | String | Fecha de registro |
| ultimoAcceso | String | Último acceso |
| googleId | String | Google ID |
| fotoGoogleUrl | String | URL foto Google |
| rolId | int | ID de rol |
| carreraId | int | ID de carrera |

### RespuestaApi\<T\> (Wrapper genérico)
- `exito` (boolean), `mensaje` (String), `datos` (T), `errores` (List<String>)

---

## 10. DTOs COMPLETOS

| DTO | Campos |
|-----|--------|
| `ChatRequest` | mensaje, conversacionId |
| `ChatResponse` | respuesta, conversacionId |
| `CompletarRegistroGoogleDto` | nombre, correoElectronico, fechaNacimiento, genero, carreraId, aceptoTerminos, googleId, fotoGoogleUrl |
| `DashboardResponse` | kpis { habitosPendientes, misionesPendientes }, frase { frase, autor } |
| `GamificacionResponse` | xpTotal, nivel, titulo, rachaActual, xpParaSiguienteNivel, porcentajeProgreso, imagenPersonaje |
| `GoogleAuthDto` | googleId, correo, nombre, fotoUrl |
| `HabitoHoyDto` | id, nombre, estadoHoy (Completado/Fallado/Pendiente), xpPotencial, categoria. Der: isCompletado(), isFallado(), isPendiente() |
| `LoginDto` | correo, contrasena |
| `MisionDto` | id, nombre, descripcion, nombreCurso, fechaLimite, prioridad, estado, xpOtorgado, fechaCreacion, categoriaId. Der: isCompletada() |
| `PausaActivaDto` | ciclosCompletados |
| `PerfilResponse` | perfil { nombre, correo, codigoUnico, genero, carreraNombre, nivelActual, xpTotal, rachaActual, rachaMaxima, fechaRegistro }, imagenPersonaje |
| `PreguntaGuiaResponse` | pregunta |
| `RecomendacionPausaDto` | tipo, duracionSegundos, descripcion, icono |
| `RecuperarContrasenaDto` | correo |
| `RefreshDto` | refreshToken |
| `RegistroRequestDto` | nombre, correoElectronico, contrasena, confirmarContrasena, fechaNacimiento, genero, carreraId, aceptoTerminos |
| `RestablecerContrasenaDto` | token, nuevaContrasena |

---

## 11. BASE DE DATOS LOCAL (ROOM / SQLite)

**Database:** `epycus_cache` (versión 1)

### Tabla: `usuarios` (UsuarioEntity)
| Columna | Tipo | Notas |
|---------|------|-------|
| id | INT PK | ID usuario |
| nombre | TEXT | Nombre |
| correo_electronico | TEXT | Email |
| token | TEXT | JWT |
| refresh_token | TEXT | Refresh token |
| fecha_registro | TEXT | Fecha registro |

### Tabla: `habitos` (HabitoEntity)
| Columna | Tipo | Notas |
|---------|------|-------|
| id | INT PK | ID hábito |
| nombre | TEXT | Nombre |
| descripcion | TEXT | Descripción |
| frecuencia | TEXT | Frecuencia |
| racha_actual | INT | Racha |
| racha_maxima | INT | Racha máxima |
| esta_activo | BOOLEAN | Activo |
| categoria_id | INT | Categoría |

### Tabla: `progresos` (ProgresoEntity)
| Columna | Tipo | Notas |
|---------|------|-------|
| id | INT PK | ID |
| xp_total | INT | XP total |
| racha_actual | INT | Racha |
| racha_maxima | INT | Racha máxima |
| nivel_actual_id | INT | Nivel |
| fecha_ultima_actividad | TEXT | Última actividad |

### Tabla: `cache` (CacheEntity)
| Columna | Tipo | Notas |
|---------|------|-------|
| key | TEXT PK | Clave |
| value | TEXT | Valor JSON |

### DAOs
- `UsuarioDao`: insert(), getById(), deleteAll()
- `HabitoDao`: insertAll(), getActivos(), deleteAll()
- `ProgresoDao`: insert(), getById(), deleteAll()
- `CacheDao`: insert(), getValue(), delete(), deleteAll()

---

## 12. REPOSITORIOS

| Repositorio | APIs | DAOs | Métodos clave |
|------------|------|------|--------------|
| `AuthRepository` | ApiAuthService | UsuarioDao | login, loginGoogle, registro, refresh, obtenerCarreras, logout, saveSession, isLoggedIn, clearSession, cacheUsuario |
| `DiarioRepository` | ApiDiarioService | CacheDao | hoy, porFecha, crear, racha, preguntaGuia, cacheJson |
| `HabitosRepository` | ApiHabitosService | CacheDao, HabitoDao | hoy, completar, fallar, listar, crear, actualizar, eliminar, categorias, cacheHabitos |
| `MisionesRepository` | ApiMisionesService | CacheDao | listar, completar, crear, eliminar, categorias, cacheJson |
| `PomodoroRepository` | ApiPomodoroService | CacheDao | iniciar, cicloCompletado, finalizar, cancelar, configuracion, tipAleatorio |

---

## 13. INTERFAZ DE USUARIO

### 13.1 Actividades

| Actividad | Propósito | Estado |
|-----------|-----------|--------|
| `SplashActivity` | Pantalla de carga (1.5s), ruteo según sesión | ✅ |
| `LoginActivity` | Login email + Google + recuperación contraseña | ✅ |
| `RegistroActivity` | Registro completo (DatePicker, spinners, términos) | ✅ |
| `MainContainerActivity` | Bottom Navigation (5 tabs) + overlay Pomodoro | ✅ |
| `IaChatActivity` | Chat con IA "Edy" a pantalla completa | ✅ |
| `MainActivity` | Código muerto — redirige a MainContainer | ❌ Eliminar |
| `DashboardActivity` | Mínima — probablemente no usada | ❌ Eliminar |

### 13.2 Fragmentos

| Fragmento | Pestaña | Propósito |
|-----------|---------|-----------|
| `InicioFragment` | 1 — Inicio | Dashboard con KPIs, frase, personaje, acceso Pomodoro |
| `HabitosFragment` | 2 — Hábitos | Lista hábitos del día con swipe, filtro, CRUD |
| `MisionesFragment` | 3 — Misiones | Lista misiones con prioridad, checkbox, CRUD |
| `DiarioFragment` | 4 — Diario | Estado de ánimo (5), notas, historial, pregunta guía, acceso IA |
| `PerfilFragment` | 5 — Perfil | Datos, personaje, logros, stats, ajustes, logout |
| `PomodoroFragment` | Overlay | Temporizador 25/5/15 min, ciclo, consejos |

### 13.3 Navegación

```
SplashActivity
 ├── (sin sesión) → LoginActivity
 │    ├── [login] → MainContainerActivity
 │    ├── [registro] → RegistroActivity
 │    └── [Google] → MainContainerActivity
 └── (con sesión) → MainContainerActivity
      ├── InicioFragment
      │    └── [card Pomodoro] → PomodoroFragment
      ├── HabitosFragment
      ├── MisionesFragment
      ├── DiarioFragment
      │    └── [botón "Habla con Edy"] → IaChatActivity
      └── PerfilFragment
           └── [logout] → LoginActivity
```

### 13.4 Layouts (19 archivos XML)

| Layout | Propósito |
|--------|-----------|
| `activity_splash.xml` | Splash |
| `activity_login.xml` | Login |
| `activity_registro.xml` | Registro |
| `activity_main.xml` | (código muerto) |
| `activity_main_container.xml` | Bottom nav |
| `activity_dashboard.xml` | (no usado) |
| `activity_ia_chat.xml` | Chat IA |
| `fragment_inicio.xml` | Dashboard |
| `fragment_habitos.xml` | Hábitos |
| `fragment_misiones.xml` | Misiones |
| `fragment_diario.xml` | Diario/ánimo |
| `fragment_perfil.xml` | Perfil |
| `fragment_pomodoro.xml` | Pomodoro |
| `item_habito_hoy.xml` | Item hábito |
| `item_mision.xml` | Item misión |
| `item_chat_edy.xml` | Burbuja IA |
| `item_chat_usuario.xml` | Burbuja usuario |
| `dialog_nuevo_habito.xml` | Diálogo hábito |
| `dialog_cambiar_contrasena.xml` | Diálogo contraseña |

---

## 14. SEGURIDAD

### 14.1 Autenticación
- **JWT**: `Authorization: Bearer <token>` via AuthInterceptor (OkHttp Interceptor)
- **Auto-refresh**: 401 → intenta `POST /api/auth/refresh` → si falla, logout y redirige a Login
- **Google Sign-In**: `play-services-auth` 21.3.0 con GOOGLE_CLIENT_ID en BuildConfig

### 14.2 Almacenamiento Seguro
- **EncryptedSharedPreferences**: AES256_GCM master key, AES256_SIV key encryption, AES256_GCM value encryption. Fallback a SharedPreferences planas.
- **Tokens**: jwt_token, refresh_token, user_id, user_name, user_email

### 14.3 Red
- **Network Security Config**: cleartext deshabilitado para `app.epycus.es`
- **Timeouts OkHttp**: 30s connect/read/write
- **Logging HTTP**: HEADERS (debug), NONE (release)

### 14.4 Firma Digital (Pendiente según sílabo)
- No implementada. Requerida para aceptación de términos y condiciones.

---

## 15. TEMAS VISUALES

### Tema Claro — "Kawaii"
- **Background**: `#fef5ff` | **Accent**: `#ff6b9d` | **Secondary**: `#c77dff`
- Base: `Theme.Material3.Light.NoActionBar`

### Tema Oscuro — "Solo Leveling"
- **Background**: `#0a0e1a` | **Accent**: `#8b5cf6` | **Secondary**: `#6366f1`
- Base: `Theme.Material3.Dark.NoActionBar`

### 19 Atributos Personalizados `ep*`
| Atributo | Propósito |
|----------|-----------|
| `epBgPrimary`, `epBgSecondary`, `epBgElevated` | Fondos |
| `epSurface`, `epSurfaceBorder` | Superficies |
| `epTextPrimary`, `epTextSecondary`, `epTextTertiary` | Textos |
| `epAccent`, `epAccentLight`, `epAccentSecondary` | Acentos |
| `epSuccess`, `epWarning`, `epError`, `epInfo` | Semánticos |
| `epRoundedSm`(2dp), `epRoundedMd`(4dp), `epRoundedLg`(8dp), `epRoundedXl`(12dp) | Bordes |

---

## 16. RECURSOS ESTÁTICOS

### Personajes (assets/personajes/)
```
generico/masculino/placeholder.png
generico/femenino/placeholder.png
ing-sistemas/masculino/n1.png, n2.png, n3.png
ing-sistemas/femenino/n1.png, n2.png
medicina/masculino/n1.png, n2.png
medicina/femenino/n1.png, n2.png
[administracion, arquitectura, comunicaciones, contabilidad, derecho,
 educacion, enfermeria, ing-civil, ing-industrial, psicologia] → VACÍO
```

### Drawables (38)
Iconos: home, habits, mision, profile, pomodoro, moods(5), filter, add, edit, delete, check, back, send, sun, moon, streak, level, lock, lightbulb, journal
Backgrounds: card_rounded, accent_gradient, accent_circle, timer_ring, timer_circle, stat_card
Chat: chat_message_sent, chat_message_received

---

## 17. SEGUIMIENTO DEL SÍLABO — CHECKLIST DE ENTREGABLES

### Evidencia de Práctica de Campo — Semana 4 (Unidad I)
- [ ] Contexto de la empresa / análisis del problema
- [ ] Evaluación de restricciones realistas
- [ ] Antecedentes que justifiquen la solución propuesta
- [ ] Alcance de la propuesta
- [ ] Reglas de negocio
- [ ] Product Backlog
- [ ] **Anexo 1**: Casos de campañas de marketing irresponsable. Malas prácticas. Soluciones éticas. Transparencia y veracidad en publicidad. Estrategias de eficiencia energética.

### Evidencia de Práctica de Campo — Semana 7 (Unidad II)
- [ ] Definición de Sprints
- [ ] Historias de usuario y criterios de aceptación
- [ ] Requerimientos no funcionales
- [ ] Arquitectura de la aplicación
- [ ] Desarrollo de prototipos
- [ ] **Anexo 2**: Consideraciones en diseño de aplicaciones con impacto social. Importancia de apps promoviendo consumo responsable. Características que incentiven comportamientos sostenibles.

### Evidencia de Práctica de Campo — Semana 11 (Unidad III)
- [x] Ejecución de Sprints
- [ ] Conexión con SQLite (✅ Room/SQLite)
- [ ] Consumo de servicios SOAP
- [x] Consumo de servicios REST (Retrofit)
- [ ] Firma digital de la aceptación
- [ ] Geolocalización
- [ ] Plan de despliegue
- [x] Consideraciones de seguridad (JWT, Encriptación)
- [ ] Términos y condiciones
- [ ] Diagrama de despliegue actualizado
- [ ] Consideraciones de despliegue en Google Play

### Evidencia de Práctica de Campo — Semana 13 (Unidad IV) — ACTUAL
- [ ] Ejecución de Sprints finales
- [ ] Entregables según Sprint
- [ ] Informe completo
- [ ] Producto implementado al 100%
- [ ] **Evidencia ODS 12** en el producto
- [ ] Registro en Blackboard

---

## 18. PLAN DE DESPLIEGUE

### Entornos
| Entorno | URL / Ubicación |
|---------|----------------|
| Desarrollo | Android Studio + Emulador / Dispositivo físico |
| Backend | `https://app.epycus.es/` |
| Base de Datos | SQLite local (Room) + PostgreSQL remoto (backend) |

### Build
- Sistema: Gradle (AGP 9.0.1)
- Tipos: `debug` / `release`
- ProGuard: `proguard-android-optimize.txt`
- SDK: compile 36, minSdk 28, targetSdk 36

### Distribución
- APK/AAB: `./gradlew assembleRelease`
- Google Play Store: Pendiente de publicación (requiere keystore, listing, screenshots, política de privacidad)
- Firma: Keystore release (no incluido en repositorio)

### Diagrama de Despliegue (propuesto)
```
[Dispositivo Android]
    │ App: Epycus (APK/AAB)
    │ SQLite: epycus_cache (Room)
    │
    ├── HTTPS ──→ [Servidor Web]
    │               │ Nginx/Apache
    │               │
    │               ├── API REST (Retrofit) ──→ [Backend]
    │               │                            │ Java/Spring Boot
    │               │                            │ PostgreSQL
    │               │
    │               ├── SOAP Service (pendiente)
    │               │
    │               └── Google Maps API (pendiente)
    │
    └── Google Sign-In ──→ [Google Identity Platform]
```

---

## 19. OBSERVACIONES TÉCNICAS Y DEUDA TÉCNICA

### Código muerto por eliminar
- `MainActivity.java`: Solo redirige a MainContainerActivity
- `DashboardActivity.java`: Actividad de bienvenida no utilizada
- Dependencias `navigation`, `viewmodel`, `livedata` no usadas

### Deuda técnica
- ❌ **MVVM no implementado** (requerido Sem 7). Actual: capas simples sin ViewModel
- ❌ **Sin manejo de estados** Loading/Error/Empty sistemático en UI
- ❌ **Room con `allowMainThreadQueries=true`** — debería usarse background thread
- ❌ **Sin pruebas unitarias ni de UI** (dependencias declaradas pero sin tests)
- ❌ **Parseo manual con JsonObject/JsonArray** en lugar de modelos tipados
- ❌ **Sin inyección de dependencias**

### Assets incompletos
- Personajes: solo `generico`, `ing-sistemas` y `medicina` tienen assets. Faltan 10 carreras.

---

## 20. CRONOGRAMA DETALLADO vs. SÍLABO

| Semana | Fecha (Lun) | Unidad | Evento | Entregable |
|--------|------------|--------|--------|------------|
| 1 | 23 Mar | I | Inicio semestre | — |
| 2 | 30 Mar | I | **Inicio proyecto** | — |
| 3 | 06 Abr | I | Prototipos XML | — |
| 4 | 13 Abr | I | Material Design | **Entrega: Contexto, Alcance, Backlog, Anexo 1** |
| 5 | 20 Abr | II | SQLite / SharedPref | — |
| 6 | 27 Abr | II | **T1 (10%)** | Evaluación |
| 7 | 04 May | II | MVVM | **Entrega: Sprints, HU, Arquitectura, Prototipos, Anexo 2** |
| 8 | 11 May | III | Web Services | — |
| 9 | 18 May | III | Retrofit CRUD | — |
| 10 | 25 May | III | **T2 (20%)** | Evaluación |
| 11 | 01 Jun | III | Geolocalización | **Entrega: Sprints, SQLite, SOAP, REST, Geolocalización, Despliegue, Seguridad** |
| 12 | 08 Jun | IV | Flutter intro | — |
| 13 | 15 Jun | IV | **T3 (30%) — ACTUAL** | **Entrega: Sprints finales, Informe final, Producto 100%, ODS 12** |
| 14 | 22 Jun | IV | Flutter widgets + SQLite | — |
| 15 | 29 Jun | IV | Flutter WebService | — |
| 16 | 06 Jul | IV | **Evaluación Final (40%)** | **Presentación y exposición** |

---

## 21. SPRINT 4 — IMPLEMENTACIÓN FINAL (Semana 13)

### Objetivos
1. Finalizar todas las funcionalidades del proyecto Android
2. Completar documentación pendiente (Anexo 1, Anexo 2, HU, sprints)
3. Implementar ODS 12 evidence
4. Iniciar versión Flutter (Unidad IV)
5. Preparar presentación final para Semana 16

### Pendientes por Prioridad

| Prioridad | Tarea | Módulo |
|-----------|-------|--------|
| 🔴 Alta | Documentar Anexo 1 (marketing ético) | Documentación |
| 🔴 Alta | Documentar Anexo 2 (apps con impacto social) | Documentación |
| 🔴 Alta | Completar Product Backlog, HU, Sprints | Documentación |
| 🔴 Alta | Evidencia ODS 12 en producto | Documentación |
| 🟡 Media | Agregar Términos y Condiciones en UI | RegistroActivity |
| 🟡 Media | Eliminar código muerto (MainActivity, DashboardActivity) | Refactor |
| 🟡 Media | Completar assets de personajes (10 carreras restantes) | Assets |
| 🟢 Baja | Geolocalización (Google Maps API) | Nuevo módulo |
| 🟢 Baja | Iniciar versión Flutter | Unidad IV |

---

## 22. BIBLIOGRAFÍA DEL SÍLABO

| # | Autor | Título | Año | URL |
|---|-------|--------|-----|-----|
| 1 | Moreno, Valeriano | Creación de aplicaciones con Android | 2021 | https://elibro.bibliotecaupn.elogim.com/es/lc/upnorte/titulos/222661 |
| 2 | Nolasco Valenzuela, J. | Desarrollo de aplicaciones con Android | 2019 | https://digitalia.bibliotecaupn.elogim.com/a/110142 |
| 3 | Abuchar Porras, A. | Metodologías ágiles para el desarrollo de software | 2023 | https://digitalia.bibliotecaupn.elogim.com/a/128149 |

---

Este documento está alineado al sílabo del curso "Desarrollo de Aplicaciones Móviles" (UPN) y contiene TODA la información del proyecto Epycus para que una IA genere los artefactos solicitados sin salirse del contexto del proyecto.
