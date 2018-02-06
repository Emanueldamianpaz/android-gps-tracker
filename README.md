# GPS Route Tracker

Aplicación que guarda una ruta de coordenadas en fichero con estándar KML y ofrece opción al usuario de mostrar la ruta en Google Maps.

## Cómo conectar el repositorio con Android Studio

Lo mejor es clonar el proyecto mediante New -> Proyect from version control -> GitHub.
Seguimos los pasos que aparecen y después accedemos a todas las operaciones git en la pestaña VCS.

También puedes mirar este [tutorial](https://androidstudiofaqs.com/tutoriales/como-usar-git-en-android-studio)

## ¿Cómo puedo colaborar?

En la página Issues puedes encontrar tareas por realizar (issues) y objetivos que acaparan muchas tareas (milestones).
* Programando
* Gestionando tareas: añadiendo, proponiendo funciones, clarificando, clasificando, ...
* Mejorando el fichero [README](README.md) y la documentación
* Limpiando GitHub de archivos que no sean necesarios para correr el proyecto

## Errores típicos

### Cannot resolve AppCompActivity

Las siguientes opciones pueden solucionarlo:

* Clean/Build/Rebuild project.
* File -> Invalidate Caches / Restart
* En gradle, cambiar el compile de AppCompActivity a otro, sincronizar y volver a ponerlo como originalmente estaba

### "Please select SDK"

No hemos encontrado una solución >.<

