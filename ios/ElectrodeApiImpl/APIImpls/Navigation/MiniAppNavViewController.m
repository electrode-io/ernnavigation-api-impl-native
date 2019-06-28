//
//  MiniAppViewController.m
//  ElectrodeContainer
//
//  Created by Lianci Liu on 5/17/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import "MiniAppNavViewController.h"
#import "ENNavigationDelegate.h"

@interface MiniAppNavViewController ()
@property(nonatomic) ENNavigationDelegate *delegate;
@end

@implementation MiniAppNavViewController
- (instancetype) initWithProperties:(nullable NSDictionary *)properties withMiniAppName:(nonnull NSString *)miniApp {
    if (self = [super init]) {
        self.properties = properties;
        self.miniAppName = miniApp;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = [[ENNavigationDelegate alloc] init];
    [self.delegate viewDidLoad:self];
    self.delegate.delegate = self;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:YES];
    [self.delegate viewWillAppear];
}

-(void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:YES];
    [self.delegate viewDidDisappear];
}

- (void)popToViewControllerWithPath:(NSString *)path withBlock:(ERNNavigationCompletionBlock)completion {
    [self.delegate popToViewControllerWithPath:path withBlock:completion];
}

- (void)updateNavigationBar:(NavigationBar *)navBar withBlock:(ERNNavigationCompletionBlock)completion {
    [self.delegate updateNavigationBar:navBar withBlock:completion];
}

- (void)handleFinishFlow:(NSString *)finalPayLoad withBlock:(ERNNavigationCompletionBlock)completion {
    [self.delegate handleFinishFlow:finalPayLoad withBlock:completion];
}

- (void)handleNavigationRequestWithPath:(NSDictionary *)routeData withBlock:(ERNNavigationCompletionBlock)completion {
    [self.delegate handleNavigationRequestWithPath:routeData withBlock:completion];
}
@end
