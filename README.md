# TVSeries

Small application to search and find information of popular tv shows

This application support phone sizes, portrait/landscape orientations and dark theme if is
configured on device.

Setting of application includes the possibility of lock screen and unlock via pin or biometrics when
is available.

## Structure

* `build.gradle` - root gradle config file
* `settings.gradle` - root gradle settings file
* `app` - our only project in this repo
* `app/build.gradle` - project gradle config file
* `app/src` - main project source directory
* `app/src/main` - main project flavour
* `app/src/main/AndroidManifest.xml` - manifest file
* `app/src/main/java` - java source directory
* `app/src/main/res` - resources directory

## Building

It is recommended that you run Gradle with the `--daemon` option, as starting up the tool from
scratch often takes at least a few seconds. You can kill the java process that it leaves running
once you are done running your commands.

Tasks work much like Make targets, so you may concatenate them. Tasks are not re-done if multiple
targets in a single command require them. For example, running `assemble install` will not compile
the apk twice even though
`install` depends on `assemble`.

please make sure sdk is using Java 11

#### Clean

	./gradlew clean

#### Debug

This compiles a debugging apk in `build/outputs/apk/` signed with a debug key, ready to be installed
for testing purposes.

	./gradlew assembleDebug

You can also install it on your attached device:

	./gradlew installDebug

#### Release

This compiles an unsigned release (non-debugging) apk in `build/outputs/apk/`. It's not signed, you
must sign it before it can be installed by any users.

	./gradlew assembleRelease

#### Dist

A distribution debug apk is stored on `dist` folder for testing propose. Generation date:
17/may/2022

`adb install <apk route>`

#### Test

Were you to add automated java tests, you could configure them in your
`build.gradle` file and run them within gradle as well.

	./gradlew test