import {
  NativeModules,
  Platform,
  DeviceEventEmitter,
  NativeEventEmitter
} from "react-native";

const { RNGetSocial } = NativeModules;

/**
 * create a wrapper arround RNGetSocial so this module can be easily mocked in jest tests using MockModule
 * See: https://github.com/facebook/react-native/blob/4148976a83ab96029de1b26b515ff95d3cb58c10/Libraries/Utilities/Dimensions.js#L22
 */

//store the constants here so they are not exported as properties in the wrapper
const userInfo = {};
let initialized = undefined;

class Wrapper {
  /**
   * This should only be called from native code by sending the
   * getSocialUserChanged event.
   *
   * @param {object} user Simple string-keyed object of user information
   */
  static _setUser(user) {
    Object.assign(userInfo, user);
  }

  /**
   * This should only be called from native code by sending the
   * getSocialInitialized event.
   */
  static _setInitialized() {
    initialized = true;
  }

  /**
   * Promise chain that resolves when the sdk has initialized
   */
  static whenInitialized = () => RNGetSocial.whenInitialized();

  /**
   * Promise chain that resolves with the link params
   */
  static getReferralData = () => RNGetSocial.getReferralData();

  //
  // Check if the GetSocial SDK is initialized, or not
  //
  static isInitialized = () =>
    //return from our cache, or default to the NativeModule "constants" otherwise
    initialized ? initialized : RNGetSocial.initialized;

  //
  // Get the user Id associated with GetSocial
  //
  static getUserId = () =>
    //return from our cache, or default to the NativeModule "constants" otherwise
    userInfo.userId ? userInfo.userId : RNGetSocial.userId;

  //
  // Show the invite UI
  //
  static showInviteUI = (params, config) =>
    RNGetSocial.showInviteUI(params, Object.assign({}, config)); //make sure to allways send 2 params to the native side

  //
  // Hoist static variables, but in such a way that they can be mocked
  //
  static KEY_CUSTOM_TITLE = RNGetSocial
    ? RNGetSocial.KEY_CUSTOM_TITLE
    : "$title";
  static KEY_CUSTOM_DESCRIPTION = RNGetSocial
    ? RNGetSocial.KEY_CUSTOM_DESCRIPTION
    : "$description";
  static KEY_CUSTOM_IMAGE = RNGetSocial
    ? RNGetSocial.KEY_CUSTOM_IMAGE
    : "$image";
  static KEY_CUSTOM_YOUTUBE_VIDEO = RNGetSocial
    ? RNGetSocial.KEY_CUSTOM_YOUTUBE_VIDEO
    : "$youtube_video";
}

const EventEmitter = Platform.select({
  android: DeviceEventEmitter,
  ios: new NativeEventEmitter(RNGetSocial)
});

//
//listen for events to set the proper information
//
EventEmitter.addListener("getSocialUserChanged", user => {
  Wrapper._setUser(user);
});

EventEmitter.addListener("getSocialInitialized", () => {
  Wrapper._setInitialized();
});

export default Wrapper;
