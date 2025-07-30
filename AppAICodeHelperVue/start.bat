@echo off
echo 正在启动AI编程小助手...
echo.

REM 检查Node.js是否安装
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未检测到Node.js，请先安装Node.js
    echo 下载地址：https://nodejs.org/
    pause
    exit /b 1
)

REM 检查npm是否可用
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：npm不可用，请检查Node.js安装
    pause
    exit /b 1
)

REM 安装依赖
echo 正在安装依赖...
npm install
if %errorlevel% neq 0 (
    echo 错误：依赖安装失败
    pause
    exit /b 1
)

echo.
echo 依赖安装完成！
echo 正在启动开发服务器...
echo.
echo 应用将在浏览器中自动打开
echo 如果没有自动打开，请访问：http://localhost:3000
echo.
echo 按 Ctrl+C 停止服务器
echo.

REM 启动开发服务器
npm run dev

pause 