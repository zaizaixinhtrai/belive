package com.appster.webservice;

import com.appster.comments.ItemClassComments;
import com.appster.domain.BlockedUserModel;
import com.appster.domain.CashItemModel;
import com.appster.domain.FriendSuggestionModel;
import com.appster.domain.LikedUsersItemModel;
import com.appster.domain.PhoneVerification;
import com.appster.domain.WithdrawnAccountItemModel;
import com.appster.domain.WithdrawnItemModel;
import com.appster.features.messages.MessageItemModelClass;
import com.appster.home.ItemModelClassHomeScreen;
import com.appster.models.AppConfigModel;
import com.appster.models.FaceUnityStickerModel;
import com.appster.models.FollowItemModel;
import com.appster.models.GiftReceiverModel;
import com.appster.models.HomeCurrentEventModel;
import com.appster.models.HomeItemModel;
import com.appster.models.LeaderBoardModel;
import com.appster.models.NetworkUploadSpeedModel;
import com.appster.models.PostDataModel;
import com.appster.models.PostDetailModel;
import com.appster.models.ProductModel;
import com.appster.models.ResourcesLoadUserImage;
import com.appster.models.SearchModel;
import com.appster.models.StarsToGemsModel;
import com.appster.models.StreamModel;
import com.appster.models.TagListLiveStreamModel;
import com.appster.models.TopFanModel;
import com.appster.models.TopFansList;
import com.appster.models.UserPostModel;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.pocket.CreditsModel;
import com.appster.webservice.request_models.AddCommentRequestModel;
import com.appster.webservice.request_models.ApiDebugModel;
import com.appster.webservice.request_models.BasePagingRequestModel;
import com.appster.webservice.request_models.BeginStreamRequestModel;
import com.appster.webservice.request_models.BlockUserRequestModel;
import com.appster.webservice.request_models.BotActionsRequestModel;
import com.appster.webservice.request_models.BotFollowRequestModel;
import com.appster.webservice.request_models.BotSendGiftRequestModel;
import com.appster.webservice.request_models.ChatBotUserRequestModel;
import com.appster.webservice.request_models.ChatHistoryRequest;
import com.appster.webservice.request_models.CommentListRequestModel;
import com.appster.webservice.request_models.CreateStreamRequestModel;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.request_models.DeleteCommentRequestModel;
import com.appster.webservice.request_models.DeleteMessageRequestModel;
import com.appster.webservice.request_models.DeletePostRequestModel;
import com.appster.webservice.request_models.DeleteStreamRequestModel;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.request_models.FollowAllUsersResquestModel;
import com.appster.webservice.request_models.FollowRequestModel;
import com.appster.webservice.request_models.GetBeanHistoryRequestModel;
import com.appster.webservice.request_models.GetLatestLiveRequest;
import com.appster.webservice.request_models.GetListMessageRequestModel;
import com.appster.webservice.request_models.GetListPostByUserRequestModel;
import com.appster.webservice.request_models.GetTopFanModel;
import com.appster.webservice.request_models.GetWallFeedRequest;
import com.appster.webservice.request_models.GiftStoreRequestModel;
import com.appster.webservice.request_models.GoogleLoginRequestModel;
import com.appster.webservice.request_models.InstagramLoginRequestModel;
import com.appster.webservice.request_models.LeaveCurrentConversationRequestModel;
import com.appster.webservice.request_models.LikePostRequestModel;
import com.appster.webservice.request_models.LikeStreamRequestModel;
import com.appster.webservice.request_models.LikedStreamUsersRequestModel;
import com.appster.webservice.request_models.LikedUsersRequestModel;
import com.appster.webservice.request_models.LoginFacebookRequestModel;
import com.appster.webservice.request_models.LogoutRequestModel;
import com.appster.webservice.request_models.LuckyWheelSpinResultRequest;
import com.appster.webservice.request_models.MakeExchangeRequestModel;
import com.appster.webservice.request_models.NearbyRequestModel;
import com.appster.webservice.request_models.NewsFeedRequestModel;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.appster.webservice.request_models.PhoneVerificationRequest;
import com.appster.webservice.request_models.PhoneVerifyVerificationCodeRequest;
import com.appster.webservice.request_models.PlayTokenAccountConnectModel;
import com.appster.webservice.request_models.PopularByTagRequestModel;
import com.appster.webservice.request_models.ReportRequestModel;
import com.appster.webservice.request_models.ReportStreamRequestModel;
import com.appster.webservice.request_models.ReportUserRequestModel;
import com.appster.webservice.request_models.SaveChatRequestModel;
import com.appster.webservice.request_models.SendGiftRequestModel;
import com.appster.webservice.request_models.SetFollowUserRequestModel;
import com.appster.webservice.request_models.SetUnfollowUserRequestModel;
import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.appster.webservice.request_models.SinglePostRequestModel;
import com.appster.webservice.request_models.SpinRequest;
import com.appster.webservice.request_models.SubStreamRequest;
import com.appster.webservice.request_models.TrendingListRequestModel;
import com.appster.webservice.request_models.TwitterLoginRequestModel;
import com.appster.webservice.request_models.UnblockUserRequestModel;
import com.appster.webservice.request_models.UpdateDeviceTokenRequestModel;
import com.appster.webservice.request_models.UpdateLocationRequestModel;
import com.appster.webservice.request_models.UserProfileRequestModel;
import com.appster.webservice.request_models.VerifyIAPRequestModel;
import com.appster.webservice.request_models.VerifyIAppPayRequestModel;
import com.appster.webservice.request_models.VerifyUsernameRequestModel;
import com.appster.webservice.request_models.VersionRequestModel;
import com.appster.webservice.request_models.ViewVideosRequestModel;
import com.appster.webservice.request_models.WeChatLoginRequestModel;
import com.appster.webservice.request_models.WeiboLoginRequestModel;
import com.appster.webservice.request_models.WithdrawnRequestModel;
import com.appster.webservice.response.AddCommentResponseModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.BeanHistoryDataResponseModel;
import com.appster.webservice.response.ChatBotDataResultModel;
import com.appster.webservice.response.ChatHistoryResponseModel;
import com.appster.webservice.response.ChatPostImageResponseModel;
import com.appster.webservice.response.ChatPostVideoResponseModel;
import com.appster.webservice.response.EditProfileResponseModel;
import com.appster.webservice.response.EndStreamDataModel;
import com.appster.webservice.response.FollowUserResponseModel;
import com.appster.webservice.response.GetCreditDataResponse;
import com.appster.webservice.response.GetRecommendResponse;
import com.appster.webservice.response.GetUserImageResponseModel;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.appster.webservice.response.LikePostResponseModel;
import com.appster.webservice.response.LoginResponseModel;
import com.appster.webservice.response.LuckyWheelAwards;
import com.appster.webservice.response.LuckyWheelSpinResponseModel;
import com.appster.webservice.response.LuckyWheelSpinResult;
import com.appster.webservice.response.MaintenanceModel;
import com.appster.webservice.response.MakeExchangeResponseModel;
import com.appster.webservice.response.NewWallfeedResponseModel;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;
import com.appster.webservice.response.RefillListResponseModel;
import com.appster.webservice.response.RegisterWithFacebookResponseModel;
import com.appster.webservice.response.SaveChatResponseModel;
import com.appster.webservice.response.SendGiftResponseModel;
import com.appster.webservice.response.StatisticStream;
import com.appster.webservice.response.StreamsRecentResponse;
import com.appster.webservice.response.SubStreamData;
import com.appster.webservice.response.SuggestionResponseModel;
import com.appster.webservice.response.UserProfileResponseModel;
import com.appster.webservice.response.VerifyIAPResponeModel;
import com.appster.webservice.response.VerifyIAppPayResponseModel;
import com.appster.webservice.response.VersionResponseModel;
import com.appster.webservice.response.VideosCountModel;
import com.appster.webservice.response.VotingLevels;
import com.appster.webservice.response.VotingSetting;
import com.data.entity.DailyBonusCheckDaysEntity;
import com.data.entity.DailyTreatListInfoEntity;
import com.data.entity.EarnPointsEntity;
import com.data.entity.EditReferralCodeEntity;
import com.data.entity.GiftStoreEntity;
import com.data.entity.LiveShowEntity;
import com.data.entity.LiveShowStatusEntity;
import com.data.entity.LiveShowWalletEntity;
import com.data.entity.MutualFriendEntity;
import com.data.entity.MysteryBoxEntity;
import com.data.entity.NotificationListEntity;
import com.data.entity.PrizeBagEntity;
import com.data.entity.PrizeEntity;
import com.data.entity.SearchUserEntity;
import com.data.entity.SocialFriendsNumEntity;
import com.data.entity.StatusEntity;
import com.data.entity.TotalCashEntity;
import com.data.entity.TransactionHistoryEntity;
import com.data.entity.TreatCollectEntity;
import com.data.entity.TreatEntity;
import com.data.entity.TriviaFinishEntity;
import com.data.entity.TriviaInfoEntity;
import com.data.entity.TriviaRankingListPagingEntity;
import com.data.entity.TriviaResultEntity;
import com.data.entity.TriviaReviveEntity;
import com.data.entity.TriviaWinnerListPagingEntity;
import com.data.entity.UserPrizeBagInfoEntity;
import com.data.entity.requests.ContactRequestWrapperEntity;
import com.data.entity.requests.EarnPointsRequestEntity;
import com.data.entity.requests.EditReferralCodeRequestEntity;
import com.data.entity.requests.LiveShowFriendNumberEntity;
import com.data.entity.requests.NotificationListRequestEntity;
import com.data.entity.requests.PickPrizeItemRequestEntity;
import com.data.entity.requests.SearchUserRequestEntity;
import com.data.entity.requests.StreamsRecentRequestEntity;
import com.data.entity.requests.SubmitRedemptionEntity;
import com.data.entity.requests.TriviaAnswerRequestEntity;
import com.data.entity.requests.TriviaFinishRequestEntity;
import com.data.entity.requests.TriviaInfoRequestEntity;
import com.data.entity.requests.TriviaRankingRequestModel;
import com.data.entity.requests.TriviaResultRequestEntity;
import com.data.entity.requests.TriviaReviveRequestEntity;
import com.data.entity.requests.TriviaWinnerListRequestEntity;
import com.domain.models.NextBonusInformationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by User on 9/8/2015.
 */
