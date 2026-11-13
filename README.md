# FocusUp Web

Aplicación web de FocusUp (Spring Boot 3.2 · Java 21 · Thymeleaf · JPA · MySQL).
SENA — Análisis y Desarrollo de Software, ficha 3336243.

## Módulos

| Módulo | Ruta | Quién |
|---|---|---|
| Registro / inicio de sesión | `/registro`, `/login` | público |
| Inicio (resumen, racha, calendario) | `/inicio` | usuario autenticado |
| Tareas | `/tareas` | usuario autenticado (solo las suyas) |
| Pomodoro | `/pomodoro` | usuario autenticado |
| Estadísticas | `/estadisticas` | usuario autenticado |
| Mi cuenta | `/cuenta` | usuario autenticado |
| Administración de usuarios | `/usuarios` | solo ADMIN |

## Ejecutar

1. Encender MySQL (la base `focusup_db` y las tablas se crean solas).
2. Ejecutar `FocusUpApplication` o `mvn spring-boot:run`.
3. Abrir `http://localhost:8080/focusup/login`.
   Administrador inicial: `admin@focusup.com` / `Admin123*` (cámbiala al entrar).
4. Pruebas: `mvn test` (usan H2 en memoria, no tocan MySQL).

## Estructura

```
FocusUp-Web/
├── docs/INTEGRACION.md   decisiones de diseño, seguridad y permisos (para sustentar)
├── src/main/java/com/focusup/
│   ├── controlador/      pantallas y API
│   ├── servicio/         reglas de negocio
│   ├── repositorio/      acceso a datos (Spring Data)
│   ├── modelo/           entidades (Usuario, Tarea, SesionPomodoro)
│   ├── dto/              formularios y respuestas
│   ├── seguridad/        sesión, CSRF, autenticación, rol admin
│   └── config/           rutas protegidas, datos iniciales
├── src/main/resources/   templates (Thymeleaf), css, application.properties
└── src/test/             pruebas unitarias e de integración
```
