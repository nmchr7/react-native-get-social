#import "RNGetSocial.h"
#import <GetSocial/GetSocial.h>
#import <GetSocialUI/GetSocialUI.h>

@implementation RNGetSocial

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

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
