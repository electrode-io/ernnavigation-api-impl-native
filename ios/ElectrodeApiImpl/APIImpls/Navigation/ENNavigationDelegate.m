//
//  ENNavigationDelegate.m
//  ErnRunner
//
//  Created by Lianci Liu on 5/31/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ENNavigationDelegate.h"
#import "ENCoreDelegate.h"
#import "ENBarButtonItem.h"

@implementation ENNavigationDelegate

-(void)viewDidLoad:(UIViewController *)viewController {
    if ([viewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navigationController = (UINavigationController *)viewController;
        if(![navigationController conformsToProtocol:@protocol(EnMiniAppDataProvider)]) {
            [NSException raise:@"viewController does not conform to EnMiniAppDataProvider" format:@"The view controller must implement EnMiniAppDataProvider protocol"];
        }
        NSString *miniAppName = [(id<EnMiniAppDataProvider>)navigationController miniAppName];
        NSDictionary *properties = [(id<EnMiniAppDataProvider>)navigationController properties];
        MiniAppNavViewController *viewController = [[MiniAppNavViewController alloc] initWithProperties:properties withMiniAppName:miniAppName];
        viewController.view.frame = [UIScreen mainScreen].bounds;
        navigationController.navigationBar.translucent = NO;
        [navigationController pushViewController:viewController animated:NO];
    } else {
        [super viewDidLoad: viewController];
    }
}

-(void)viewWillAppear {
    self.viewController.navigationController.navigationBarHidden = NO;
    if([self.viewController conformsToProtocol:@protocol(ENNavigationProtocol)]) {
        [ENNavigationAPIImpl sharedInstance].delegate = self.viewController;
    }
}

-(void)viewDidDisappear {
    self.viewController.navigationController.navigationBarHidden = NO;
}

-(void)popToViewControllerWithPath:(NSString *)path withBlock:(ERNNavigationCompletionBlock)completion {
    if (path == nil) {
        [self.viewController.navigationController popViewControllerAnimated:YES];
        return completion(@"success");
    }
    for (UIViewController *vc in [self.viewController.navigationController viewControllers]) {
        if ([vc isKindOfClass:[MiniAppNavViewController class]]) {
            MiniAppNavViewController * miniappVC = (MiniAppNavViewController *)vc;
            if ([path isEqualToString:miniappVC.miniAppName]) {
                [self.viewController.navigationController popToViewController:miniappVC animated:YES];
                return completion(@"success");
            }
        }
    }
    return completion(@"cannot find path from view Controllet stack");
}

- (void)handleFinishFlow:(NSString *)finalPayLoad withBlock:(ERNNavigationCompletionBlock)completion {
    NSDictionary * payloadDict;
    if (finalPayLoad != nil) {
        payloadDict = [self convertStringToDictionary:finalPayLoad];
    }
    if (payloadDict != nil) {
        //used in identity verification
        NSString *path = payloadDict[@"page"];
        if ([path isEqualToString:@"finishFlow"]) {
            [self.viewController dismissViewControllerAnimated:YES completion:^{
                completion(@"Finished status");
                if (self.viewController.finishedCallback) {
                    self.viewController.finishedCallback(nil);
                }
            }];
            return;
        }
    } else {
        if (self.viewController.finishedCallback) {
            self.viewController.finishedCallback(finalPayLoad);
        }
        completion(@"Finished status");
        return;
    }
}

- (void)updateNavigationBar:(NavigationBar *)navBar withBlock:(ERNNavigationCompletionBlock)completion {
    [self getNavBarTitle:navBar.title viewController:self.viewController];
    [self getNavBarButtons:navBar.buttons viewController:self.viewController];
    return completion(@"success");
}

- (void)handleNavigationRequestWithPath:(NSDictionary *)routeData withBlock:(ERNNavigationCompletionBlock)completion {
    NSString *path = routeData[@"path"];
    MiniAppNavViewController *vc = [[MiniAppNavViewController alloc] initWithProperties:routeData withMiniAppName:path];
    NavigationBar *navBar = [[NavigationBar alloc] initWithDictionary:routeData[@"navigationBar"]];
    [self getNavBarTitle:navBar.title viewController:vc];
    [self getNavBarButtons:navBar.buttons viewController:vc];
    vc.finishedCallback = self.viewController.finishedCallback;
    [self.viewController.navigationController pushViewController:vc animated:YES];
    completion(@"success");
    return;
}

- (void)getNavBarTitle:(NSString *)title viewController:(UIViewController *)vc {
    vc.title = title;
}

- (void)getNavBarButtons:(NSArray *) buttons viewController:(UIViewController *)vc {
    NSMutableArray *leftNavigationButtons = [NSMutableArray array];
    NSMutableArray *rightNavigationButtons = [NSMutableArray array];
    NSMutableArray *rightButtons = [NSMutableArray array];
    for (NSDictionary *button in buttons) {
        if ([button isKindOfClass:[NavigationBarButton class]]) {
            NavigationBarButton* btn = (NavigationBarButton *)button;
            if ([btn.location isEqualToString:@"left"]) {
                [leftNavigationButtons addObject:btn];
            } else {
                [rightNavigationButtons insertObject:btn atIndex:0];
            }
        }
    }
    NSAssert([leftNavigationButtons count] <= 1 && [rightNavigationButtons count] <= 3, @"cannot have more than one left navigation button or three right navigation buttons");
    if ([leftNavigationButtons count] == 1) {
        vc.navigationItem.leftBarButtonItem = [self getUIBarButtonItem:leftNavigationButtons[0]];
    }
    for (NavigationBarButton *rightButton in rightNavigationButtons) {
        [rightButtons addObject:[self getUIBarButtonItem:rightButton]];
    }
    vc.navigationItem.rightBarButtonItems = rightButtons;
}

- (ENBarButtonItem *)getUIBarButtonItem: (NavigationBarButton *) navigationButton {
    ENBarButtonItem *button;
    if (navigationButton.icon) {
        NSData *imageData = [[NSData alloc] initWithContentsOfURL:[NSURL URLWithString:navigationButton.icon]];
        UIImage * image = [UIImage imageWithData:imageData];
        button = [[ENBarButtonItem alloc]initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(clickButtonWithbuttonId:)];
        button.stringTag = navigationButton.id;
    } else {
        button = [[ENBarButtonItem alloc]initWithTitle:navigationButton.title style:UIBarButtonItemStylePlain target:self action:@selector(clickButtonWithbuttonId:)];
        button.stringTag = navigationButton.id;
    }
    return button;
}

- (void)clickButtonWithbuttonId:(ENBarButtonItem *)sender {
    [[[ENNavigationAPIImpl sharedInstance]navigationAPI].events emitEventOnNavButtonClickWithButtonId:sender.stringTag];
}

- (NSDictionary *)convertStringToDictionary:(NSString *)jsonPayLoad {
    NSData *data = [jsonPayLoad dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *json = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    return json;
}

@end