public interface AppsterWebserviceAPI {

    @POST("api/users/LoginWithFacebook")
    Observable<BaseResponse<LoginResponseModel>> loginWithFacebook(@Body LoginFacebookRequestModel request);

    @POST("api/users/LoginWithGoogle")
    Observable<BaseResponse<LoginResponseModel>> loginWithGoogle(@Body GoogleLoginRequestModel request, @Header("Connection") String connection);

    @POST("api/users/LoginWithInstagram")
    Observable<BaseResponse<LoginResponseModel>> loginWithInstagram(@Body InstagramLoginRequestModel request, @Header("Connection") String connection);

    @POST("api/users/LoginWithTwitter")
    Observable<BaseResponse<LoginResponseModel>> loginTwitter(@Body TwitterLoginRequestModel request);

    @POST("api/users/LoginWithWeChat")
    Observable<BaseResponse<LoginResponseModel>> loginWithWeChat(@Body WeChatLoginRequestModel request);

    @POST("api/users/LoginWithWeibo")
    Observable<BaseResponse<LoginResponseModel>> loginWithWeibo(@Body WeiboLoginRequestModel request);

    @POST("api/users/LoginWithPhone")
    Observable<BaseResponse<LoginResponseModel>> loginWithPhoneNumber(@Body PhoneLoginRequestModel requestModel);

