CloudTech 简易版API
===

说明
---

该API供应用后端服务调用，实时返回广告，调用者需提供IP白名单

接口URI
---

`http://api.cloudmobi.net:30001/api/v1/realtime/get`

HTTP方法
---

GET

请求参数
---

| **字段名** | **类型** | **是否必填** | **字段含义** |
|:--:|:--:|:--:|:--:|
| token | 字符串 | 必填 | 广告位标识 |
| os | 字符串 | 必填 | 操作系统 可选值：Android,iOS |
| osv | 浮点 | 必填 | Android: Build.VERSION.SDK, iOS: [[[UIDevice currentDevice] systemVersion] floatValue]; |
| dt | 字符串 | 必填 | 设备类型 可选值：phone,tablet,ipad,watch |
| nt | 整型 | 必填 | 网络类型 Android: NetworkInfo.getType()  iOS: [[dataNetWorkItemView valueForKey:@"dataNetworkType"] integerValue] |
| clip | 字符串 | 必填 | 客户端ip |
| imgw | 整型 | 必填 | 表示需要的图片素材的宽(单位 像素) 缺省值为slot的宽 |
| imgh | 整型 | 必填 | 表示需要的图片素材的高(单位 像素) 缺省值为slot的高 |
| pn | 字符串 | 必填 | 当前宿主包名 |
| sv | 字符串 | 必填 | SDK版本号 |
| gaid | 字符串 | 选填 | Google Advertising Id |
| aid | 字符串 | 选填 | 设备Android ID|
| keywords | 字符串 | 选填 | 搜索关键词字符串 |
| idfa | 字符串 | 选填 | 设备IDFA |
| imei | 字符串 | 选填 | 设备IMEI |
| icc | 字符串 | 选填 | ISO country code SIM卡国家代码  Android: TelephonyManager.getNetworkCountryIso()  iOS: [[NSLocale  currentLocale] objectForKey:NSLocaleCountryCode]; |
| gp | 整型 | 选填 | 1:已安装google play 2:未安装google play |
| dpd | 字符串 | 选填 | Device product |
| cn | 字符串 | 选填 | Carrier name 运营商名称 |
| la | 浮点数 | 选填 | 纬度 |
| lo | 浮点数 | 选填 | 经度 |
| tz | 字符串 | 选填 | 时区 |
| lang | 字符串 | 选填 | 当前系统语言 |
| isdebug | 整型 | 选填 | 是否是测试流量，isdebug=1表明是测试流量 |


消息回复格式
===

外层对象
---

| **字段名** | **类型** | **是否一定有值** | **字段含义** |
|:--:|:--:|:--:|:--:|
| ad_list | 数组 | 是 | 广告对象数组 |
| err_msg | 字符串 | 是 | 出错原因，如果没有错误，返回ok |
| err_no | 整数 | 是 | 错误号，如果没有错误。返回0, 返回1表示没有合适广告。其他值参考err_msg |

广告对象
---

| **字段名** | **类型** | **是否一定有值** | **字段含义** |
|:--:|:--:|:--:|:--:|
| landing_type | int | 是 | 0：应用下载 1：外开落地页 2：内开落地页 3：订阅 4：DeepLink (从yeahmobi离线api拉的都是0或者1，默认是0)|
| clk_url | 字符串 | 是 | 点击链接 |
| imp_tks | 字符串数组 | 否 | 曝光监测链接(自己的监测) |
| clk_tks | 字符串数组 | 否 | 点击监测链接(自己的监测) |
| icon | string | 是 | 广告图标 |
| title | string | 是 | 广告标题 |
| image | string | 是 | 广告图片 |
| desc | string | 是 | 广告说明，大段文字 |


API请求示例
---
`http://api.cloudmobi.net:30001/api/v1/realtime/get?os=iOS&token=44&osv=0.1&dt=phone&nt=wifi&pn=wifi&sv=sv&clip=52.77.232.84&isdebug=1`


JSON回复示例
---
```
{
    "err_msg": "ok",
    "err_no": 0,
    "ad_list": [
        {
            "landing_type": 0,
            "clk_url": "https://github.com",
            "imp_tks": ["imp_tk1", "imp_tk2"],
            "clk_tks": ["clk_tk1", "clk_tk2"],
            "icon": "http://www.matatransit.com/uploadedImages/Main_Site/Content/Maps_and_Schedules/Route_Schedules/route%2042%20icon.jpg",
            "title": "Answer",
            "image": "http://steven-universe.wikia.com/wiki/File:42_Answer.jpg",
            "desc": "Welcome to use http://www.cloudmobi.net",
            "button": "Install"
        }
    ]
}
```
