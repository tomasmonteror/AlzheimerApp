# AlzheimerApp 🧠

Aplicación Android orientada a la estimulación cognitiva en pacientes con Alzheimer y deterioro cognitivo leve/moderado.

---

## 🎯 Objetivos del proyecto

- Favorecer la **estimulación cognitiva** mediante ejercicios interactivos.
- Mantener y reforzar **memoria visual, atención sostenida y reconocimiento**.
- Adaptar la dificultad progresivamente según el nivel del usuario.
- Permitir el uso de **imágenes personalizadas** (familiares, objetos cotidianos).
- Proporcionar una herramienta útil tanto para:
  - Uso doméstico (cuidadores/familia)
  - Contexto clínico (terapeutas, neuropsicólogos)

---

## 🧠 Enfoque clínico

El diseño está pensado para:

- Pacientes con:
  - Alzheimer en fases iniciales y avanzadas
  - Deterioro cognitivo leve
- Apoyo en terapias de:
  - Rehabilitación cognitiva
  - Estimulación de memoria episódica y semántica
- Uso de **refuerzos positivos** (feedback visual)
- Interfaz:
  - Sencilla, accesible y con carga cognitiva minimizada.

---

## 🎮 Tipos de juegos

### 1. Juego de reconocimiento
- Las cartas aparecen **boca arriba**.
- El usuario debe seleccionar las parejas correctas.
- Diseñado para fases más avanzadas, favoreciendo la asociación visual e identificación.

### 2. Juego de emparejamiento (Memoria)
- El usuario debe encontrar parejas de cartas ocultas.
- Entrena la memoria de trabajo y la atención.
- Adaptación progresiva mediante el incremento del número de elementos.

### 3. Encuentra las Diferencias 🆕
- Comparación de dos imágenes (original y modificada) en paralelo.
- El usuario debe identificar cambios sutiles pulsando sobre la pantalla.
- **Beneficios:**
  - Potencia la **atención selectiva**.
  - Trabaja el **rastreo visual**.
  - Estimula la paciencia y la concentración.

---

## 🛠️ Herramientas de Creación y Edición

Una de las características clave de la app es que no es estática; permite crear contenido adaptado:

### Editor de Diferencias
- **Creación personalizada:** Permite al terapeuta o familiar cargar dos imágenes propias y definir los puntos de diferencia manualmente.
- **Interfaz táctil:** Basta con tocar sobre la imagen para establecer el área de colisión (`DifferenceArea`) y el radio de detección.
- **Persistencia:** Los niveles creados se guardan para ser utilizados en las sesiones de juego.

---

## 🖼️ Gestión de imágenes personalizadas

- **Carga flexible:** Importación de fotos de familiares, objetos de la casa o lugares conocidos para reforzar la **memoria emocional**.
- **Clasificación:** Organización de imágenes en categorías (Objetos, Recompensas).
- **Control total:** Sistema de reordenación mediante *drag & drop* y eliminación selectiva.

---

## 🔧 Tecnologías utilizadas

- **Kotlin** & **Jetpack Compose** (UI declarativa moderna).
- **Coil:** Gestión eficiente y carga de imágenes (incluyendo soporte para `android_asset`).
- **Canvas API:** Dibujo de elementos gráficos dinámicos (círculos de detección y aciertos).
- **Navigation Compose:** Gestión fluida de flujos entre pantallas.
- **Pointer Input:** Detección de gestos avanzada para el marcado de diferencias.

---

## 📱 Características destacadas

- Interfaz adaptada a accesibilidad.
- Feedback visual inmediato (círculos rojos en edición, verdes en aciertos).
- Sistema de coordenadas normalizadas (0.0 a 1.0) para que los niveles sean compatibles con cualquier resolución de pantalla.
- Diseño orientado a usabilidad en pacientes con deterioro cognitivo.

---

## 🚀 Futuras mejoras

- Feedback auditivo y vibración háptica.
- Registro de progreso y estadísticas de rendimiento del paciente.
- Modo terapeuta con configuración avanzada de tiempos y dificultad.
- Exportación/Importación de niveles personalizados entre dispositivos.

---

## 📌 Nota
Este proyecto tiene un enfoque funcional y experimental para apoyo terapéutico y estimulación cognitiva. No sustituye tratamiento médico profesional.

---

## 👤 Autor
Proyecto desarrollado por **Tomás Montero Ripoll** en el ámbito de investigación y desarrollo de herramientas digitales para la estimulación cognitiva.