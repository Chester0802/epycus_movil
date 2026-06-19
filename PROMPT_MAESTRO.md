# 🚀 PROMPT MAESTRO — Reconstrucción Completa UI/UX Epycus Android

> **Objetivo**: Rehacer desde cero la app Android `C:\Users\marco\Pictures\Epycus` para que consuma **todos los módulos** del backend `C:\Users\marco\Pictures\EpycusApp`, con diseño **game-like**, medidas perfectas, sin superposiciones, imágenes de personajes sin fondo como avatar principal, y funcionalidad completa en cada pantalla.

---

## 📐 REGLAS DE ORO DEL DISEÑO

1. **Nada pequeño, nada montado**: Cada elemento debe tener tamaño mínimo legible (texto ≥12sp, botones ≥48dp alto, iconos ≥24dp).
2. **Sin superposición**: Usar `ScrollView` + `LinearLayout` o `ConstraintLayout` con restricciones correctas. Verificar en vista previa de layout.
3. **Medidas exactas**: Usar `dp` para尺寸, nunca `px`. Márgenes internos de 16dp, padding de cards 16dp.
4. **Personajes como avatares**: Imágenes PNG con fondo transparente en grande en Inicio (min 120dp), redondeadas suavemente (12dp radio) o en círculo.
5. **Game-like**: Cards con sombra (`cardElevation: 4dp`), colores vibrantes, animaciones sutiles, progreso visual con barras y niveles.
6. **Responsive a la base de datos**: Toda pantalla debe cargar datos reales del backend vía Retrofit. Sin datos mock. Mostrar loading mientras carga y empty state si no hay datos.
7. **Tema dual**: Kawaii (rosa claro) para modo claro / Solo Leveling (púrpura oscuro) para modo oscuro. Mantener `ThemeManager.java`.

---

## 📱 PANTALLAS A IMPLEMENTAR

### 1. SPLASH (SplashActivity)
- Logo "E" grande centrado, nombre "Epycus" debajo, subtítulo "Tu compañero de productividad"
- ProgressBar animado
- Verificar sesión → si hay token válido → MainContainerActivity, si no → LoginActivity
- Duración: 1.5 segundos

### 2. LOGIN (LoginActivity)
- Fondo del tema actual
- Logo "E" circular arriba
- Título "Iniciar sesión"
- Campo email (TextInputLayout con icono de email)
- Campo contraseña (TextInputLayout con toggle de visibilidad)
- Botón "Recuperar contraseña" (texto pequeño, click → diálogo con campo email)
- Botón "Iniciar sesión" (acento, relleno, 48dp alto, texto "Iniciar sesión")
- Separador "o continúa con"
- Botón Google (icono Google + texto "Google")
- Texto "¿No tienes cuenta? Regístrate" (click → RegistroActivity)
- **Integración API**: `POST /api/auth/login` → guardar JWT en EncryptedSharedPreferences

### 3. REGISTRO (RegistroActivity)
- Título "Crear cuenta"
- Campo nombre completo
- Campo correo electrónico
- Campo contraseña + confirmar contraseña
- Campo fecha nacimiento (click → DatePickerDialog, no input manual)
- Spinner género (Masculino, Femenino, Otro, Prefiero no decirlo)
- Spinner carrera (cargado desde `GET /api/auth/carreras`)
- Checkbox "Acepto términos y condiciones"
- Botón "Registrarse"
- **Integración API**: `POST /api/auth/registro` → AuthResponse

### 4. CONTENEDOR PRINCIPAL (MainContainerActivity)
- CoordinatorLayout con BottomAppBar + FAB central
- BottomNavigationView con 5 ítems: Inicio, Hábitos, [FAB], Diario, Perfil
- FragmentContainerView ocupando todo el espacio arriba del BottomAppBar
- FAB: icono de Pomodoro (reloj) → abre PomodoroFragment
- FragmentManager con show/hide + animaciones fade (200ms)
- Backstack: al presionar back desde Pomodoro → volver al fragment anterior

### 5. INICIO (InicioFragment) ⭐ PANTALLA PRINCIPAL
```
┌──────────────────────────────┐
│  👤 Avatar grande (120dp)    │
│  ¡Hola, [Nombre]!           │
│  Nivel 7 - Titulo          │
├──────────────────────────────┤
│  Barra de XP: ████████░░ 70% │
│  320 / 450 XP               │
├──────────────────────────────┤
│  🔥 Racha: 5 días            │
│  📊 Hábitos hoy: 3/4         │
│  🎯 Misiones pend: 2         │
├──────────────────────────────┤
│  "Frase motivacional del día" │
│  — Autor                     │
├──────────────────────────────┤
│  [🧘 Pausa Activa] [🗣️ Edy] │
│  [📊 Ver Estadísticas]        │
└──────────────────────────────┘
```

