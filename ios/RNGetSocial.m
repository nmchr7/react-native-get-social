#import "RNGetSocial.h"
#import <GetSocial/GetSocial.h>
#import <GetSocialUI/GetSocialUI.h>

@implementation RNGetSocial

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_REMAP_METHOD(sampleMethod,
                  sampleMethodWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSArray *events = @[@"1", @"2", @"3", @"4", @"5"];
    
    BOOL wasShown = [[GetSocialUI createInvitesView] show];
    NSLog(@"GetSocial Smart Invites UI was shown %d", wasShown);
    if (events) {
        resolve(events);
    } else {
        NSMutableDictionary* details = [NSMutableDictionary dictionary];
        [details setValue:@"OMG!" forKey:NSLocalizedDescriptionKey];
        NSError *error = [NSError errorWithDomain:@"You FAILED!" code:200 userInfo:details];
        reject(@"no_events", @"There were no events - ", error);
    }
}

@end
