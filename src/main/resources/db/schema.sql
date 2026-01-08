CREATE TABLE IF NOT EXISTS boards (
    id VARCHAR(45) PRIMARY KEY,
    api_key VARCHAR(45) UNIQUE NOT NULL,
    owner_token VARCHAR(90) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_board_api_key (api_key),
    INDEX idx_board_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS mock_rules (
    id VARCHAR(45) PRIMARY KEY,
    board_id VARCHAR(45) NOT NULL,
    api_key VARCHAR(45) NOT NULL,
    method VARCHAR(20) NOT NULL,
    path VARCHAR(255) NOT NULL,
    headers VARCHAR(3200),
    body VARCHAR(10000),
    status_code INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_mock_board_id_key (board_id),
    INDEX idx_mock_api_key (api_key),
    INDEX idx_mock_created_at (created_at),
    CONSTRAINT fk_mock_rules_board
        FOREIGN KEY (board_id)
            REFERENCES boards(id)
);