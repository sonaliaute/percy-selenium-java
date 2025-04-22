@echo off
SET NODE_TLS_REJECT_UNAUTHORIZED=0
echo Running Percy test...
npx percy exec -- mvn test -Dtest=BajajDemoTest
pause