# Auditoría UX/UI — Epycus

> Fecha: 24 Junio 2026 | Versión: App 1.0 | Tipo: Full Audit Pre-Play Store
> Equipo: UX Lead, UI Designer (MD3), Android Engineer Senior, Accessibility Specialist, Performance Engineer, Play Store Reviewer

---

## Resumen Ejecutivo

| Dimensión | Score | Estado |
|-----------|-------|--------|
| **UX General** | **42/100** | CRITICO - Multiples bloqueos de flujo, navegacion fragil, estados vacios pobres |
| **UI General** | **38/100** | CRITICO - Iconos rotos en dark mode, jerarquia inconsistente, tipografia sin sistema |
| **Material Design 3** | **29/100** | CRITICO - Sin sistema de color M3, esquinas inconsistentes, sin elevation system |
| **Accesibilidad** | **22/100** | CRITICO - Sin content descriptions, contraste insuficiente, 10sp texto, emojis como iconos |
| **Produccion** | **28/100** | CRITICO - Sin SplashScreen API, dead code, Google Client ID expuesto |

**Evaluacion General: NO APTO PARA PLAY STORE**

Se identificaron **2 criterios bloqueantes** que impedirian la publicacion y **47 hallazgos** entre criticos y altos.

---

## Hallazgos Criticos

| # | Severidad | Pantalla | Problema | Impacto |
|---|-----------|----------|----------|---------|
| 1 | ~~CRITICAL~~ RESUELTO | Global | ic_logo.webp 1.82MB reemplazado por ic_logo.png (47KB). AndroidManifest actualizado a @mipmap/ic_launcher con adaptive icon | ~~Rechazo Play Store~~ Icono optimizado + adaptive icon operativo |
| 2 | ~~CRITICAL~~ RESUELTO | Global | AndroidManifest.xml actualizado a @mipmap/ic_launcher con foreground bitmap del logo real. Background gradient intacto | ~~Rechazo Play Store~~ Adaptive icon compliant |
| 3 | CRITICAL | Global | Sin SplashScreen API de Android 12+. Usa Handler.postDelayed legacy. Sin dependencia core-splashscreen | Experiencia rota en Android 12+: doble splash + parpadeo |
| 4 | CRITICAL | Global | GOOGLE_CLIENT_ID hardcodeado en build.gradle.kts:42 expuesto a cualquiera con acceso al APK | Riesgo de seguridad. Cualquier decompilacion roba el Client ID |
| 5 | CRITICAL | Global | 6 iconos de bottom nav (ic_home, ic_habits, ic_mision, ic_journal, ic_profile, ic_streak) usan #FF000000 fillColor sin android:tint | En dark mode estos iconos son INVISIBLES |
| 6 | CRITICAL | Dashboard | SwipeRefreshLayout wrapping ScrollView con FragmentTransaction hide/show. Bottom nav no guarda estado | Scroll position perdido al cambiar tabs. Experiencia frustrante |
| 7 | CRITICAL | Perfil | requireActivity().recreate() en toggle de tema sin preservar estado del fragmento | Pantalla en blanco temporal. Perdida de scroll. Camara lenta |
| 8 | CRITICAL | Perfil | Dialog de Configuracion mezcla editar perfil, cambiar tema, contrasena y notificaciones. Carga asincrona sin feedback | Usuario hace clic en Guardar antes de que carguen las carreras -> crash |
| 9 | HIGH | Global | Dialog_editar_perfil.xml usa Widget.Material3.TextInputLayout.OutlinedBox (estilo por defecto) en vez de Widget.Epycus.Input | Inconsistencia visual: input sin los colores de la app |
| 10 | HIGH | Global | Mood icons (ic_mood_*.xml) usan @color/mood_* hardcodeados en vez de ?attr/ep* | No se adaptan a dark mode. Colores fijos rompen el tema Solo Leveling |
| 11 | HIGH | Dashboard | Card de Mision Diaria usa @drawable/shape_dot como icono (un circulo blanco) | Placeholder sin reemplazar. Parece bug, no feature |
| 12 | HIGH | Login | Boton Google es un LinearLayout con texto G en vez de usar el boton oficial de Google Sign-In | Violacion de brand guidelines de Google. Puede causar rechazo |
| 13 | HIGH | Pomodoro | bg_timer_circle.xml usa gradient type=radial con gradientRadius=50% en shape oval | Artefactos visuales en ciertas densidades de pantalla |
| 14 | HIGH | Habitos | SwipeRefreshLayout no tiene como hijo directo la vista scrollable. RecyclerView dentro de LinearLayout | Pull-to-refresh no funciona correctamente |
| 15 | HIGH | Global | epRoundedXl definido como 12dp en attrs. Material 3 especifica 28dp para rounded corners grandes | No cumple con MD3 specification |
| 16 | HIGH | Misiones | tvMisionPrioridad.setTextColor(color) con colores hardcodeados (R.color.priority_*) sin adaptarse al tema | Texto de prioridad Alta puede tener contraste insuficiente en dark mode |
| 17 | MEDIUM | Global | No existe dimens.xml | Cada layout define sus propios valores numericos, imposible mantener consistencia |
| 18 | MEDIUM | Global | res/anim/ no existe. Cero animaciones definidas como recursos | Transiciones usan android.R.anim.fade_in/out genericos |
| 19 | MEDIUM | Global | res/font/ no existe. No se usa tipografia personalizada ni fontFamily del sistema consistente | La app usa sans-serif por defecto, sin personalidad de marca |
| 20 | MEDIUM | Diario | MoodHistoryAdapter crea TextViews en codigo Java sin inflar layouts | Sin theming, sin ripple, sin contenedor. Parece depuracion |
| 21 | MEDIUM | Splash | SplashActivity no usa android:windowBackground personalizado. Tema tarda en aplicar | Pantalla blanca momentanea antes del splash |
| 22 | MEDIUM | Registro | Spinners no tienen labels ni Material DropDownFields. Usan android.widget.Spinner con background drawable | Aspecto obsoleto, no Material 3 |
| 23 | MEDIUM | Chat IA | android:theme="@style/Theme.Epycus" en item_chat_usuario.xml | Uso incorrecto de android:theme. Puede causar problemas de theming |
| 24 | MEDIUM | Perfil | tvMiembroDesde usa android:text="" (vacio) y se rellena desde API | Si la API falla, se ve un espacio vacio que parece error |
| 25 | MEDIUM | Logros/Personajes | AlertDialog con builder.setItems() y listas planas. Sin imagenes, sin progreso, sin diseno | Dialogos pobres que rompen la experiencia RPG |
| 26 | LOW | Global | Dead code: MainActivity.java, activity_main.xml, activity_dashboard.xml declarados en manifest | Codigo muerto que confunde y aumenta APK size |
| 27 | LOW | Login | String G hardcodeado para boton Google en vez de icono vectorial SVG de Google | Apariencia amateur |
| 28 | LOW | Bottom Nav | nav_bottom.xml usa strings literales (Inicio, Habitos, etc.) en vez de @string referencias | No soporta localizacion |

