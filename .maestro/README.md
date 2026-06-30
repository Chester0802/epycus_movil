# Tests de UI con Maestro

Flujos black-box (YAML) para Epycus (`appId: es.epycus.app`). Validan los recorridos
clave y, en particular, los requisitos del *filtro humano*: navegación que no se rompe
y opción clara de cerrar sesión.

## Requisitos

1. **Emulador o dispositivo** conectado (`adb devices` lo muestra).
2. La **app instalada**. Genera e instala el debug:
   ```bash
   ./gradlew :app:installDebug
   ```
3. **Maestro** instalado (ya tienes Maestro Studio). CLI: `maestro --version`.
4. Un **usuario de prueba** válido (el login pega contra `app.epycus.es`).

## Credenciales

Se pasan por variables de entorno, no se hardcodean:

```bash
maestro test -e EMAIL=tu@correo.es -e PASSWORD=tuClave .maestro/00_smoke.yaml
```

## Flujos

| Archivo | Qué valida |
|---------|------------|
| `login.yaml` | Subflujo reutilizable (arranca limpio + inicia sesión). Lo usan los demás vía `runFlow`. |
| `01_login.yaml` | Inicio de sesión correcto. |
| `02_navegacion_tabs.yaml` | Las 5 pestañas del bottom nav no se rompen. |
| `03_navegacion_atras.yaml` | El fix de "atrás" (`OnBackPressedDispatcher`): Pomodoro → atrás → vuelve a Inicio sin salir. |
| `04_cerrar_sesion.yaml` | Cerrar sesión: diálogo de confirmación → vuelve a login. |
| `00_smoke.yaml` | Todo lo anterior en una sola sesión. |

## Ejecutar

```bash
# Un flujo concreto
maestro test -e EMAIL=... -e PASSWORD=... .maestro/02_navegacion_tabs.yaml

# Todos (ejecuta cada .yaml de la carpeta)
maestro test -e EMAIL=... -e PASSWORD=... .maestro/

# Maestro Studio (inspección interactiva de la jerarquía de la UI)
maestro studio
```

## Notas

- Los `id:` son los `resource-id` de Android (p. ej. `etCorreo`, `nav_perfil`,
  `btnCerrarSesion`, `cardFocus`, `tvTiempo`, `tvBienvenida`). Maestro hace match parcial.
- Si cambian textos/ids en la UI, actualiza los flujos.
- En un run de carpeta, `login.yaml` también se ejecuta suelto (es inofensivo: solo loguea).
