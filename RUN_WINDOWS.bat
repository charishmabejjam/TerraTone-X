@echo off
cd /d "%~dp0"
where python >nul 2>nul
if errorlevel 1 (echo Python 3 is required. Install Python and try again.& pause & exit /b 1)
start "TerraTone X" http://127.0.0.1:8000
echo Starting TerraTone X. Keep this window open while using the prototype.
python python\server.py
pause