---

## Auditoria por Pantalla

---

## Splash

### Problema SPL-01: Sin SplashScreen API Android 12+
- **Severidad:** CRITICAL
- **Evidencia:** SplashActivity.java usa Handler(Looper.getMainLooper()).postDelayed(() -> {...}, 1500). No hay dependencia core-splashscreen en gradle. No hay SplashScreen.installSplashScreen().
- **Impacto:** En Android 12+, el sistema muestra su propio splash ANTES de que la Activity se renderice. Doble splash: sistema + app. Experiencia entrecortada.
- **Solucion:** Agregar dependencia core-splashscreen. En onCreate() antes de super.onCreate() llamar a SplashScreen.installSplashScreen(this).
- **Esfuerzo:** S

### Problema SPL-02: Pantalla blanca antes del splash
- **Severidad:** MEDIUM
- **Evidencia:** activity_splash.xml usa android:background="?attr/epBgPrimary" pero el tema base Theme.Material3.DayNight.NoActionBar tiene window background blanco.
- **Impacto:** Fogonazo blanco (light theme) antes de que se pinte el layout.
- **Solucion:** Agregar item name=android:windowBackground al tema con el color de fondo.
- **Esfuerzo:** S

### Problema SPL-03: ProgressBar sin descripcion
- **Severidad:** LOW
- **Evidencia:** ProgressBar en splash sin contentDescription.
- **Impacto:** Screen reader no anuncia que la app esta cargando.
- **Solucion:** Agregar android:contentDescription="@string/cargando" al ProgressBar.
- **Esfuerzo:** S

---

## Login

### Problema LOG-01: Boton Continuar con Google no oficial
- **Severidad:** HIGH
- **Evidencia:** activity_login.xml:143-169 define un LinearLayout con un TextView G y texto plano. No usa com.google.android.gms.common.SignInButton oficial ni sus estilos.
- **Impacto:** Viola las Google Brand Guidelines. Play Store podria rechazar. Sin icono SVG de Google.
- **Solucion:** Usar com.google.android.gms.common.SignInButton con setSize(SignInButton.SIZE_WIDE) y setColorScheme(SignInButton.COLOR_LIGHT).
- **Esfuerzo:** S

