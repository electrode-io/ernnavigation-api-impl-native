//
//  MiniAppViewController.h
//  ElectrodeContainer
//
//  Created by Lianci Liu on 5/17/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ENNavigationProtocol.h"
#import "ENNavigationDelegate.h"

typedef void(^MiniAppFinishedCallback)(NSString *_Nullable);

@protocol EnMiniAppNavDataProvider <EnMiniAppDataProvider>
- (MiniAppFinishedCallback _Nullable) finishedCallback;
@end

@interface MiniAppNavViewController : UIViewController <ENNavigationProtocol>

@property (nonatomic, strong, nonnull) NSString* miniAppName;
@property (nonatomic, copy, nullable) MiniAppFinishedCallback finishedCallback;
@property (nonatomic, copy, nullable) NSDictionary* properties;

- (instancetype _Nullable) initWithProperties:(nullable NSDictionary *)properties withMiniAppName:(nonnull NSString *)miniApp;


@end