    @POST("api/users/verify-phone")
    Observable<BaseResponse<PhoneVerification>> verifyPhone(@Header("Authorization") String auth, @Body PhoneVerificationRequest request);

    @POST("api/users/send-otp")
    Observable<BaseResponse<Boolean>> requestPhoneVerificationCode(@Header("SecretKey") String secretKey, @Body Map otp);

    @POST("api/users/verify-otp")
    Observable<BaseResponse<Boolean>> verifyPhoneVerificationCode(@Body PhoneVerifyVerificationCodeRequest request);

    @GET("api/users/suggest-username")
    Observable<BaseResponse<String>> getSuggestedUserId(@Header("Authorization") String auth, @Query("suggestName") String expectUserId);

    @GET("api/users/suggest-username-register")
    Observable<BaseResponse<String>> getSuggestedUserId(@Header("Authorization") String auth);

    @POST("api/users/forgot-password")
    Observable<BaseResponse<PhoneLoginForgotPasswordResponse>> forgotPassword(@Body PhoneLoginForgotPasswordRequestModel request);

    @POST("api/users/reset-password")
    Observable<BaseResponse<Boolean>> resetPassword(@Body PhoneLoginResetPasswordRequest request);

    @POST("api/chats/History")
    Observable<BaseResponse<ChatHistoryResponseModel>> getHistoryChat(@Header("Authorization") String authen, @Body ChatHistoryRequest request);


    @POST("api/chats/SendMessage")
    Observable<BaseResponse<SaveChatResponseModel>> savedChat(@Header("Authorization") String authen, @Body SaveChatRequestModel request);

    @POST("api/users/Nearby")
    Observable<BaseResponse<BaseDataPagingResponseModel<ItemModelClassHomeScreen>>> homeGetNearbyYou(@Header("Authorization") String authen, @Body NearbyRequestModel request);


    @POST("api/posts/Feed")
    Observable<BaseResponse<BaseDataPagingResponseModel<ItemModelClassNewsFeed>>> newsFeedGetData(@Header("Authorization") String authen, @Body NewsFeedRequestModel request);

    @POST("api/posts/TrendingPost")
    Observable<BaseResponse<BaseDataPagingResponseModel<ItemModelClassNewsFeed>>> newsFeedTrendingPosts(@Header("Authorization") String authen, @Body NewsFeedRequestModel request);


    @POST("api/posts/UploadImage")
    Observable<BaseResponse<ChatPostImageResponseModel>> chatPostImage(@Header("Authorization") String authen, @Body MultipartBody request);


    @POST("api/posts/UploadVideo")
    Observable<BaseResponse<ChatPostVideoResponseModel>> chatPostVideo(@Header("Authorization") String authen, @Body MultipartBody request);


    @POST("api/posts/CreatePost")
    Observable<BaseResponse<PostDataModel>> postCreatePost(@Header("Authorization") String authen, @Body MultipartBody request);


    @POST("api/users/Details")
    Observable<BaseResponse<UserProfileResponseModel>> getUserProfile(@Header("Authorization") String authen, @Body UserProfileRequestModel request);


    @POST("api/Follows/UserFollowersList")
    Observable<BaseResponse<BaseDataPagingResponseModel<FollowItemModel>>> getFollowersUsers(@Header("Authorization") String authen, @Body FollowRequestModel request);


    @POST("api/Follows/UserFollowingList")
    Observable<BaseResponse<BaseDataPagingResponseModel<FollowItemModel>>> getFollowingUsers(@Header("Authorization") String authen, @Body FollowRequestModel request);

