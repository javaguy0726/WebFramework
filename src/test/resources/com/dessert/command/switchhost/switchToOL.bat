@echo off 
IF NOT EXIST C:\Windows\System32\drivers\etc\hosts.bak COPY /y hosts C:\Windows\System32\drivers\etc\hosts.bak
DEL hosts
COPY /y .\myhosts\77.hosts C:\Windows\System32\drivers\etc\hosts