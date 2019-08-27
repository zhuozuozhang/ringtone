package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.controller;

import com.alibaba.fastjson.JSONObject;
import com.hrtxn.ringtone.common.utils.StringUtils;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.WxUtils.WxAuthUtil;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain.KedaPublicUser;
import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.service.KedaPublicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @Author:zcy
 * @Date:2019-08-26 10:11
 * @Description:<描述>
 */
@Controller
@RequestMapping("/public")
@Slf4j
public class KedaPublicController {

    @Autowired
    private KedaPublicService kedaPublicService;

    /**
     * @Author zcy
     * @Date 2019-8-26 13:26
     * @Description 公众号微信登录授权
     */
    @GetMapping("/index")
    public String wxLogin() throws ParseException {
        log.info("========================================开始登陆授权========================================");
        //这个url的域名必须要进行再公众号中进行注册验证，这个地址是成功后的回调地址
        // 第一步：用户同意授权，获取code
        log.info("第一步：用户同意授权，获取code====================");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxAuthUtil.APPID
                + "&redirect_uri=" + URLEncoder.encode(WxAuthUtil.INDEXBACKURL) + "&response_type=code" + "&scope=snsapi_userinfo" + "&state=STATE#wechat_redirect";
        log.info("====================forward重定向地址{" + url + "}====================");
        // 必须重定向，否则不能成功
        return "redirect:" + url;
    }

    /**
     * @Author zcy
     * @Date 2019-8-26 13:28
     * @Description 公众号微信登录授权回调函数
     */
    @GetMapping("/indexCallBack")
    public String callBack(HttpServletRequest req) throws IOException {
        // 从URL中获取code
        String code = req.getParameter("code");
        // 第二步：通过code换取网页授权access_token
        log.info("第二步：通过code换取网页授权access_token====================");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxAuthUtil.APPID + "&secret=" + WxAuthUtil.APPSECRET + "&code=" + code + "&grant_type=authorization_code";
        log.info("====================通过code获取access_token url{}====================", url);
        /**
         {  "access_token":"ACCESS_TOKEN",
         "expires_in":7200,
         "refresh_token":"REFRESH_TOKEN",
         "openid":"OPENID",
         "scope":"SCOPE"
         }
         */
        JSONObject jsonObject = WxAuthUtil.doGetJson(url);
        log.info("====================获取access_token结果{}====================", jsonObject);
        String openid = jsonObject.getString("openid");
        String access_token = jsonObject.getString("access_token");
        String refresh_token = jsonObject.getString("refresh_token");
        // 第五步验证access_token是否失效
        log.info("第五步验证access_token是否失效====================");
        String chickUrl = "https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid;
        log.info("====================验证access_token url{}====================", chickUrl);
        JSONObject chickuserInfo = WxAuthUtil.doGetJson(chickUrl);
        log.info("====================验证access_token结果{}====================", chickuserInfo);
        if (!"0".equals(chickuserInfo.getString("errcode"))) {
            // 第三步：刷新access_token（如果需要）-----暂时没有使用,参考文档https://mp.weixin.qq.com/wiki，
            log.info("第三步：刷新access_token（如果需要）====================");
            String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + openid + "&grant_type=refresh_token&refresh_token=" + refresh_token;
            log.info("====================刷新access_token url{}====================", refreshTokenUrl);
            JSONObject refreshInfo = WxAuthUtil.doGetJson(refreshTokenUrl);
            /*
             * { "access_token":"ACCESS_TOKEN",
                "expires_in":7200,
                "refresh_token":"REFRESH_TOKEN",
                "openid":"OPENID",
                "scope":"SCOPE" }
             */
            log.info("====================刷新access_token结果{}====================", refreshInfo);
            access_token = refreshInfo.getString("access_token");
        }
        // 第四步：拉取用户信息(需scope为 snsapi_userinfo)
        log.info("第四步：拉取用户信息(需scope为 snsapi_userinfo)====================");
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
        log.info("====================拉取用户信息 url{}====================", infoUrl);
        /*
         {
            "openid":" OPENID",
            "nickname": NICKNAME,
            "sex":"1",
            "province":"PROVINCE"
            "city":"CITY",
            "country":"COUNTRY",
            "headimgurl":"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
            "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
            "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
            }
         */
        JSONObject userInfo = WxAuthUtil.doGetJson(infoUrl);
        log.info("====================拉取用户信息结果{}====================", userInfo);
        String openid2 = userInfo.getString("openid");
        // 根据openid查询用户信息
        Integer id;
        KedaPublicUser _kedaPublicUser = kedaPublicService.selectByOpenId(openid2);
        if (StringUtils.isNull(_kedaPublicUser)) {
            String nickname = userInfo.getString("nickname");
            String sex = userInfo.getString("sex");
            String province = userInfo.getString("province");
            String city = userInfo.getString("city");
            String country = userInfo.getString("country");
            String headimgurl = userInfo.getString("headimgurl");
            log.info("====================执行添加用户信息====================");
            KedaPublicUser kedaPublicUser = new KedaPublicUser();
            kedaPublicUser.setOpenid(openid2);
            kedaPublicUser.setNickname(nickname);
            kedaPublicUser.setSex(sex);
            kedaPublicUser.setProvince(province);
            kedaPublicUser.setCity(city);
            kedaPublicUser.setCountry(country);
            kedaPublicUser.setHeadimgurl(headimgurl);
            kedaPublicUser.setCreateTime(new Date());
            int count = kedaPublicService.insert(kedaPublicUser);
            log.info("添加用户信息结果{}", count);
            id = kedaPublicUser.getId();
        } else {
            id = _kedaPublicUser.getId();
        }
        return "redirect:/public/index/" + id;
    }

    /**
     * @Author zcy
     * @Date 2019-8-26 17:31
     * @Description 跳转到公众号首页
     */
    @GetMapping("/index/{id}")
    public String toIndex(@PathVariable Integer id, ModelMap map) {
        map.put("id", id);
        return "threenets/kedas/kedapublic/self_help/index";
    }

}
