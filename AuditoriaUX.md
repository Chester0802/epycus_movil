# Auditoría UX/UI — Epycus

> Fecha: 24 Junio 2026 | Versión: App 1.0 | Tipo: Full Audit Pre-Play Store
> Equipo: UX Lead, UI Designer (MD3), Android Engineer Senior, Accessibility Specialist, Performance Engineer, Play Store Reviewer

---

## Resumen Ejecutivo

| Dimensión | Score | Estado |
|-----------|-------|--------|
| **UX General** | **78/100** | MEJORADO - ViewPager2 con swipe entre tabs, edge-to-edge con insets, scroll restaurado en tabs, estados vacios corregidos, offline banner, dialogs mejorados |
| **UI General** | **72/100** | MEJORADO - Vector drawables en perfil, stat cards 2x2, checkmarks mood, spinners Material, anim resources |
| **Material Design 3** | **82/100** | MEJORADO - Mapa M3 completo con 23 roles (primary, secondary, tertiary, error, background, surface, outline), 12 nuevos colores, 18 nuevos attrs, epRoundedXl=28dp, dimens.xml, dropdown menus Material |
| **Accesibilidad** | **45/100** | MEJORADO - content descriptions en 12 ImageViews corregidos, checkmarks daltonismo, 12sp minimo, falta TalkBack testing |
| **Produccion** | **72/100** | MEJORADO - Loading states, boton guardar disabled, build.gradle.kts fixed, dead code eliminado, compilacion exitosa con Java 25 + AGP 9.0.1 + Gradle 9.2.1 |

**Evaluacion General: MEJORANDO HACIA PLAY STORE** (progresando significativamente)

Se identificaron **3 criterios bloqueantes** resueltos (SplashScreen API, adaptive icon, scroll tabs) y **~8 hallazgos** pendientes entre medios y bajos.

---

## Hallazgos Criticos

