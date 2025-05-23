mariadb -u root -p$MARIADB_ROOT_PASSWORD --execute \
"CREATE USER IF NOT EXISTS 'shortener'@'%' IDENTIFIED BY '$URLS_DB_SHORTENER_PASSWORD';
GRANT ALL ON *.* TO 'shortener'@'%';
CREATE USER IF NOT EXISTS 'redirect'@'%' IDENTIFIED BY '$URLS_DB_REDIRECT_PASSWORD';
GRANT SELECT ON *.* TO 'redirect'@'%';
FLUSH PRIVILEGES;"