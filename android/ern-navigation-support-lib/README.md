## ern-navigation-support-lib

Android library created to store resources required for ern-navigation. 
 
Even though the [ern-navigation-native-lib](https://github.com/electrode-io/ernnavigation-api-impl-native/tree/master/android/lib) is an android library, the electrode native container(seperate aar library) only pulls in the soruce code from this library. So any reference that is being made to the `.R` file with in [ern-navigation-native-lib](https://github.com/electrode-io/ernnavigation-api-impl-native/tree/master/android/lib) would fail on import statements. 

However, the library would work well when used stand alone since it does not have the container restriction. 


Adding this support library solves that problem. Since the .R import belongs to the support library, the container aar as well as the independent aar works well and uses the same `ern.navigation.support.lib.R` import. 

This repository is published to `mavenCentral()` 



## build.gradle

Add the following to your dependencies

```
dependencies {
  implementation 'com.walmartlabs.ern:ern.navigation.support.lib:0.0.1'
}
```


