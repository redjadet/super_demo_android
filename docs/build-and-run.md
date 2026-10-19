# Build and run

Checkout lives on the Lacie SSD:

`/Volumes/Lacie_Ssd/projects/super_demo_android`

## Tooling

Requires JDK 17+ (21 recommended) and an Android SDK with platform-tools + an AVD.

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || echo "$JAVA_HOME")"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
cd /path/to/super_demo_android
```

`local.properties` should contain `sdk.dir` (gitignored). On this Mac Mini the
checkout is `/Volumes/Lacie_Ssd/projects/super_demo_android`.

## Gradle

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew :app:installDebug
```

## Emulator

```bash
emulator -avd Small_Phone_3 -netdelay none -netspeed full &
adb wait-for-device
adb shell getprop sys.boot_completed   # expect 1
./gradlew connectedDebugAndroidTest
```

Android Studio: open the project root and run the `app` configuration.
