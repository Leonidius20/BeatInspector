# BeatInspector
An Android app to view the BPM, key, genre, time signature, and average loudness of a track from Spotify API.

---
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid2.png" width="200px">](https://apt.izzysoft.de/fdroid/index/apk/io.github.leonidius20.beatinspector/)

---

## Purpose
The data provided by the app is meant to help music producers remix other tracks or use them as references for thier own work.
## Technology
Built with [Jetpack Compose](https://developer.android.com/jetpack/compose), Single-Activity Architecture, Android Architecture Components ([ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [Navigation Component](https://developer.android.com/jetpack/androidx/releases/navigation)), [Retrofit](https://square.github.io/retrofit/), [Dagger/Hilt](https://dagger.dev/hilt/), [Coil](https://coil-kt.github.io/coil/), [AppAuth](https://github.com/openid/AppAuth-Android) to implement the Authorization code with PKCE flow, [Jetpack Palette library](https://developer.android.com/jetpack/androidx/releases/palette), and the [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) library. 
## Screenshots
![Screenshots collage](/docs/screenshots/all.png)
## Building from sources
### Prerequisites 
- In order to build the app yourself, you need to register the app with Spotify API according to [the guide](https://developer.spotify.com/documentation/web-api/concepts/apps). When registering, use a name other than BeatInspector. Mind that the API access will be granted in "Development mode" by default, which means that only you and up to 25 manually added users will be able to use the app (read more [here](https://developer.spotify.com/documentation/web-api/concepts/quota-modes)).
- You will need a keystore to sign the app. Name the keystore file `android-keystore.jks` and place it in the project's root folder. Make sure not to commit this file to a public repository. By default both the debug and the release builds are signed with this keystore. However, if you only want to build a debug version with a debug signature, you can remove the `buildTypes.debug.signingConfig` property from `app/build.gradle.kts`. In this case the keystore file would not be needed.
### Build locally
- Clone the repository.
- Create a file called `secrets.properties` in the project's root folder with the following content (`SIGNATURE_` values are not needed if you chose to use the debug key):
```
SPOTIFY_CLIENT_ID=<your app's client id from Spotify developer dashboard>

SIGNATURE_KEYSTORE_PASSWORD=<password to the keystore you've provided>
SIGNATURE_KEY_ALIAS=<alias of the key from the keystore that you want to use to sign the app>
SIGNATURE_KEY_PASSWORD=<password to that key>
```
Make sure that you do not commit this file to a public repository.
- Build the project using Android Studio's UI or run the commands in the project's root folder:
```
./gradlew assembleDebug
```
to create a debug build of the app at `app/build/outputs/apk/debug/app-debug.apk`, or
```
./gradlew assembleRelease
```
for the release version at `/app/build/outputs/apk/release/app-release.apk`.
### Build using Github Actions
You will only be able to make a release build with this method.
- Create a fork of this repository.
- Add the following Repository secrets:
  - `SPOTIFY_CLIENT_ID` - your app's client id from Spotify developer dashboard
  - `SIGNATURE_KEYSTORE_BASE64` - your keystore file encoded in base64
  - `SIGNATURE_KEYSTORE_PASSWORD` - the password to the key in the keystore that you want to use for signing the app.
- Add a Repository variable with the following name and value:
  - `SIGNATURE_KEY_ALIAS` - the alias of the key that you want to use.
- Run the Build Release APK workflow.
- When the workflow job is finished, the apk will be available as its artifact. A draft release will also be created and the apk will be attached to it.
## Credits
The app's icon is from Freepik.
## License
This project is licensed under the [GNU General Public License v3](LICENSE).
