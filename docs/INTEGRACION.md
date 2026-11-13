# FocusUp Web — diseño, seguridad y permisos

Una sola aplicación Spring Boot (`http://localhost:8080/focusup`) y una sola base de datos (`focusup_db`).
El módulo web de usuarios (Servlets/JSP + MySQL `focusup_web`) se integró aquí, conservando sus
reglas y agregando roles. El proyecto `FocusUp-Web` sigue existiendo como evidencia del módulo original.

## Flujo

```
/login ─┐
        ├─> /tareas (pantalla principal) ──> /inicio       (resumen, racha, calendario semanal)
/registro ┘          │                   ├─> /pomodoro     (temporizador; guarda sesiones)
                     │                   ├─> /estadisticas (semana / mes / total)
                     │                   ├─> /cuenta       (todos: ver/editar SOLO mis datos)
                     │                   └─> /usuarios     (solo ADMIN: ver/editar a todos)
```

## Módulos portados de la app Android (v3)

| Módulo Android | Página web | Cómo funciona |
|---|---|---|
| Inicio (Home) | `/inicio` | Saludo, pendientes, minutos de hoy, racha, próxima tarea (la pendiente con fecha límite más cercana) y calendario lunes–domingo con tareas por día |
| Pomodoro | `/pomodoro` | Temporizador en JavaScript (duración configurable, 25 por defecto; iniciar/pausar/reanudar/reiniciar; tarea pendiente opcional o "Sesión libre"). Al terminar hace `POST /pomodoro/sesiones` con token CSRF y se guarda en la tabla `sesiones_pomodoro` |
| Estadísticas + Resumen total | `/estadisticas?periodo=semana\|mes\|total` | Tareas completadas, minutos, sesiones, gráfico de minutos por día, últimas 5 sesiones; en "total": días estudiados, promedio diario, mejor semana, récord en un día, racha máxima |
| Perfil | `/cuenta` | Ya existía (Mi cuenta) |

Decisiones de diseño (para sustentar):
- **La racha no se guarda**: se calcula cada vez a partir de los datos. Un día es "activo" si el usuario terminó una sesión o completó una tarea (misma regla de la app). Si hoy aún no hay actividad, la racha cuenta desde ayer. Así no hay un contador que pueda quedar desincronizado. Lógica pura en `Racha` (fácil de probar).
- La tabla nueva es solo `sesiones_pomodoro`; el resto sale de `tareas` (`fechaCompletada`, `fechaLimite`).
- El navegador mide el tiempo, pero **el servidor valida**: minutos entre 1 y 180, y la tarea debe ser del usuario y estar pendiente (si no, se guarda como "Sesión libre", sin revelar la tarea ajena). Limitación honesta: el servidor no puede comprobar que el tiempo realmente transcurrió; en un sistema con reglas competitivas habría que guardar la hora de inicio en el servidor.
- El usuario de las sesiones sale siempre de la sesión HTTP, igual que en tareas.
- Los cálculos de estadísticas se hacen en memoria sobre los datos de un solo usuario (suficiente para este alcance); con mucho volumen se pasarían a consultas `GROUP BY`.
- Pantalla principal tras login sigue siendo `/tareas` (flujo pedido); "Inicio" está en el menú.

Pruebas nuevas: `RachaTest`, `PomodoroServicioTest`, `EstadisticasServicioTest` (unitarias, fecha fija) y 5 casos más en `AccesoPorRolTest` (rutas protegidas, sesión guardada al usuario correcto, CSRF, tarea ajena, aislamiento de datos).

## Quién puede qué

| Acción | Sin sesión | USUARIO | ADMIN |
|---|---|---|---|
| Registrarse / iniciar sesión | sí | — | — |
| Ver y gestionar **sus** tareas | no (→ login) | sí | sí |
| Ver y editar **sus** datos y contraseña (`/cuenta`) | no | sí | sí |
| Listar todos los usuarios (`/usuarios`) | no | **403** | sí |
| Editar a otro usuario, cambiar rol, activar/desactivar, restablecer clave | no | **403** | sí |
| Ver tareas de otro usuario | no | no (404) | no (las tareas son personales) |

