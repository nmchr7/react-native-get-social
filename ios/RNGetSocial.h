#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#else
#import "RCTBridgeModule.h"
#endif
#import <GetSocial/GetSocial.h>

@interface RNGetSocial : NSObject <RCTBridgeModule>

@property (class, readonly, nonnull) GetSocial *getSocial;

+ (void)initGetSocial:(NSString * _Nonnull)appId;

@end
