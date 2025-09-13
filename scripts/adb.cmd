@echo off

if exist %LOCALAPPDATA%\Android\sdk\platform-tools\adb.exe PATH=%LOCALAPPDATA%\Android\sdk\platform-tools;%PATH%

adb %*
