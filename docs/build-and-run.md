# Build and run

## Prerequisites

- JDK 21, matching the CI toolchain.
- Android SDK with platform **37** and platform-tools. Gradle resolves the build
  tools required by the Android plugin.
- Emulator or device running **API 26+** for installation and connected tests.

Open the repository root in Android Studio, select JDK 21 as the Gradle JDK,
install the required SDK platform and run the `app` configuration.

## Command line

Set `JAVA_HOME` to JDK 21 and `ANDROID_HOME` to your SDK directory. On macOS:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

On other hosts, use the equivalent local JDK and SDK paths. An ignored
`local.properties` file with `sdk.dir` is also supported.

From the repository root:

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew :app:installDebug
adb shell am start -n com.redjadet.superdemo.debug/com.redjadet.superdemo.MainActivity
```

Debug builds install as `com.redjadet.superdemo.debug`. Release builds use
`com.redjadet.superdemo`.

## Device selection and full validation

```bash
adb devices -l
export ANDROID_SERIAL=emulator-5554  # replace with the chosen serial
adb -s "$ANDROID_SERIAL" shell getprop sys.boot_completed  # expect 1
./bin/checklist
```

The checklist clears the debug app’s local data before instrumentation to make
seeded test state deterministic. Use a test device or emulator.

## Try a deep link

```bash
adb shell am start -a android.intent.action.VIEW -d "superdemo://feed/1" -p com.redjadet.superdemo.debug
```

See [navigation](navigation.md) for supported routes and [testing](testing.md) for
the checks behind each behavior.
