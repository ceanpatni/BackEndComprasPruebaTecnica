PRAGMA foreign_keys = ON;

-- =========================
-- TABLA: INVENTARIO
-- =========================
CREATE TABLE IF NOT EXISTS inventario(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0),
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_producto_inventario UNIQUE (producto_id)
);

-- =========================
-- TABLA: COMPRAS
-- =========================
CREATE TABLE IF NOT EXISTS compras(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario >= 0),
    total REAL NOT NULL CHECK (total >= 0),
    fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- TABLA: AUDITORIA
-- =========================
CREATE TABLE IF NOT EXISTS auditoria(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entidad TEXT NOT NULL,
    entidad_id INTEGER,
    request_json TEXT NOT NULL,
    response_json TEXT,
    mensaje TEXT NOT NULL,
    exitoso BOOLEAN NOT NULL CHECK (exitoso IN (0,1)),
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuario(
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 username TEXT UNIQUE,
 password TEXT,
 role TEXT
);

INSERT OR IGNORE INTO usuario(username,password,role)
VALUES('admin','1234','ROLE_ADMIN');