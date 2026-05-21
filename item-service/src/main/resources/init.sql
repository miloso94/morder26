-- ─────────────────────────────────────────────────────────────
-- itemdb schema
-- Executed once by MySQL docker-entrypoint on first container start
-- ─────────────────────────────────────────────────────────────

USE itemdb;

CREATE TABLE IF NOT EXISTS items (
    uid               VARCHAR(36)  NOT NULL,
    item_id           VARCHAR(100) NOT NULL,
    name              VARCHAR(255) NOT NULL,
    available_quantity INT         NOT NULL DEFAULT 0,
    created_at        DATETIME(6)  NOT NULL,
    last_updated_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (uid),
    UNIQUE KEY uq_item_id (item_id),
    CONSTRAINT chk_qty CHECK (available_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reservation_logs (
    uid VARCHAR(36) NOT NULL DEFAULT (UUID()),
    order_id           VARCHAR(100) NOT NULL,
    item_id            VARCHAR(100) NOT NULL,
    requested_quantity INT          NOT NULL,
    status             ENUM('RESERVED','INSUFFICIENT','ITEM_NOT_FOUND') NOT NULL,
    message            TEXT,
    processed_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (uid),
    INDEX idx_order_id (order_id),
    INDEX idx_item_id  (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO items (uid, item_id, name, available_quantity, created_at, last_updated_at) VALUES
    (UUID(), 'item-1', 'Plazma Keks',  50, NOW(6), NOW(6)),
    (UUID(), 'item-2', 'Coca Cola',  30, NOW(6), NOW(6)),
    (UUID(), 'item-3', 'Smoki',      100, NOW(6), NOW(6)),
    (UUID(), 'item-4', 'Fruvita',    20, NOW(6), NOW(6)),
    (UUID(), 'item-5', 'Najlepse Zelje',  5, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE item_id = item_id;
