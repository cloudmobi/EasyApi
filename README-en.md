CloudTech Easy API
===

Instruction
---

The real-time return ads provide APIs of Server2Server and Client2Server. They have same return format, but different parameters.

Server to server API
---

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

Client to server API
---
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
| cn | string | optional | Carrier name |
| la | float | optional | latitude |
| lo | float | optional | longitude |
| tz | string | optional | time zone |
| lang | string | optional | language of mobile system |
| isdebug | int | optional | if it is debug data flow, set isdebug=1 |

Response
===

Outer Object
---

| name | type | required | desc |
|:--:|:--:|:--:|:--:|
| ad_list | array of Ad Object | required | Array of Ad Object |
| err_msg | string | required | error message. if there's no error, back OK |
| err_no | int | required | error number,0 means success, 1 means no matched ads.Other values refer to err_msg |

Ad Object
---

| name | type | required | desc |
|:--:|:--:|:--:|:--:|
| landing_type | int | required | 0:App Download, 1:Using default browser to load landing page, 2:you can use web-view to load landing page |
| clk_url | string | required | click link |
| imp_tks | array of string | optional | third party impression monitor link(when the ads impressed, if this array is not empty, the caller needs to use asynchronous call to call the link |
| clk_tks | array of string | optional | third party click monitor link(when the ads clicked, if this array is not empty, the caller needs to use asynchronous call to call the link |
| icon | string | required | icon |
| icon_size | string | required | size of icon, format: "${width}x${height}", e.g.: "1x1" |
| title | string | required | title of the ad (app name) |
| pkg_name | string | required | package name |
| image | string | required | ad image |
| image_size | string | required | size of the ad materials, format: "${width}x${height}", eg: "300x250" |
| desc | string | required | ad description |


API Request Demo
---

* Server2Server API:

        http://api.cloudmobi.net:30001/api/v1/realtime/get?gaid=xxx&os=iOS&token=44&osv=0.1&dt=phone&nt=wifi&pn=wifiv&clip=52.77.232.84&isdebug=1

* Client2Server API:

        http://api.cloudmobi.net:30001/api/v1/realtime/m/get?gaid=xxx&os=iOS&token=44&osv=0.1&dt=phone&nt=wifi&pn=wifi&isdebug=1


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
FAQ
---

Q：Are impression and click still monitored if the arrays, `imp_tks`and`clk_tks`,are null ? 

A：For the impression monitor, to simplify your access work,when the real-time API requests ads in the backend server,the impression monitor will be called at the server-side. And we use redirection technology to implement the click monitor automatically. We keep these two arrays for possible third party monitoring.

