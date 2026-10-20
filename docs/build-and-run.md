# Build and run

## Tooling

Requires JDK 17+ (21 recommended) and an Android SDK with platform-tools + an AVD
or device.

```bash
export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 21 2>/dev/null)}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
cd /path/to/super_demo_android
```

`local.properties` should contain `sdk.dir` (gitignored).

## Gradle

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew :app:installDebug
```

## Emulator / device

Boot any AVD (or attach a device), then:

```bash
adb wait-for-device
adb shell getprop sys.boot_completed   # expect 1
./bin/checklist
```

Optional: `export ANDROID_SERIAL=emulator-5554` to pin a device.

Android Studio: open the project root and run the `app` configuration.
