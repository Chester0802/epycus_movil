## Progress
### Done
- Explorada estructura completa del proyecto (Java puro, Single-Activity con fragments, MD3, MVVM-like)
- #6: Scroll position preservado al cambiar tabs (InicioFragment.onHiddenChanged)
- #8: Theme toggle separado del diálogo de configuración como botón independiente en perfil
- #13 (POM-01): bg_timer_circle.xml cambiado de radial a linear gradient (sin artefactos)
- #15 + #17: epRoundedXl=28dp + dimen.xml completo (spacing, corner radius, buttons, inputs, icons, text)
- #18: res/anim/ creado con 5 animaciones (fade_in, fade_out, slide_up, slide_down, scale_in)
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
- **Compilación exitosa**: 5 errores corregidos:
  - `app/build.gradle.kts`: `java.util.Properties` no resuelto → parseo manual de secrets.properties
  - `app/build.gradle.kts`: `compileSdk { version = release(36) }` inválido → `compileSdk = 36`
  - `dialog_nueva_mision.xml`: etiquetas `</FrameLayout>` y `</LinearLayout>` invertidas → corregido anidamiento
  - `DiarioFragment.java`: falta `import android.widget.TextView` en ViewHolder → agregado
  - `DashboardActivity.java`: dead code que referenciaba `ActivityDashboardBinding` (layout eliminado) → archivo eliminado
  - `HabitosFragment.java`: `R.style.Widget.Epycus.Chip.Filter` → `R.style.Widget_Epycus_Chip_Filter` (dots → underscores)
  - `styles_epycus.xml`: `checkedChipBackgroundColor` no encontrado → atributo eliminado
  - `scale_in.xml`: `@android:anim/fast_out_slow_in_interpolator` no encontrado → reemplazado por `accelerate_decelerate_interpolator`
- **Sistema de color M3 completo**: 12 nuevos colores (containers, surface/outline variants), 18 nuevos attrs ep*, 23 M3 roles mapeados en ambos temas (light Kawaii + dark Solo Leveling)

### In Progress
- (ninguno)

### Blocked
- #19 (tipografía personalizada): requiere archivos .ttf que no están en el proyecto
- POM-03: el código ya manejaba onSaveInstanceState + reanudarTimer(), no requiere cambios

## Key Decisions
- No se usó GridLayout para los stat cards porque layout_columnWeight no es estándar en framework GridLayout; se optó por LinearLayouts anidados (2 filas × 2 columnas)
- Theme toggle se eliminó del diálogo de configuración y se dejó como botón independiente en perfil (no requiere pantalla aparte)
- DIA-03 (Chat Edy) se dejó como Activity lanzada desde MainContainerActivity en lugar de BottomSheetDialogFragment para evitar refactor mayor
- mostrarCargandoMision recibe el overlay View directamente (no dialog.findViewById) para evitar problemas de compatibilidad

## Next Steps
- ~~Verificar que el proyecto compile~~ ✅ COMPILACIÓN EXITOSA
- Hacer commit de todos los cambios

## Relevant Files
- app/src/main/res/values/dimens.xml: sistema completo de dimensiones
- app/src/main/res/anim/: 5 animaciones como recursos
- app/src/main/res/drawable/: 7 nuevos drawables vector
- app/src/main/res/layout/item_historial_animo.xml: layout inflado por MoodHistoryAdapter
- app/src/main/res/values/themes.xml + values-night/themes.xml: mapa M3 completo (23 roles)
- app/src/main/res/values/colors.xml: 12 nuevos colores M3 (containers, surface/outline variants)
- app/src/main/res/values/attrs.xml: 18 nuevos attrs ep* (onPrimaryContainer, secondaryContainer, tertiary, etc.)
- AuditoriaUX.md: documento de auditoría actualizado con todos los cambios
