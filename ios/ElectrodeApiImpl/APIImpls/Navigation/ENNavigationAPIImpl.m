//
//  ENNavigationAPIImpl.m
//  ErnRunner
//
//  Created by Lianci Liu on 6/18/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import "ENNavigationProtocol.h"

@implementation ENNavigationAPIImpl
+ (instancetype _Nonnull)sharedInstance {
    static dispatch_once_t onceToken;
    static id sharedInstance;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype) init {
    if (self = [super init]) {
        self.navigationAPI = [[EnNavigationAPI alloc] init];
        [self registerNavigationRequestHandler];
        [self registerBackRequestHandler];
        [self registerUpdateRequestHandler];
        [self registerFinishRequestHandler];
    }
    return self;
}

- (void)registerFinishRequestHandler {
    (void) [self.navigationAPI.requests registerFinishRequestHandlerWithHandler:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        NSDictionary *routeData =  [data toDictionary];
        if (self.delegate && [self.delegate respondsToSelector:@selector(handleFinishFlow:withBlock:)]) {
            [self.delegate handleFinishFlow:routeData[@"finalPayload"] withBlock:^(NSString * _Nonnull messageCompletion) {
                block(messageCompletion, nil);
                return ;
            }];
        }
    }];
}

- (void)registerUpdateRequestHandler {
    (void) [self.navigationAPI.requests registerUpdateRequestHandlerWithHandler:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        NSDictionary *routeData = [data toDictionary];
        if(self.delegate && [self.delegate respondsToSelector:@selector(updateNavigationBar:withBlock:)]) {
            NavigationBar *navBar = [[NavigationBar alloc] initWithDictionary:routeData[@"navigationBar"]];
            [self.delegate updateNavigationBar:navBar withBlock:^(NSString * _Nonnull message) {
                block(message, nil);
                return;
            }];
        }
        return;
    }];
}

- (void)registerBackRequestHandler {
    (void) [self.navigationAPI.requests registerBackRequestHandlerWithHandler:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        NSDictionary *routeData = [data toDictionary];
        NSString *path = routeData == nil ? nil : routeData[@"path"];
        if(self.delegate && [self.delegate respondsToSelector:@selector(popToViewControllerWithPath:withBlock:)]) {
            [self.delegate popToViewControllerWithPath: path withBlock:^(NSString * _Nonnull message) {
                block(message, nil);
                return;
            }];
        }
        return;
    }];
}

- (void)registerNavigationRequestHandler {
    (void) [self.navigationAPI.requests registerNavigateRequestHandlerWithHandler:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        NSDictionary *routeData =  [data toDictionary];
        NSString *path = routeData[@"path"];
        if ([path isEqualToString:@"finishFlow"]) {
            if (self.delegate && [self.delegate respondsToSelector:@selector(handleFinishFlow:withBlock:)]) {
                [self.delegate handleFinishFlow:routeData[@"jsonPayload"] withBlock:^(NSString * _Nonnull messageCompletion) {
                    block(messageCompletion, nil);
                    return ;
                }];
            }
        } else {
            if (self.delegate && [self.delegate respondsToSelector:@selector(handleNavigationRequestWithPath:withBlock:)]) {
                [self.delegate handleNavigationRequestWithPath:routeData withBlock:^(NSString * _Nonnull messageCompletion) {
                    block(messageCompletion, nil);
                    return;
                }];
            }
        }
    }];
}
@end
