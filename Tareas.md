# Tareas - Epycus App

## ✅ Errores de build corregidos

- [x] `boxCornerRadius` no existe en Material3 `TextInputLayout` → eliminado del estilo `Widget.Epycus.Input`
- [x] `android:topLeft`, `android:topRight`, `android:bottomLeft`, `android:bottomRight` en `<corners>` no existen → corregido a `topLeftRadius`, `topRightRadius`, `bottomLeftRadius`, `bottomRightRadius`

## ✅ Warnings de lint/IDE corregidos

### Expression lambda simplificadas (3 archivos)
- [x] `DiarioFragment.java` — `v -> { startActivity(...); }` → `v -> startActivity(...)`
- [x] `HabitosFragment.java` — `v -> { Snackbar.make(...); }` → `v -> Snackbar.make(...)`
- [x] `LoginActivity.java` — `v -> { startActivity(...); }` → `v -> startActivity(...)`

### @Nullable en onCreate (6 archivos)
- [x] `MainActivity.java`
- [x] `DashboardActivity.java`
- [x] `IaChatActivity.java`
- [x] `LoginActivity.java`
- [x] `RegistroActivity.java`
- [x] `MainContainerActivity.java`

### @NonNull en parámetros (varios archivos)
- [x] `AuthInterceptor.java` — `intercept(@NonNull Chain chain)`
- [x] `DiarioFragment.java` — `onResponse` y `onFailure` de Callbacks
- [x] `HabitosFragment.java` — `onResponse` y `onFailure` de Callbacks
- [x] `InicioFragment.java` — `onResponse` y `onFailure` de Callbacks
- [x] `PerfilFragment.java` — `onResponse` y `onFailure` de Callbacks
- [x] `IaChatActivity.java` — `onResponse` y `onFailure` de Callbacks

### Diamond operator <> (5 archivos)
- [x] `new Callback<RespuestaApi<Object>>()` → `new Callback<>()`

### @SuppressLint("SetTextI18n") añadido (9 archivos)
- [x] `DashboardActivity.java`
- [x] `InicioFragment.java`
- [x] `PerfilFragment.java`
- [x] `PomodoroFragment.java`
- [x] `LoginActivity.java`
- [x] `RegistroActivity.java`
- [x] `HabitoHoyAdapter.java`
- [x] `HabitosFragment.java`
- [x] `IaChatActivity.java`

## 🔲 Pendientes / Mejoras detectadas

### TODO en código
- [ ] `RegistroActivity.java:56` — `cargarCarreras()` no implementado (cargar carreras desde API y poblar spinner)

### Callbacks onFailure vacíos (sin feedback al usuario)
- [ ] `DiarioFragment.java:128` — onFailure vacío en `cargarPreguntaGuia()`
- [ ] `HabitosFragment.java:146` — onFailure vacío en `fallarHabito()`
- [ ] `InicioFragment.java:90-92` — onFailure solo oculta loading, sin mensaje
- [ ] `InicioFragment.java:118` — onFailure vacío en `cargarProgreso()`

### Catch blocks silenciosos
- [ ] `DiarioFragment.java:123` — `catch (Exception ignored) {}` traga excepciones
- [ ] `InicioFragment.java:113` — `catch (Exception ignored) {}` traga excepciones

### Posibles mejoras
- [ ] Extraer strings hardcodeadas a `strings.xml` (remover `@SuppressLint("SetTextI18n")`)
- [ ] Migrar a ViewBinding para eliminar `findViewById`
- [ ] Agregar logging en catch blocks
