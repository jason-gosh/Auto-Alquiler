-- ======================================================== 
--  (HU1)
-- ======================================================== 

-- ======================================================
-- 1. GEOGRAFÍA Y SEGURIDAD
-- ======================================================
CREATE TABLE IF NOT EXISTS locations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    department TEXT NOT NULL,
    municipality TEXT NOT NULL,
    country_id BIGINT NOT NULL,
    CONSTRAINT fk_country FOREIGN KEY (country_id) REFERENCES countries(id),
    CONSTRAINT unique_per_dept_muny_coty UNIQUE (department, municipality, country_id)

)//

CREATE UNIQUE INDEX IF NOT EXISTS idx_location_unique_dm ON locations (department, municipality)//
CREATE UNIQUE INDEX IF NOT EXISTS idx_location_unique_dmc ON locations (department, municipality, country_id)//

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    enabled INTEGER DEFAULT 1
)//

CREATE TABLE IF NOT EXISTS user_role (
    user_id INTEGER NOT NULL PRIMARY KEY,
    role_name TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)//

-- ======================================================
-- 2. TABLAS OPERATIVAS (FLOTA)
-- ======================================================
CREATE TABLE IF NOT EXISTS vehicles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plate TEXT NOT NULL UNIQUE CHECK (plate = UPPER(plate)),
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    year INTEGER NOT NULL,
    type TEXT NOT NULL,
    daily_rate INTGER NOT NULL,
    status TEXT NOT NULL,
    location_id INTEGER NOT NULL,
    location_details TEXT,
    admin_id INTEGER NOT NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (admin_id) REFERENCES users(id) 
    
)//

CREATE TABLE IF NOT EXISTS vehicles_images (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicle_id INTEGER NOT NULL,
    image_path TEXT NOT NULL,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
)//

-- ======================================================
-- 3. TABLAS ESPEJO (HISTORIAL DE AUDITORÍA)
-- ======================================================
CREATE TABLE IF NOT EXISTS vehicles_hist (
    hist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER, -- ID original del vehículo
    plate TEXT,
    brand TEXT,
    model TEXT,
    year INTEGER,
    type TEXT,
    daily_rate INTEGER,
    status TEXT,
    location_id INTEGER,
    location_details TEXT,
    admin_id INTEGER,
    operation_type TEXT, -- INSERT, UPDATE, DELETE
    operation_date DATETIME DEFAULT CURRENT_TIMESTAMP
)//

CREATE TABLE IF NOT EXISTS vehicles_images_hist (
    hist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER, -- ID original de la imagen
    vehicle_id INTEGER,
    image_path TEXT,
    operation_type TEXT,
    operation_date DATETIME DEFAULT CURRENT_TIMESTAMP
)//

-- ======================================================
-- 4. TRIGGERS DE AUDITORÍA (VEHÍCULOS)
-- ======================================================
DROP TRIGGER IF EXISTS trg_v_ins//
CREATE TRIGGER trg_v_ins AFTER INSERT ON vehicles BEGIN
    INSERT INTO vehicles_hist (id, plate, brand, model, year, type, daily_rate, status, location_id, location_details, admin_id, operation_type)
    VALUES (new.id, upper(new.plate), new.brand, new.model, new.year, new.type, new.daily_rate, new.status, new.location_id, new.location_details, new.admin_id, 'INSERT');
END//

DROP TRIGGER IF EXISTS trg_v_upd//
CREATE TRIGGER trg_v_upd AFTER UPDATE ON vehicles BEGIN
    INSERT INTO vehicles_hist (id, plate, brand, model, year, type, daily_rate, status, location_id, location_details, admin_id, operation_type)
    VALUES (new.id, upper(new.plate), new.brand, new.model, new.year, new.type, new.daily_rate, new.status, new.location_id, new.location_details, new.admin_id, 'UPDATE');
END//

DROP TRIGGER IF EXISTS trg_v_del//
CREATE TRIGGER trg_v_del BEFORE DELETE ON vehicles BEGIN
    INSERT INTO vehicles_hist (id, plate, brand, model, year, type, daily_rate, status, location_id, location_details, admin_id, operation_type)
    VALUES (old.id, upper(old.plate), old.brand, old.model, old.year, old.type, old.daily_rate, old.status, old.location_id, old.location_details, old.admin_id, 'DELETE');
END//

