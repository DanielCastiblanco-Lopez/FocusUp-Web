# FocusUp-Web

Aplicación web (Servlets + JSP + JDBC + MySQL) para gestión de usuarios de FocusUp.

## Arquitectura (MVC por capas)
| Capa | Paquete / carpeta | Responsabilidad |
|---|---|---|
| Vista | `web/WEB-INF/jsp` | JSP, siempre escapando con `HtmlUtil.escapar` |
| Controlador | `com.focusup.servlet` | Login, Logout, Registro, Usuario (CRUD) |
| Acceso a datos | `com.focusup.dao` | `IUsuarioDAO` / `UsuarioDAO` (PreparedStatement) |
| Modelo | `com.focusup.modelo` | `Usuario` |
| Utilidades | `com.focusup.util` | Validador, Encriptador, HtmlUtil |
| Conexión | `com.focusup.conexion` | `ConexionBD` (Singleton) |

## Requisitos
JDK 21+, Apache Tomcat 10.1 (Jakarta EE 10), MySQL 8, MySQL Connector/J, NetBeans (Ant).

## Puesta en marcha
1. `mysql -u root -p < sql/focusup_web.sql`
2. Ajustar usuario/clave en `ConexionBD.java`.
3. Abrir en NetBeans, *Clean and Build*, ejecutar sobre Tomcat: `http://localhost:8080/FocusUp-Web/`

## Pruebas unitarias
JUnit 4 en `test/`: validador, encriptador, HtmlUtil y modelo (32 pruebas). En NetBeans: *Test Project* (Alt+F6).

## Seguridad aplicada
Escape de salida (XSS), `PreparedStatement` (SQLi), regeneración de id de sesión al iniciar sesión,
cookie HttpOnly, timeout 30 min, mensajes de error genéricos con detalle solo en el log.
Pendiente: reemplazar MD5 por BCrypt/Argon2 y mover credenciales a variables de entorno.
