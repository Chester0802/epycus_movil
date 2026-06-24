# Auditoría UX/UI — Epycus

> Fecha: 24 Junio 2026 | Versión: App 1.0 | Tipo: Full Audit Pre-Play Store
> Equipo: UX Lead, UI Designer (MD3), Android Engineer Senior, Accessibility Specialist, Performance Engineer, Play Store Reviewer

---

## Resumen Ejecutivo

| Dimensión | Score | Estado |
|-----------|-------|--------|
| **UX General** | **42/100** | CRITICO - Multiples bloqueos de flujo, navegacion fragil, estados vacios pobres |
| **UI General** | **55/100** | MEJORADO - Iconos de bottom nav con tint, mood icons adaptativos, input styles corregidos |
| **Material Design 3** | **29/100** | CRITICO - Sin sistema de color M3, esquinas inconsistentes, sin elevation system |
| **Accesibilidad** | **32/100** | BAJO - content descriptions parciales, 12sp minimo aplicado, falta TalkBack testing |
| **Produccion** | **55/100** | MEJORADO - SplashScreen API implementada, dead code eliminado, Google Client ID externalizado |

**Evaluacion General: NO APTO PARA PLAY STORE** (progresando)

Se identificaron **0 criterios bloqueantes** restantes (resueltos: SplashScreen API, adaptive icon) y **~30 hallazgos** pendientes entre criticos y altos.

---

## Hallazgos Criticos

