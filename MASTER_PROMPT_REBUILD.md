# PROMPT MAESTRO — Reconstrucción Completa UI/UX Epycus Android

> **Propósito**: Este prompt es para que cualquier IA重构 (reconstruya) desde cero la app Android de Epycus con UI/UX profesional, gamificada, conectada al 100% con el backend ASP.NET Core 9 en `https://app.epycus.es/`.

---

## 📋 INSTRUCCIONES GENERALES

### Stack tecnológico
- **Lenguaje**: Java 11 (NO Kotlin)
- **UI**: Material 3 + ViewBinding + CoordinatorLayout
- **HTTP**: Retrofit 2.11 + OkHttp 4.12 + Gson 2.11
- **Auth**: JWT Bearer + EncryptedSharedPreferences + auto-refresh via AuthInterceptor
- **Local DB**: Room 2.6.1 (solo para caché offline)
- **Imágenes**: Glide o Coil para cargar personajes PNG desde URL
- **Build**: Gradle KTS, SDK 36, minSdk 28
- **API Base**: `https://app.epycus.es/`

### Reglas de oro del UI/UX
1. **Nada pequeño**: Mínimo 48dp para botones, 20sp para títulos, 16dp para padding
2. **Nada superpuesto**: Cada elemento debe tener layout_weight, márgenes y constraints correctos
3. **Medidas perfectas**: Usar dimensiones exactas (no `wrap_content` sin margen)
4. **Responsive**: Funcionar en 360dp a 480dp de ancho (teléfonos)
5. **Game-like**: La pantalla de inicio debe mostrar el personaje como un juego (centrado, grande, con fondo decorativo)
6. **Transparencias**: Las imágenes de personajes son PNG sin fondo — mostrarlas sin recortes
7. **Feedback háptico** en botones importantes
8. **Animaciones suaves** en transiciones de fragments y cambios de estado
9. **Dark mode + Light mode** con toggle persistente (ThemeManager existente)
10. **SwipeRefreshLayout** en todas las pantallas con datos
11. **Loading skeleton** (no solo ProgressBar) en pantallas principales
12. **Empty states** con ilustraciones y botones de acción
13. **Error states** con retry button y mensajes claros

---

## 🏗️ ARQUITECTURA DE PANTALLAS

### Navegación Principal
```
SplashActivity (LAUNCHER, 1.5s, verifica sesión)
  ├── No sesión → LoginActivity
  │                  ├── Login (correo+contraseña)
  │                  ├── Google OAuth
  │                  ├── Recuperar contraseña
  │                  └── RegistroActivity
  └── Sesión activa → MainContainerActivity
                        ├── BottomAppBar + FAB central
                        ├── 5 tabs: Inicio | Hábitos | FAB(IA) | Diario | Perfil
                        └── FAB → IaChatActivity
```

### Cada pantalla debe tener:

---

## 1. SPLASH (SplashActivity)
- Logo "E" grande centrado (72dp) con glow animado
- Nombre "Epycus" en grande (28sp bold)
- Subtítulo "Tu compañero de productividad"
- ProgressBar animado abajo
- Fondo con gradiente sutil
- Duración: 1.5s mínimo, pero salir antes si ya hay sesión válida
- ✅ Backend: `AuthRepository.isLoggedIn()`

---

## 2. LOGIN (LoginActivity)
- **Top**: Toggle tema (modo oscuro/claro) en esquina superior derecha
- **Centro**: Logo "E" en círculo con fondo accent (80dp) + "Epycus" título + "Tu compañero de productividad" subtítulo
- **Formulario** (campos grandes, 52dp altura):
  - `TextInputLayout` correo (icono email)
  - `TextInputLayout` contraseña (con toggle visibilidad)
  - Link "¿Olvidaste tu contraseña?" → diálogo recuperación
