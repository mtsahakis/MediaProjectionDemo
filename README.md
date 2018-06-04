Android Lollipop 5.0 MediaProjectionManager Demo
------------------------------------------------
This is a sample app that demonstrates how to capture screenshots based on the MediaProjection API.
More on the API can be found in https://developer.android.com/reference/android/media/projection/package-summary.html
Clone and import the project in Android Studio. No special dependencies and extra libraries required.
Note that in order to run the code you need to create a device running Lollipop and above.

Limitations
------------------------------------------------ 
As of this writting I am getting a black screen when running the app in an emulator running Android 6.0 and lower versions. The screen captures are working successfully in emulators with Android 6.0.1 and later versions (see [issue 5](https://github.com/mtsahakis/MediaProjectionDemo/issues/5) for additional info). Also, Android 5.1 has a bug when we check "Do not show again" box, please refer to https://code.google.com/p/android/issues/detail?id=159613.