-- ======================================================
-- 5. TRIGGERS DE AUDITORÍA (IMÁGENES)
-- ======================================================
DROP TRIGGER IF EXISTS trg_img_ins//
CREATE TRIGGER trg_img_ins AFTER INSERT ON vehicles_images BEGIN
    INSERT INTO vehicles_images_hist (id, vehicle_id, image_path, operation_type)
    VALUES (new.id, new.vehicle_id, new.image_path, 'INSERT');
END//

DROP TRIGGER IF EXISTS trg_img_upd//
CREATE TRIGGER trg_img_upd AFTER UPDATE ON vehicles_images BEGIN
    INSERT INTO vehicles_images_hist (id, vehicle_id, image_path, operation_type)
    VALUES (new.id, new.vehicle_id, new.image_path, 'UPDATE');
END//

DROP TRIGGER IF EXISTS trg_img_del//
CREATE TRIGGER trg_img_del BEFORE DELETE ON vehicles_images BEGIN
    INSERT INTO vehicles_images_hist (id, vehicle_id, image_path, operation_type)
    VALUES (old.id, old.vehicle_id, old.image_path, 'DELETE');
END//


-- ======================================================== 
--  (HU2,HU3)
-- ======================================================== 

CREATE TABLE IF NOT EXISTS reservations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    contract_number TEXT UNIQUE NOT NULL,
    client_id INTEGER NOT NULL,
    vehicle_id INTEGER NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    total_amount INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    termination_reason TEXT DEFAULT NULL,
    updated_by TEXT DEFAULT NULL,
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
)//

-- ======================================================
-- 6. DETALLES DE USUARIO Y AUDITORÍA DE RESERVAS Y RELACIONES
-- ======================================================

CREATE TABLE IF NOT EXISTS identifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
)//

CREATE TABLE IF NOT EXISTS countries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    country TEXT NOT NULL UNIQUE,
    postal_number TEXT NOT NULL
)//

-- -- Tabla espejo para el historial de reservas (HU4)
CREATE TABLE IF NOT EXISTS reservations_hist (
    hist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER,
    contract_number TEXT,
    client_id INTEGER,
    vehicle_id INTEGER,
    start_date TEXT,
    end_date TEXT,
    total_amount INTEGER,
    status TEXT,
    termination_reason TEXT,
    updated_by TEXT,
    operation_type TEXT,
    operation_date DATETIME DEFAULT CURRENT_TIMESTAMP
)//

-- Tabla para datos complementarios del cliente (Obligatorios para la reserva)
CREATE TABLE IF NOT EXISTS user_detail (
    user_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address TEXT NOT NULL,
    id_type INTEGER NOT NULL,
    identification TEXT NOT NULL,
    country_id INTEGER NOT NULL,
    location_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_type) REFERENCES identifications(id),
    FOREIGN KEY (country_id) REFERENCES countries(id),
    FOREIGN KEY (location_id) REFERENCES locations(Id),
    CONSTRAINT unique_id_per_type UNIQUE (id_type, identification)
)//

-- ======================================================
-- 7. TRIGGERS DE AUDITORÍA (RESERVACIONES)
-- ======================================================

CREATE TRIGGER IF NOT EXISTS trg_res_ins AFTER INSERT ON reservations BEGIN
    INSERT INTO reservations_hist (id, contract_number, client_id, vehicle_id, start_date, end_date, total_amount, status, termination_reason, updated_by, operation_type)
    VALUES (new.id, new.contract_number, new.client_id, new.vehicle_id, new.start_date, new.end_date, new.total_amount, new.status, new.termination_reason, new.updated_by, 'INSERT');
END//

CREATE TRIGGER IF NOT EXISTS trg_res_upd AFTER UPDATE ON reservations BEGIN
    INSERT INTO reservations_hist (id, contract_number, client_id, vehicle_id, start_date, end_date, total_amount, status, termination_reason, updated_by, operation_type)
    VALUES (new.id, new.contract_number, new.client_id, new.vehicle_id, new.start_date, new.end_date, new.total_amount, new.status, new.termination_reason, new.updated_by, 'UPDATE');
END//

CREATE TRIGGER IF NOT EXISTS trg_res_del BEFORE DELETE ON reservations BEGIN
    INSERT INTO reservations_hist (id, contract_number, client_id, vehicle_id, start_date, end_date, total_amount, status, termination_reason, updated_by, operation_type)
    VALUES (old.id, old.contract_number, old.client_id, old.vehicle_id, old.start_date, old.end_date, old.total_amount, old.status, old.termination_reason, old.updated_by, 'DELETE');