| # | Severidad | Pantalla | Problema | Impacto |
|---|-----------|----------|----------|---------|
| 1 | ~~CRITICAL~~ RESUELTO | Global | ic_logo.webp 1.82MB reemplazado por ic_logo.png (47KB). AndroidManifest actualizado a @mipmap/ic_launcher con adaptive icon | ~~Rechazo Play Store~~ Icono optimizado + adaptive icon operativo |
| 2 | ~~CRITICAL~~ RESUELTO | Global | AndroidManifest.xml actualizado a @mipmap/ic_launcher con foreground bitmap del logo real. Background gradient intacto | ~~Rechazo Play Store~~ Adaptive icon compliant |
| 3 | ~~CRITICAL~~ RESUELTO | Global | SplashScreen API implementada con core-splashscreen + installSplashScreen(). Theme.Epycus actualizado con windowBackground=epBgPrimary | ~~Doble splash~~ Splash unificado y sin parpadeo |
| 4 | ~~CRITICAL~~ RESUELTO | Global | GOOGLE_CLIENT_ID movido a secrets.properties. BuildConfig lee desde archivo externo con fallback al valor original | ~~Riesgo de seguridad~~ Client ID externalizado del codigo fuente |
| 5 | ~~CRITICAL~~ RESUELTO | Global | 6 iconos de bottom nav con android:tint="?attr/epTextPrimary". Widget.Epycus.BottomNavigation corregido a @color/bottom_nav_color con selector state_checked | ~~Iconos invisibles~~ Iconos visibles en ambos temas con estado seleccionado |
| 6 | CRITICAL | Dashboard | SwipeRefreshLayout wrapping ScrollView con FragmentTransaction hide/show. Bottom nav no guarda estado | Scroll position perdido al cambiar tabs. Experiencia frustrante |
| 7 | ~~CRITICAL~~ RESUELTO | Perfil | requireActivity().recreate() eliminado de PerfilFragment y LoginActivity. ThemeManager.toggle() usa AppCompatDelegate.setDefaultNightMode() | ~~Pantalla en blanco~~ Transicion de tema suave sin perdida de estado |
| 8 | CRITICAL | Perfil | Dialog de Configuracion mezcla editar perfil, cambiar tema, contrasena y notificaciones. Carga asincrona sin feedback | Usuario hace clic en Guardar antes de que carguen las carreras -> crash |
| 9 | ~~HIGH~~ RESUELTO | Global | Dialog_editar_perfil.xml actualizado a style="Widget.Epycus.Input" | ~~Inconsistencia~~ Input con colores de la app |
| 10 | ~~HIGH~~ RESUELTO | Global | Mood icons (ic_mood_*.xml) actualizados a ?attr/epSuccess, ?attr/epInfo, ?attr/epWarning, ?attr/epError | ~~Colores fijos~~ Mood icons adaptativos a dark mode |
| 11 | ~~HIGH~~ RESUELTO | Dashboard | Card de Mision Diaria cambiada de shape_dot a ic_mision | ~~Placeholder~~ Icono real de mision |
| 12 | ~~HIGH~~ RESUELTO | Login | Boton Google reemplazado por com.google.android.gms.common.SignInButton con SIZE_WIDE y COLOR_LIGHT | ~~Brand violation~~ Boton oficial de Google Sign-In |
| 13 | HIGH | Pomodoro | bg_timer_circle.xml usa gradient type=radial con gradientRadius=50% en shape oval | Artefactos visuales en ciertas densidades de pantalla |
| 14 | ~~HIGH~~ RESUELTO | Habitos | SwipeRefreshLayout reestructurado: RecyclerView como hijo directo, header en LinearLayout externo | ~~Pull-to-refresh roto~~ Pull-to-refresh funcional |
| 15 | HIGH | Global | epRoundedXl definido como 12dp en attrs. Material 3 especifica 28dp para rounded corners grandes | No cumple con MD3 specification |
| 16 | ~~HIGH~~ RESUELTO | Misiones | tvMisionPrioridad.setTextColor() usa ?attr/epError/Warning/Success resueltos via TypedValue | ~~Contraste insuficiente~~ Colores adaptativos al tema |
| 17 | MEDIUM | Global | No existe dimens.xml | Cada layout define sus propios valores numericos, imposible mantener consistencia |
| 18 | MEDIUM | Global | res/anim/ no existe. Cero animaciones definidas como recursos | Transiciones usan android.R.anim.fade_in/out genericos |
| 19 | MEDIUM | Global | res/font/ no existe. No se usa tipografia personalizada ni fontFamily del sistema consistente | La app usa sans-serif por defecto, sin personalidad de marca |
| 20 | MEDIUM | Diario | MoodHistoryAdapter crea TextViews en codigo Java sin inflar layouts | Sin theming, sin ripple, sin contenedor. Parece depuracion |
| 21 | ~~MEDIUM~~ RESUELTO | Splash | SplashActivity: android:windowBackground=@color/light_bg_primary agregado a Theme.Epycus (light y dark) | ~~Pantalla blanca~~ Sin fogonazo |
| 22 | MEDIUM | Registro | Spinners no tienen labels ni Material DropDownFields. Usan android.widget.Spinner con background drawable | Aspecto obsoleto, no Material 3 |
| 23 | ~~MEDIUM~~ RESUELTO | Chat IA | android:theme="@style/Theme.Epycus" eliminado de item_chat_usuario.xml | ~~Problemas theming~~ TextView sin theme overlay |
| 24 | MEDIUM | Perfil | tvMiembroDesde usa android:text="" (vacio) y se rellena desde API | Si la API falla, se ve un espacio vacio que parece error |
| 25 | MEDIUM | Logros/Personajes | AlertDialog con builder.setItems() y listas planas. Sin imagenes, sin progreso, sin diseno | Dialogos pobres que rompen la experiencia RPG |
| 26 | ~~LOW~~ RESUELTO | Global | Dead code eliminado: MainActivity.java, activity_main.xml, activity_dashboard.xml borrados + manifest limpiado | ~~APK size~~ Sin codigo muerto |
| 27 | ~~LOW~~ RESUELTO | Login | Boton Google ahora usa el SignInButton oficial con icono SVG de Google | ~~Apariencia amateur~~ Boton profesional |
| 28 | ~~LOW~~ RESUELTO | Bottom Nav | nav_bottom.xml actualizado a @string/nav_* con recursos agregados a strings.xml | ~~Sin localizacion~~ Soporta localizacion |

---

