##项目介绍：
    该项目是一款针对移动、电信、联通号码设置业务的运营性网站，其中包含三网平台（移动、联通、电信）、科大平台（科大网站、科大公众号）、号码认证、400、视频制作、
    号卡、微信小程序、铃音录制、企业秀、来去电名片、工具箱（包含铃音内容、背景音乐库、图片转文字、背景音乐合成、视频制作、铃音转换）

##项目架构介绍：
本项目采用Spring Boot作为核心架构，数据库类型采用MySQL，数据访问采用Mybatis框架，安全框架采用shiro，并辅以其他第三方框架

1. Spring Boot ----------  核心架构
2. Mybatis     ----------  数据访问
3. shiro       ----------  安全框架
4. Quartz      ----------  定时器框架(不使用，改用springboot自带的定时器)
5. Reactor     ----------  异步传递消息（本项目整合了Springboot自带的异步任务以及Springboot整合Reactor异步框架两种方式，操作日志采用springboot自带的异步，
具体业务采用第二种）
(Reactor是一个轻量级的JVM基础库，它可以帮助我们构建的服务和应用高效而异步的传递消息。)
6. Thymeleaf   ----------  模板引擎
7. Logback     ----------  日志
(每一天生成info等级以及error等级日志文件)

##项目路径详解：
### 公共文件
* doc ----------  项目介绍以及需求
* sql ----------  SQL文件
### 后台源码
* src/main/java   ----------  存放项目源码
    * com.hrtxn.ringtone.common    ----------  自定义约束、异常全局配置、过滤器、自定义封装类
        * constant ---------- 常量类
        * exception ---------- 全局异常
        * support ---------- 支持类
        * utils --------- 工具类
    * com.hrtxn.ringtone.project  ----------  存放本项目所需业务代码，包含控制器、业务层以及数据访问层
        * system    -----------  项目管理平台源码以及一些系统操作
            * telcertification  ----------  用户端号码认证模块
            * threenets ----------  用户端三网整合模块
                * kedas ----------  用户端三网整合模块科大子模块
                    * kedapublic ----------  科大公众号
                    * kedasites  ----------  科大网站
                * threenet ----------  用户端三网整合模块三网整合子模块
    * com.hrtxn.ringtone.freemark  ----------  项目架构配置
        * config ------------ 具体项目配置文件
        * enums ------------ 枚举文件
### mapper.xml

    src.main.resources.mybatis.mapper

### 页面源码
#### 静态资源
* src/main/resources/static ----------  存放所有静态资源
    * admin ----------  管理平台所需css/js/images静态资源
    * client ----------  用户端所需css/js/images静态资源
    * public ----------  公共css/js/images静态资源
#### 页面
* src/main/resources/templates  ----------- 存放所有页面资源
    * admin ---------- 管理端页面
    * system ---------- 用户端公共页面
    * threenets ---------- 三网
        * threenet ---------- 三网页面
        * kedas ---------- 科大
            * kedapublic ---------- 科大公众号
            * kedasites  ----------  科大网站
    * error ----------  错误页面（400/404/500）