@ECHO OFF
setlocal

set TempFile_Name=%SystemRoot%\System32\BatTestUACin_SysRt%Random%.batemp
( echo "BAT Test UAC in Temp" >%TempFile_Name% ) 1>nul 2>nul
if exist %TempFile_Name% (
  ECHO Got Administrator Privilege
) else (
  if "%1"=="1" (
      ECHO Try run this script as Administrator fail.
      ECHO Make sure you are a member of the user group Administrators
      pause & exit /b 2
  )
  ECHO Cannot get Administrator Privilege...
  ECHO It will popup the UAC dialog, please click [Yes] to continue.
  ECHO Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
  ECHO UAC.ShellExecute "cmd.exe", "/k %~s0 %* 1", "", "runas", 1 >> "%temp%\getadmin.vbs"
  "%temp%\getadmin.vbs"
  exit /b 2
)
del /F /Q %TempFile_Name% 1>nul 2>nul
SET appName=Cloudbility YunAgent Service
SET install_dir=%programfiles(x86)%\Cloudbility\YunAgent

if ""%1"" == ""auto"" goto DO_UNINSTALL
if not exist "%install_dir%\lib" goto DO_UNINSTALL

set NotifyOK=0
REM notify YunGuanJia

:DO_UNINSTALL
ECHO Uninstalling %appName%...
if exist "%install_dir%\wrapper\bin\AppCommand.bat" (
    call "%install_dir%\wrapper\bin\AppCommand.bat" remove
)
sc query YunAgent 1>nul 2>nul
if not errorlevel 1 (
    sc stop YunAgent 1>nul 2>nul
    sc delete YunAgent 1>nul 2>nul
)
echo Removing directory %install_dir% ...
REM make sure file removed
start /wait /B rmdir /S /Q "%install_dir%" 1>nul 2>nul
if "%1" == "auto" (
    ping 127.0.0.1 -n 3 >nul
    del /F /S /Q "%install_dir%" 1>nul 2>nul
    rd /S /Q "%install_dir%" 1>nul 2>nul
)

ECHO Uninstall %appName% completed.
if %NotifyOK% neq 0 (
    ECHO Please remove this YunAgent in https://yun.cloudbility.com
)