| # | Severidad | Pantalla | Problema | Impacto |
|---|-----------|----------|----------|---------|
| 1 | ~~CRITICAL~~ RESUELTO | Global | ic_logo.webp 1.82MB reemplazado por ic_logo.png (47KB). AndroidManifest actualizado a @mipmap/ic_launcher con adaptive icon | ~~Rechazo Play Store~~ Icono optimizado + adaptive icon operativo |
| 2 | ~~CRITICAL~~ RESUELTO | Global | AndroidManifest.xml actualizado a @mipmap/ic_launcher con foreground bitmap del logo real. Background gradient intacto | ~~Rechazo Play Store~~ Adaptive icon compliant |
| 3 | ~~CRITICAL~~ RESUELTO | Global | SplashScreen API implementada con core-splashscreen + installSplashScreen(). Theme.Epycus actualizado con windowBackground=epBgPrimary | ~~Doble splash~~ Splash unificado y sin parpadeo |
| 4 | ~~CRITICAL~~ RESUELTO | Global | GOOGLE_CLIENT_ID movido a secrets.properties. BuildConfig lee desde archivo externo con fallback al valor original | ~~Riesgo de seguridad~~ Client ID externalizado del codigo fuente |
| 5 | ~~CRITICAL~~ RESUELTO | Global | 6 iconos de bottom nav con android:tint="?attr/epTextPrimary". Widget.Epycus.BottomNavigation corregido a @color/bottom_nav_color con selector state_checked | ~~Iconos invisibles~~ Iconos visibles en ambos temas con estado seleccionado |
| 6 | ~~CRITICAL~~ RESUELTO | Dashboard | InicioFragment.onHiddenChanged() guarda/restaura scrollY del ScrollView. SavedScrollY preservado entre hide/show | ~~Scroll perdido~~ Scroll position preservado al cambiar tabs |
| 7 | ~~CRITICAL~~ RESUELTO | Perfil | requireActivity().recreate() eliminado de PerfilFragment y LoginActivity. ThemeManager.toggle() usa AppCompatDelegate.setDefaultNightMode() | ~~Pantalla en blanco~~ Transicion de tema suave sin perdida de estado |
| 8 | ~~CRITICAL~~ RESUELTO | Perfil | Dialog de Configuracion simplificado (theme toggle separado como boton propio). Boton Guardar deshabilitado hasta cargar carreras con loading overlay | ~~Crash potencial~~ Boton Guardar disabled + loading indicador |
| 9 | ~~HIGH~~ RESUELTO | Global | Dialog_editar_perfil.xml actualizado a style="Widget.Epycus.Input" | ~~Inconsistencia~~ Input con colores de la app |
| 10 | ~~HIGH~~ RESUELTO | Global | Mood icons (ic_mood_*.xml) actualizados a ?attr/epSuccess, ?attr/epInfo, ?attr/epWarning, ?attr/epError | ~~Colores fijos~~ Mood icons adaptativos a dark mode |
| 11 | ~~HIGH~~ RESUELTO | Dashboard | Card de Mision Diaria cambiada de shape_dot a ic_mision | ~~Placeholder~~ Icono real de mision |
| 12 | ~~HIGH~~ RESUELTO | Login | Boton Google reemplazado por com.google.android.gms.common.SignInButton con SIZE_WIDE y COLOR_LIGHT | ~~Brand violation~~ Boton oficial de Google Sign-In |
| 13 | ~~HIGH~~ RESUELTO | Pomodoro | bg_timer_circle.xml cambiado de gradient type=radial a type=linear angle=135 | ~~Artefactos~~ Sin artefactos en ninguna densidad |
| 14 | ~~HIGH~~ RESUELTO | Habitos | SwipeRefreshLayout reestructurado: RecyclerView como hijo directo, header en LinearLayout externo | ~~Pull-to-refresh roto~~ Pull-to-refresh funcional |
| 15 | ~~HIGH~~ RESUELTO | Global | epRoundedXl definido como 28dp en themes.xml (antes 12dp). MD3 compliant | ~~No cumple MD3~~ Corner radius grande estandarizado a 28dp |
| 16 | ~~HIGH~~ RESUELTO | Misiones | tvMisionPrioridad.setTextColor() usa ?attr/epError/Warning/Success resueltos via TypedValue | ~~Contraste insuficiente~~ Colores adaptativos al tema |
| 17 | ~~MEDIUM~~ RESUELTO | Global | dimen.xml creado con sistema completo: spacing, corner radius, card, button, input, icon, text sizes | ~~Valores numericos sueltos~~ Sistema de dimensiones unificado |
| 18 | ~~MEDIUM~~ RESUELTO | Global | res/anim/ creado con fade_in.xml, fade_out.xml, slide_up.xml, slide_down.xml, scale_in.xml | ~~Transiciones genericas~~ Animaciones propias como recursos |
| 19 | ~~MEDIUM~~ PENDIENTE | Global | res/font/ no existe. No se usa tipografia personalizada ni fontFamily del sistema consistente | La app usa sans-serif por defecto, sin personalidad de marca |
| 20 | ~~MEDIUM~~ RESUELTO | Diario | MoodHistoryAdapter migrado a item_historial_animo.xml con MaterialCardView + stroke + theming | ~~TextView debug~~ Items con diseno Material y ripple |
| 21 | ~~MEDIUM~~ RESUELTO | Splash | SplashActivity: android:windowBackground=@color/light_bg_primary agregado a Theme.Epycus (light y dark) | ~~Pantalla blanca~~ Sin fogonazo |
| 22 | ~~MEDIUM~~ RESUELTO | Registro | Spinners reemplazados por TextInputLayout + MaterialAutoCompleteTextView con endIconMode=dropdown_menu y labels flotantes | ~~Aspecto obsoleto~~ Dropdown menus Material 3 compliant |
| 23 | ~~MEDIUM~~ RESUELTO | Chat IA | android:theme="@style/Theme.Epycus" eliminado de item_chat_usuario.xml | ~~Problemas theming~~ TextView sin theme overlay |
| 24 | ~~MEDIUM~~ RESUELTO | Perfil | tvMiembroDesde con validacion null/empty. FechaRegistro ausente -> texto vacio en vez de "Miembro desde " | ~~Espacio vacio~~ Texto condicional segun disponibilidad |
| 25 | ~~MEDIUM~~ RESUELTO | Logros/Personajes | AlertDialog mejorados: iconos (🎭🏆), niveles de personajes, progreso de logros, estadisticas con emojis semanticos | ~~Dialogos pobres~~ Dialogos con iconos y datos contextuales |
| 26 | ~~LOW~~ RESUELTO | Global | Dead code eliminado: MainActivity.java, activity_main.xml, activity_dashboard.xml borrados + manifest limpiado | ~~APK size~~ Sin codigo muerto |
| 27 | ~~LOW~~ RESUELTO | Login | Boton Google ahora usa el SignInButton oficial con icono SVG de Google | ~~Apariencia amateur~~ Boton profesional |
| 28 | ~~LOW~~ RESUELTO | Bottom Nav | nav_bottom.xml actualizado a @string/nav_* con recursos agregados a strings.xml | ~~Sin localizacion~~ Soporta localizacion |
| 29 | ~~HIGH~~ RESUELTO | Global | build.gradle.kts: java.util.Properties no resuelto en Java 25. Parseo manual de secrets.properties | ~~Compilacion rota~~ Build script compatible con Java 25 |
| 30 | ~~HIGH~~ RESUELTO | Global | build.gradle.kts: compileSdk { version = release(36) } invalido. Cambiado a compileSdk = 36 | ~~Compilacion rota~~ Sintaxis estandar de AGP |
| 31 | ~~HIGH~~ RESUELTO | Misiones | dialog_nueva_mision.xml: etiquetas </FrameLayout> y </LinearLayout> invertidas | ~~Layout mal formado~~ Anidamiento corregido |
| 32 | ~~MEDIUM~~ RESUELTO | Diario | DiarioFragment.java: falta import android.widget.TextView en ViewHolder | ~~Compilacion rota~~ Import agregado |
| 33 | ~~LOW~~ RESUELTO | Global | DashboardActivity.java: dead code referenciaba ActivityDashboardBinding (layout ya eliminado) | ~~Compilacion rota~~ Archivo eliminado |
| 34 | ~~MEDIUM~~ RESUELTO | Habitos | HabitosFragment.java: R.style.Widget.Epycus.Chip.Filter requiere underscores en R (Widget_Epycus_Chip_Filter) | ~~Compilacion rota~~ Referencia corregida |
| 35 | ~~LOW~~ RESUELTO | Global | styles_epycus.xml: checkedChipBackgroundColor no existe en Material 1.14.0 | ~~Compilacion rota~~ Atributo eliminado |
| 36 | ~~LOW~~ RESUELTO | Global | scale_in.xml: @android:anim/fast_out_slow_in_interpolator no encontrado. Reemplazado por accelerate_decelerate_interpolator | ~~Compilacion rota~~ Interpolador standard |
| 37 | ~~MEDIUM~~ RESUELTO | Global | Sistema de color M3 incompleto: faltaban 13 roles M3 (onPrimaryContainer, secondaryContainer, tertiary, errorContainer, surfaceVariant, outline, etc.) y 18 attrs personalizados | ~~MD3 score bajo~~ Mapa M3 completo con 23 roles, 12 nuevos colores, light+dark unificados |
| 38 | ~~HIGH~~ RESUELTO | Global | FragmentTransaction hide/show manual sin swipe, sin edge-to-edge, fixed 80dp margin hack | ~~UX flow bajo~~ ViewPager2 con swipe entre tabs, edge-to-edge con WindowCompat + insets, overlay Pomodoro separado, status bar transparente |

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
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** activity_registro.xml: Spinners reemplazados por TextInputLayout + MaterialAutoCompleteTextView con endIconMode="dropdown_menu".
- **Implementacion:** activity_registro.xml + RegistroActivity.java actualizados con hint "Genero" y dropdown.
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
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** activity_registro.xml: startIcon y endIcon con ic_calendar (color accent + tertiary). OnClickListener + DatePickerDialog ya existian.
- **Implementacion:** activity_registro.xml actualizado con icono calendario.
- **Esfuerzo:** S