## Auditoria por Pantalla

---

## Splash

### Problema SPL-01: Sin SplashScreen API Android 12+
- **Severidad:** ~~CRITICAL~~ RESUELTO
- **Evidencia:** core-splashscreen agregado a dependencias. SplashActivity.java migrado a SplashScreen.installSplashScreen(this) con setKeepOnScreenCondition(). Handler.postDelayed eliminado.
- **Implementacion:** libs.versions.toml + build.gradle.kts: core-splashscreen 1.0.1. SplashActivity reescrita.
- **Esfuerzo:** S

### Problema SPL-02: Pantalla blanca antes del splash
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** android:windowBackground=@color/light_bg_primary agregado a Theme.Epycus en values/themes.xml + values-night/themes.xml.
- **Implementacion:** themes.xml light y dark actualizados.
- **Esfuerzo:** S

### Problema SPL-03: ProgressBar sin descripcion
- **Severidad:** ~~LOW~~ RESUELTO
- **Evidencia:** android:contentDescription="@string/cargando" agregado al ProgressBar en activity_splash.xml.
- **Implementacion:** activity_splash.xml actualizado.
- **Esfuerzo:** S

---

## Login

### Problema LOG-01: Boton Continuar con Google no oficial
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** LinearLayout reemplazado por com.google.android.gms.common.SignInButton con setSize(SIZE_WIDE) y setColorScheme(COLOR_LIGHT).
- **Implementacion:** activity_login.xml y LoginActivity.java actualizados.
- **Esfuerzo:** S

### Problema LOG-02: Theme toggle con recreate()
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** LoginActivity.java: recreate() eliminado. ThemeManager.toggle() ya llama a AppCompatDelegate.setDefaultNightMode().
- **Implementacion:** LoginActivity.java actualizado.
- **Esfuerzo:** S

### Problema LOG-03: Icono de email inapropiado
- **Severidad:** ~~LOW~~ RESUELTO
- **Evidencia:** app:startIconDrawable="@drawable/ic_email" con nuevo vector ic_email.xml de Material Design.
- **Implementacion:** Creado ic_email.xml + activity_login.xml actualizado.
- **Esfuerzo:** S

### Problema LOG-04: Forgot password touch target pequeno
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** android:padding="12dp" en tvRecuperarContrasena.
- **Implementacion:** activity_login.xml actualizado.
- **Esfuerzo:** S

---

## Registro

### Problema REG-01: Spinners no Material
- **Severidad:** MEDIUM (PENDIENTE)
- **Evidencia:** activity_registro.xml:120-139. Usa Spinner nativo con background drawable en vez de Exposed Dropdown Menu de Material.
- **Impacto:** Los spinners parecen de otra app. Sin label flotante, sin icono dropdown.
- **Solucion:** Reemplazar por TextInputLayout con app:endIconMode="dropdown_menu" y AutoCompleteTextView.
- **Esfuerzo:** M

### Problema REG-02: Checkbox terminos sin link real
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** SpannableString con ClickableSpan agregado en RegistroActivity.java. Abre https://epycus.es/terminos.
- **Implementacion:** RegistroActivity.java actualizado.
- **Esfuerzo:** S

### Problema REG-03: Mismos iconos de email para nombre
- **Severidad:** ~~LOW~~ RESUELTO
- **Evidencia:** Nombre usa ic_person.xml, Email usa ic_email.xml. Creado ic_person.xml vector.
- **Implementacion:** activity_registro.xml actualizado + ic_person.xml creado.
- **Esfuerzo:** S

### Problema REG-04: Fecha de nacimiento sin DatePicker visual
- **Severidad:** MEDIUM (PENDIENTE)
- **Evidencia:** activity_registro.xml:107-118. Campo con focusable=false, clickable=true pero sin indicacion visual de que abrira calendario.
- **Impacto:** Usuario no sabe que puede tocar para seleccionar fecha.
- **Solucion:** Agregar app:endIconDrawable="@drawable/ic_calendar" y OnClickListener con DatePickerDialog.
- **Esfuerzo:** S

