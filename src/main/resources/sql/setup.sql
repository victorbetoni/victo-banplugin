CREATE TABLE IF NOT EXISTS banishments(
    player VARCHAR(36),
    reason VARCHAR(128),
    starts_in DATETIME,
    ends_in DATETIME
);
