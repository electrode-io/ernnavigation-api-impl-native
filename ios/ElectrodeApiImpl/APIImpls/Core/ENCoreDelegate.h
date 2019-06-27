//
//  ENCoreDelegate.h
//  ErnRunner
//
//  Created by Lianci Liu on 6/3/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^MiniAppFinishedCallback)(NSString *);

@class MiniAppNavViewController;

@protocol EnMiniAppDataProvider <NSObject>
- (NSString *_Nonnull) miniAppName;
- (NSDictionary *_Nullable) properties;
- (MiniAppFinishedCallback _Nullable) finishedCallback;
@end

@interface ENCoreDelegate : NSObject

@property (nonatomic, strong, nonnull) MiniAppNavViewController* viewController;
-(void)viewDidLoad:(UIViewController *_Nonnull)viewController;

@end
