//
//  ENCoreDelegate.m
//  ErnRunner
//
//  Created by Lianci Liu on 6/3/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import "ENCoreDelegate.h"
#import "MiniAppNavViewController.h"
#import "ElectrodeReactNative.h"

@implementation ENCoreDelegate

- (void)viewDidLoad:(UIViewController *)viewController {
    if([viewController isKindOfClass:[MiniAppNavViewController class]]) {
        MiniAppNavViewController *miniAppVC = (MiniAppNavViewController *)viewController;
        UIView* v = [self createView:miniAppVC.miniAppName properties:miniAppVC.properties];
        [viewController.view addSubview:v];
        if (@available(iOS 11.0, *)){
            v.frame = viewController.view.safeAreaLayoutGuide.layoutFrame;
        }
        if (@available(iOS 11, *)) {
            UILayoutGuide *guide = viewController.view.safeAreaLayoutGuide;
            [NSLayoutConstraint activateConstraints:@[
                                                      [v.leadingAnchor constraintEqualToAnchor:guide.leadingAnchor],
                                                      [v.trailingAnchor constraintEqualToAnchor:guide.trailingAnchor],
                                                      [v.topAnchor constraintEqualToAnchor:guide.topAnchor],
                                                      [v.bottomAnchor constraintEqualToAnchor:guide.bottomAnchor]
                                                      ]];
        } else {
            [NSLayoutConstraint activateConstraints:@[
                                                      [v.leadingAnchor constraintEqualToAnchor:viewController.view.leadingAnchor],
                                                      [v.topAnchor constraintEqualToAnchor:viewController.view.topAnchor],
                                                      [v.trailingAnchor constraintEqualToAnchor:viewController.view.trailingAnchor],
                                                      [v.bottomAnchor constraintEqualToAnchor:viewController.view.bottomAnchor]
                                                      ]];
        }
        self.viewController = miniAppVC;
    } else {
        [NSException raise:@"invalid ViewController" format:@"viewController must be a MiniAppViewController"];
    }
}

-(UIView *)createView:(NSString *)name properties:(NSDictionary *)properties {
    UIViewController *viewController = [[ElectrodeReactNative sharedInstance] miniAppWithName:name properties:properties];
    UIView *rnView = viewController.view;
    rnView.translatesAutoresizingMaskIntoConstraints = NO;
    return rnView;
}

@end