**Especificaciones**:
- **Avatar**: Imagen del personaje seleccionado (PNG sin fondo) en ~120dp x 120dp, fondo circular `bg_accent_circle` con borde sutil. Si no hay imagen, inicial del nombre en círculo acento.
- **Barra XP**: ProgressBar personalizada con color acento, altura 12dp, esquinas redondeadas 6dp, con texto dentro "320/450 XP"
- **KPIs**: 3 cards horizontales con icono + número + etiqueta
- **Frase motivacional**: card con comillas decorativas, autor en itálica
- **Botones de acción rápida**: Pausa Activa (abre fragmento bienestar), Edy (abre IaChatActivity), Estadísticas (diálogo)
- **SwipeRefreshLayout** para recargar
- **Integración API**: `GET /api/dashboard/resumen` + `GET /api/gamificacion/mi-progreso`

### 6. HÁBITOS (HabitosFragment)
```
┌──────────────────────────────┐
│  📋 Hábitos de hoy (4)      │
├──────────────────────────────┤
│  ┌────────────────────────┐  │
│  │ ✅ Meditar    10 XP   │  │
│  │ 🧘 Salud              │  │
│  ├────────────────────────┤  │
│  │ ◻ Leer 30min   15 XP  │  │
│  │ 📚 Estudio          │  │
│  ├────────────────────────┤  │
│  │ ❌ Ejercicio    20 XP  │  │
│  │ 🏋️ Salud            │  │
│  └────────────────────────┘  │
│                              │
│  [+ Nuevo hábito]            │
└──────────────────────────────┘
```

**Especificaciones**:
- **Cada hábito**: Card de 72dp alto, icono de categoría a la izquierda, nombre + categoría, XP a la derecha
- **Estado**: Checkbox ✅ para completado, ❌ para fallado, ◻ para pendiente (hacer clic alterna entre los 3)
- **Tap hábito**: Opciones de menú contextual (Completar, Fallar, Editar, Eliminar)
- **Botonera**: Filtro por categoría (scroll horizontal de chips), "Nuevo hábito" flotante
- **Diálogo crear hábito**: Nombre, categoría (spinner con carga de API), frecuencia (Diaria, Semanal), dificultad (Fácil/Media/Difícil)
- **Integración API**: `GET /api/habitos/hoy`, `POST /api/habitos/{id}/completar`, `POST /api/habitos/{id}/fallar`, `POST /api/habitos`, `DELETE /api/habitos/{id}`, `GET /api/habitos/categorias`

### 7. DIARIO (DiarioFragment)
```
┌──────────────────────────────┐
│  📖 Diario de ánimo          │
│  "¿Cómo te sientes hoy?"     │
├──────────────────────────────┤
│  😄 😊 😐 😔 😫             │
│  Genial Bien Normal Cansado  │
│  [Seleccionar uno]           │
├──────────────────────────────┤
│  📝 Notas (opcional)         │
│  ┌────────────────────────┐  │
│  │ Hoy me siento...       │  │
│  └────────────────────────┘  │
│  [💾 Guardar estado]         │
├──────────────────────────────┤
│  Tu entrada de hoy:           │
│  "Hoy te sientes: Genial"     │
│  "Tu nota personal aquí"      │
├──────────────────────────────┤
│  🗣️ Habla con Edy IA          │
│  Tu asistente personal        │
└──────────────────────────────┘
```

**Especificaciones**:
- **Moods**: 5 emoticonos grandes (32dp) con colores: Genial (verde), Bien (azul), Normal (gris), Cansado (naranja), Estresado (rojo)
- **Selección**: Borde acento gradient cuando está seleccionado
- **Notas**: TextInputLayout multilínea (máx 3 líneas visibles, scroll si más)
- **Botón guardar**: Deshabilitado si no hay mood seleccionado
- **Entrada de hoy**: Carga desde `GET /api/diario/hoy`. Muestra estado + notas.
- **Pregunta guía**: Texto arriba, cargado desde `GET /api/diario/pregunta-guia`
- **Edy IA**: Card con icono "E" + subtítulo, click → IaChatActivity
- **Integración API**: `POST /api/estado-animo`, `GET /api/diario/hoy`, `GET /api/diario/pregunta-guia`