- **Botón "Iniciar sesión"**: Full width, 56dp altura, accent, cornerRadius 16dp
- **Loading**: ProgressBar blanco sobre el botón (reemplaza texto)
- **Botón "Continuar con Google"**: Card style con icono Google y texto
- **Separador** (línea + "o" texto)
- **Botón "Crear cuenta"**: Outline style
- ✅ Backend: `POST /api/auth/login` → `{ token, refreshToken }`
- ✅ Backend: `POST /api/auth/google` → `{ token, refreshToken }`
- ✅ Backend: `POST /api/auth/recuperar-contrasena` → `{ correo }`

---

## 3. REGISTRO (RegistroActivity)
- **Título**: "Crear cuenta" (24sp bold)
- **Campos** (todos 52dp altura, con iconos):
  - Nombre completo (person icon)
  - Correo electrónico (email icon)
  - Contraseña (con requisitos mínimos indicados)
  - Confirmar contraseña
  - Fecha de nacimiento → **DatePickerDialog** al hacer clic (no input manual)
  - Género → Spinner personalizado con estilo Material
  - Carrera → Spinner con datos de `GET /api/auth/carreras`
  - Checkbox "Acepto términos y condiciones"
- **Botón "Registrarse"**: Full width, 56dp, accent
- **Loading**: ProgressBar sobre botón
- ✅ Backend: `POST /api/auth/registro` → `{ token, refreshToken }`
- ✅ Backend: `GET /api/auth/carreras` → `[{ id, nombre, area }]`

---

## 4. INICIO / DASHBOARD (InicioFragment) — 🎮 LA PANTALLA MÁS IMPORTANTE

### Header
- **Saludo**: "¡Hola, [Nombre]!" (22sp bold) con wave animation
- **Personaje**: Imagen PNG sin fondo del personaje del usuario. Debe mostrarse GRANDE (mín 120dp) como avatar circular con glow accent. Si no hay, mostrar iniciales.
  - La URL viene de `GET /api/gamificacion/mi-progreso` → `imagenPersonaje`
  - Las imágenes están en `https://app.epycus.es/img/personajes/[carrera]/[genero]/nivel[1-20].png`
- **Frase motivacional** del día: `GET /api/dashboard/resumen` → `frase.frase` + `frase.autor` (cursiva, color secundario)

### Stats Cards Fila (3 tarjetas iguales, 110dp altura)
Cada una con icono + número grande (24sp bold) + label (12sp):
1. **Hábitos pendientes** → `DashboardResponse.habitosPendientes` (icono: ic_habits)
2. **Racha actual** → `GamificacionResponse.rachaActual` + "días" (icono: fire/streak)
3. **Nivel** → `GamificacionResponse.nivel` → "Nv.X" (icono: star/level)

### Barra de XP (card completa)
- Texto "Tu Progreso" (16sp bold) + XP total a la derecha
- **ProgressBar horizontal** con gradiente accent, 12dp altura, border radius 6dp
- Texto "% completado para siguiente nivel"

### Personaje Principal — SECCIÓN GAME-LIKE
- **Card grande** (match_parent, min 200dp altura) con background gradient sutil
- **Centro**: Imagen del personaje (PNG sin fondo) de 140dp × 140dp con sombra
- Alrededor del personaje: elementos decorativos (estrellas, partículas flotantes)
- Debajo del personaje: Nombre del personaje + "Nivel X - [Título]" (ej: "Nivel 5 - Practicante")
- ✅ Backend: `GET /api/gamificacion/mi-progreso` → `{ xpTotal, nivel, titulo, rachaActual, xpParaSiguienteNivel, porcentajeProgreso, imagenPersonaje }`

### Acciones Rápidas (3 cards horizontales, 130dp altura)
Con MaterialCardView con elevation 3dp:
1. **Nuevo Hábito** → Navega a tab Hábitos y abre diálogo crear
2. **Misión Diaria** → Navega a tab Diario/Misiones
3. **Modo Enfoque** → Navega a PomodoroFragment
Cada una con icono grande (40dp) + texto (12sp)

