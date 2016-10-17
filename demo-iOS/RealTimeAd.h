#import <Foundation/Foundation.h>
@class AdResponseModel;
@interface RealTimeAd : NSObject

/**
    example:
    
    RealTimeAd *ad = [[RealTimeAd alloc]init];
    [ad getAdModelWithSlotID:@"203" adNumber:4 imgW:300 imgH:200 successResponse:^(NSArray *adArray) {
        if (adArray.count > 0) {
        for (int i = 0; i < adArray.count; i ++) {
                AdResponseADListModel *mod = [adArray objectAtIndex:i];
                NSLog(@"realtimeAd对象( %d )的title是：%@",i,mod.title);
                NSLog(@"realtimeAd对象( %d )的clk_url是：%@",i,mod.clk_url);
                NSLog(@"realtimeAd对象( %d )的icon是：%@",i,mod.icon);
            }
        }
    } failer:^(NSError *error) {
        NSLog(@"%@",error.description);
    }];
*/

/**
    调用requeset方法，传入必要的参数，成功返回广告数组，失败返回失败信息
    *slotid: 广告Id
    *adNumber:请求返回几个广告对象
    *imgW:图片素材宽
    *imgH:图片素材高
    *successResponse:请求成功返回的block,一个数组对象，包含AdResponseADListModel对象
    *failer:请求失败
*/
- (void)getAdModelWithSlotID:(NSString *)slotid adNumber:(NSInteger)adNumber imgW:(NSInteger)imageW imgH:(NSInteger)imageH successResponse:(void(^)(NSArray *adArray))successResponse failer:(void(^)(NSError *error))failer;
@end

//AD Requset Model
@interface AdRequsetModel : NSObject
@property (nonatomic, strong)NSString *token;       //广告位标识
@property (nonatomic, strong)NSString *os;          //操作系统
@property (nonatomic, assign)float osv;             //iOS:
@property (nonatomic, strong)NSString *dt;          //设备类型 可选值：phone,tablet,ipad,watch
@property (nonatomic, assign)NSInteger nt;          //网络类型
@property (nonatomic, assign)NSInteger imgw;        //表示需要的图片素材的宽(单位 像素) 缺省值为slot的宽
@property (nonatomic, assign)NSInteger imgh;        //表示需要的图片素材的高(单位 像素) 缺省值为slot的高
@property (nonatomic, strong)NSString *pn;          //当前宿主包名
@property (nonatomic, strong)NSString *sv;          //SDK版本号
@property (nonatomic, assign)NSInteger adnum;
@property (nonatomic, strong)NSString *gaid;        //Google Advertising Id
@property (nonatomic, strong)NSString *aid;         //设备Android ID
@property (nonatomic, strong)NSString *keywords;    //搜索关键词字符串
@property (nonatomic, strong)NSString *idfa;        //广告标识符
@property (nonatomic, strong)NSString *imei;        //设备IMEI
@property (nonatomic, strong)NSString *icc;         //ISO 
@property (nonatomic, assign)NSInteger gp;          //1:已安装google play 2:未安装google play
@property (nonatomic, strong)NSString *dpd;
@property (nonatomic, strong)NSString *cn;
@property (nonatomic, assign)float la;              //纬度
@property (nonatomic, assign)float lo;              //经度
@property (nonatomic, strong)NSString *tz;          //时区
@property (nonatomic, strong)NSString *lang;        //当前系统语言
@property (nonatomic, assign)NSInteger isdebug;     //是否是测试，测试则返回固定广告元素，但是模版为绑定模版

@end

//AD Response Model
@class AdResponseADListModel;
@interface AdResponseModel: NSObject
@property (nonatomic, strong)NSArray *ad_list;
@property (nonatomic, strong)NSString *err_msg;
@property (nonatomic, assign)NSInteger err_no;
@end

@interface AdResponseADListModel : NSObject
@property (nonatomic, assign)NSInteger landing_type;
@property (nonatomic, strong)NSString *clk_url;
@property (nonatomic, strong)NSArray *imp_tks;
@property (nonatomic, strong)NSArray *clk_tks;
@property (nonatomic, strong)NSString *icon;
@property (nonatomic, strong)NSString *icon_size;
@property (nonatomic, strong)NSString *title;
@property (nonatomic, strong)NSString *pkg_name;
@property (nonatomic, strong)NSString *image;
@property (nonatomic, strong)NSString *image_size;
@property (nonatomic, strong)NSString *desc;
@property (nonatomic, strong)NSString *button;
@end

//iOS10 openIDFA 限制时调用此类
@interface OpenIDFA : NSObject

+ (NSString*) sameDayOpenIDFA;
+ (NSArray*) threeDaysOpenIDFAArray;

@end
