#!/usr/bin/env bash
set -euo pipefail

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
EMULATOR="$ANDROID_SDK/emulator/emulator"
ADB="$ANDROID_SDK/platform-tools/adb"
AVD_NAME="car1920x1080"
PACKAGE="com.example.btphone"
ACTIVITY=".MainActivity"
PROJECT_DIR="$(cd "$(dirname "$0")/app" && pwd)"
APK="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()  { echo -e "${GREEN}[✔]${NC} $1"; }
warn() { echo -e "${YELLOW}[!]${NC} $1"; }
err()  { echo -e "${RED}[✘]${NC} $1"; exit 1; }

# ---------- 1. Build ----------
echo ""
echo "=============================="
echo "  编译 debug APK"
echo "=============================="
cd "$PROJECT_DIR"
./gradlew assembleDebug -q || err "编译失败，请检查代码"
log "编译完成: $APK"

# ---------- 2. Emulator ----------
echo ""
echo "=============================="
echo "  启动模拟器 $AVD_NAME"
echo "=============================="

boot_emulator() {
    if "$ADB" devices 2>/dev/null | grep -q "emulator.*device"; then
        log "模拟器已在运行"
        return 0
    fi

    warn "模拟器未运行，正在启动 $AVD_NAME ..."
    "$EMULATOR" -avd "$AVD_NAME" -no-snapshot-load -no-audio -gpu auto &
    EMULATOR_PID=$!
    disown "$EMULATOR_PID"

    local timeout=120
    local elapsed=0
    while [ $elapsed -lt $timeout ]; do
        if "$ADB" devices 2>/dev/null | grep -q "emulator.*device"; then
            local boot_done
            boot_done=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
            if [ "$boot_done" = "1" ]; then
                log "模拟器启动完成 (${elapsed}s)"
                return 0
            fi
        fi
        sleep 2
        elapsed=$((elapsed + 2))
        printf "\r  等待开机中... %ds / %ds" "$elapsed" "$timeout"
    done
    echo ""
    err "模拟器启动超时 (${timeout}s)"
}

boot_emulator

# ---------- 3. Install ----------
echo ""
echo "=============================="
echo "  安装 APK"
echo "=============================="
"$ADB" install -r "$APK" || err "APK 安装失败"
log "安装成功"

# ---------- 4. Launch ----------
echo ""
echo "=============================="
echo "  启动应用"
echo "=============================="
"$ADB" shell am force-stop "$PACKAGE" 2>/dev/null
"$ADB" shell am start -n "$PACKAGE/$ACTIVITY" || err "应用启动失败"
log "应用已启动: $PACKAGE/$ACTIVITY"

echo ""
echo -e "${GREEN}=============================="
echo "  全部完成！去模拟器上看效果吧"
echo -e "==============================${NC}"
