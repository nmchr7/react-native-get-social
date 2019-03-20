#import "RNGetSocial.h"
#import <React/RCTLog.h>
#import <GetSocial/GetSocial.h>
#import <GetSocialUI/GetSocialUI.h>


#pragma mark - Private RNGetSocial declarations

@interface RNGetSocial()
@property (nonatomic, readonly) UIViewController *currentViewController;
@end

#pragma mark - RNGetSocial implementation

@implementation RNGetSocial

RCT_EXPORT_MODULE()

+(GetSocial *)getSocial
{
    @synchronized(self) {
        static GetSocial *instance;
        static dispatch_once_t once = 0;
        dispatch_once(&once, ^{
           // TODO implement
        });
        return instance;
    }
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}


+ (void)initGetSocial:(NSString *)appId {
    [GetSocial executeWhenInitialized:^() {
        RCTLogInfo(@"Get Social SDK initialized");
        // TODO implement
    }];
    [GetSocial initWithAppId:appId];
}

// TODO implement show invite
RCT_REMAP_METHOD(showInviteUI,
                  showInviteUIWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL wasShown = [[GetSocialUI createInvitesView] show];
    if (wasShown) {
        NSLog(@"GetSocial Smart Invites UI was shown");
        resolve(@YES);
    } else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"err" forKey:NSLocalizedDescriptionKey];
        NSError *error = [NSError errorWithDomain:@"You FAILED!" code:200 userInfo:details];
        reject(@"no_ui", @"There were no invite UI shown - ", error);
    }
}

@end
