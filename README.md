# react-native-get-social

This project is a thin React Native wrapper around the [GetSocial](https://www.getsocial.im) native iOS and Android SDK

## Getting started

`$ npm install react-native-get-social --save`

or

`$ yarn add react-native-get-social`

### installation

You won't be able to build neither iOS or Android without a valid key for GetSocial. So if you want to check this project out [Create free account](https://dashboard.getsocial.im/#/register)

### Automatic installation

`$ react-native link react-native-get-social`

### Manual installation

#### iOS

Currently this library only supports installation via pods. If you have a solution for regular manual linking feel free to open a PR :)

In your Podfile add this line 

`pod 'RNGetSocial', :path => '../node_modules/react-native-get-social'`

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.reactlibrary.RNGetSocialPackage;` to the imports at the top of the file
- Add `new RNGetSocialPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```gradle
   include ':react-native-get-social'
   project(':react-native-get-social').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-get-social/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

   ```gradle

    repositories {

        //...other configuration here...

        maven { url 'http://maven.getsocial.im/' }
    }

    dependencies {

        //...other configuration here...

        implematation project(':react-native-get-social')
    }
    ```
