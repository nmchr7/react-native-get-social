# react-native-get-social

## **DISCLAIMER!**

This project is very much WIP but more is to come very soon. It is to become a thin React Native wrapper around the [GetSocial](https://www.getsocial.im) native iOS and Android SDK

## Getting started

`$ npm install react-native-get-social --save`

### Mostly automatic installation (unstable)

### **See example folder for reference!**

You won't be able to build neither iOS or Android without a valid key for GetSocial. So if you want to check this project out [Create free account](https://dashboard.getsocial.im/#/register)

`$ react-native link react-native-get-social`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-get-social` and add `RNGetSocial.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNGetSocial.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

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
