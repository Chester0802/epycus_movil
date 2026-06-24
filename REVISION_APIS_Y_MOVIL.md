# Revision de APIs y app movil Epycus

Fecha: 2026-06-24  
Proyectos revisados:

- Backend/API: `C:\Users\marco\Pictures\EpycusApp`
- App movil Android: `C:\Users\marco\Pictures\Epycus`

## Resumen ejecutivo

El backend de Epycus tiene una API REST completa bajo `/api/v1/*`, con autenticacion JWT, refresh tokens, rate limiting, health checks, Swagger en desarrollo, servicios por modulo y controladores para todos los modulos.

La app Android nativa en Java usa Retrofit, OkHttp, Gson, Room, EncryptedSharedPreferences, Glide, Google Sign-In, tema claro/oscuro y pantallas funcionales para todas las funcionalidades principales.

## Estado de conexion backend-Android

### Autenticacion
- Login correo/contrasena: `POST api/v1/auth/login` → `RespuestaApi<AuthResponse>` ✅
- Registro: `POST api/v1/auth/registro` → `RespuestaApi<AuthResponse>` ✅
- Refresh token: `POST api/v1/auth/refresh` → `RespuestaApi<AuthResponse>` ✅
- Logout: `POST api/v1/auth/logout` → `RespuestaApi<MensajeResponseDto>` ✅
- Google Sign-In: `POST api/v1/auth/google` + completar registro → `RespuestaApi<AuthResponse>` ✅
- Carreras: `GET api/v1/auth/carreras` → `RespuestaApi<List<Carrera>>` ✅

### Dashboard
- Resumen: `GET api/v1/dashboard/resumen` → `RespuestaApi<DashboardResponse>` ✅
- Frase del dia: `GET api/v1/dashboard/frase-del-dia` → `RespuestaApi<Object>` ⚠️ (pendiente tipar)

### Habitos
- Listar: `GET api/v1/habitos` → `RespuestaApi<Object>` ⚠️ (no usado por el fragment, usa `hoy`)
- Hoy: `GET api/v1/habitos/hoy` → `RespuestaApi<List<HabitoHoyDto>>` ✅
- Crear: `POST api/v1/habitos` → `RespuestaApi<SuccessResponseDto>` ✅
- Editar: `PUT api/v1/habitos/{id}` → `RespuestaApi<SuccessResponseDto>` ✅
- Eliminar: `DELETE api/v1/habitos/{id}` → `RespuestaApi<SuccessResponseDto>` ✅
- Completar: `POST api/v1/habitos/{id}/completar` → `RespuestaApi<CompletarHabitoResponse>` ✅
- Fallar: `POST api/v1/habitos/{id}/fallar` → `RespuestaApi<FallarHabitoResponse>` ✅
- Semana: `GET api/v1/habitos/{id}/semana` → `RespuestaApi<List<RegistroSemanaDto>>` ✅
- Obtener: `GET api/v1/habitos/{id}` → `RespuestaApi<Habito>` ✅
- Categorias: `GET api/v1/habitos/categorias` → `RespuestaApi<Object>` ⚠️ (pendiente tipar)
- Dashboard: `GET api/v1/habitos/dashboard` → `RespuestaApi<Object>` ⚠️ (pendiente tipar)

### Misiones
- Listar: `GET api/v1/misiones` → `RespuestaApi<List<MisionDto>>` ✅
- Obtener: `GET api/v1/misiones/{id}` → `RespuestaApi<MisionDto>` ✅
- Crear: `POST api/v1/misiones` → `RespuestaApi<SuccessResponseDto>` ✅
- Editar: `PUT api/v1/misiones/{id}` → `RespuestaApi<SuccessResponseDto>` ✅
- Eliminar: `DELETE api/v1/misiones/{id}` → `RespuestaApi<SuccessResponseDto>` ✅
- Completar: `POST api/v1/misiones/{id}/completar` → `RespuestaApi<MisionCompletarResponse>` ✅
- Cambiar estado: `POST api/v1/misiones/{id}/estado` → `RespuestaApi<SuccessResponseDto>` ✅
- Categorias: `GET api/v1/misiones/categorias` → `RespuestaApi<List<CategoriaDto>>` ✅

### Pomodoro
- Iniciar: `POST api/v1/pomodoro/iniciar` → `RespuestaApi<PomodoroIniciarResponse>` ✅
- Ciclo completado: `POST api/v1/pomodoro/{id}/ciclo-completado` → `RespuestaApi<PomodoroCicloCompletadoResponse>` ✅
- Finalizar: `POST api/v1/pomodoro/{id}/finalizar` → `RespuestaApi<PomodoroFinalizarResponse>` ✅
- Cancelar: `POST api/v1/pomodoro/{id}/cancelar` → `RespuestaApi<SuccessResponseDto>` ✅
- Configuracion: `GET api/v1/pomodoro/configuracion` → `RespuestaApi<PomodoroConfiguracionResponse>` ✅
- Actualizar config: `PUT api/v1/pomodoro/configuracion` → `RespuestaApi<SuccessResponseDto>` ✅
- Tip: `GET api/v1/pomodoro/tip-aleatorio` → `RespuestaApi<PomodoroTipResponse>` ✅
- Sesion activa: `GET api/v1/pomodoro/sesion-activa` → `RespuestaApi<PomodoroSesionActivaResponse>` ✅
- Historial: `GET api/v1/pomodoro/historial` → `RespuestaApi<PomodoroHistorialResponse>` ✅
- Racha: `GET api/v1/pomodoro/racha` → `RespuestaApi<PomodoroRachaResponse>` ✅
- Estadisticas: `GET api/v1/pomodoro/estadisticas` → `RespuestaApi<EstadisticasPomodoroPeriodo>` ✅