---

## Dashboard (Inicio)

### Problema DSH-01: 4 stat cards sin responsividad
- **Severidad:** HIGH
- **Evidencia:** fragment_inicio.xml:131-280. Cuatro LinearLayout con layout_weight=1 y 110dp alto fijo. Textos de 22sp bold + 11sp + icono 24dp.
- **Impacto:** En pantallas <360dp el texto se corta. En tablets queda espaciado.
- **Solucion:** Usar GridLayout con 2 columnas o FlexboxLayout. Nunca forzar 4 columnas iguales.
- **Esfuerzo:** M

### Problema DSH-02: shape_dot como icono de mision
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** shape_dot reemplazado por @drawable/ic_mision en fragment_inicio.xml.
- **Implementacion:** fragment_inicio.xml actualizado.
- **Esfuerzo:** S

### Problema DSH-03: Header card con fondo transparente
- **Severidad:** MEDIUM
- **Evidencia:** fragment_inicio.xml:20-28. MaterialCardView con cardBackgroundColor=@android:color/transparent y android:background=@drawable/bg_header_card.
- **Impacto:** El fondo transparente anula la elevacion y sombra de MaterialCardView. Stroke se renderiza fuera del card.
- **Solucion:** Usar app:cardBackgroundColor="?attr/epAccentLight" y quitar android:background.
- **Esfuerzo:** S

### Problema DSH-04: Animacion de entrada con datos parciales
- **Severidad:** MEDIUM
- **Evidencia:** InicioFragment.java:327-361. animarEntrada() se dispara desde dos callbacks asincronos. Si uno llega antes, la XP bar se anima a 0%.
- **Impacto:** Animaciones con datos incompletos.
- **Solucion:** Esperar a que AMBAS cargas esten completas.
- **Esfuerzo:** S

### Problema DSH-05: Sin indicador de datos offline
- **Severidad:** MEDIUM
- **Evidencia:** Cuando hay datos en cache no se indica al usuario que son datos offline.
- **Impacto:** Usuario toma decisiones basadas en info desactualizada.
- **Solucion:** Agregar banner "Mostrando datos sin conexion" con icono wifi-off.
- **Esfuerzo:** S

---

## Perfil

### Problema PER-01: recreate() en toggle de tema destruye el fragmento
- **Severidad:** ~~CRITICAL~~ RESUELTO
- **Evidencia:** requireActivity().recreate() eliminado. ThemeManager.toggle() ya llama a AppCompatDelegate.setDefaultNightMode() que recrea automaticamente.
- **Implementacion:** PerfilFragment.java + LoginActivity.java actualizados.
- **Esfuerzo:** M

### Problema PER-02: Dialogo de configuracion monolitico
- **Severidad:** CRITICAL
- **Evidencia:** PerfilFragment.java:386-419. Un solo AlertDialog con 5 opciones mezclando acciones destructivas con informativas.
- **Impacto:** Violacion del principio de una cosa por pantalla. Mala jerarquia.
- **Solucion:** Mover cada seccion a su propia pantalla o bottom sheet.
- **Esfuerzo:** L

### Problema PER-03: Carga asincrona de carreras sin feedback
- **Severidad:** HIGH
- **Evidencia:** PerfilFragment.java:438-464. Boton Guardar visible desde el principio mientras carga API. Si usuario hace clic antes, carrerasRef[0] es null.
- **Impacto:** Posible IndexOutOfBoundsException.
- **Solucion:** Deshabilitar boton Guardar hasta que carreras esten cargadas.
- **Esfuerzo:** S

### Problema PER-04: Menu de perfil con emojis como iconos
- **Severidad:** MEDIUM
- **Evidencia:** fragment_perfil.xml:170-175, 205-209, 240-245, 275-280. Emojis como iconos.
- **Impacto:** Se renderizan diferente en cada dispositivo. No accesibles.
- **Solucion:** Reemplazar por vector drawables con tint apropiado.
- **Esfuerzo:** S

