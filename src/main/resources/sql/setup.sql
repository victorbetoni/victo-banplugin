CREATE TABLE IF NOT EXISTS ban_log(
    player VARCHAR(36),
    issuer VARCHAR(36),
    reason VARCHAR(128),
    action ENUM('ban','unban'),
    issued_on DATETIME,
    expire_on DATETIME
);