    @POST("api/users/EditProfile")
    Observable<BaseResponse<EditProfileResponseModel>> editProfile(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/topups/TopUpList")
    Observable<BaseResponse<RefillListResponseModel>> getRefillList(@Header("Authorization") String authen);


    @POST("api/users/PopularLeaderboard")
    Observable<BaseResponse<List<LeaderBoardModel>>> getTrendingList(@Header("Authorization") String authen, @Body TrendingListRequestModel request);

    @POST("api/posts/PostComment")
    Observable<BaseResponse<BaseDataPagingResponseModel<ItemClassComments>>> getCommentList(@Header("Authorization") String authen, @Body CommentListRequestModel request);

    @GET("api/streams/{slug}/comments")
    Observable<BaseResponse<BaseDataPagingResponseModel<ItemClassComments>>> getRecordedCommentList(@Header("Authorization") String auth, @Path("slug") String slug, @Query("nextId") int nextId, @Query("limit") int limit);


    @POST("api/comments/AddComment")
    Observable<BaseResponse<AddCommentResponseModel>> addComment(@Header("Authorization") String authen, @Body AddCommentRequestModel request);


    @POST("api/chats/DeleteConversation")
    Observable<BaseResponse<Boolean>> deleteMessage(@Header("Authorization") String authen, @Body DeleteMessageRequestModel request);


    @POST("api/chats/Conversations")
    Observable<BaseResponse<BaseDataPagingResponseModel<MessageItemModelClass>>> getListMessage(@Header("Authorization") String authen, @Body GetListMessageRequestModel request);

    @POST("api/users/Settings")
    Observable<BaseResponse<Boolean>> setSettingFeatures(@Header("Authorization") String authen, @Body SettingFeaturesRequestModel request);

    @POST("api/users/LogOut")
    Observable<BaseResponse<Boolean>> logoutApp(@Header("Authorization") String authen, @Body LogoutRequestModel request);

    @POST("api/users/DeactivateAccount")
    Observable<BaseResponse<Boolean>> deactivateAccount(@Header("Authorization") String authen, @Body DeactivateAccountRequsetModel request);

    @POST("api/users/Credit")
    Observable<BaseResponse<CreditsModel>> getUserCredits(@Header("Authorization") String authen, @Body CreditsRequestModel requset);

    @POST("api/gifts/GiftStore")
    Observable<BaseResponse<GiftStoreEntity>> getGiftStore(@Header("Authorization") String authen);

    @GET("api/face-stickers")
    Observable<BaseResponse<List<FaceUnityStickerModel>>> getFaceUnityStickerList(@Header("Authorization") String auth, @Query("type") int OsType);

    @POST("api/Gifts/SendGift")
    Observable<BaseResponse<SendGiftResponseModel>> sendGift(@Header("Authorization") String authen, @Body SendGiftRequestModel request);

    @POST("api/posts/EditPost")
    Observable<BaseResponse<PostDataModel>> editPost(@Header("Authorization") String authen, @Body EditPostRequestModel request);


    @POST("api/posts/LikePost")
    Observable<BaseResponse<LikePostResponseModel>> likePost(@Header("Authorization") String authen, @Body LikePostRequestModel request);


    @POST("api/gifts/HasBeenReceived")
    Observable<BaseResponse<BaseDataPagingResponseModel<GiftReceiverModel>>> getListGiftReceive(@Header("Authorization") String authen, @Body GiftStoreRequestModel request);


    @POST("api/gifts/HasBeenSent")
    Observable<BaseResponse<BaseDataPagingResponseModel<GiftReceiverModel>>> getListGiftSend(@Header("Authorization") String authen, @Body GiftStoreRequestModel request);


    @POST("api/ledgershares/BeanHistory")
    Observable<BaseResponse<BeanHistoryDataResponseModel>> getBeansHistory(@Header("Authorization") String authen, @Body GetBeanHistoryRequestModel request);


    @POST("api/ledgershares/GoldHistory")
    Observable<BaseResponse<BeanHistoryDataResponseModel>> getGoldHistory(@Header("Authorization") String authen, @Body GetBeanHistoryRequestModel request);


    @POST("api/chats/LeaveCurrentConversation")
    Observable<BaseResponse<Boolean>> leaveConversation(@Header("Authorization") String authen, @Body LeaveCurrentConversationRequestModel request);


    @POST("api/users/UpdateDeviceToken")
    Observable<BaseResponse<Boolean>> updateDeviceToken(@Header("Authorization") String authen, @Body UpdateDeviceTokenRequestModel request);


    @POST("api/posts/details")
    Observable<BaseResponse<PostDetailModel>> getSinglePost(@Header("Authorization") String authen, @Body SinglePostRequestModel request);


    @POST("api/Follows/Follow")
    Observable<BaseResponse<FollowUserResponseModel>> setFollowUser(@Header("Authorization") String authen, @Body SetFollowUserRequestModel request);

    @POST("api/Follows/Unfollow")
    Observable<BaseResponse<FollowUserResponseModel>> setUnfollowUser(@Header("Authorization") String authen, @Body SetUnfollowUserRequestModel request);

    @POST("api/topups/InAppPurchase")
    Observable<BaseResponse<VerifyIAPResponeModel>> verifyIAPPurchased(@Header("Authorization") String authen, @Body VerifyIAPRequestModel request);

    @POST("api/topups/InAppPurchaseChina")
    Observable<BaseResponse<VerifyIAppPayResponseModel>> verifyIAppPayPurchased(@Header("Authorization") String authen, @Body VerifyIAppPayRequestModel request);


    @POST("api/users/RegisterWithGoogle")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithGoogle(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/users/RegisterWithFacebook")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithFacebook(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/users/RegisterWithInstagram")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithInstagram(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/users/RegisterWithTwitter")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithTwitter(@Body MultipartBody request);

    @POST("api/users/RegisterWithWeChat")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithWeChat(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/users/RegisterWithWeibo")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithWeibo(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/users/RegisterWithPhone")
    Observable<BaseResponse<RegisterWithFacebookResponseModel>> registerWithPhoneNumber(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/reports/ReportUser")
    Observable<BaseResponse<Boolean>> reportUser(@Header("Authorization") String authen, @Body ReportUserRequestModel request);

    @POST("api/users/BlockUser")
    Observable<BaseResponse<Boolean>> blockUser(@Header("Authorization") String authen, @Body BlockUserRequestModel request);

    @POST("api/users/UnBlockUser")
    Observable<BaseResponse<Boolean>> unblockUser(@Header("Authorization") String authen, @Body UnblockUserRequestModel request);

    @POST("api/users/ListPostByUser")
    Observable<BaseResponse<BaseDataPagingResponseModel<UserPostModel>>> getListPostByUser(@Header("Authorization") String authen, @Body GetListPostByUserRequestModel request);


    @POST("api/streams/CreateStream")
    Observable<BaseResponse<StreamModel>> createStream(@Header("Authorization") String authen, @Body CreateStreamRequestModel request);

    @PUT("api/streams/{slug}/recording")
    Observable<BaseResponse> saveStream(@Header("Authorization") String auth, @Path("slug") String slug);

    @POST("api/streams/BeginStream")
    Observable<BaseResponse<Boolean>> beginStream(@Header("Authorization") String authen, @Body BeginStreamRequestModel request);


    @POST("api/streams/EndStream")
    Observable<BaseResponse<EndStreamDataModel>> endStream(@Header("Authorization") String authen, @Body BeginStreamRequestModel request);

    @POST("api/streams/StreamDetail")
    Observable<BaseResponse<StreamModel>> streamDetail(@Header("Authorization") String authen, @Body BeginStreamRequestModel request);

    @GET("api/users/{id}/product")
    Observable<BaseResponse<ProductModel>> getProductDetail(@Header("Authorization") String auth, @Path("id") String userId);

    @GET("api/users/{id}/products")
    Observable<BaseResponse<List<ProductModel>>> getProductsDetail(@Header("Authorization") String auth, @Path("id") String userId);

    @GET("api/streams/{slug}/resume")
    Observable<BaseResponse<StreamModel>> streamResumeDetail(@Header("Authorization") String authen, @Path("slug") String slug);

    @POST("api/streams/StatisticStream")
    Observable<BaseResponse<StatisticStream>> statisticStream(@Header("Authorization") String authen, @Body BeginStreamRequestModel request);

    @POST("api/streams/LikeStream")
    Observable<BaseResponse<LikePostResponseModel>> likeStream(@Header("Authorization") String authen, @Body LikeStreamRequestModel request);


    @POST("api/streams/TagList")
    Observable<BaseResponse<List<TagListLiveStreamModel>>> getTagListLiveStream(@Header("Authorization") String authen);


    @POST("api/streams/PopularByTag")
    Observable<BaseResponse<BaseDataPagingResponseModel<HomeItemModel>>> getPopularByTag(@Header("Authorization") String authen, @Body PopularByTagRequestModel request);


    @GET("api/streams/latest-live")
    Observable<BaseResponse<BaseDataPagingResponseModel<HomeItemModel>>> getLatestLive(@Header("Authorization") String authen, @Query("nextId") int nextId, @Query("limit") int limit);

    @GET("api/streams/nearby")
    Observable<BaseResponse<BaseDataPagingResponseModel<HomeItemModel>>> getNearbyLive(@Header("Authorization") String authen, @Query("latitude") double lat, @Query("longitude") double lon, @Query("nextId") int nextId, @Query("limit") int limit);

    @GET("api/events/CurrentEvent")
    Observable<BaseResponse<List<HomeCurrentEventModel>>> getCurrentEvent(@Header("Authorization") String authen, @Query("type") int categoryId);

    @POST("api/users/UpdateLocation")
    Observable<BaseResponse<Boolean>> updateLocation(@Header("Authorization") String authen, @Body UpdateLocationRequestModel request);

    @GET("api/users/UserImage")
    Observable<GetUserImageResponseModel> getUserImageAndDisplayName(@Query("username") String username);


    @GET("api/exchange/Rates")
    Observable<BaseResponse<List<StarsToGemsModel>>> getExchangeRate();

    @POST("api/exchange/MakeExchange")
    Observable<BaseResponse<MakeExchangeResponseModel>> makeExchangeRate(@Header("Authorization") String authen, @Body MakeExchangeRequestModel request);

    @POST("api/users/TopFan")
    Observable<BaseResponse<BaseDataPagingResponseModel<TopFanModel>>> getTopFan(@Header("Authorization") String authen, @Body GetTopFanModel request);

    @GET("api/users/top-fan")
    Observable<BaseResponse<BaseDataPagingResponseModel<TopFanModel>>> getTopFanStream(@Header("Authorization") String authen, @QueryMap Map<String, String> params);

    @GET("api/users/topfanList ")
    Observable<BaseResponse<TopFansList>> getAllTopFanStream(@Header("Authorization") String authen, @QueryMap Map<String, String> params);

    @POST("api/streams/BotUsers")
    Observable<BaseResponse<ChatBotDataResultModel>> getBotUsers(@Header("Authorization") String authen, @Body ChatBotUserRequestModel request);

    @POST("api/streams/BulkBotAction")
    Observable<BaseResponse<StatisticStream>> botActions(@Header("Authorization") String authen, @Body BotActionsRequestModel request);

    @POST("api/Gifts/BotSendGift")
    Observable<BaseResponse<SendGiftResponseModel>> botSendGift(@Header("Authorization") String authen, @Body BotSendGiftRequestModel request);

    @POST("api/posts/ListUserLike")
    Observable<BaseResponse<BaseDataPagingResponseModel<LikedUsersItemModel>>> getLikedUsers(@Header("Authorization") String authen, @Body LikedUsersRequestModel request);

    @POST("api/users/Blocks")
    Observable<BaseResponse<BaseDataPagingResponseModel<BlockedUserModel>>> getBlockedUsers(@Header("Authorization") String authen, @Body BasePagingRequestModel request);

    @GET("api/users/suggestion")
    Observable<BaseResponse<GetRecommendResponse>> getRecommend(@Header("Authorization") String authen);

    @GET("api/users/suggestion")
    Observable<SuggestionResponseModel> getSuggestion(@Header("Authorization") String authen);

    @GET("api/users/latest-post-top-user")
    Observable<BaseResponse<LatestPostTopResponseModel>> getLatestPostTop(@Header("Authorization") String authen, @Query("NextId") int nextId, @Query("StartPage") int startPage);

    @POST("api/settings/ForceUpdateVersion")
    Observable<BaseResponse<VersionResponseModel>> checkVersion(@Header("Authorization") String authen, @Body VersionRequestModel requestModel);

    @POST("api/streams/SaveChatHistory")
    Observable<BaseResponse<Boolean>> saveStreamChatHistory(@Header("Authorization") String authen, @Body MultipartBody request);

    @POST("api/streams/LatestLive")
    Observable<BaseResponse<BaseDataPagingResponseModel<StreamModel>>> getLatestLive(@Header("Authorization") String authen, @Body GetLatestLiveRequest request);

    @POST("api/users/WallFeed")
    Observable<BaseResponse<BaseDataPagingResponseModel<UserPostModel>>> getWallFeed(@Header("Authorization") String authen, @Body GetWallFeedRequest request);

    @GET("api/settings/GetNaughtyWords")
    Observable<BaseResponse<ArrayList<String>>> getNaughtyWords();

    @POST("api/users/VerifyUsername")
    Observable<BaseResponse<Boolean>> verifyUsername(@Header("Authorization") String authen, @Body VerifyUsernameRequestModel request);

    @POST("api/posts/DeletePost")
    Observable<BaseResponse<Boolean>> deletePost(@Header("Authorization") String authen, @Body DeletePostRequestModel request);

    @POST("api/streams/Delete")
    Observable<BaseResponse<Boolean>> deleteStream(@Header("Authorization") String authen, @Body DeleteStreamRequestModel request);

    @POST("api/streams/Report")
    Observable<BaseResponse<Boolean>> reportStream(@Header("Authorization") String authen, @Body ReportStreamRequestModel request);

    @POST("api/streams/Unreport")
    Observable<BaseResponse<Boolean>> unReportStream(@Header("Authorization") String authen, @Body ReportStreamRequestModel request);

    @POST("api/reports/Report")
    Observable<BaseResponse<Boolean>> report(@Header("Authorization") String authen, @Body ReportRequestModel request);

    @POST("api/reports/Unreport")
    Observable<BaseResponse<Boolean>> unReport(@Header("Authorization") String authen, @Body ReportRequestModel request);

    @POST("api/posts/TrackingView")
    Observable<BaseResponse<VideosCountModel>> viewVideos(@Header("Authorization") String authen, @Body ViewVideosRequestModel request);

    @GET
    Observable<MaintenanceModel> checkMaintenance(@Url String url);

    @POST("api/streams/ListUserLikes")
    Observable<BaseResponse<BaseDataPagingResponseModel<LikedUsersItemModel>>> getLikedStreamUsers(@Header("Authorization") String authen, @Body LikedStreamUsersRequestModel request);

    @GET("api/users/{id}/Credit")
    Observable<BaseResponse<GetCreditDataResponse>> getCredit(@Path("id") String id);

    @POST("api/streams/SaveCoverImage")
    Observable<BaseResponse<Boolean>> saveFirstStreamImage(@Header("Authorization") String authen, @Body MultipartBody request);

    @GET("api/payments/Rates")
    Observable<BaseResponse<ArrayList<CashItemModel>>> getPaymentRates(@Header("Authorization") String authen);

    @GET("api/payments/AccountList")
    Observable<BaseResponse<ArrayList<WithdrawnAccountItemModel>>> getAccountList(@Header("Authorization") String authen);

    @POST("api/payments/Withdrawn")
    Observable<BaseResponse<WithdrawnItemModel>> withdrawn(@Header("Authorization") String authen, @Body WithdrawnRequestModel withdrawnRequestModel);

    @GET("api/votings/levels")
    Observable<BaseResponse<ArrayList<VotingLevels>>> getVotingLevels(@Header("Authorization") String authen);

    @GET("api/votings/levels/{id}/awards")
    Observable<BaseResponse<ArrayList<LuckyWheelAwards>>> getVoteAwards(@Header("Authorization") String authen, @Path("id") int id);

    @POST("api/votings/luckywheel/spin")
    Observable<BaseResponse<LuckyWheelSpinResponseModel>> getLuckyWheelResult(@Header("Authorization") String authen, @Body SpinRequest spinRequest);

    @PUT("api/votings/luckywheel/spin/{id}")
    Observable<BaseResponse<LuckyWheelSpinResult>> luckyWheelResult(@Header("Authorization") String auth, @Path("id") int spinId, @Body LuckyWheelSpinResultRequest spinResultRequest);

    @GET("api/votings/settings")
    Observable<BaseResponse<VotingSetting>> getEventSetting(@Header("Authorization") String auth);

    @GET("api/settings/resources")
    Observable<ResourcesLoadUserImage> getResourcesLoadUserImage();

    @GET
    Observable<NetworkUploadSpeedModel> getBytesInRate(@Header("Accept") String auth, @Url String url);

    @GET("api/settings/configs")
    Observable<BaseResponse<AppConfigModel>> getAppConfigs(@Header("Authorization") String auth);


    @POST("api/streams/{slug}/mute")
    Observable<BaseResponse<Boolean>> muteUser(@Header("Authorization") String auth, @Path("slug") String slug, @Body int userId);

    @DELETE("api/streams/{slug}/mute/{userId}")
    Observable<BaseResponse<Boolean>> unMuteUser(@Header("Authorization") String auth, @Path("slug") String slug, @Path("userId") String userId);

    @GET("api/streams/{slug}/mute/{userId}")
    Observable<BaseResponse<Boolean>> checkMutedUser(@Header("Authorization") String auth, @Path("slug") String slug, @Path("userId") String userId);

    @PATCH("api/streams/{slug}/leave-stream")
    Observable<BaseResponse<Boolean>> leaveStream(@Header("Authorization") String auth, @Path("slug") String slug, @Body int streamUserId);

    @POST("api/playtoken/account/connect")
    Observable<BaseResponse<Boolean>> playTokenAccountConnect(@Header("Authorization") String auth, @Body PlayTokenAccountConnectModel model);

    @POST("api/playtoken/debug")
    Observable<BaseResponse<Boolean>> playTokenDebug(@Body ApiDebugModel model);

    @GET("api/users/facebook-friends")
    Observable<BaseResponse<BaseDataPagingResponseModel<FriendSuggestionModel>>> getFriendListOnBelive(@Header("Authorization") String auth, @QueryMap Map<String, String> params);

    @POST("api/streams/{slug}/position")
    Observable<BaseResponse> storeStreamTitleSticker(@Header("Authorization") String auth, @Path("slug") String slug, @Body String data);

    @GET("api/streams/{slug}/position")
    Observable<BaseResponse<String>> getStreamTitleSticker(@Header("Authorization") String auth, @Path("slug") String slug);

    @PATCH("api/streams/{slug}/resume")
    Observable<BaseResponse<Boolean>> resumeStream(@Header("Authorization") String auth, @Path("slug") String slug);

    @PATCH("api/streams/{slug}/afk")
    Observable<BaseResponse<Boolean>> streamAFK(@Header("Authorization") String auth, @Path("slug") String slug);

    @DELETE("api/streams/{slug}/afk")
    Observable<BaseResponse<Boolean>> resumeFromAFK(@Header("Authorization") String auth, @Path("slug") String slug);

    @GET("api/users/suggestion")
    Observable<BaseResponse<List<SearchModel>>> getSuggestion(@Header("Authorization") String authen, @Query("type") int type);

    @POST("api/Follows/BotFollow")
    Observable<BaseResponse<FollowUserResponseModel>> botFollow(@Header("Authorization") String authen, @Body BotFollowRequestModel request);

    @PATCH("api/users/{userid}/ban")
    Observable<BaseResponse<Boolean>> banUser(@Header("Authorization") String auth, @Path("userid") String userid);

    @PATCH("api/users/{userid}/stop")
    Observable<BaseResponse<Boolean>> stopStream(@Header("Authorization") String auth, @Path("userid") String userid);

    @PATCH("api/streams/{slug}/hide/{status}")
    Observable<BaseResponse<Boolean>> hideStream(@Header("Authorization") String auth, @Path("slug") String slug, @Path("status") int status);

    @POST("api/streams/{slug}/call")
    Observable<BaseResponse<SubStreamData>> createSubStream(@Header("Authorization") String auth, @Path("slug") String slug, @Body SubStreamRequest request);

    @PATCH("api/streams/{slug}/call/{callSlug}/status/{status}")
    Observable<BaseResponse<SubStreamData>> updateSubStreamStatus(@Header("Authorization") String auth, @Path("slug") String slug, @Path("callSlug") String callSlug, @Path("status") int status);

    @PATCH("api/streams/{slug}/call/{callSlug}/afk")
    Observable<BaseResponse<Boolean>> subStreamAFK(@Header("Authorization") String auth, @Path("slug") String slug, @Path("callSlug") String subStreamSlug);

    @DELETE("api/streams/{slug}/call/{callSlug}/afk")
    Observable<BaseResponse<Boolean>> subStreamResumeFromAFK(@Header("Authorization") String auth, @Path("slug") String slug, @Path("callSlug") String subStreamSlug);

    @GET("api/users/unread-message")
    Observable<BaseResponse<Integer>> getUnreadMessage(@Header("Authorization") String auth);

    @POST("api/Follows/BatchFollow")
    Observable<BaseResponse<Boolean>> followAllUsers(@Header("Authorization") String authen, @Body FollowAllUsersResquestModel request);

    @POST("api/Follows/BatchUnfollow")
    Observable<BaseResponse<Boolean>> unfollowAllUsers(@Header("Authorization") String authen, @Body FollowAllUsersResquestModel request);

    @GET("api/users/new-wallfeed")
    Observable<BaseResponse<NewWallfeedResponseModel>> getNewWallfeed(@Header("Authorization") String auth);

    @GET("api/streams/{slug}/call/{callSlug}")
    Observable<BaseResponse<SubStreamData>> getSubStreamDetail(@Header("Authorization") String auth, @Path("slug") String slug, @Path("callSlug") String subStreamSlug);

    @GET("api/cashouts/check-cashout")
    Observable<BaseResponse<Boolean>> checkCashout(@Header("Authorization") String authen);

    @GET("api/cashouts/total_cashout")
    Observable<BaseResponse<TotalCashEntity>> getTotalCashout(@Header("Authorization") String authen);

    @POST("api/cashouts/histories")
    Observable<BaseResponse<BaseDataPagingResponseModel<TransactionHistoryEntity>>> getTransactionHistory(@Header("Authorization") String authen, @Body BasePagingRequestModel pagingRequestModel);

    @GET("api/daily-bonus/next-information")
    Observable<BaseResponse<NextBonusInformationModel>> getBonusInformation(@Header("Authorization") String authen);

    @GET("api/daily-bonus/treat-list")
    Observable<BaseResponse<List<DailyTreatListInfoEntity>>> getTreatListInfo(@Header("Authorization") String authen);

    @GET("api/daily-bonus/items")
    Observable<BaseResponse<List<TreatEntity>>> getTreatList(@Header("Authorization") String authen);

    @POST("api/daily-bonus/collect")
    Observable<BaseResponse<TreatCollectEntity>> collect(@Header("Authorization") String authen);

    @GET("api/daily-bonus/claimed")
    Observable<BaseResponse<TreatEntity>> getClaimedTreat(@Header("Authorization") String authen);

    @GET("api/daily-bonus/show")
    Observable<BaseResponse<Boolean>> checkDailyBonusDisplayed(@Header("Authorization") String authen);

    @PATCH("api/daily-bonus/show")
    Observable<BaseResponse<Boolean>> updateDailyBonusDisplayed(@Header("Authorization") String authen);

    @POST("api/comments/DeleteComment")
    Observable<BaseResponse<Boolean>> deleteComment(@Header("Authorization") String authen, @Body DeleteCommentRequestModel requestModel);

    @POST("api/users/phone-contacts")
    Observable<BaseResponse<List<MutualFriendEntity>>> getMutualFriends(@Header("Authorization") String authen, @Body ContactRequestWrapperEntity requestModel);

    @GET("api/users/friends")
    Observable<BaseResponse<SocialFriendsNumEntity>> getSocialFriends(@Header("Authorization") String authen);

    @POST("api/Trivia/Info")
    Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfo(@Header("Authorization") String authen, @Body TriviaInfoRequestEntity requestParams);

    @POST("api/Trivia/Answer")
    Observable<BaseResponse<StatusEntity>> getTriviaAnswer(@Header("Authorization") String authen, @Body TriviaAnswerRequestEntity requestParams);

    @POST("api/Trivia/Result")
    Observable<BaseResponse<TriviaResultEntity>> getTriviaResult(@Header("Authorization") String authen, @Body TriviaResultRequestEntity requestParams);

    @POST("api/Trivia/Finish")
    Observable<BaseResponse<TriviaFinishEntity>> getTriviaFinish(@Header("Authorization") String authen, @Body TriviaFinishRequestEntity requestParams);

    @POST("api/Trivia/ReviveCheck")
    Observable<BaseResponse<TriviaReviveEntity>> checkRevive(@Header("Authorization") String authen, @Body TriviaReviveRequestEntity requestParams);

    @POST("api/Trivia/ReviveUse")
    Observable<BaseResponse<StatusEntity>> useRevive(@Header("Authorization") String authen, @Body TriviaReviveRequestEntity requestParams);

    @POST("api/Trivia/Ranking")
    Observable<BaseResponse<TriviaRankingListPagingEntity>> getTriviaRankingList(@Header("Authorization") String authen, @Body TriviaRankingRequestModel requestParams);

    @POST("api/Trivia/Winners")
    Observable<BaseResponse<TriviaWinnerListPagingEntity>> getTriviaWinnerList(@Header("Authorization") String authen, @Body TriviaWinnerListRequestEntity requestParams);

    @POST("api/users/EditRefCode")
    Observable<BaseResponse<EditReferralCodeEntity>> editRefCode(@Header("Authorization") String authen, @Body EditReferralCodeRequestEntity request);

    @POST("api/streams/Recent")
    Observable<BaseResponse<StreamsRecentResponse>> getStreamsRecent(@Header("Authorization") String authen, @Body StreamsRecentRequestEntity request);

    @GET("api/streams/CheckLive")
    Observable<BaseResponse<Boolean>> checkHasLiveVideo(@Header("Authorization") String authen);

    @POST("api/users/Search")
    Observable<BaseResponse<BaseDataPagingResponseModel<SearchUserEntity>>> searchUser(@Header("Authorization") String authen, @Body SearchUserRequestEntity request);

    @GET("api/live-shows/shows")
    Observable<BaseResponse<List<LiveShowEntity>>> fetchLiveShows(@Header("Authorization") String authen);

    @GET("api/live-shows/status")
    Observable<BaseResponse<LiveShowStatusEntity>> checkStatus(@Header("Authorization") String authen, @Query("id") int showId);

    @GET("api/daily-bonus/today")
    Observable<BaseResponse<DailyBonusCheckDaysEntity>> checkDays(@Header("Authorization") String authen);

    @GET("api/cashouts/cashout-trivia")
    Observable<BaseResponse<LiveShowWalletEntity>> liveShowWallet(@Header("Authorization") String authen, @Query("wallet") int walletGroup);

    @POST("api/Follows/JoinedStream")
    Observable<BaseResponse<LiveShowFriendNumberEntity>> liveShowFriendNumber(@Header("Authorization") String authen, @Query("streamId") int streamId);

    @POST("api/Trivia/InfoQuestions")
    Observable<BaseResponse<TriviaInfoEntity>> getTriviaQuestion(@Header("Authorization") String authen, @Query("triviaId") int triviaId);

    @POST("api/Trivia/InfoTime")
    Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfoTime(@Header("Authorization") String authen, @Body TriviaInfoRequestEntity requestParams);

    @POST("api/points/Earn")
    Observable<BaseResponse<EarnPointsEntity>> earnPoints(@Header("Authorization") String authen, @Body EarnPointsRequestEntity requestParams);

    @GET("api/prize/boxes")
    Observable<BaseResponse<List<MysteryBoxEntity>>> getMysteryBoxes(@Header("Authorization") String authen);

    @GET("api/prize/boxes/{id}/items")
    Observable<BaseResponse<List<PrizeEntity>>> getPrizeList(@Header("Authorization") String authen, @Path("id") int mysteryBoxId);

    @GET("api/prize/user-bag")
    Observable<BaseResponse<List<PrizeBagEntity>>> getPrizeBagList(@Header("Authorization") String authen);

    @POST("api/prize/redeem-item")
    Observable<BaseResponse<Boolean>> submitRedemption(@Header("Authorization") String authen, @Body SubmitRedemptionEntity requestParams);

    @POST("api/prize/user-bag")
    Observable<BaseResponse<String>> pickPrizeBagItem(@Header("Authorization") String authen, @Body int id);

    @POST("api/prize/pick-item")
    Observable<BaseResponse<PrizeEntity>> openMysteryBox(@Header("Authorization") String authen, @Body PickPrizeItemRequestEntity prizeItemRequestEntity);

    @GET("api/prize/user-bag/picked-items")
    Observable<BaseResponse<UserPrizeBagInfoEntity>> getUserPrizeBagInfo(@Header("Authorization") String authen);

    @POST("api/notifications/Notifications")
    Observable<BaseResponse<BaseDataPagingResponseModel<NotificationListEntity>>> getNotificationList(@Header("Authorization") String authen, @Body NotificationListRequestEntity notificationRequestModel);
}
