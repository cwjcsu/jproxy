@ECHO OFF

setlocal ENABLEEXTENSIONS

cd /d %~dp0\..
if exist "%cd%\wrapper\bin\AppCommand.bat" (
    call "%cd%\wrapper\bin\AppCommand.bat" start
) else (
	goto NotFound
)

goto end
:NotFound
echo Start fail:Not in jproxy directory
:end