END//

-- ======================================================
-- 8. PAGOS Y CONTROL ADMINISTRATIVO
-- ======================================================

CREATE TABLE IF NOT EXISTS payment_method(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL
)//

-- TABLA OPERATIVA DE PAGOS
CREATE TABLE IF NOT EXISTS payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reservation_id INTEGER NOT NULL,
    amount BIGINT NOT NULL,
    status TEXT NOT NULL CHECK(status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    payment_method_id INTEGER NOT NULL, 
    payment_date TEXT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(id) ON DELETE NO ACTION
)//

-- TABLA HISTÓRICA / AUDITORÍA DE PAGOS (READ-ONLY POR ENTORNO)
CREATE TABLE IF NOT EXISTS payments_hist (
    hist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER,
    reservation_id INTEGER,
    amount BIGINT,
    status TEXT,
    payment_method_hist_id INTEGER,
    payment_date TEXT,
    operation_type TEXT NOT NULL,
    action_timestamp TEXT DEFAULT CURRENT_TIMESTAMP
)//

CREATE TABLE IF NOT EXISTS payment_method_hist (
    hist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id INTEGER,
    name TEXT,
    operation_type TEXT NOT NULL,
    action_timestamp TEXT DEFAULT CURRENT_TIMESTAMP
)//
-- TRIGGERs: AUDITORÍA AUTOMÁTICA ANTES DE (CUD) PAGO 

CREATE TRIGGER IF NOT EXISTS trg_pay_ins
AFTER INSERT ON payments
BEGIN
    INSERT INTO payments_hist (id, reservation_id, amount, status, payment_date, payment_method_hist_id, operation_type)
    VALUES (NEW.id, NEW.reservation_id, NEW.amount, NEW.status, NEW.payment_date, NEW.payment_method_id, 'INSERT');
END//

CREATE TRIGGER IF NOT EXISTS trg_pay_upd
BEFORE UPDATE ON payments
BEGIN
    INSERT INTO payments_hist (id, reservation_id, amount, status, payment_date, payment_method_hist_id, operation_type)
    VALUES (NEW.id, NEW.reservation_id, NEW.amount, NEW.status, NEW.payment_date,  NEW.payment_method_id, 'UPDATE');
END//

CREATE TRIGGER IF NOT EXISTS trg_pay_del
BEFORE DELETE ON payments
BEGIN
    INSERT INTO payments_hist (id, reservation_id, amount, status, payment_date, payment_method_hist_id, operation_type)
    VALUES (OLD.id, OLD.reservation_id, OLD.amount, OLD.status, OLD.payment_date, OLD.payment_method_id, 'DELETE');
END//

-- payment_method

CREATE TRIGGER IF NOT EXISTS trg_pay_method_hist_ins
AFTER INSERT ON payment_method
BEGIN
    INSERT INTO payment_method_hist (id, name, operation_type)
    VALUES (NEW.id, NEW.name, 'INSERT');
END//

CREATE TRIGGER IF NOT EXISTS trg_pay_method_hist_upd
AFTER UPDATE ON payment_method
BEGIN
    INSERT INTO payment_method_hist (id, name, operation_type)
    VALUES (NEW.id, NEW.name, 'UPDATE');
END//

CREATE TRIGGER IF NOT EXISTS trg_pay_method_hist_del
BEFORE DELETE ON payment_method
BEGIN
    INSERT INTO payment_method_hist (id, name, operation_type)
    VALUES (OLD.id, OLD.name, 'UPDATE');
END//

-- ======================================================
-- 9. INDICES PARA AUMENTAR EL RENDIMIENTO DE LA BD
-- ======================================================
CREATE INDEX IF NOT EXISTS idx_reservations_vehicle_client ON reservations_hist(vehicle_id, client_id)//
CREATE INDEX IF NOT EXISTS idx_res_hist_user ON reservations_hist(client_id)//
CREATE INDEX IF NOT EXISTS idx_res_hist_vehicle ON reservations_hist(vehicle_id)//
CREATE INDEX IF NOT EXISTS idx_pay_hist_reservation ON payments_hist(reservation_id)//
CREATE INDEX IF NOT EXISTS idx_veh_hist_admin ON vehicles_hist(admin_id)//