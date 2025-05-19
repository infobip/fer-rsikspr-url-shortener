-- Očekujemo da recording-service definira SQL shemu urls baze.
-- Zato za integracijeske testove redirect-servicea unaprijed
-- definiramo testnu bazu sa gotovom schemom:

CREATE TABLE IF NOT EXISTS urls(
    short_code VARCHAR(32) PRIMARY KEY NOT NULL,
    full_url VARCHAR(2048) NOT NULL,
    customer_id VARCHAR(512) NOT NULL
);

TRUNCATE TABLE urls;

INSERT INTO urls (short_code, full_url, customer_id) VALUES ('WIJJnm', 'https://www.example.com/some/long/url', 'firstCustomer');
INSERT INTO urls (short_code, full_url, customer_id) VALUES ('StKHSQ', 'https://www.example.com/some/long/url', 'secondCustomer');