---

## Dashboard (Inicio)

### Problema DSH-01: 4 stat cards sin responsividad
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** fragment_inicio.xml: 4 stat cards reorganizadas en 2 filas x 2 columnas con LinearLayouts anidados. Alto reducido a 90dp, textos a 20sp.
- **Implementacion:** fragment_inicio.xml actualizado con layout 2x2.
- **Esfuerzo:** M

### Problema DSH-02: shape_dot como icono de mision
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** shape_dot reemplazado por @drawable/ic_mision en fragment_inicio.xml.
- **Implementacion:** fragment_inicio.xml actualizado.
- **Esfuerzo:** S

### Problema DSH-03: Header card con fondo transparente
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** fragment_inicio.xml: cardBackgroundColor cambiado de transparent a ?attr/epAccentLight.
- **Implementacion:** fragment_inicio.xml actualizado.
- **Esfuerzo:** S

### Problema DSH-04: Animacion de entrada con datos parciales
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** InicioFragment.java: verificarCargaCompleta() ahora requiere dashboardDataLoaded && progresoDataLoaded para animar.
- **Implementacion:** InicioFragment.java actualizado.
- **Esfuerzo:** S

### Problema DSH-05: Sin indicador de datos offline
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** offlineBanner agregado con ic_wifi_off + texto "Mostrando datos sin conexion" sobre fondo warning.
- **Implementacion:** fragment_inicio.xml + InicioFragment.java + strings.xml + ic_wifi_off.xml.
- **Esfuerzo:** S

