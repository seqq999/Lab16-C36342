DROP TABLE IF EXISTS AUDIT_LOGS;
DROP TABLE IF EXISTS EXPEDIENTES_MEDICOS;
DROP TABLE IF EXISTS USUARIOS;

CREATE TABLE USUARIOS (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password_hash    VARCHAR(100) NOT NULL,
    nombre_completo  VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    rol              VARCHAR(20)  NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE EXPEDIENTES_MEDICOS (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id            BIGINT       NOT NULL,
    medico_asignado_id     BIGINT       NOT NULL,
    codigo_expediente      VARCHAR(30)  NOT NULL UNIQUE,
    diagnostico_principal  VARCHAR(255) NOT NULL,
    notas_clinicas         TEXT         NOT NULL,
    nivel_confidencialidad VARCHAR(20)  NOT NULL,
    fecha_registro         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exp_paciente FOREIGN KEY (paciente_id) REFERENCES USUARIOS(id),
    CONSTRAINT fk_exp_medico   FOREIGN KEY (medico_asignado_id) REFERENCES USUARIOS(id)
);

-- Reto B: bitacora de auditoria de seguridad (solo INSERT desde la app)
CREATE TABLE AUDIT_LOGS (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    ip_origen      VARCHAR(45)  NOT NULL,
    accion         VARCHAR(40)  NOT NULL,
    codigo_http    INT          NOT NULL,
    recurso        VARCHAR(255),
    fecha_evento   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
