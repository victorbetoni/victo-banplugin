CREATE TABLE IF NOT EXISTS ban_log(
    id VARCHAR(36),
    player VARCHAR(36),
    issuer VARCHAR(36),
    reason VARCHAR(128),
    action ENUM('ban','unban'),
    issued_on VARCHAR(24),
    expire_on VARCHAR(24)
);