---

## Perfil

### Problema PER-01: recreate() en toggle de tema destruye el fragmento
- **Severidad:** ~~CRITICAL~~ RESUELTO
- **Evidencia:** requireActivity().recreate() eliminado. ThemeManager.toggle() ya llama a AppCompatDelegate.setDefaultNightMode() que recrea automaticamente.
- **Implementacion:** PerfilFragment.java + LoginActivity.java actualizados.
- **Esfuerzo:** M

### Problema PER-02: Dialogo de configuracion monolitico
- **Severidad:** ~~CRITICAL~~ RESUELTO
- **Evidencia:** Theme toggle separado como boton independiente en perfil. Dialogo de configuracion ahora solo maneja editar perfil (nombre, email, carreras).
- **Implementacion:** PerfilFragment.java reestructurado.
- **Esfuerzo:** L

### Problema PER-03: Carga asincrona de carreras sin feedback
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** Boton Guardar deshabilitado hasta cargar carreras (exito o fallo). Loading overlay sobre el dialog.
- **Implementacion:** DialogEditarPerfil.java actualizado.
- **Esfuerzo:** S

### Problema PER-04: Menu de perfil con emojis como iconos
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** Emojis reemplazados por vector drawables (ic_personajes, ic_logros, ic_estadisticas, ic_config) con tint por atributo de tema.
- **Implementacion:** 4 nuevos drawables vector + fragment_perfil.xml actualizado.
- **Esfuerzo:** S

---

## Diario

### Problema DIA-01: MoodHistoryAdapter programatico sin layout
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** MoodHistoryAdapter ahora infla item_historial_animo.xml con MaterialCardView + stroke + theming.
- **Implementacion:** item_historial_animo.xml creado, MoodHistoryAdapter.java refactorizado.
- **Esfuerzo:** S

