@ECHO OFF

setlocal ENABLEEXTENSIONS

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

cd /d %~dp0\..
if exist "%cd%\wrapper\bin\AppCommand.bat" (
    call "%cd%\wrapper\bin\AppCommand.bat" stop
) else (
	goto NotFound
)

goto end
:NotFound
echo Stop fail:Not in YunAgent directory
:end