### 8. POMODORO (PomodoroFragment)
```
┌──────────────────────────────┐
│  🍅 Pomodoro                 │
│  ENFOQUE                     │
├──────────────────────────────┤
│        ┌──────────┐          │
│        │  25:00   │          │
│        │  ⏱️ Foco  │          │
│        └──────────┘          │
│                              │
│    [▶ Iniciar / ⏸ Pausar]   │
├──────────────────────────────┤
│  💡 Tip: "Toma respiros..."  │
├──────────────────────────────┤
│  📊 Ciclos: 3  |  Hoy: 5     │
└──────────────────────────────┘
```

**Especificaciones**:
- **Timer**: Círculo grande (260dp) con fondo card, tiempo en grande (48sp), estado debajo
- **Botón control**: Texto cambia (Iniciar/Pausar/Reanudar), color acento
- **Ciclos**: Foco 25min → Pausa 5min → Foco → ... → cada 4 ciclos, pausa larga 15min
- **Tip motivacional**: Cargado desde `GET /api/pomodoro/tip-aleatorio`
- **Estadísticas**: Ciclos completados en esta sesión, total de hoy
- **Integración API**: `POST /api/pomodoro/iniciar`, `POST /api/pomodoro/{id}/ciclo-completado`, `POST /api/pomodoro/{id}/finalizar`, `GET /api/pomodoro/configuracion`, `GET /api/pomodoro/tip-aleatorio`
- **Persistencia**: Al salir, guardar estado en Bundle + notificar al backend si hay sesión activa

### 9. PERFIL (PerfilFragment)
```
┌──────────────────────────────┐
│  👤 Avatar grande (100dp)    │
│  Nombre del Usuario          │
│  usuario@email.com           │
├──────────────────────────────┤
│  Nivel 7        🔥 5 días    │
│  320 XP         🎓 Ingeniería│
├──────────────────────────────┤
│  ┌────────────────────────┐  │
│  │ 🎭 Mis personajes   ›  │  │
│  │ 🏆 Logros          ›   │  │
│  │ 📊 Estadísticas    ›   │  │
│  │ ⚙️ Configuración  ›   │  │
│  └────────────────────────┘  │
│                              │
│  🌙 Modo oscuro              │
│  [🚪 Cerrar sesión]          │
└──────────────────────────────┘
```

**Cada ítem de menú**: 52dp alto, icono a la izquierda (20dp), texto, flecha ">" a la derecha

**Sub-funcionalidades**:
- **Personajes**: Diálogo con lista de personajes desde `GET /api/perfil/personajes`. Cada uno con nombre y preview. Tap → seleccionar y llamar `PUT /api/perfil/personaje`.
- **Logros**: Diálogo con lista desde `GET /api/perfil/logros`. Cada logro con nombre, descripción, icono de desbloqueado/bloqueado.
- **Estadísticas**: Diálogo con KPIs: XP total, nivel, racha actual, racha máxima, hábitos completados, misiones completadas, sesiones pomodoro.
- **Configuración**: Submenú con "Cambiar contraseña" (diálogo con 3 campos), "Notificaciones" (próximamente), "Acerca de" (versión, descripción).
- **Modo oscuro**: Toggle con texto dinámico (modo claro/oscuro). Al cambiar, recrear actividad.
- **Cerrar sesión**: Confirmación "¿Estás seguro?" → `POST /api/auth/logout` → limpiar sesión → LoginActivity.

### 10. IA CHAT (IaChatActivity) — Activity independiente
```
┌──────────────────────────────┐
│  ←  🤖 Edy AI                │
├──────────────────────────────┤
│  ┌────────────────────────┐  │
│  │ ¡Hola! Soy Edy, tu... │  │
│  └────────────────────────┘  │
│              ┌─────────────┐ │
│              │ ¿Qué es XP? │ │
│              └─────────────┘ │
│  ┌────────────────────────┐  │
│  │ XP es la experiencia...│  │
│  └────────────────────────┘  │
│         ⋮                    │
├──────────────────────────────┤
│  ┌──────────────────────┐ 📎 │
│  │ Escribe un mensaje...│ ▶ │
│  └──────────────────────┘   │
└──────────────────────────────┘
```

**Especificaciones**:
- **Header**: Barra con botón back (flecha `ic_back`), avatar circular "E" + nombre "Edy AI"
- **Burbujas**: Mensajes usuario (derecha, fondo acento, texto blanco), mensajes Edy (izquierda, fondo elevado, texto primario). Bordes redondeados asimétricos.
- **Input**: TextInputLayout con icono de adjuntar (opcional), botón enviar (icono `ic_send`), tecla Enter del teclado también envía
- **Loading**: Indicador de "Edy está escribiendo..." con animación de puntos
- **Integración API**: `POST /api/ia/chat` con mensaje y conversacionId (para hilo)

