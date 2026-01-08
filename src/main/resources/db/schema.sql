CREATE TABLE IF NOT EXISTS boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_id VARCHAR(45) UNIQUE NOT NULL,
    api_key VARCHAR(45) UNIQUE NOT NULL,
    owner_token VARCHAR(90) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_board_id_key (board_id),
    INDEX idx_api_key (api_key),
    INDEX idx_created_at (created_at)
);