### Pull-to-refresh + Loading skeleton
- ✅ Backend: `GET /api/dashboard/resumen` → `{ kpis, habitosPendientes, misionesPendientes, frase }`

---

## 5. HÁBITOS (HabitosFragment)

### Header
- Título: "Hábitos de hoy" (22sp bold)
- Subtítulo: "X hábitos hoy" con contador dinámico

### Lista de Hábitos (RecyclerView)
Cada item (72dp altura):
- **Checkbox redondo** grande (28dp) para completar con animación
- **Nombre** del hábito (15sp bold)
- **Categoría** (12sp) con color por categoría
- **XP potencial** (12sp accent, bold) "15 XP"
- **Estado**: Completado → opacidad 50% + check verde, Fallado → opacidad 50% + tachado, Pendiente → normal
- **Debounce** de 500ms en clicks
- **Swipe** izquierda para fallar 😢
- **Swipe** derecha para completar ✅
- **Long press** para editar/eliminar (bottom sheet)

### Categorías como chips scrollables horizontales (filter)
- ✅ Backend: `GET /api/habitos/categorias`
- ✅ Backend: `GET /api/habitos/hoy` → `[{ id, nombre, estadoHoy, xpPotencial, categoria }]`

### FAB: "+ Nuevo hábito" → BottomSheetDialog
Campos:
- Nombre (TextInputLayout)
- Categoría (Spinner con datos de categorías)
- Frecuencia (Spinner: Diaria, Semanal, Personalizada)
- Días de semana (si es semanal) → Chips seleccionables
- Con Pomodoro (switch)
- Botón "Crear" accent + "Cancelar"
- ✅ Backend: `POST /api/habitos` → `{ nombre, categoriaId, frecuencia, ... }`
- ✅ Backend: `POST /api/habitos/{id}/completar` → `{ xpGanado }`
- ✅ Backend: `POST /api/habitos/{id}/fallar` → `{ rachaRota }`

### Empty state: Ilustración + "¡Crea tu primer hábito!" + botón grande

---

## 6. DIARIO / MODO DIARIO (DiarioFragment)

### Pregunta Guía (card destacada)
- **Label**: "Reflexión del día" (14sp bold)
- **Texto**: Pregunta guía desde backend (16sp, cursiva, con comillas decorativas)
- Icono de pregunta decorativo
- ✅ Backend: `GET /api/diario/pregunta-guia` → `{ pregunta: string }`

### Selector de Estado de Ánimo (5 moods en horizontal, 100dp cada uno)
- **Genial** 😄 → verde (`@color/mood_genial`) → `ic_mood_genial`
- **Bien** 🙂 → azul (`@color/mood_bien`) → `ic_mood_bien`
- **Normal** 😐 → amarillo (`@color/mood_normal`) → `ic_mood_normal`
- **Cansado** 😫 → naranja (`@color/mood_cansado`) → `ic_mood_cansado`
- **Estresado** 😤 → rojo (`@color/mood_estresado`) → `ic_mood_estresado`

Cada mood: icono emoji vectorial de 32dp arriba + texto label abajo (11sp bold)
- Al seleccionar: background cambia a `bg_accent_gradient` + scale animación
- ✅ Backend: `POST /api/estado-animo` → `{ estado: "Genial"|"Bien"|"Normal"|"Cansado"|"Estresado", notas?: string }`

### Notas (TextInputLayout multi-línea, 3 líneas mín)
- Hint: "¿Qué más quieres compartir?" (opcional)
- ✅ Backend: Se envía junto con el estado de ánimo en `POST /api/estado-animo`

### Botón "Guardar estado de ánimo" — 56dp, accent, full width

### Entrada de Hoy (card)
- **Label**: "Tu entrada de hoy" (14sp bold)
- **Texto**: Muestra "Hoy te sientes: [estado]" + notas si existe
- Si no hay entrada: "Aún no has registrado tu estado de ánimo hoy."
- ✅ Backend: `GET /api/diario/hoy` → `{ estado, notas, fecha }`

