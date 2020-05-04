# ernnavigation-api-impl-native

[![Actions Status][1]][2]

Native implementation of [`ernnavigation-api`][3].

## Getting Started

### Android

Inside the `android/` directory, run the following command:

```sh
./gradlew createAndPublishErnDevContainer
```

The container will be created in a temporary directory and published to the
local Maven repository (`~/.m2`), from where it will be consumed by the library
project.

### iOS

Create the iOS Container:

```sh
yarn createiOSContainer
```

After opening the `ios/` directory in Xcode (`xed ios`), you can either build
the ElectrodeApiImpl project at `ios/ElectrodeApiImpl.xcodeproj` or you can run
the moviesreloadedMiniApp at `/ios/moviesreloadedMiniApp/ErnRunner.xcodeproj`
to try Electrode Native Navigation on iOS side.

[1]: https://github.com/electrode-io/ernnavigation-api-impl-native/workflows/ci/badge.svg
[2]: https://github.com/electrode-io/ernnavigation-api-impl-native/actions
[3]: https://github.com/electrode-io/ern-navigation-api#electrode-native-navigation-api
