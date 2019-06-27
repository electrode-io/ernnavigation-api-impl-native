//
//  ENNavigationProtocol.h
//  ElectrodeContainer
//
//  Created by Lianci Liu on 5/17/19.
//  Copyright © 2019 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ElectrodeApiImpl/ElectrodeApiImpl-Swift.h>
@class NavigationBar;
@class EnNavigationAPI;

NS_ASSUME_NONNULL_BEGIN

typedef void(^ERNNavigationCompletionBlock)(NSString *);

@protocol ENNavigationProtocol <NSObject>
@optional
- (void)handleNavigationRequestWithPath:(NSDictionary * _Nonnull)routeData withBlock:(ERNNavigationCompletionBlock)completion;
/**
 handle "finish" flow for navigation.
 @param finalPayLoad Optional payload send by React Native when flow is complete
 @param completion block
 */
- (void)handleFinishFlow:(NSString * _Nullable)finalPayLoad withBlock:(ERNNavigationCompletionBlock)completion;
- (void)popToViewControllerWithPath:(NSString *_Nullable)path withBlock:(ERNNavigationCompletionBlock)completion;
- (void)updateNavigationBar:(NavigationBar * _Nonnull)navBar withBlock:(ERNNavigationCompletionBlock)completion;
@end

@interface ENNavigationAPIImpl : NSObject

+ (instancetype _Nonnull )sharedInstance;

@property (nonatomic, strong, nonnull) EnNavigationAPI* navigationAPI;
@property (nonatomic, weak) id <ENNavigationProtocol> delegate;

@end

NS_ASSUME_NONNULL_END
