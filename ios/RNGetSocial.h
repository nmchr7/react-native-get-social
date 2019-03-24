#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#else
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#endif
#import <GetSocial/GetSocial.h>

@interface RNGetSocial : RCTEventEmitter <RCTBridgeModule>

@property (class, readonly, nonnull) GetSocial *getSocial;

+ (void)initGetSocial:(NSString * _Nonnull)appId;

@end