### Problema LOG-02: Theme toggle con recreate()
- **Severidad:** HIGH
- **Evidencia:** LoginActivity.java:71-74 llama a ThemeManager.getInstance(this).toggle(); recreate();
- **Impacto:** recreate() destruye y recrea la Activity sin transicion. Datos de formularios se pierden.
- **Solucion:** Usar AppCompatDelegate.setDefaultNightMode() directamente sin recreate(). Guardar estado con onSaveInstanceState().
- **Esfuerzo:** S

### Problema LOG-03: Icono de email inapropiado
- **Severidad:** LOW
- **Evidencia:** activity_login.xml:68 usa app:startIconDrawable="@drawable/ic_mood_normal" (cara amarilla) como icono del campo email.
- **Impacto:** Icono de estado de animo normal como icono de email es confuso.
- **Solucion:** Cambiar por icono de email de Material Design.
- **Esfuerzo:** S

### Problema LOG-04: Forgot password touch target pequeno
- **Severidad:** MEDIUM
- **Evidencia:** activity_login.xml:104-114. TextView tvRecuperarContrasena con android:padding="4dp". Touch target menor a 48dp.
- **Impacto:** Dificultad para usuarios con dedos grandes. No cumple Material Design touch target guideline.
- **Solucion:** Cambiar a android:padding="12dp".
- **Esfuerzo:** S

---

## Registro

### Problema REG-01: Spinners no Material
- **Severidad:** MEDIUM
- **Evidencia:** activity_registro.xml:120-139. Usa Spinner nativo con background drawable en vez de Exposed Dropdown Menu de Material.
- **Impacto:** Los spinners parecen de otra app. Sin label flotante, sin icono dropdown.
- **Solucion:** Reemplazar por TextInputLayout con app:endIconMode="dropdown_menu" y AutoCompleteTextView.
- **Esfuerzo:** M

### Problema REG-02: Checkbox terminos sin link real
- **Severidad:** MEDIUM
- **Evidencia:** activity_registro.xml:141-148. Checkbox con texto "Acepto los terminos y condiciones" pero no hay enlace clickeable a los terminos.
- **Impacto:** Usuario acepta terminos que no puede leer. Posible problema legal.
- **Solucion:** Agregar ClickableSpan que abra URL de terminos.
- **Esfuerzo:** S

### Problema REG-03: Mismos iconos de email para nombre
- **Severidad:** LOW
- **Evidencia:** activity_registro.xml:30 y 48: ambos campos (Nombre y Email) usan app:startIconDrawable="@drawable/ic_mood_normal".
- **Impacto:** Inconsistencia.
- **Solucion:** Usar ic_person para nombre y ic_email para email.
- **Esfuerzo:** S

### Problema REG-04: Fecha de nacimiento sin DatePicker visual
- **Severidad:** MEDIUM
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
- **Severidad:** HIGH
- **Evidencia:** fragment_inicio.xml:463: card de Mision Diaria usa android:src="@drawable/shape_dot" (circulo blanco solido).
- **Impacto:** Placeholder sin reemplazar. Parece bug.
- **Solucion:** Reemplazar con ic_mision o icono de mision real.
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
- **Severidad:** CRITICAL
- **Evidencia:** PerfilFragment.java:81-84: ThemeManager.getInstance(requireContext()).toggle(); requireActivity().recreate();
- **Impacto:** Activity.recreate() destruye y recrea toda la Activity incluyendo FragmentManager. Perdida de scroll, estado, referencias.
- **Solucion:** Usar AppCompatDelegate.setDefaultNightMode() sin recreate.
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
- **Severidad:** HIGH
- **Evidencia:** fragment_habitos.xml:2-7. SwipeRefreshLayout contiene LinearLayout, no vista scrollable directa.
- **Impacto:** Pull-to-refresh puede fallar o activarse incorrectamente.
- **Solucion:** SwipeRefreshLayout debe envolver directamente al RecyclerView.
- **Esfuerzo:** M

### Problema HAB-02: Chips creados programaticamente sin estilos
- **Severidad:** MEDIUM
- **Evidencia:** HabitosFragment.java:255-289. Chips con new Chip(requireContext()) sin estilo base.
- **Impacto:** No heredan estilos de styles_epycus.xml. Si cambia el tema, los chips no se actualizan.
- **Solucion:** Usar new Chip(new ContextThemeWrapper(requireContext(), R.style.Widget.Epycus.Chip)).
- **Esfuerzo:** S