### Chat con Edy (card clickable)
- Avatar "E" circular 40dp + "Habla con Edy IA" + "Tu asistente personal de bienestar"
- Al hacer clic → IaChatActivity

### Pull-to-refresh

---

## 7. PERFIL (PerfilFragment)

### Avatar Section (centrado, espaciado generoso)
- **Avatar**: Círculo de 80dp con iniciales o imagen del personaje
- **Nombre**: 22sp bold
- **Correo**: 14sp secondary
- **Carrera**: 13sp tertiary
- **Nivel badge**: Card pill con "Nivel X" en accent
- **Racha + XP**: Dos stat cards horizontales (70dp)

### Menú de opciones (cards con icono + texto + flecha >)
1. **🎭 Mis personajes** → BottomSheet con grid de personajes disponibles (con imágenes PNG) → Seleccionar cambia el personaje vía API
2. **🏆 Logros** → BottomSheet con lista de logros (desbloqueados con color, bloqueados en gris)
3. **📊 Estadísticas** → BottomSheet con stats: XP total, racha actual, racha máxima, hábitos completados, misiones completadas, sesiones pomodoro
4. **⚙️ Configuración** → BottomSheet con opciones:
   - Cambiar contraseña (3 campos: actual, nueva, confirmar)
   - Preferencias de notificaciones (futuro)
   - Acerca de Epycus (versión, descripción)
5. **🌓 Tema** → toggle modo oscuro/claro (con icono luna/sol)
6. **🚪 Cerrar sesión** → Confirmación dialog + logout + redirección a Login

### Miembro desde (texto pequeño al fondo)
- ✅ Backend: `GET /api/perfil` → `{ perfil: { nombre, correo, nivelActual, xpTotal, rachaActual, rachaMaxima, fechaRegistro, carreraNombre }, imagenPersonaje }`
- ✅ Backend: `GET /api/perfil/personajes` → `[{ id, nombre, genero, esSeleccionado, imagenPreviewUrl }]`
- ✅ Backend: `PUT /api/perfil/personaje` → `{ personajeId: int }`
- ✅ Backend: `GET /api/perfil/logros` → array de logros del usuario
- ✅ Backend: `PUT /api/perfil/cambiar-contrasena` → `{ contrasenaActual, nuevaContrasena }`

---

## 8. POMODORO (PomodoroFragment) — MODO ENFOQUE

### Header: "Pomodoro" (22sp bold)
### Estado actual: "Foco" / "Pausa" / "Pausa larga" (14sp accent)

### Timer Circular
- **Círculo grande**: 260dp × 260dp con fondo `bg_card_rounded`
- **Tiempo**: 56sp bold accent (formato MM:SS)
- **Animación**: Progress circular alrededor (opcional)

### Botón Control (circular, 72dp × 72dp)
- "Iniciar" → accent
- "Pausar" → warning
- "Reanudar" → accent
- "Iniciar pausa" → secondary

### Tip motivacional (13sp secondary, center)
- ✅ Backend: `GET /api/pomodoro/tip-aleatorio` → `{ consejo: string }`

### Stats (2 cards horizontales)
- "Ciclos: X" (completados hoy)
- "Hoy: X completados"

### Configuración (gear icon en header)
- Diálogo para cambiar: tiempo foco, tiempo pausa, pausa larga, ciclos antes pausa larga
- ✅ Backend: `GET /api/pomodoro/configuracion`
- ✅ Backend: `PUT /api/pomodoro/configuracion`

### Backend Sync (automático)
- Iniciar sesión: `POST /api/pomodoro/iniciar` → `{ id: sesionId }`
- Ciclo completado: `POST /api/pomodoro/{sesionId}/ciclo-completado`
- Finalizar: `POST /api/pomodoro/{sesionId}/finalizar`
- Cancelar: `POST /api/pomodoro/{sesionId}/cancelar`
- Stats diarias se sincronizan al completar ciclos

