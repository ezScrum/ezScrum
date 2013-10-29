@echo off
cls

:setting
set BaseDir=robotTesting

set TestDir=./%BaseDir%

set OutputDir=_AT
set OutputDir=-d %OutputDir%

set BrowserConfig=-E colon:Z -E slash:Y

rem ####### Varaiable Define #######
set URL=httpZYY140.124.181.87Z8080YezScrum
set var_URL=--variable ezScrum_LOGIN_URL:%URL%

set ProjectName=fromWinClient
set var_ProjectName=--variable projectName:%ProjectName%

set ProjectDisplayName=fromWinCDisplayName
set var_ProjectDisplayName=--variable projectDisplayName:%ProjectDisplayName%


:showSetting
echo @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
echo ------- Location/Path Setting
echo root DIR   =	%BaseDir% 
echo Test DIR   = %TestDir%
echo output DIR = %OutputDir%
echo ------- Variable Define
echo test target URL ..........= %var_URL%
echo test Project Name         = %var_ProjectName%
echo test Project Display Name = %var_ProjectDisplayName%
echo ------- Browser Setting
echo Browser Config            = %var_BrowserConfig%


:run
cd ..
echo @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
pybot %OutputDir% %BrowserConfig% %var_URL% %var_ProjectName% %var_ProjectDisplayName% %TestDir%
echo @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


:goback
cd %BaseDir%

