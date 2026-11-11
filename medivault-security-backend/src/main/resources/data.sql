-- Todas las cuentas usan la contrasena: Password123!  (hash BCrypt factor 10)
INSERT INTO USUARIOS (id, username, password_hash, nombre_completo, email, rol, estado) VALUES
 (1, 'admin_root',      '$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK', 'Administrador Auditor', 'admin@medivault.cr',  'ROLE_ADMIN',    'ACTIVO'),
 (2, 'dr_ramirez',      '$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK', 'Dra. Laura Ramirez',    'lramirez@medivault.cr','ROLE_DOCTOR',  'ACTIVO'),
 (3, 'dr_solis',        '$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK', 'Dr. Andres Solis',      'asolis@medivault.cr', 'ROLE_DOCTOR',   'ACTIVO'),
 (4, 'paciente_carlos', '$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK', 'Carlos Mora Vargas',    'carlos@correo.cr',    'ROLE_PACIENTE', 'ACTIVO'),
 (5, 'paciente_maria',  '$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK', 'Maria Jimenez Rojas',   'maria@correo.cr',     'ROLE_PACIENTE', 'ACTIVO'),
 (6, 'paciente_inactivo','$2a$10$f.0BwR9cCeKFXXzN/NmwGuYwZm6BiC27whwvkzY/Nj4vKTJ1ddFfK','Pedro Inactivo',        'pedro@correo.cr',     'ROLE_PACIENTE', 'BLOQUEADO');

ALTER TABLE USUARIOS ALTER COLUMN id RESTART WITH 100;

INSERT INTO EXPEDIENTES_MEDICOS (id, paciente_id, medico_asignado_id, codigo_expediente, diagnostico_principal, notas_clinicas, nivel_confidencialidad) VALUES
 (101, 4, 2, 'EXP-2026-101', 'Diabetes Mellitus tipo 2', 'Paciente con control glicemico irregular. Metformina 850mg cada 12h.', 'ALTO'),
 (102, 5, 3, 'EXP-2026-102', 'Hipertension arterial',    'Presion 150/95. Se inicia Losartan 50mg diario y dieta hiposodica.', 'MEDIO'),
 (103, 4, 3, 'EXP-2026-103', 'Rinitis alergica',         'Tratamiento con loratadina 10mg segun necesidad.', 'BAJO');

ALTER TABLE EXPEDIENTES_MEDICOS ALTER COLUMN id RESTART WITH 200;
