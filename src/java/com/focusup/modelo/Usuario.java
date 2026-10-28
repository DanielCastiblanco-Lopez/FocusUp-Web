package com.focusup.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase modelo que representa un usuario del sistema FocusUp.
 * Corresponde a la entidad 'usuarios' en la base de datos MySQL.
 *
 * Convenciones aplicadas:
 *  - Atributos privados (encapsulamiento)
 *  - Métodos públicos getter/setter
 *  - Nombres de atributos en camelCase
 *  - Nombres de clase en PascalCase
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class Usuario {

    // ── Atributos privados ────────────────────────────────────────────────────
    private int           id;
    private String        nombres;
    private String        apellidos;
    private String        cedula;
    private String        correo;
    private String        contrasena;
    private LocalDate     fechaNacimiento;
    private LocalDateTime fechaRegistro;
    private boolean       activo;

    // ── Constructores ─────────────────────────────────────────────────────────

    /**
     * Constructor vacío requerido para instanciación general.
     */
    public Usuario() {}

    /**
     * Constructor completo para crear un nuevo usuario (sin id ni fechaRegistro,
     * ya que los asigna la base de datos automáticamente).
     *
     * @param nombres         nombres del usuario
     * @param apellidos       apellidos del usuario
     * @param cedula          número de cédula (único)
     * @param correo          correo electrónico (único)
     * @param contrasena      contraseña en texto plano (se encripta en la capa DAO)
     * @param fechaNacimiento fecha de nacimiento del usuario
     */
    public Usuario(String nombres, String apellidos, String cedula,
                   String correo, String contrasena, LocalDate fechaNacimiento) {
        this.nombres         = nombres;
        this.apellidos       = apellidos;
        this.cedula          = cedula;
        this.correo          = correo;
        this.contrasena      = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.activo          = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getId()                      { return id; }
    public String getNombres()              { return nombres; }
    public String getApellidos()            { return apellidos; }
    public String getCedula()               { return cedula; }
    public String getCorreo()               { return correo; }
    public String getContrasena()           { return contrasena; }
    public LocalDate getFechaNacimiento()   { return fechaNacimiento; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public boolean isActivo()               { return activo; }

    /** Retorna el nombre completo del usuario (nombres + apellidos). */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(int id)                              { this.id = id; }
    public void setNombres(String nombres)                 { this.nombres = nombres; }
    public void setApellidos(String apellidos)             { this.apellidos = apellidos; }
    public void setCedula(String cedula)                   { this.cedula = cedula; }
    public void setCorreo(String correo)                   { this.correo = correo; }
    public void setContrasena(String contrasena)           { this.contrasena = contrasena; }
    public void setFechaNacimiento(LocalDate fechaNac)     { this.fechaNacimiento = fechaNac; }
    public void setFechaRegistro(LocalDateTime fechaReg)   { this.fechaRegistro = fechaReg; }
    public void setActivo(boolean activo)                  { this.activo = activo; }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Usuario{"
             + "id=" + id
             + ", nombre='" + getNombreCompleto() + '\''
             + ", cedula='" + cedula + '\''
             + ", correo='" + correo + '\''
             + ", activo=" + activo
             + '}';
    }
}
