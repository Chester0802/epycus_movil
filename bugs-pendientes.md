# Bugs e Issues Pendientes — 24 Jun 2026

## 1. Perfil — Avatar sin imagen de personaje
**Pantalla:** Perfil > Avatar

**Problema:** El avatar de perfil no muestra ninguna imagen. En modo oscuro solo se ve un fondo morado. Debería mostrar la imagen del personaje seleccionado (solo la cabeza, no cuerpo completo).

**Solución esperada:** Usar el mismo recurso del personaje activo que se muestra en Inicio, recortado para mostrar solo la cabeza (ej. scaleType="centerCrop" con un container cuadrado).

---

## 2. Inicio — Header card: pedir descripciones de mejora
**Pantalla:** Inicio > Card superior (nombre + nivel + personaje)

**Contexto:** El usuario dice que la card está "genial" pero quiere sugerencias para mejorarla visualmente. Se piden **dos descripciones** de cómo se podría mejorar.

**(Las descripciones van aquí — el usuario solicita que se las dé primero)**

---

## 3. Pomodoro — Navegación entre tabs no funciona
**Pantalla:** Pomodoro > BottomNavigationView

**Problema:** Cuando se está en el fragmento Pomodoro y se presionan los botones de Hábitos, Misiones, Diario o Perfil, la navegación no funciona.

**Posible causa:** Algún listener, overlay o diálogo del Pomodoro está consumiendo el evento táctil o bloqueando la interacción con el BottomNavigationView.

---

## 4. Diario — Icono "Habla con Edy" muestra letra "E" en vez de robot
**Pantalla:** Diario > Botón "Habla con Edy"

**Problema:** El icono del chat con Edy muestra una letra "E" dentro de un círculo. Debería mostrarse un icono de robot.

**Posible solución:** Reemplazar el TextView con la letra "E" por un ImageView con un drawable de robot (ic_robot.xml).

---

## 5. Registro — Spinner de Género no carga opciones
**Pantalla:** Registro > Spinner "Género"

**Problema:** Al desplegar el selector de género no aparecen las opciones "Masculino" ni "Femenino". El dropdown se muestra vacío.

**Posible causa:** El adapter del MaterialAutoCompleteTextView no está recibiendo el array de recursos (R.array.generos) o el array no existe en strings.xml.

---

## 6. Perfil — Editar perfil: cambio de carrera no persiste
**Pantalla:** Perfil > Editar perfil > Carrera

**Problema:** Se selecciona una nueva carrera y aparece el mensaje "Carrera cambiada correctamente". Sin embargo, al volver a abrir el diálogo de edición, la carrera sigue mostrando el valor anterior.

**Posible causa:** La API devuelve éxito pero la actualización local (SharedPreferences, Room, o DataStore) no se guarda, o el campo en el diálogo se rellena desde el caché desactualizado antes de que la respuesta actualice el modelo local.

---

## 7. Perfil — Mis personajes: cambio de personaje no se aplica correctamente
**Pantalla:** Perfil > Mis personajes

**Problema:** Al seleccionar un personaje (ej. "Kai") se muestra el personaje femenino (Luna) en lugar del seleccionado. Al cambiar a otro (ej. "Ares") sigue mostrando el mismo personaje femenino.

**Posible causa:** El selector de personajes siempre devuelve o renderiza el mismo recurso drawable (Luna) independientemente del personaje seleccionado. Puede ser un error en el mapeo personaje → drawable en el adaptador o en la lógica de selección.
