CloudTech Easy API
===

Instruction
---

The real-time return ads provide APIs of Server2Server and Client2Server. They have same return format, but different parameters.

API URI
---
`http://api.cloudmobi.net:30001/api/v1/realtime/get`

HTTP Method
---

Only GET supported

Parameters
---
| name | type | required | desc |
|:--:|:--:|:--:|:--:|
| token | string | required | identification of ad slot. slot_id is provided by cloudmobi. |
| os | string | required | Operation System, valid values: Android,iOS |
| osv | float | required | for Android: `Build.VERSION.SDK`, for iOS: `[[[UIDevice currentDevice] systemVersion] floatValue];` |
| dt | string | required | device type, valid values：phone,tablet,ipad,watch |
| nt | int | required | network type. for Android: `NetworkInfo.getType();` for iOS: `[[dataNetWorkItemView valueForKey:@"dataNetworkType"] integerValue]` |
| clip | string | required | client ip |
| imgw | int | required | width of the image(unit: pixel), the default value is width of slot |
| imgh | int | required | height of the image(unit: pixel),  the default value is height of slot |
| pn | string | required | package name |
| adnum | int | optional | the number of ads backed from ad_list, the default value is 1 |
| gaid | string | optional | Google Advertising Id(note:it's better to fill in,cause it has important influence on the conversion) |
| aid | string | optional | Android ID of mobile device(note:it's better to fill in,cause it has important influence on the conversion) |
| idfa | string | optional | IDFA of mobile device(note:it's better to fill in,cause it has important influence on the conversion) |
| imei | string | optional | IMEI of mobile device |
| ck_md5 | string | optional | the md5 value of cookie(note:At least one value of these five parameters(gaid, aid, idfa, imei, ck_md5) should be provided,or there comes no ads. |
| keywords | string | optional | search key-words|
| icc | string | optional | ISO country code(country code of SIM card). for Android: `TelephonyManager.getNetworkCountryIso()`  for iOS: `[[NSLocale  currentLocale] objectForKey:NSLocaleCountryCode];` |
| gp | int | optional | 1:google play is installed, 2: google play isn't installed |
| dpd | string | optional | Device product |
| cn | string | optional | Carrier name |
| la | float | optional | latitude |
| lo | float | optional | longitude |
| tz | string | optional | time zone |
| lang | string | optional | language of mobile system |
| isdebug | int | optional | if it is debug data flow, set isdebug=1. if not,set isdebug=0 |


Response
===

Outer Object
---

| name | type | required | desc |
|:--:|:--:|:--:|:--:|
| ad_list | array of Ad Object | required | Array of Ad Object |
| err_msg | string | required | error message |
| err_no | int | required | error number，0 means success, 1 means no matched ads |

Ad Object
---

| name | type | required | desc |
|:--:|:--:|:--:|:--:|
| landing_type | int | required | 0：App Download, 1：Using default browser to load landing page, 2：you can use web-view to load landing page, 3：RSS |
| clk_url | string | required | click link |
| imp_tks | array of string | optional | impression monitor |
| clk_tks | array of string | optional | click monitor |
| icon | string | required | icon |
| icon_size | string | required | size of icon, format: "${width}x${height}", eg: "1x1" |
| title | string | required | title(app name) |
| pkg_name | string | required | package name |
| image | string | required | image source of creative |
| image_size | string | required | size of creative picture, format: "${width}x${height}", eg: "300x250" |
| desc | string | required | ad description |


API Request Demo
---
`http://api.cloudmobi.net:30001/api/v1/realtime/get?os=iOS&token=44&osv=0.1&dt=phone&nt=wifi&pn=wifi&sv=sv&clip=52.77.232.84&isdebug=1`


JSON Response Demo
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
            "pkg_name": "net.cloudmobi.www",
            "icon": "http://www.matatransit.com/uploadedImages/Main_Site/Content/Maps_and_Schedules/Route_Schedules/route%2042%20icon.jpg",
            "icon_size": "1x1",
            "title": "Answer",
            "image": "http://steven-universe.wikia.com/wiki/File:42_Answer.jpg",
            "image_size": "300x250",
            "desc": "Welcome to use http://www.cloudmobi.net",
            "button": "Install"
        }
    ]
}
```