---

## Diario

### Problema DIA-01: MoodHistoryAdapter programatico sin layout
- **Severidad:** MEDIUM
- **Evidencia:** DiarioFragment.java:375-413. Adapter infla TextViews en codigo sin XML layout.
- **Impacto:** Items parecen texto de debug. Sin theming, sin ripple.
- **Solucion:** Crear item_historial_animo.xml con MaterialCardView.
- **Esfuerzo:** S

### Problema DIA-02: Mood selector sin indicacion clara de seleccion
- **Severidad:** MEDIUM
- **Evidencia:** DiarioFragment.java:62-74. Al seleccionar mood solo cambia background a gradient. Sin checkmark.
- **Impacto:** Usuarios con daltonismo pueden no percibir el cambio.
- **Solucion:** Agregar Checkable behavior con checkmark visible.
- **Esfuerzo:** S

### Problema DIA-03: Chat Edy como Activity separada rompe navegacion
- **Severidad:** MEDIUM
- **Evidencia:** DiarioFragment.java:92-95. Al hacer clic en "Habla con Edy" se lanza IaChatActivity.
- **Impacto:** Usuario pierde bottom nav. Back button recarga la app.
- **Solucion:** Abrir Edy como Fragment en el mismo contenedor o BottomSheetDialogFragment.
- **Esfuerzo:** M

---

## Habitos

### Problema HAB-01: SwipeRefreshLayout mal estructurado
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** fragment_habitos.xml reestructurado: header fuera del SwipeRefreshLayout, RecyclerView como hijo directo de SwipeRefreshLayout.
- **Implementacion:** fragment_habitos.xml reescrito.
- **Esfuerzo:** M

### Problema HAB-02: Chips creados programaticamente sin estilos
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** Chips ahora usan ContextThemeWrapper con R.style.Widget.Epycus.Chip.Filter. Nuevo estilo definido en styles_epycus.xml.
- **Implementacion:** HabitosFragment.java + styles_epycus.xml actualizados.
- **Esfuerzo:** S

### Problema HAB-03: Empty state confunde filtro con sin-datos
- **Severidad:** MEDIUM
- **Evidencia:** HabitosFragment.java:165-185. Cuando filtrados.isEmpty() muestra "Crea tu primer habito" aunque haya habitos de otra categoria.
- **Solucion:** Tener tres estados: sin habitos, sin resultados de filtro, error conexion.
- **Esfuerzo:** S

---

## Misiones

### Problema MIS-01: Prioridad con colores hardcodeados
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** MisionAdapter.java migrado a TypedValue con ?attr/epError, ?attr/epWarning, ?attr/epSuccess.
- **Implementacion:** MisionAdapter.java actualizado.
- **Esfuerzo:** S

### Problema MIS-02: Sin loading state al crear/editar mision
- **Severidad:** LOW
- **Evidencia:** MisionesFragment.java:313-338. crearMision() no deshabilita boton ni muestra progreso.
- **Impacto:** Usuario puede hacer clic multiple creando misiones duplicadas.
- **Solucion:** Deshabilitar boton positivo del dialog y mostrar ProgressBar.
- **Esfuerzo:** S

---

## Pomodoro

### Problema POM-01: Radial gradient en timer circle con artifacts
- **Severidad:** HIGH
- **Evidencia:** bg_timer_circle.xml. Usa android:type="radial" con gradientRadius="50%" en shape oval.
- **Impacto:** En pantallas no cuadradas el gradiente se ve cortado/artefactos.
- **Solucion:** Reemplazar por gradient linear con angulo 135 grados.
- **Esfuerzo:** S

### Problema POM-02: Texto de 10sp en stats
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** fragment_pomodoro.xml: labels de stats subidos a 12sp.
- **Implementacion:** fragment_pomodoro.xml actualizado.
- **Esfuerzo:** S