### State save/restore en rotación (onSaveInstanceState)

---

## 9. CHAT IA (IaChatActivity) — "EDY"

### Header
- Back button (ic_back) + Avatar "E" circular + "Edy" bold + "Online" green dot
- Toolbar con accent background o surface

### Messages Area (RecyclerView)
- **Mensajes usuario**: Burbuja accent, alineada derecha, border radius 16dp (bottomRight=4dp)
- **Mensajes Edy**: Burbuja surface/elevated, alineada izquierda, border radius 16dp (bottomLeft=4dp)
- **Loading indicator**: Tres dots animados o ProgressBar pequeño
- **Auto-scroll** al último mensaje
- **Límite**: 200 mensajes máximo en memoria

### Input Area (fija abajo)
- `EditText` con hint "Escribe un mensaje..." + border radius 24dp
- **Botón enviar**: Círculo accent 48dp con icono send
- **Enter** en teclado → enviar
- **Debounce**: No enviar mensajes vacíos o repetidos

### Mensaje de bienvenida: "¡Hola! Soy Edy, tu asistente de bienestar. ¿Cómo te sientes hoy?"
- ✅ Backend: `POST /api/ia/chat` → `{ mensaje: string, conversacionId?: string }` → `{ respuesta: string, conversacionId: string }`

---

## 10. MISONES (MisionAdapter + Integración)

> Nota: Las misiones se muestran como sección dentro de Hábitos o como un tab adicional

### Lista de Misiones (RecyclerView)
Cada item (72dp):
- **Checkbox** para completar
- **Nombre** (15sp bold)
- **Prioridad badge**: Alto (rojo) / Medio (amarillo) / Bajo (verde)
- **Fecha límite** (11sp tertiary)
- Completada → opacidad 50%

### FAB: "+ Nueva misión" → Dialog
Campos: nombre, descripción, curso, fecha límite (DatePicker), prioridad (spinner), categoría (spinner)

### Filter chips por prioridad
- ✅ Backend: `GET /api/misiones` → `[{ id, nombre, prioridad, fechaLimite, completada }]`
- ✅ Backend: `POST /api/misiones/{id}/completar`
- ✅ Backend: `DELETE /api/misiones/{id}`

---

## 11. ADAPTADORES (Mejorados)

### HabitoHoyAdapter
- ViewBinding en ViewHolder
- DiffUtil para actualizaciones eficientes
- Debounce 500ms on click (completar/fallar)
- Checkbox animado redondo personalizado
- Swipe actions (SwipeRefreshLayout + ItemTouchHelper)

### MisionAdapter  
- ViewBinding en ViewHolder
- DiffUtil
- Prioridad colorida (rojo/amarillo/verde)

### MensajeChatAdapter
- ViewBinding en ViewHolder
- 2 view types (usuario/IA)
- Límite 200 mensajes
- Auto-scroll en inserción

---

## 12. MEJORAS TRANSVERSALES

### AuthInterceptor (existente, mantener)
- ✅ Intercepta 401 → refresh con authless client → retry con X-Retry header
- ✅ Force logout en main thread si refresh falla

### RetrofitClient (existente, mantener)
- ✅ Singleton thread-safe
- ✅ 14 servicios API (NO ELIMINAR NINGUNO)
- ✅ Logging condicional (HEADERS en debug, NONE en release)
- ✅ Authless retrofit para refresh token y carreras
- ✅ Timeouts 30s

### SessionManager (existente, mantener)
- ✅ EncryptedSharedPreferences con AES256-GCM

### ThemeManager (existente, mantener)
- ✅ Toggle claro/oscuro con persistencia