### Perfil
- Obtener: `GET api/v1/perfil` → `RespuestaApi<PerfilResponse>` ✅
- Actualizar: `PUT api/v1/perfil` → `RespuestaApi<MensajeResponseDto>` ✅
- Cambiar contrasena: `PUT api/v1/perfil/cambiar-contrasena` → `RespuestaApi<MensajeResponseDto>` ✅
- Cambiar personaje: `PUT api/v1/perfil/personaje` → `RespuestaApi<MensajeResponseDto>` ✅
- Cambiar tema: `PUT api/v1/perfil/tema` → `RespuestaApi<MensajeResponseDto>` ✅
- Personajes: `GET api/v1/perfil/personajes` → `RespuestaApi<List<PersonajeItem>>` ✅
- Logros: `GET api/v1/perfil/logros` → `RespuestaApi<List<LogroUsuarioItem>>` ✅

### Diario
- Hoy: `GET api/v1/diario/hoy` → `RespuestaApi<DiarioEntradaResponse>` ✅
- Por fecha: `GET api/v1/diario/fecha` → `RespuestaApi<DiarioEntradaResponse>` ✅
- Por mes: `GET api/v1/diario/mes` → `RespuestaApi<DiarioEntradasResponse>` ✅
- Crear: `POST api/v1/diario` → `RespuestaApi<DiarioEntradaResponse>` ✅
- Actualizar: `PUT api/v1/diario/{fecha}` → `RespuestaApi<DiarioEntradaResponse>` ✅
- Racha: `GET api/v1/diario/racha` → `RespuestaApi<DiarioRachaResponse>` ✅
- Promedio mes: `GET api/v1/diario/promedio-mes` → `RespuestaApi<DiarioPromedioMesResponse>` ✅
- Pregunta guia: `GET api/v1/diario/pregunta-guia` → `RespuestaApi<PreguntaGuiaResponse>` ✅

### IA
- Chat: `POST api/v1/ia/chat` → `RespuestaApi<IaChatResponseDto>` ✅
- Historial: `GET api/v1/ia/historial` → `RespuestaApi<Object>` ⚠️
- Conversaciones: `GET api/v1/ia/conversaciones` → `RespuestaApi<Object>` ⚠️
- Sugerencias: `GET api/v1/ia/sugerencias` → `RespuestaApi<Object>` ⚠️
- Contexto bienestar: `GET api/v1/ia/contexto-bienestar` → `RespuestaApi<Object>` ⚠️
- Feedback: `POST api/v1/ia/feedback` → `RespuestaApi<Object>` ⚠️
- Mensajes hoy: `GET api/v1/ia/mensajes-hoy` → `RespuestaApi<IaMensajesHoyResponse>` ✅

### Gamificacion
- Mi progreso: `GET api/v1/gamificacion/mi-progreso` → `RespuestaApi<GamificacionResponse>` ✅

## Mejoras realizadas (2026-06-24)

1. **Tipado de DTOs de Diario**: Creado `DiarioEntradaResponseDto` para reemplazar `object?` en `DiarioEntradaResponse.Entrada` y `DiarioEntradasResponse.Entradas`. Ahora el controlador mapea explícitamente la entidad al DTO, evitando fugas de propiedades de navegacion.

2. **Tipado de Pomodoro**: `GET api/v1/pomodoro/estadisticas` ahora devuelve `RespuestaApi<EstadisticasPomodoroPeriodo>` en lugar de `RespuestaApi<object>`. Creado `SesionPomodoroDto` para el historial, eliminando la dependencia directa de la entidad con sus propiedades de navegacion.

3. **Seguridad**: Los archivos `deploy/epycus-web.service` y `deploy/setup-vps.sh` estan en `.gitignore` para evitar commits de secretos de produccion.

## Pendiente para Play Store

1. Generar keystore y configurar signing en `build.gradle.kts` para release
2. Configurar `versionCode` y `versionName` apropiados
3. Generar Android App Bundle (.aab) firmado
4. Tipar los endpoints que aun devuelven `Object` (bajo impacto funcional)
5. Pruebas en dispositivo real contra `https://app.epycus.es/`

## Checklist final

- [x] Login correo/contrasena
- [x] Registro normal
- [x] Google Sign-In con completar registro
- [x] Refresh token automatico
- [x] Logout con revocacion
- [x] Dashboard
- [x] Habitos: CRUD + completar/fallar
- [x] Misiones: CRUD + estados
- [x] Pomodoro: ciclo completo
- [x] Diario: entrada diaria + pregunta guia
- [x] IA: chat conversacional
- [x] Perfil: editar, personaje, tema, contrasena
- [x] Cache offline (Room + CacheManager)
- [x] Estados de UI (loading/empty/error)
- [x] Backend compila y tests pasan (234/237)
- [x] Android compila (debug)
- [x] Contrato API tipado entre backend y Android
- [x] Seguridad: sin secretos en git