### Problema HAB-03: Empty state confunde filtro con sin-datos
- **Severidad:** MEDIUM
- **Evidencia:** HabitosFragment.java:165-185. Cuando filtrados.isEmpty() muestra "Crea tu primer habito" aunque haya habitos de otra categoria.
- **Solucion:** Tener tres estados: sin habitos, sin resultados de filtro, error conexion.
- **Esfuerzo:** S

---

## Misiones

### Problema MIS-01: Prioridad con colores hardcodeados
- **Severidad:** HIGH
- **Evidencia:** MisionAdapter.java:93-104. Switch con strings "Alta"/"Media" y R.color.priority_* fixos.
- **Impacto:** Si se internacionaliza la app, los colores no se aplican. Rojo #ef4444 puede no contrastar en dark mode.
- **Solucion:** Usar enum con @IntDef y colores basados en atributos del tema (?attr/epError, ?attr/epWarning, ?attr/epSuccess).
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
- **Severidad:** MEDIUM
- **Evidencia:** fragment_pomodoro.xml:154, 183, 218. Labels de stats usan 10sp.
- **Impacto:** No cumple WCAG AA (minimo 12sp). Ilegible en pantallas pequenas.
- **Solucion:** Subir a minimo 12sp.
- **Esfuerzo:** S

### Problema POM-03: Sesion activa no persiste entre rotaciones
- **Severidad:** MEDIUM
- **Evidencia:** PomodoroFragment.java:111-118. onSaveInstanceState restaura flags pero CountDownTimer no se reanuda.
- **Solucion:** En onCreateView si isRunning, llamar reanudarTimer() automaticamente.
- **Esfuerzo:** S

### Problema POM-04: trackColor hardcodeado
- **Severidad:** LOW
- **Evidencia:** fragment_pomodoro.xml:57: app:trackColor="#33FFFFFF".
- **Impacto:** Color blanco fijo se ve mal en light theme (fondos rosas).
- **Solucion:** Cambiar a ?attr/epSurfaceBorder.
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

## Quick Wins (< 1 dia)

1. Agregar android:tint="?attr/epTextPrimary" a todos los iconos del bottom nav con fillColor=#FF000000
2. Reemplazar @drawable/ic_logo por @mipmap/ic_launcher en AndroidManifest.xml
3. Eliminar dead code: MainActivity.java, activity_main.xml, activity_dashboard.xml
4. Agregar contentDescription a todos los ImageView sin descripcion
5. Cambiar 10sp a 12sp en stats del Pomodoro
6. Cambiar nav_bottom.xml a usar @string references
7. Reemplazar ic_mood_normal en inputs por iconos semanticos (email, lock, person)
8. Reemplazar shape_dot por icono real en mision diaria

---

## Roadmap UX

### Semana 1 - Bloqueantes
- Arreglar launcher icon (WEBP 1.8MB -> adaptive icon vectorial en mipmap)
- Implementar SplashScreen API Android 12+
- Eliminar GOOGLE_CLIENT_ID del build.gradle (usar secrets.properties)
- Agregar android:tint a todos los iconos del bottom nav
- Eliminar dead code

### Semana 2 - UI System
- Crear dimens.xml y unificar medidas
- Reemplazar Spinners nativos por Exposed Dropdown Menus de Material
- Reemplazar emojis del perfil por vector drawables
- Estandarizar corner radii (20dp botones, 16dp cards)
- Crear sistema de color M3 completo

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
- [ ] Splash API Android 12+: NO implementado. BLOQUEANTE
- [ ] Accesibilidad: Sin content descriptions en 80% de iconos. 10sp texto. REPROBADO
- [ ] Rendimiento: ic_logo 1.8MB en drawable. Overdraw por layouts anidados
- [ ] Permisos: Solo INTERNET. OK pero sin notificaciones
- [ ] Pantallas Grandes: No probado. Layouts con fixed heights pueden fallar
- [ ] Dark Mode: Iconos de bottom nav invisibles. Mood icons con colores fijos. REPROBADO
- [ ] Localizacion: strings.xml solo espanol. nav_bottom con literales hardcodeados
- [ ] Crash Prevention: PerfilFragment puede crash si API lenta. Sin try-catch en carga asincrona
- [ ] Versionado: versionCode=1, versionName=1.0. Sin estrategia
- [ ] Firma: Signing config definido pero requiere keystore.properties o env vars
- [ ] ProGuard: Configurado con proguard-android-optimize.txt + proguard-rules.pro
- [ ] Google Services: google-services.json no presente (ni en proyecto)
- [ ] Google Client ID: Expuesto en build.gradle.kts. RIESGO DE SEGURIDAD
- [ ] Dead Code: MainActivity.java, activity_main.xml, activity_dashboard.xml. ELIMINAR

