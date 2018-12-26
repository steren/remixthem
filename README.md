# Remixthem

An Android app to edit and blend faces.

[Download it from Google Play](https://play.google.com/store/apps/details?id=fr.steren.remixthem.market)

Read a [blog post about the app](https://labs.steren.fr/2018/12/26/remixthem/).

## Code

* `Android`: contains the Android app.
* `image sources`: contains sources (e.g. `.svg`) of the icons or other marketing material.

The `scripts` folder contains a script to convert a a strip down version of the app (intended to be free to charge). 
The `test suite` folder contains pictures taken with a few different phones.

## How it works

The app uses the [face detection API built-into Android](https://developer.android.com/reference/android/media/FaceDetector) to get the [location of the eyes](https://github.com/steren/remixthem/blob/master/Android/src/fr/steren/remixthem/market/BackgroundFace.java#L33-L62) in both pictures.
Then it uses some [alpha masks](https://github.com/steren/remixthem/blob/master/Android/res/drawable/alphamask_eyes.png) to extract these features and later blend them. The user can also edit each part manually.