Reglas extra: un usuario normal no puede ascenderse (el formulario de "Mi cuenta" no recibe rol ni estado);
un admin no puede quitarse su rol ni desactivarse; siempre debe quedar al menos un admin activo.

## Arquitectura (capas)

| Capa | Clases |
|---|---|
| Seguridad (nueva) | `CsrfInterceptor`, `AutenticacionInterceptor`, `AdminInterceptor`, `Sesion`, `WebConfig` |
| Controladores web | `AuthWebControlador`, `TareaControlador`, `CuentaControlador`, `UsuariosControlador`, `PanelControlador` (inicio/estadísticas), `PomodoroControlador` |
| Controladores API (JSON) | `AuthControlador`, `TareaApiControlador` |
| Servicios | `AuthServicio`, `UsuarioServicio`, `TareaServicio`, `PomodoroServicio`, `EstadisticasServicio`, `Racha` |
| Datos | `Usuario` (+`Rol`), `Tarea` (+`usuarioId`), `SesionPomodoro`, repositorios Spring Data |
| Arranque | `InicializadorDatos` (admin inicial, completa cuentas viejas, asigna tareas sin dueño) |

Decisión: se unió **sobre el backend de tareas** (no al revés) porque ya usaba BCrypt, JPA y tenía la API
que consumirá la app Android. Del módulo web se trajeron las reglas (validaciones, baja lógica, cédula/correo únicos),
no el código, porque Servlets/JSP y Spring Boot no se mezclan en un mismo servidor.

## Seguridad aplicada

- Contraseñas con **BCrypt** (se reemplaza el MD5 sin sal del módulo web).
- **Autorización en el servidor** con interceptores; ocultar el enlace "Usuarios" es solo comodidad.
- El id del usuario sale **siempre de la sesión**, nunca del formulario (evita editar datos ajenos cambiando un id).
- Rol y estado no existen en el formulario de "Mi cuenta" (evita ascenso de privilegios).
- Cada consulta de tareas filtra por dueño (`findByIdAndUsuarioId`); una tarea ajena responde "no encontrada".
- **CSRF**: todo POST web lleva token; las acciones que cambiaban datos por GET ahora son POST.
- Sesión: id nuevo al iniciar (contra fijación de sesión), 30 min, cookie `HttpOnly` y `SameSite=Lax`.
- Cuenta desactivada = pierde el acceso en la siguiente petición (el usuario se recarga de la BD en cada petición).
- Mensaje de login genérico (no revela si el correo existe). Thymeleaf escapa el HTML (XSS). JPA usa consultas parametrizadas (SQL injection).

## Cómo ejecutar

1. MySQL encendido. La app crea/actualiza las tablas sola (`ddl-auto=update`).
2. Ejecutar `FocusUpApplication` (o `mvn spring-boot:run`).
3. Entrar a `http://localhost:8080/focusup/login` con el administrador inicial:
   `admin@focusup.com` / `Admin123*` (de `application.properties`; **cámbiala en "Mi cuenta"**).
4. Cuentas que ya existían en `focusup_db` siguen funcionando (como USUARIO). Sus tareas viejas pasan al admin.
   Pueden completar su perfil en "Mi cuenta".

## Pruebas

`mvn test` ejecuta:
- `AuthServicioTest`, `TareaServicioTest`, `UsuarioServicioTest`: reglas de negocio (Mockito, sin BD).
- `AccesoPorRolTest`: la aplicación completa con H2 en memoria y peticiones HTTP simuladas
  (acceso sin sesión, 403 para usuario normal, no ascenso, tareas ajenas, CSRF, cuenta desactivada, API con sesión).

## API (Postman)

Ahora `/api/tareas/**` exige sesión: primero `POST /focusup/api/auth/login` (guarda la cookie `JSESSIONID`;
Postman la conserva sola). Sin sesión responde 401. `POST /focusup/api/auth/logout` cierra la sesión.

## Limitaciones conocidas (mejoras futuras)

- Sin bloqueo por intentos fallidos de login.
- Seguridad hecha a mano con interceptores; el siguiente paso natural es Spring Security.
- La app Android todavía no usa la sesión de la API (requeriría un token, p. ej. JWT).
- La contraseña de MySQL y la del admin inicial están en `application.properties`: moverlas a variables de entorno.
- La política de contraseñas es solo "mínimo 6 caracteres".