### Room Offline (existente, expandir)
- Mantener entidades: UsuarioEntity, HabitoEntity, ProgresoEntity, CacheEntity
- Cachear respuestas API clave en CacheEntity (key-value) para offline
- Mostrar datos cacheados cuando no hay conexión

### Bienestar (futuro/integración)
- Endpoints disponibles: `GET /api/bienestar/resumen`, `GET /api/bienestar/alertas`, `GET /api/bienestar/pausa-activa`
- Las alertas pueden mostrarse como cards en InicioFragment
- Las pausas activas como recomendaciones en PomodoroFragment

---

## 13. RECURSOS Y ASSETS

### Crear/Usar estos drawables:
- `ic_back.xml` — flecha atrás
- `ic_send.xml` — icono enviar
- `ic_mood_genial.xml` — emoji feliz
- `ic_mood_bien.xml` — emoji bien
- `ic_mood_normal.xml` — emoji neutral
- `ic_mood_cansado.xml` — emoji cansado
- `ic_mood_estresado.xml` — emoji estresado
- `ic_lock.xml` — candado
- `ic_streak.xml` — fuego/racha
- `ic_level.xml` — estrella/nivel
- `ic_add.xml` — plus
- `ic_check.xml` — check
- `ic_delete.xml` — basurero
- `ic_edit.xml` — lápiz
- `ic_filter.xml` — filtro
- `ic_moon.xml` — luna (dark mode)
- `ic_sun.xml` — sol (light mode)

### Imágenes de personajes (del servidor, no locales)
- URL base: `https://app.epycus.es/img/personajes/{carrera}/{genero}/nivel{nivel}.png`
- Cargar con Glide: `Glide.with(context).load(url).circleCrop().into(imageView)`
- Fallback: mostrar iniciales del usuario

### Assets existentes que NO eliminar:
- `bg_card_rounded.xml`, `bg_accent_circle.xml`, `bg_accent_gradient.xml`
- `bg_chat_message_sent.xml`, `bg_chat_message_received.xml`
- `shape_dot.xml`
- `bottom_nav_color.xml`

---

## 14. STRINGS (Español, mantener consistencia)

TODOS los textos en `strings.xml`. NO hardcodear textos en Java.
Temas: usar `?attr/epAccent`, `?attr/epTextPrimary`, etc. NO colores hardcodeados.

---

## 15. ARCHIVOS A MODIFICAR

### Java (reemplazar completamente):
| Archivo | Acción |
|---------|--------|
| `ui/home/InicioFragment.java` | REESCRIBIR completo con game-like UI |
| `ui/habitos/HabitosFragment.java` | REESCRIBIR con chips, swipe, editar |
| `ui/diario/DiarioFragment.java` | REESCRIBIR con moods mejorados |
| `ui/perfil/PerfilFragment.java` | REESCRIBIR con menú completo |
| `ui/pomodoro/PomodoroFragment.java` | REESCRIBIR con backend sync |
| `ui/ia/IaChatActivity.java` | REESCRIBIR con UI pulida |
| `ui/auth/LoginActivity.java` | MEJORAR |
| `ui/auth/RegistroActivity.java` | MEJORAR |
| `ui/MainContainerActivity.java` | MEJORAR navegación |
| `ui/adapters/HabitoHoyAdapter.java` | REESCRIBIR con swipe |
| `ui/adapters/MisionAdapter.java` | MEJORAR |
| `ui/adapters/MensajeChatAdapter.java` | MEJORAR |

