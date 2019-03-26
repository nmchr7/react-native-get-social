#import "RNGetSocial.h"
#import <React/RCTConvert.h>
#import <React/RCTLog.h>
#import <GetSocial/GetSocial.h>
#import <GetSocialUI/GetSocialUI.h>


#pragma mark - Private RNGetSocial declarations

@interface RNGetSocial()
@property (nonatomic, readonly) UIViewController *currentViewController;
@end

#pragma mark - RNGetSocial implementation

@implementation RNGetSocial
{
    bool hasListeners;
}

// Will be called when this module's first listener is added.
-(void)startObserving {
    hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    hasListeners = NO;
}

RCT_EXPORT_MODULE()

- (instancetype)init {
    self = [super init];
    if (self) {
        [self setupGetSocial];
    }
    return self;
}

- (NSDictionary *)constantsToExport
{
    return @{
             
             // add the LinkParams constants
             @"KEY_CUSTOM_TITLE": GetSocial_Custom_Title,
             @"KEY_CUSTOM_DESCRIPTION": GetSocial_Custom_Description,
             @"KEY_CUSTOM_IMAGE": GetSocial_Custom_Image,
             @"KEY_CUSTOM_YOUTUBE_VIDEO": GetSocial_Custom_YouTubeVideo,
             
             // use "constants" on the module to account for the scenario where the events
             // have been issued
             // before the js has been loaded
             @"initialized": @([GetSocial isInitialized]),
             @"userId": [GetSocial isInitialized] ? [GetSocialUser userId] : @""
             };
}


+ (BOOL)requiresMainQueueSetup {
    return NO;  //run on background thread
}


//
// whenInitialized Promise
//
RCT_REMAP_METHOD(whenInitialized,
                 whenInitializedWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{

    [GetSocial executeWhenInitialized:^() {
        // GetSocial is ready to be used

        // finalize the promise
        resolve(nil);
    }];
}




//
// getReferralData Promise
//
RCT_REMAP_METHOD(getReferralData,
                 getReferralDataWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    
    [GetSocial referralDataWithSuccess:^(GetSocialReferralData *_Nullable referralData) {
       
        if (referralData == nil)
        {
            // finalize the promise with No details
            resolve(nil);
        }
        else
        {
            resolve([referralData referralLinkParams]);
        }
    }
     
    failure:^(NSError *_Nonnull error) {
        
        // something went wrong
        reject(@"GetSocial", @"Failed getting referral data", error);
    }];
}

//
// showInviteUI Promise
//
RCT_REMAP_METHOD(showInviteUI,
                  showInviteUIWithParams: (NSDictionary *)linkParams
                  config: (NSDictionary *)config
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    
    //run the invite on the UI thread
    dispatch_async(dispatch_get_main_queue(), ^{
        
        GetSocialUIInvitesView *uiBuilder = [GetSocialUI createInvitesView];
        
        //
        // set the link params
        //
        [uiBuilder setLinkParams:linkParams];
        
        
        //
        // create the invite content (social media config)
        //
        GetSocialMutableInviteContent *contentBuilder = [GetSocialMutableInviteContent new];
        
        if([config objectForKey:@"subject"]){
            
            [contentBuilder setSubject:config[@"subject"]];
        }
        
        if ([config objectForKey:@"text"]) {

            [contentBuilder setText:config[@"text"]];
        }
        
        if ([config objectForKey:@"imageUrl"]) {
            
            [contentBuilder setMediaAttachment:[GetSocialMediaAttachment imageUrl:config[@"imageUrl"]]];
        }

        [uiBuilder setCustomInviteContent:contentBuilder];
        
        
        //
        // change the window title
        //
        if ([config objectForKey:@"windowTitle"]) {
            
            [uiBuilder setWindowTitle:config[@"windowTitle"]];
        }
        
        
        //
        // Setup callback to fulfill the promises
        //
        [uiBuilder setHandlerForInvitesSent:^(NSString *channelId) {
            
            //invite sent, so finalize the promise
            resolve(nil);
        }
         
        cancel: ^(NSString *channelId){
            
            reject(@"GetSocial", @"Canceled", nil); // TODO: we should probably add an object {isCanceled: true}
        }
         
        failure: ^(NSString *channelId , NSError *error){
                                        
            reject(@"GetSocial", @"Failed sending invite", error);
        }];
        
        
        //
        // show the UI
        //
        BOOL wasShown = [uiBuilder show];
        if (wasShown) {
            NSLog(@"GetSocial Smart Invites UI was shown");
        }
    });
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"getSocialInitialized", @"getSocialUserChanged"];
}

- (void)setupGetSocial {
    
    NSLog(@"GetSocial Setup Function");
    
    //listen to the library being initialized
    [GetSocial executeWhenInitialized:^() {
        // notify the JS thread that the GetSocial SDK has been initialized
        // so it can be cached there
        if (hasListeners) { // Only send events if anyone is listening
         
            [self sendEventWithName:@"getSocialInitialized" body:nil];
        }
    }];
    
    [GetSocialUser setOnUserChangedHandler:^(){
        
        // notify the JS thread about the new getsocial user id
        if (hasListeners) { // Only send events if anyone is listening

            [self sendEventWithName:@"getSocialUserChanged" body:@{@"userId": [GetSocialUser userId]}];
        }
    }];
}

@end