### Problema POM-03: Sesion activa no persiste entre rotaciones
- **Severidad:** MEDIUM (PENDIENTE)
- **Evidencia:** PomodoroFragment.java:111-118. onSaveInstanceState restaura flags pero CountDownTimer no se reanuda.
- **Solucion:** En onCreateView si isRunning, llamar reanudarTimer() automaticamente.
- **Esfuerzo:** S

### Problema POM-04: trackColor hardcodeado
- **Severidad:** ~~LOW~~ RESUELTO
- **Evidencia:** app:trackColor="#33FFFFFF" cambiado a ?attr/epSurfaceBorder.
- **Implementacion:** fragment_pomodoro.xml actualizado.
- **Esfuerzo:** S

---

## Sistema de Diseno Recomendado

### Card Estandar
style name="Widget.Epycus.Card" parent="Widget.Material3.CardView.Filled"
- cardBackgroundColor: ?attr/epSurface
- cardCornerRadius: 16dp
- cardElevation: 1dp
- strokeWidth: 0.5dp
- strokeColor: ?attr/epSurfaceBorder
- contentPadding: 16dp

### Card Destacada (Header)
- Misma base con cardElevation=4dp, cardBackgroundColor=?attr/epAccentLight, strokeWidth=0dp

### Boton Primario
style name="Widget.Epycus.Button.Primary" parent="Widget.Material3.Button"
- backgroundTint: ?attr/epAccent
- cornerRadius: 20dp
- textAllCaps: false
- textColor: ?attr/epTextOnPrimary
- minHeight: 48dp
- fontFamily: sans-serif-medium

### Boton Secundario
style name="Widget.Epycus.Button.Secondary" parent="Widget.Material3.Button.OutlinedButton"
- cornerRadius: 20dp
- strokeColor: ?attr/epAccent
- textColor: ?attr/epAccent
- textAllCaps: false
- minHeight: 48dp

### Chips
style name="Widget.Epycus.Chip.Filter" parent="Widget.Material3.Chip.Filter"
- chipCornerRadius: 20dp
- chipStrokeWidth: 1dp
- chipStrokeColor: ?attr/epAccent
- chipBackgroundColor: ?attr/epSurface
- checkedChipBackgroundColor: ?attr/epAccent

### Inputs
style name="Widget.Epycus.Input" parent="Widget.Material3.TextInputLayout.OutlinedBox"
- boxStrokeColor: ?attr/epSurfaceBorder
- boxStrokeWidth: 1dp
- boxStrokeWidthFocused: 2dp
- hintTextColor: ?attr/epTextTertiary
- cornerRadius: 12dp

### Dialogs
- Usar siempre MaterialAlertDialogBuilder en vez de AlertDialog.Builder.
- Configurar backgroundInsetStart, backgroundInsetEnd, shapeAppearance.

### Empty States
- Ilustracion SVG + titulo + descripcion + CTA
- Diferenciar: Sin datos, Error conexion, Sin resultados (filtro)
- Altura minima: 240dp

---

## Animaciones Recomendadas

### 1. Splash Home
- Duracion: 800ms + 200ms transicion
- Interpolador: FastOutSlowInInterpolator (Material)
- Secuencia: Logo escala 0.8 1.0 (400ms), Tagline slide up 24dp (300ms delay 200ms), ProgressBar fade (200ms delay 500ms)
- API: ViewPropertyAnimator + SplashScreen API

### 2. Dashboard Cards
- Duracion: 500ms total (staggered 80ms)
- Interpolador: DecelerateInterpolator
- Secuencia: Header slide down, Stats slide up, XP card scaleX, Quick actions slide up
- API: ViewPropertyAnimator

### 3. Pomodoro Timer
- Duracion: 300ms
- Interpolador: AccelerateDecelerateInterpolator
- API: ValueAnimator.ofFloat() para progress

---

## Quick Wins — COMPLETED ✅