### Layouts (reemplazar completamente):
| Archivo | Acción |
|---------|--------|
| `layout/fragment_inicio.xml` | REESCRIBIR con game-like |
| `layout/fragment_habitos.xml` | REESCRIBIR |
| `layout/fragment_diario.xml` | REESCRIBIR |
| `layout/fragment_perfil.xml` | REESCRIBIR |
| `layout/fragment_pomodoro.xml` | REESCRIBIR |
| `layout/activity_ia_chat.xml` | REESCRIBIR |
| `layout/activity_login.xml` | MEJORAR |
| `layout/activity_registro.xml` | MEJORAR |
| `layout/activity_main_container.xml` | MEJORAR |
| `layout/item_habito_hoy.xml` | REESCRIBIR (72dp, checkbox) |
| `layout/item_mision.xml` | MEJORAR |
| `layout/item_chat_edy.xml` | MEJORAR |
| `layout/item_chat_usuario.xml` | MEJORAR |
| `layout/dialog_nuevo_habito.xml` | MEJORAR |
| `layout/dialog_cambiar_contrasena.xml` | MEJORAR |

### NO TOCAR (salvo bug):
- `api/*` (14 servicios + RetrofitClient + AuthInterceptor)
- `repository/*` (5 repositorios)
- `model/*` (todos los DTOs y entidades)
- `util/*` (SessionManager, ThemeManager, NetworkUtils)
- `data/local/*` (Room database, DAOs, entidades)
- `gradle/*` (build files, versions)
- `AndroidManifest.xml`
- `values/colors.xml`, `values/themes.xml`, `values/styles_epycus.xml`
- `values-night/themes.xml`
- `color/bottom_nav_color.xml`

---

## 16. VERIFICACIÓN FINAL

- [ ] Build exitoso: `./gradlew assembleDebug`
- [ ] No warnings de lint críticos
- [ ] Todos los fragments cancelan Retrofit calls en `onDestroyView()`
- [ ] ViewBinding nullificado en `onDestroyView()`
- [ ] Timers cancelados en `onDestroyView()` (Pomodoro)
- [ ] No hardcodeo de strings
- [ ] Dark mode funcional en todas las pantallas
- [ ] Pull-to-refresh en todas las pantallas con datos
- [ ] Offline fallback con Room cache
- [ ] Personajes visibles como PNG sin fondo
- [ ] Nada superpuesto en ninguna resolución (360dp-480dp)
- [ ] Todos los botones ≥ 48dp altura
- [ ] Animaciones suaves en transiciones
- [ ] Loading indicators en todas las llamadas API
- [ ] Empty states con acción
- [ ] Error states con retry

---

## 17. POSIBLES MODIFICACIONES AL BACKEND (EpycusApp)

Si durante la reconstrucción se identifica que algún endpoint falta datos o necesita ajustes:

1. **Imágenes de personajes**: Asegurar que `GET /api/gamificacion/mi-progreso` devuelva `imagenPersonaje` con URL completa a `https://app.epycus.es/img/personajes/.../nivelX.png`
2. **Dashboard**: Asegurar que `GET /api/dashboard/resumen` devuelva `kpis { habitosPendientes, misionesPendientes }` y `frase { frase, autor }`
3. **Perfil**: Asegurar que `GET /api/perfil` devuelva `perfil { nombre, correo, nivelActual, xpTotal, rachaActual, rachaMaxima, totalHabitosCompletados, fechaRegistro, carreraNombre }` e `imagenPersonaje`
4. **Misiones**: Asegurar que `GET /api/misiones` devuelva `[{ id, nombre, prioridad, fechaLimite, completada }]`
5. **CORS**: Si la app Android necesita acceder a las imágenes del servidor, verificar que `Program.cs` tenga CORS configurado para `https://app.epycus.es`
6. **Static Files**: Verificar que `wwwroot/img/personajes/` sea accesible públicamente y que todas las combinaciones carrera/género/nivel tengan imágenes o placeholders

---

## 📌 NOTA PARA LA IA

Este prompt es una especificación completa. Debes:
1. Leer los archivos existentes para entender estructura actual
2. Reescribir cada archivo Java y layout según esta especificación
3. Mantener compatibilidad con las APIs existentes
4. No eliminar archivos de API, repositorios, modelos o utilidades
5. Verificar que el build compile después de cada cambio importante
6. Probar en emulador con resoluciones 360dp y 480dp de ancho
