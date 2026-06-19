# Epycus App — Prompts para Prototipos Visuales (IA Generativa)

> **Propósito**: Prompts para que una IA generativa de imágenes (Midjourney, DALL-E, Stable Diffusion, Leonardo) cree prototipos visuales de las pantallas de la app Android Epycus.
>
> **Estilo general**: App mobile Android, Material 3, diseño limpio, colores rosa pastel (#ff6b9d) para modo claro y morado oscuro (#8b5cf6) para modo oscuro. Interfaz en español.

---

## Contexto General (incluir en todos los prompts)

```
App móvil Android (Material 3 gamificada) para profesionales universitarios.
Dos temas: Light Mode (fondo rosa pastel #fef5ff, acento rosa #ff6b9d, texto #4a2545)
y Dark Mode (fondo azul oscuro #0a0e1a, acento morado #8b5cf6, texto #e8eaed).
Layout: 1080x2400px, ratio 9:20, diseño mobile vertical.
```

---

## 1. Pantalla de Inicio (Dashboard)

```
Mockup de pantalla de inicio de app Android Material 3 gamificada.
Arriba: saludo "Hola, Marco" con avatar circular con iniciales "MC".
Tarjeta de progreso: barra de XP con porcentaje, nivel actual "Nv.5", racha "12 días".
Indicadores: "3 hábitos pendientes hoy", "2 misiones activas".
Frase motivacional del día con autor.
Tema claro: fondo rosa pastel #fef5ff, acentos rosa #ff6b9d.
Estilo Material 3, bordes redondeados, sombras suaves.
1080x2400 vertical.
```

## 2. Pantalla de Hábitos

```
Mockup de lista de hábitos diarios en app Android Material 3.
Checklist visual con 5 hábitos: "Meditar 10min" ✅, "Leer 30min" ⏳, "Ejercicio" ⏳,
"Beber 2L agua" ✅, "Estudiar inglés" ⏳.
Cada hábito muestra: nombre, categoría, XP potencial (+25 XP).
Checkbox circular a la izquierda, tarjeta blanca con borde redondeado.
Contador arriba: "5 hábitos hoy".
Modo claro: rosa pastel.
1080x2400 vertical.
```

## 3. Pantalla Pomodoro (Foco)

```
Mockup de temporizador Pomodoro en app Android Material 3.
Timer grande en el centro: "25:00" con círculo de progreso alrededor.
Estado: "Foco" o "Pausa" arriba del timer.
Botón circular grande abajo: "Iniciar" / "Pausar".
Contadores: "Ciclos: 3", "Hoy: 2 completados".
Interfaz limpia, minimalista, modo oscuro (fondo #0a0e1a, acento morado #8b5cf6).
1080x2400 vertical.
```

## 4. Pantalla de Diario / Estado de Ánimo

```
Mockup de registro de estado de ánimo en app Android.
5 cards de estado de ánimo en horizontal: "Genial" (verde), "Bien" (azul),
"Normal" (amarillo), "Cansado" (naranja), "Estresado" (rojo).
Pregunta guía arriba: "¿Cómo te sientes hoy?".
Abajo: frase reflexiva del día.
Botón flotante "Hablar con Edy" para chat con IA.
Modo claro, rosa pastel.
1080x2400 vertical.
```

## 5. Pantalla de Chat con IA (Edy)

```
Mockup de chat con IA en app Android Material 3.
Burbujas de chat: usuario derecha (rosa), IA izquierda (gris claro con avatar circular "E").
Input abajo con campo de texto y botón enviar.
Header: "Edy" con icono de cerebro/IA.
Primer mensaje: "¡Hola! Soy Edy, tu asistente de bienestar. ¿Cómo te sientes hoy?".
Fondo blanco, modo claro, Material 3.
1080x2400 vertical.
```

## 6. Pantalla de Perfil

```
Mockup de perfil de usuario en app Android Material 3.
Arriba: avatar circular grande con iniciales "MC", nombre "Marco Castillo",
correo "marco@ejemplo.com", carrera "Ingeniería de Sistemas".
Tarjetas de estadísticas: racha actual, XP total, nivel.
Menú de opciones: "Mis personajes", "Logros", "Estadísticas", "Configuración",
"Modo oscuro" (con toggle), "Cerrar sesión" (botón outline).
Modo claro, rosa pastel.
1080x2400 vertical.
```

## 7. Pantalla de Login

```
Mockup de pantalla de login de app Android Material 3.
Logo: círculo con inicial "E" en rosa, nombre "Epycus" abajo.
Campos: correo electrónico, contraseña (con toggle visibilidad).
Botón "Iniciar sesión" (relleno rosa).
Botón "Continuar con Google" (outline con icono de Google).
Link "¿No tienes cuenta? Crear cuenta" abajo.
Toggle de tema "Modo oscuro" en esquina superior derecha.
Modo claro, rosa pastel.
1080x2400 vertical.
```

## 8. Pantalla de Registro

```
Mockup de formulario de registro en app Android Material 3.
Campos: nombre, correo, contraseña, confirmar contraseña, fecha de nacimiento,
género (spinner), carrera (spinner desplegable con opciones).
Botón "Crear cuenta" rosa.
Checkbox "Acepto términos y condiciones".
Diseño limpio, scrolleable, modo claro rosa pastel.
1080x2400 vertical.
```

## 9. Pantalla de Misiones (opcional)

```
Mockup de lista de misiones/quests en app Android Material 3 gamificada.
Tarjetas con: nombre de misión, descripción, barra de progreso, XP recompensa,
fecha límite, prioridad (Alta roja, Media amarilla, Baja verde).
Estados visuales: "Completada" con check verde, "Activa" resaltada,
"Bloqueada" en gris.
Modo oscuro, fondo #0a0e1a, acento morado.
1080x2400 vertical.
```

---

## Cómo usar

1. Copia el **Contexto General** + el prompt de la pantalla deseada
2. Pégalo en Midjourney, DALL-E, Leonardo, o Stable Diffusion
3. Especifica: `--ar 9:20` (Midjourney) o `aspect ratio 9:20`
4. Pide "UI mockup, app screen, mobile design, high fidelity"

Para variaciones, cambia `"modo claro"` por `"modo oscuro"` en cualquier prompt.