---

## 🧩 MÓDULOS ADICIONALES (Integrar si existen endpoints)

### Bienestar (Pausa Activa)
- Desde Inicio, botón "🧘 Pausa Activa"
- Muestra una recomendación de ejercicio/estiramiento desde `POST /api/bienestar/pausa-activa`
- Timer de cuenta regresiva (2-5 minutos)
- Al finalizar, notificar al backend

### Misiones (Integrado en Hábitos o pantalla separada)
- Lista de misiones desde `GET /api/misiones`
- Cada misión: nombre, prioridad (Alta/Media/Baja), fecha límite, estado
- Botón "Completar" → `POST /api/misiones/{id}/completar`
- FAB para crear nueva misión → `POST /api/misiones`
- Si no hay espacio en bottom nav, acceder desde Inicio o desde Hábitos con un TabLayout

### Gamificación
- La barra de XP y nivel en Inicio ya cubre esto
- Animación al subir de nivel (confeti o alerta)
- Logros: ya implementado en Perfil

---

## 🎨 GUÍA DE ESTILOS

### Colores (Ya definidos en `colors.xml` y `themes.xml`)
| Rol | Claro (Kawaii) | Oscuro (Solo Leveling) |
|-----|----------------|------------------------|
| BG Principal | `#fef5ff` | `#0a0e1a` |
| Surface (cards) | `#ffffff` | `#0f1419` |
| Acento | `#ff6b9d` | `#8b5cf6` |
| Acento Secundario | `#c77dff` | `#a78bfa` |
| Texto Primario | `#4a2545` | `#e8eaed` |
| Texto Secundario | `#7d5f7a` | `#a8b3cf` |
| Success | `#4caf50` | `#4caf50` |
| Error | `#e53935` | `#ef5350` |

### Tipografía
- Títulos: `sans-serif-medium`, 22sp, bold
- Subtítulos: `sans-serif`, 15sp, medium
- Cuerpo: `sans-serif`, 14sp, regular
- Etiquetas pequeñas: `sans-serif`, 12sp, regular
- Números grandes (timer): `sans-serif-medium`, 48sp

### Espaciado
- Márgen exterior pantalla: 16dp
- Padding cards: 16dp
- Entre cards: 8dp
- Entre elementos dentro de card: 8dp
- Altura mínima botones: 48dp
- Altura items lista: 72dp
- Altura items menú: 52dp

### Cards
- Fondo: `?attr/epSurface`
- Borde: 1dp `?attr/epSurfaceBorder`
- Radio esquinas: 12dp
- Elevación: 2dp (sutil)
- Foreground: `?attr/selectableItemBackground` para elementos clickeables

---

## 🔌 INTEGRACIÓN API — REGLAS GENERALES

1. **RetrofitClient** singleton con OkHttp + AuthInterceptor (JWT Bearer + auto-refresh en 401)
2. **RespuestaApi\<T\>** modelo genérico con `exito`, `mensaje`, `datos`, `errores`
3. **Cache offline**: Room con entidad `CacheEntity` (key-value) para datos de dashboard y perfil
4. **Mostrar loading** mientras carga, **empty state** si no hay datos, **error state** con opción de reintentar
5. **Snackbar** para errores de red, **Toast** para confirmaciones
6. **SwipeRefreshLayout** en todas las pantallas con datos

---

## 🖼️ SISTEMA DE PERSONAJES

- Los personajes vienen de `GET /api/perfil/personajes` (array con `id`, `nombre`, `urlImagen` o `ruta`)
- La imagen seleccionada se muestra en Inicio (120dp) y Perfil (100dp) como avatar principal
- Las imágenes son PNG con fondo transparente, almacenadas en `wwwroot/imagenes/personajes/`
- En Android, cargar con `Glide` o `Coil` desde `https://app.epycus.es/imagenes/personajes/{archivo}`
- Si falla la carga, mostrar inicial del personaje en círculo acento

---

## 📁 ARCHIVOS A MODIFICAR/CREAR

### Android (`C:\Users\marco\Pictures\Epycus`)