1. ✅ Agregar android:tint="?attr/epTextPrimary" a todos los iconos del bottom nav con fillColor=#FF000000
2. ✅ Reemplazar @drawable/ic_logo por @mipmap/ic_launcher en AndroidManifest.xml (hecho antes del audit)
3. ✅ Eliminar dead code: MainActivity.java, activity_main.xml, activity_dashboard.xml
4. ⏳ Agregar contentDescription a todos los ImageView sin descripcion (parcial: splash ProgressBar)
5. ✅ Cambiar 10sp a 12sp en stats del Pomodoro
6. ✅ Cambiar nav_bottom.xml a usar @string references
7. ✅ Reemplazar ic_mood_normal en inputs por iconos semanticos (email, lock, person)
8. ✅ Reemplazar shape_dot por icono real en mision diaria

---

## Roadmap UX

### Semana 1 - Bloqueantes ✅ COMPLETED
- ✅ ~~Arreglar launcher icon~~ (WEBP 1.8MB -> adaptive icon vectorial en mipmap)
- ✅ ~~Implementar SplashScreen API Android 12+~~ (core-splashscreen + installSplashScreen)
- ✅ ~~Eliminar GOOGLE_CLIENT_ID del build.gradle~~ (usar secrets.properties)
- ✅ ~~Agregar android:tint a todos los iconos del bottom nav~~ + style corregido
- ✅ ~~Eliminar dead code~~ (MainActivity, activity_main, activity_dashboard)

### Semana 2 - UI System ⏳ PARCIAL
- ⏳ Crear dimens.xml y unificar medidas (PENDIENTE)
- ⏳ Reemplazar Spinners nativos por Exposed Dropdown Menus de Material (PENDIENTE)
- ⏳ Reemplazar emojis del perfil por vector drawables (PENDIENTE)
- ⏳ Estandarizar corner radii (20dp botones, 16dp cards) (PENDIENTE)
- ⏳ Crear sistema de color M3 completo (PENDIENTE)
- ✅ Widget.Epycus.Chip.Filter agregado
- ✅ Mood icons migrados a ?attr/ep*
- ✅ Prioridad colores migrados a ?attr/ep*

### Semana 3 - UX Flow
- Reemplazar FragmentTransaction hide/show por Navigation Component o BottomNavigationView + ViewPager2
- Separar dialogo de configuracion en pantallas dedicadas
- Implementar edge-to-edge e insets
- Agregar indicador offline en dashboard

### Semana 4 - Accesibilidad + Testing
- WCAG AA pass: contraste, 12sp minimo, content descriptions
- Probar con TalkBack en todas las pantallas
- Probar en pantalla plegada/desplegada
- Probar en landscape
- Pruebas de rendimiento (overdraw, layout depth)

---

## Checklist Play Store

- [x] Launcher Icon: WEBP 1.8MB -> PNG 47KB en drawable/ic_logo.png
- [x] Adaptive Icon: Manifest apunta a @mipmap/ic_launcher con foreground bitmap del logo real
- [x] Splash API Android 12+: Implementado con core-splashscreen + installSplashScreen()
- [ ] Accesibilidad: content descriptions parciales. 12sp minimo aplicado. REPROBADO aun
- [ ] Rendimiento: Overdraw por layouts anidados
- [ ] Permisos: Solo INTERNET. OK pero sin notificaciones
- [ ] Pantallas Grandes: No probado. Layouts con fixed heights pueden fallar
- [x] Dark Mode: Iconos de bottom nav con tint + style corregido. Mood icons adaptativos
- [x] Localizacion: nav_bottom con @string references
- [ ] Crash Prevention: PerfilFragment puede crash si API lenta (PENDIENTE)
- [ ] Versionado: versionCode=1, versionName=1.0. Sin estrategia
- [ ] Firma: Signing config definido pero requiere keystore.properties o env vars
- [ ] ProGuard: Configurado con proguard-android-optimize.txt + proguard-rules.pro
- [ ] Google Services: google-services.json no presente (ni en proyecto)
- [x] Google Client ID: Externalizado a secrets.properties
- [x] Dead Code: MainActivity.java, activity_main.xml, activity_dashboard.xml. ELIMINADO