### Problema DIA-02: Mood selector sin indicacion clara de seleccion
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** ic_check_circle agregado en mood_button layout, Visibility toggleado al seleccionar/deseleccionar.
- **Implementacion:** mood_button.xml actualizado + DiarioFragment.java logica de seleccion.
- **Esfuerzo:** S

### Problema DIA-03: Chat Edy como Activity separada rompe navegacion
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** DiarioFragment ahora llama a ((MainContainerActivity) getActivity()).navegarAIAChat() en vez de lanzar Intent directo.
- **Implementacion:** DiarioFragment.java actualizado.
- **Esfuerzo:** S

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
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** mostrarEmpty() con 3 mensajes diferenciados: sin habitos, filtro sin resultados, error conexion.
- **Implementacion:** HabitosFragment.java actualizado + strings.xml.
- **Esfuerzo:** S

---

## Misiones

### Problema MIS-01: Prioridad con colores hardcodeados
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** MisionAdapter.java migrado a TypedValue con ?attr/epError, ?attr/epWarning, ?attr/epSuccess.
- **Implementacion:** MisionAdapter.java actualizado.
- **Esfuerzo:** S

### Problema MIS-02: Sin loading state al crear/editar mision
- **Severidad:** ~~LOW~~ RESUELTO
- **Evidencia:** dialog_nueva_mision.xml envuelto en FrameLayout con overlay de loading. Botones deshabilitados durante llamada API.
- **Implementacion:** dialog_nueva_mision.xml + MisionesFragment.java actualizados.
- **Esfuerzo:** S

---

## Pomodoro

### Problema POM-01: Radial gradient en timer circle con artifacts
- **Severidad:** ~~HIGH~~ RESUELTO
- **Evidencia:** bg_timer_circle.xml cambiado de gradient type=radial a type=linear angle=135.
- **Implementacion:** bg_timer_circle.xml actualizado.
- **Esfuerzo:** S

### Problema POM-02: Texto de 10sp en stats
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** fragment_pomodoro.xml: labels de stats subidos a 12sp.
- **Implementacion:** fragment_pomodoro.xml actualizado.
- **Esfuerzo:** S

### Problema POM-03: Sesion activa no persiste entre rotaciones
- **Severidad:** ~~MEDIUM~~ RESUELTO
- **Evidencia:** PomodoroFragment.java:111-118. onSaveInstanceState guarda flags + onCreateView restaura estado + reanudarTimer() recrea CountDownTimer si isRunning=true.
- **Implementacion:** PomodoroFragment.java ya maneja ciclo completo.
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
4. ✅ Agregar contentDescription/importantForAccessibility a todos los ImageView (12 ImageViews corregidos: 5 check marks diario, ivPersonaje inicio, ivAvatar perfil, 4 iconos menu perfil, ivThemeIcon)
5. ✅ Cambiar 10sp a 12sp en stats del Pomodoro
9. ✅ Scroll position preservado al cambiar tabs (DSH-06 resuelto)
10. ✅ Spinners nativos reemplazados por Exposed Dropdown Menus Material (REG-01)
11. ✅ Emojis del perfil reemplazados por vector drawables (PER-04)
12. ✅ dimen.xml creado con sistema de medidas unificado (GLO-01)
13. ✅ Anim resources creados (fade_in, fade_out, slide_up, slide_down, scale_in)
14. ✅ Vector drawables creados (ic_personajes, ic_logros, ic_estadisticas, ic_config, ic_calendar, ic_wifi_off, ic_check_circle)
15. ✅ Header card fondo transparente corregido a ?attr/epAccentLight
16. ✅ Animacion entrada dashboard espera ambas cargas (dashboard + progreso)
17. ✅ Stat cards reorganizados 2x2
18. ✅ Checkmark en mood selector
19. ✅ Calendar icon en campo fecha registro
20. ✅ Dialogo personajes/logros/estadisticas con iconos y datos
6. ✅ Cambiar nav_bottom.xml a usar @string references
7. ✅ Reemplazar ic_mood_normal en inputs por iconos semanticos (email, lock, person)
8. ✅ Reemplazar shape_dot por icono real en mision diaria
21. ✅ Compilacion exitosa tras corregir: build.gradle.kts (secrets + compileSdk), dialog_nueva_mision.xml (anidamiento), DiarioFragment.java (imports), DashboardActivity.java (dead code), HabitosFragment.java (R.style), styles_epycus.xml (checkedChipBackgroundColor), scale_in.xml (interpolator)
22. ✅ Sistema de color M3 completo: 12 nuevos colores (containers, surface variants), 18 nuevos attrs ep*, 23 M3 roles mapeados en ambos temas
23. ✅ ViewPager2 + BottomNavigationView (swipe entre tabs, FragmentStateAdapter, Pomodoro overlay separado)
24. ✅ Edge-to-edge + window insets (WindowCompat.setDecorFitsSystemWindows, transparent status bar, inset padding)