```
app/src/main/java/es/epycus/app/
├── ui/
│   ├── splash/SplashActivity.java             → Refactor (mantener)
│   ├── auth/LoginActivity.java                → Refactor UI
│   ├── auth/RegistroActivity.java             → Refactor UI + DatePicker
│   ├── MainContainerActivity.java             → Refactor navegación + FAB
│   ├── home/InicioFragment.java               → REESCRIBIR COMPLETO
│   ├── habitos/HabitosFragment.java           → REESCRIBIR COMPLETO
│   ├── diario/DiarioFragment.java             → REESCRIBIR COMPLETO
│   ├── pomodoro/PomodoroFragment.java         → REESCRIBIR COMPLETO
│   ├── perfil/PerfilFragment.java             → REESCRIBIR COMPLETO
│   ├── ia/IaChatActivity.java                 → Refactor UI
│   └── adapters/
│       ├── HabitoHoyAdapter.java              → Refactor con estados toggle
│       └── MensajeChatAdapter.java            → Refactor burbujas
│
app/src/main/res/layout/
├── activity_splash.xml                        → Refactor centrado
├── activity_login.xml                         → Refactor completo
├── activity_registro.xml                      → Refactor completo
├── activity_main_container.xml                → Refactor BottomAppBar + FAB
├── activity_ia_chat.xml                       → Refactor header + input
├── fragment_inicio.xml                        → REESCRIBIR
├── fragment_habitos.xml                       → REESCRIBIR
├── fragment_diario.xml                        → REESCRIBIR
├── fragment_pomodoro.xml                      → REESCRIBIR
├── fragment_perfil.xml                        → REESCRIBIR
├── item_habito_hoy.xml                        → Refactor (check icon)
├── item_mision.xml                            → Mantener
├── item_chat_edy.xml                          → Refactor burbuja
└── item_chat_usuario.xml                      → Refactor burbuja

app/src/main/res/drawable/
├── ic_back.xml                                → OK (creado)
├── ic_send.xml                                → OK (creado)
├── ic_mood_genial.xml, ic_mood_bien.xml, ...  → OK (creados)
├── ic_lock.xml                                → OK (creado)
├── bg_accent_circle.xml                       → Mantener
├── bg_accent_gradient.xml                     → Mantener
├── bg_card_rounded.xml                        → Mantener
├── bg_chat_message_sent.xml                   → Mantener
└── bg_chat_message_received.xml               → Mantener
```

### Backend (`C:\Users\marco\Pictures\EpycusApp`) — Si necesita cambios

Posibles modificaciones:
- Asegurar que `GET /api/perfil` devuelva `urlImagenPersonaje` (la URL completa del PNG)
- Asegurar que `GET /api/gamificacion/mi-progreso` devuelva datos consistentes
- Añadir CORS headers si no existen para `https://app.epycus.es`
- En `wwwroot/imagenes/personajes/` deben estar los PNGs con fondo transparente

---

## 🐛 PROBLEMAS CONOCIDOS A EVITAR

1. **No usar Handler con postDelayed para Splash** → Usar `coroutines` o `postDelayed` pero limpiar en onDestroy
2. **No hardcodear texto** → Siempre usar strings.xml con `getString(R.string.xxx)`
3. **No anidar LinearLayouts demasiado** → Máximo 3 niveles, usar ConstraintLayout si es necesario
4. **No olvidar el minHeight 48dp en botones** → Accesibilidad
5. **No cargar imágenes sin cache** → Usar Glide/Coil con disk cache
6. **No bloquear UI thread** → Toda llamada API debe ser asíncrona (enqueue)
7. **No perder estado al rotar** → Guardar en Bundle (onSaveInstanceState)
8. **No mostrar datos sin verificar** → Siempre validar `response.body().isExito()`

---

## ✅ CHECKLIST FINAL

- [ ] SplashActivity: navegación correcta según sesión
- [ ] LoginActivity: login + Google + recuperación
- [ ] RegistroActivity: registro con carreras + DatePicker
- [ ] InicioFragment: avatar personaje, barra XP, KPIs, frase, acciones rápidas
- [ ] HabitosFragment: lista, crear, completar/fallar, eliminar
- [ ] DiarioFragment: moods, notas, entrada de hoy, pregunta guía, Edy
- [ ] PomodoroFragment: timer, backend sync, tips
- [ ] PerfilFragment: datos, personajes, logros, stats, config, cerrar sesión
- [ ] IaChatActivity: burbujas, API, keyboard
- [ ] Bienestar: pausa activa desde Inicio
- [ ] Misiones: lista desde API (si se integra)
- [ ] Build: `gradlew assembleDebug` sin errores
- [ ] Commit + push al VPS

---

> **¡A TRABAJAR!** Cada pantalla debe ser una obra maestra de UI/UX, funcional al 100% con datos reales, y con ese toque game-like que hace a Epycus único. 🚀
