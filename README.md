# BASE - Android #

## Synopsis ##

This repository contains the base project to mobile2you android apps. Fork this and update this README.

## Settings ##

```
minSdkVersion 16
targetSdkVersion 25
```

## API Reference ##

https://baseapp.herokuapp.com/webservices/


## Libraries
```
    final PLAY_SERVICES_VERSION = '9.8.1'
    final SUPPORT_LIBRARY_VERSION = '25.0.0'
    final RETROFIT_VERSION = '2.1.0'
    final DAGGER_VERSION = '2.5'

    compile "com.android.support:appcompat-v7:$SUPPORT_LIBRARY_VERSION"
    compile "com.android.support:recyclerview-v7:$SUPPORT_LIBRARY_VERSION"
    compile "com.android.support:design:$SUPPORT_LIBRARY_VERSION"
    compile "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"
    compile "com.squareup.retrofit2:converter-gson:$RETROFIT_VERSION"
    compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.squareup.okhttp3:logging-interceptor:3.0.1'
```