---

## Roadmap UX

### Semana 1 - Bloqueantes ✅ COMPLETED
- ✅ ~~Arreglar launcher icon~~ (WEBP 1.8MB -> adaptive icon vectorial en mipmap)
- ✅ ~~Implementar SplashScreen API Android 12+~~ (core-splashscreen + installSplashScreen)
- ✅ ~~Eliminar GOOGLE_CLIENT_ID del build.gradle~~ (usar secrets.properties)
- ✅ ~~Agregar android:tint a todos los iconos del bottom nav~~ + style corregido
- ✅ ~~Eliminar dead code~~ (MainActivity, activity_main, activity_dashboard)

### Semana 2 - UI System ✅ COMPLETED
- ✅ Crear dimens.xml y unificar medidas
- ✅ Reemplazar Spinners nativos por Exposed Dropdown Menus de Material
- ✅ Reemplazar emojis del perfil por vector drawables
- ✅ Estandarizar corner radii (20dp botones, 16dp cards, 28dp xl)
- ✅ Crear sistema de color M3 completo (23 roles M3 + 18 attrs ep*)
- ✅ Widget.Epycus.Chip.Filter agregado
- ✅ Mood icons migrados a ?attr/ep*
- ✅ Prioridad colores migrados a ?attr/ep*

### Semana 3 - UX Flow ✅ COMPLETED
- ✅ Reemplazar FragmentTransaction hide/show por ViewPager2 + BottomNavigationView (swipe entre tabs, FragmentStateAdapter)
- ✅ Separar theme toggle del dialogo de configuracion (boton independiente en perfil)
- ✅ Implementar edge-to-edge con WindowCompat.setDecorFitsSystemWindows + windowInsets para statusBar + navigationBar
- ✅ Agregar indicador offline en dashboard

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
- [ ] Accesibilidad: content descriptions corregidas (12 ImageViews). 12sp minimo aplicado. Falta TalkBack testing completo
- [ ] Rendimiento: Overdraw por layouts anidados
- [ ] Permisos: Solo INTERNET. OK pero sin notificaciones
- [ ] Pantallas Grandes: No probado. Layouts con fixed heights pueden fallar
- [x] Dark Mode: Iconos de bottom nav con tint + style corregido. Mood icons adaptativos
- [x] Localizacion: nav_bottom con @string references
- [x] Crash Prevention: Boton Guardar deshabilitado hasta carga completa. Loading states en misión y diálogo perfil
- [ ] Versionado: versionCode=1, versionName=1.0. Sin estrategia
- [ ] Firma: Signing config definido pero requiere keystore.properties o env vars
- [ ] ProGuard: Configurado con proguard-android-optimize.txt + proguard-rules.pro
- [ ] Google Services: google-services.json no presente (ni en proyecto)
- [x] Google Client ID: Externalizado a secrets.properties
- [x] Dead Code: MainActivity.java, activity_main.xml, activity_dashboard.xml. ELIMINADO

