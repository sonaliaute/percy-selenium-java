@echo off 
set NODE_TLS_REJECT_UNAUTHORIZED=0 
npx percy exec -- mvn test -Dtest=BajajDemoTest 
pause 
