## Progress
### Done
- Explorada estructura completa del proyecto (Java puro, Single-Activity con fragments, MD3, MVVM-like)
- #6: Scroll position preservado al cambiar tabs (InicioFragment.onHiddenChanged)
- #8: Theme toggle separado del diálogo de configuración como botón independiente en perfil
- #13 (POM-01): bg_timer_circle.xml cambiado de radial a linear gradient (sin artefactos)
- #15 + #17: epRoundedXl=28dp + dimen.xml completo (spacing, corner radius, buttons, inputs, icons, text)
- #18: res/anim/ creado con 5 animaciones (fade_in, fade_out, slide_up, slide_down, scale_in)
- #19: res/font/ creado con Quicksand como Google Font descargable (sin .ttf en repo)
- #20 (DIA-01): MoodHistoryAdapter infla item_historial_animo.xml con MaterialCardView
- #22 (REG-01): Spinners nativos → Material Exposed Dropdown Menus en registro
- #24: tvMiembroDesde con validación null/empty
- #25: Diálogos personajes/logros/estadísticas con iconos y datos contextuales
- REG-04: Icono calendario en campo fecha (DatePickerDialog ya existente)
- DSH-01: Stat cards reorganizados en 2 filas × 2 columnas
- DSH-03: Header card de transparent a ?attr/epAccentLight
- DSH-04: verificarCargaCompleta() espera ambas cargas antes de animar
- DSH-05: offlineBanner con ic_wifi_off + texto "Mostrando datos sin conexión"
- PER-02: Diálogo config simplificado (theme toggle separado)
- PER-03: Botón Guardar deshabilitado hasta cargar carreras
- PER-04: Emojis → vector drawables (ic_personajes, ic_logros, ic_estadisticas, ic_config)
- DIA-02: Checkmark en mood buttons (ic_check_circle)
- DIA-03: DiarioFragment usa navegarAIAChat() vía MainContainerActivity
- HAB-03: mostrarEmpty() con 3 mensajes diferenciados
- MIS-02: Loading overlay en dialog_nueva_mision.xml; botones deshabilitados durante API
- build.gradle.kts: error sintaxis corregido en secrets.properties
- Creados 7 drawables vector + múltiples strings
- AuditoriaUX.md: actualizada con todos los RESUELTOs
- **Compilación exitosa**: 8 errores corregidos (build.gradle.kts, layouts, imports, dead code, R.style, styles, interpolator)
- **Sistema de color M3 completo**: 12 nuevos colores, 18 nuevos attrs ep*, 23 M3 roles en ambos temas
- **ViewPager2 + BottomNavigationView + edge-to-edge + insets**
- **Accessibilidad completa**: 38/38 ImageViews, 22/22 EditTexts, 7 Spinners, 11 ProgressBar guia TalkBack
- **Análisis de rendimiento**: max nesting depth=4, sin issues de overdraw significativos
- **Play Store ready**: signing config (keystore.properties + env vars), keystore.properties.example, versionado semántico documentado, ProGuard configurado, permisos revisados (INTERNET, POST_NOTIFICATIONS, USE/SCHEDULE_EXACT_ALARM)
- **Tipografía personalizada**: Quicksand vía Google Fonts descargable (Downloadable Fonts API, API 28+ nativo)
- **Play Store checklist actualizado**: permisos, ProGuard, pantallas grandes documentados en AuditoriaUX.md
- **Pomodoro 500 fix**: commit fdc6250 deployeado a /var/www/epycus-web/
- **Auth NRE fix**: null checks en Login/Refresh/Registro endpoints
- **AddApplicationPart removido**: API controllers ya no caen en pipeline MVC con antiforgery
- **nginx WebSocket fix**: map $connection_upgrade para SignalR
- **Glide OOM fix**: .override(300,600) en ivPersonaje

### In Progress
- (ninguno)

### Pending
- Build de Release APK para Aptoide
- Pruebas en pantalla plegada/desplegada y landscape
- TalkBack testing manual en todas las pantallas

## Key Decisions
- No se usó GridLayout para los stat cards porque layout_columnWeight no es estándar en framework GridLayout; se optó por LinearLayouts anidados (2 filas × 2 columnas)
- Theme toggle se eliminó del diálogo de configuración y se dejó como botón independiente en perfil (no requiere pantalla aparte)
- DIA-03 (Chat Edy) se dejó como Activity lanzada desde MainContainerActivity en lugar de BottomSheetDialogFragment para evitar refactor mayor
- mostrarCargandoMision recibe el overlay View directamente (no dialog.findViewById) para evitar problemas de compatibilidad

## Relevant Files
- app/src/main/res/values/dimens.xml: sistema completo de dimensiones
- app/src/main/res/anim/: 5 animaciones como recursos
- app/src/main/res/drawable/: 7 nuevos drawables vector
- app/src/main/res/layout/item_historial_animo.xml: layout inflado por MoodHistoryAdapter
- app/src/main/res/values/themes.xml + values-night/themes.xml: mapa M3 completo (23 roles)
- app/src/main/res/values/colors.xml: 12 nuevos colores M3 (containers, surface/outline variants)
- app/src/main/res/values/attrs.xml: 18 nuevos attrs ep* (onPrimaryContainer, secondaryContainer, tertiary, etc.)
- app/src/main/res/font/quicksand.xml: tipografía personalizada vía Google Fonts descargable
- AuditoriaUX.md: documento de auditoría actualizado con todos los cambios
