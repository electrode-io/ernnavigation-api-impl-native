//
//  ENNavigationDelegate.h
//  ErnRunner
//
//  Created by Lianci Liu on 5/31/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import "MiniAppNavViewController.h"
#import "ENCoreDelegate.h"
#import <UIKit/UIKit.h>

@interface ENNavigationDelegate : ENCoreDelegate

@property (nonatomic, strong, nonnull) EnNavigationAPI* navigationAPI;
@property (nonatomic, weak, nullable) id <ENNavigationProtocol> delegate;

- (void)handleFinishFlow:(NSString *_Nonnull)finalPayLoad withBlock:(ERNNavigationCompletionBlock _Nullable)completion;
- (void)handleNavigationRequestWithPath:(NSDictionary *_Nonnull)routeData withBlock:(ERNNavigationCompletionBlock _Nullable)completion;
- (void)popToViewControllerWithPath:(NSString *_Nullable)path withBlock:(ERNNavigationCompletionBlock _Nullable)completion;
- (void)updateNavigationBar:(NavigationBar *_Nonnull)navBar withBlock:(ERNNavigationCompletionBlock _Nullable)completion;
- (void)viewWillAppear;
- (void)viewDidDisappear;

@end
