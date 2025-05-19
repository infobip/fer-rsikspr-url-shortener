#!/usr/bin/env bash
# Requires curl and jq tools.
# Requires system to be up and running. Run docker compose up before this script.
set -e
firstShortUrl=$(curl -s -X POST 'http://localhost:8080/v1/urls' \
-H 'content-type: application/json' \
--data '{"url": "https://www.infobip.com/docs/api", "customerId": "Infobip"}' | jq -r '.shortUrl')
secondShortUrl=$(curl -s -X POST 'http://localhost:8080/v1/urls' \
-H 'content-type: application/json' \
--data '{"url": "https://www.fer.unizg.hr/", "customerId": "FER"}' | jq -r '.shortUrl')
echo "GET ${firstShortUrl}
User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0

GET ${secondShortUrl}
User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0

GET http://localhost:8080/MadeUp
User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0

GET ${firstShortUrl}
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Safari/605.1.15

GET ${firstShortUrl}
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Safari/605.1.15

GET ${secondShortUrl}
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Safari/605.1.15

GET http://localhost:8080/MadeUp
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Safari/605.1.15

GET ${firstShortUrl}
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36

GET ${firstShortUrl}
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36

GET ${secondShortUrl}
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36

GET ${secondShortUrl}
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36

GET http://localhost:8080/MadeUp
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36" > ./targets.http