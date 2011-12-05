#!/bin/sh
# generate the lite version of the application

rm ../RemixThemLite/src/fr/steren/remixthem/lite/*.java
cp ../RemixThem/src/fr/steren/remixthem/market/*.java ../RemixThemLite/src/fr/steren/remixthem/lite/
sed -i 's/package fr.steren.remixthem.market/package fr.steren.remixthem.lite/g' ../RemixThemLite/src/fr/steren/remixthem/lite/*.java
sed -i 's/import fr.steren.remixthem.market/import fr.steren.remixthem.lite/g' ../RemixThemLite/src/fr/steren/remixthem/lite/*.java
sed -i 's/SOLD_FULL_VERSION = true/SOLD_FULL_VERSION = false/g' ../RemixThemLite/src/fr/steren/remixthem/lite/RemixThem.java

rm ../RemixThemLite/res/menu/*.xml
cp ../RemixThem/res/menu/*.xml ../RemixThemLite/res/menu/

rm ../RemixThemLite/res/layout/*.xml
cp ../RemixThem/res/layout/*.xml ../RemixThemLite/res/layout/

rm ../RemixThemLite/res/values/*.xml
cp ../RemixThem/res/values/*.xml ../RemixThemLite/res/values/

rm ../RemixThemLite/res/values-fr/*.xml
cp ../RemixThem/res/values-fr/*.xml ../RemixThemLite/res/values-fr/

rm ../RemixThemLite/res/drawable/*.png
cp ../RemixThem/res/drawable/*.png ../RemixThemLite/res/drawable/

rm ../RemixThemLite/assets/*.xml
cp ../RemixThem/assets/*.xml ../RemixThemLite/assets/

rm ../RemixThemLite/AndroidManifest.xml
cp ../RemixThem/AndroidManifest.xml ../RemixThemLite/
sed -i 's/fr.steren.remixthem.market/fr.steren.remixthem.lite/g' ../RemixThemLite/AndroidManifest.xml
sed -i 's/android:versionName="/android:versionName="Lite /g' ../RemixThemLite/AndroidManifest.xml
sed -i 's/app_name/app_name_lite/g' ../RemixThemLite/AndroidManifest.xml
