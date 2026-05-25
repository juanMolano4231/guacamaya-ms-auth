To run in local
    docker compose up --build > output.txt

To test

curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"1234"}' \
  -c cookies.txt

curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"1234"}' \
  -c cookies.txt

curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"1234"}' \
  -c cookies.txt


curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"1234"}' \
  -c cookies.txt


curl -X POST http://localhost:8081/auth/refresh \
  -b cookies.txt \
  -c cookies.txt


curl -X GET http://localhost:8081/auth/me \
  -b cookies.txt