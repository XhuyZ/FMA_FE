#!/bin/bash

# Config
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.example.fma_fe"
MAIN_ACTIVITY=".MainActivity"

# 1. Build APK
echo "🛠️  Building APK..."
./gradlew assembleDebug || {
  echo "❌ Build failed"
  exit 1
}

# 2. Check if emulator is running
echo "🔍 Checking emulator..."
adb devices | grep emulator >/dev/null
if [ $? -ne 0 ]; then
  echo "❌ No emulator found. Please start it using: emulator -avd <your_avd_name>"
  exit 1
fi

# 3. Install APK
echo "📦 Installing APK to emulator..."
adb install -r "$APK_PATH" || {
  echo "❌ Failed to install APK"
  exit 1
}

# 4. Launch app
echo "🚀 Launching app..."
adb shell am start -n "$PACKAGE_NAME/$MAIN_ACTIVITY" || {
  echo "❌ Failed to launch app"
  exit 1
}

echo "✅ Done!"
