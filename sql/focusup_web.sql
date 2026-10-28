CREATE DATABASE IF NOT EXISTS focusup_web
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE focusup_web;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombres       VARCHAR(100)        NOT NULL,
    apellidos     VARCHAR(100)        NOT NULL,
    cedula        VARCHAR(20)         NOT NULL UNIQUE,
    correo        VARCHAR(150)        NOT NULL UNIQUE,
    contrasena    VARCHAR(255)        NOT NULL,
    fecha_nac     DATE,
    fecha_registro TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    activo        TINYINT(1)          DEFAULT 1
);

-- Datos de prueba
INSERT INTO usuarios (nombres, apellidos, cedula, correo, contrasena, fecha_nac) VALUES
('Daniel',  'Castiblanco', '1000123456', 'daniel@focusup.com',  MD5('daniel123'),  '2000-05-15'),
('María',   'González',    '1000234567', 'maria@focusup.com',   MD5('maria456'),   '1998-09-22'),
('Andrés',  'Martínez',    '1000345678', 'andres@focusup.com',  MD5('andres789'),  '2001-03-10');