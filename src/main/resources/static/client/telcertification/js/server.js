(function (window) {
	var server = {};

	//server.host = "http://192.168.1.100:8088";//朱平
	//server.host = "http://192.168.1.108:8081"; //王乐义
	server.host = "http://v.130.xyz"; //线上

	server.success = 0;

	server.url = {
		getEnterprise: server.host + '/api/common/getEnterprise', //获取用户信息
		loginEnterprise: server.host + '/api/common/loginEnterprise', //登录
		resetChannelPwd: server.host + '/api/common/resetChannelPwd', //渠道重置密码
		updateEnterprise: server.host + '/api/common/updateEnterprise', //修改企业信息，禁用账户
		updatePwd: server.host + '/api/common/updatePwd', //	修改密码
		upload: server.host + '/api/common/upload', //上传图片
		uploadNoUUID: server.host + '/api/common/uploadNoUUID', //上传图片2
		uploadAddNumber: server.host + '/api/common/uploadAddNumber', //上传图片2
		recharge: server.host + '/api/product/recharge', //支付调起-充值
		rechargeRecord: server.host + '/api/product/rechargeRecord', //充值记录
		activityList: server.host + '/api/view/activityList', //公告
		carousel: server.host + '/api/view/carousel', //首页大图
		channelInfo: server.host + '/api/view/channelInfo', //渠道详细
		channelList: server.host + '/api/view/channelList', //渠道管理
		selectQuD: server.host + '/api/view/selectQuD', //渠道管理-渠道数目
		findProductInfo: server.host + '/api/view/findProductInfo', //产品详情
		findProductList: server.host + '/api/view/findProductList', //首页产品列表
		newChannel: server.host + '/api/view/newChannel', //新建渠道
		rechargeList: server.host + '/api/view/recharge', //充值面额列表
		productList: server.host + '/api/product/proList', //产品列表
		proIntro: server.host + '/api/product/proIntro', //版本购买列表
		editproductPrice: server.host + '/api/product/addPrice', //修改列表
		productDetil: server.host + '/api/product/productDetil', //购买详情
		makeOrder: server.host + '/api/order/makeOrder', //立即支付
		checkOrder: server.host + '/api/product/checkOrder', //查看微信订单状态
		orderList: server.host + '/api/order/orderList', //订单列表
		appeal: server.host + '/api/order/appeal', //订单申诉
		orderDetil: server.host + '/api/order/orderDetil', //订单详情
		makePro: server.host + '/api/order/makePro', //代制作
		editOrderPhone: server.host + '/api/order/editOrderPhone', //代制作
		choseYear: server.host + '/api/order/choseYear', //订单续费，选择年份
		renew: server.host + '/api/order/renew', //续费
		nameIsUser: server.host + '/api/order/nameIsUser', //判断用户名是不是已经被使用过
		phoneIsUser: server.host + '/api/order/phoneIsUser', //判断用户手机号是不是已经被使用过
		show: server.host + '/api/report/show', //报表
		showLand: server.host + '/api/report/showLand', //报表
		reportExportExcel: server.host + '/api/report/reportExportExcel', //报表列表导出
		AllReport: server.host + '/api/report/AllReport', //报表echats
		AllReportLand: server.host + '/api/report/AllReportLand', //网通报表echats
		editRights: server.host + '/api/view/editRights', //修改权限
		revenue: server.host + '/api/report/revenue', //收益展示
		renuveReportLeiJi: server.host + '/api/report/renuveReportLeiJi', //累计收益详情
		renuveReportDy: server.host + '/api/report/renuveReportDy', //当月收益详情
		renuveReportDr: server.host + '/api/report/renuveReportDr', //当日收益详情
		revenueMoney: server.host + '/api/report/revenueMoney', //收益money
		saveRevenueMoney: server.host + '/api/report/saveRevenueMoney', //收益转存
		revenueList: server.host + '/api/report/revenueList', //收益列表
		revenueExportExcel: server.host + '/api/report/revenueExportExcel', //收益列表导出
		reportList: server.host + '/api/report/reportList', //报表统计列表
		xiaoCodeCase: server.host + '/api/product/xiaoCodeCase', //微信小程序案例二维码
		orderRecord: server.host + '/api/order/orderRecord', //订单记录
		orderAppealRecord: server.host + '/api/order/orderAppealRecord', //订单申诉记录
		couponList: server.host + '/api/coupon/couponList', //首页展示优惠券
		getCoupon: server.host + '/api/coupon/getCoupon', //领取优惠券
		myCoupon: server.host + '/api/coupon/myCoupon', //我的优惠券（0未使用1已使用2已过期）
		useCoupon: server.host + '/api/coupon/useCoupon', //使用优惠券
		BatchUpload: server.host + '/api/product/BatchUpload', //批量修改价格
		BuyLandShow: server.host + '/api/land/BuyLandShow', //网通购买列表
		selectDetilByLandId: server.host + '/api/land/selectDetilByLandId', //网通购买详情
		selectLandList: server.host + '/api/land/selectLandList', //网通价格管理
		addRangMoneyOne: server.host + '/api/land/addRangMoneyOne', //网通价格管理-单个加价
		addRangMoneyAll: server.host + '/api/land/addRangMoneyAll', //网通价格管理-多个加价
		makeLandOrder: server.host + '/api/land/makeLandOrder', //网通立即购买
		selectLandOrderList: server.host + '/api/land/selectLandOrderList', //网通订单管理
		lookLandOrderById: server.host + '/api/land/lookLandOrderById', //网通订单管理
		selectFileReason: server.host + '/api/land/selectFileReason', //查询审核失败原因
		selectTelList: server.host + '/api/land/selectTelList', //查询成员管理
		addLandAppeal: server.host + '/api/land/addLandAppeal', //查询申诉
		insertOpenTel: server.host + '/api/land/insertOpenTel', //添加号码
		selectChannlName: server.host + '/api/land/selectChannlName', //查询订单所对应的公司名称
		// 号码认证
		selectAuthList: server.host + '/api/auth/selectAuthList', //查询号码认证产品集合
		selectAuthDetilByItemId: server.host + '/api/auth/selectAuthDetilByItemId', //根据产品编号 查询产品信息
		addMoneyOneAuthItem: server.host + '/api/auth/addMoneyOneAuthItem', //为单个认证产品进行加价设置
		addMoneyAllAuthItem: server.host + '/api/auth/addMoneyAllAuthItem', //批量进行号码认证产品加价
		buyAuthTelList: server.host + '/api/auth/buyAuthTelList', //号码认证产品购买   产品展示
		makeAuthOrder: server.host + '/api/auth/makeAuthOrder', //生成号码认证订单
		uploadModel: server.host + '/api/auth/uploadModel', //下载受理单模板
		authAgreement: server.host + '/api/auth/authAgreement', //获取订购协议
		selectAuthOrderList: server.host + '/api/auth/selectAuthOrderList', //号码认证订单
		updateAuthContent: server.host + '/api/auth/updateAuthContent', //订单修改 号码认证内容
		selectAuthTelByOrderNo: server.host + '/api/auth/selectAuthTelByOrderNo', //根据订单编号查询该条订单的信息
		choseRenewValidity: server.host + '/api/auth/choseRenewValidity', //选择续费期限集合
		authRenew: server.host + '/api/auth/authRenew', //续费接口
		insertAuthTel: server.host + '/api/auth/insertAuthTel', //新增号码认证成员
		authOrderAppeal: server.host + '/api/auth/authOrderAppeal', //号码认证申诉
		selectAuthOrder: server.host + '/api/auth/selectAuthOrder', //根据订单编号 查询订单详情
		nuselectFileReason: server.host + '/api/auth/selectFileReason', //当后台审核失败时查询失败原因
		selectAuthAppealList: server.host + '/api/auth/selectAuthAppealList', //根据订单编号  查询该订单下的所有申诉信息
		selectCouponDetilById: server.host + '/api/auth/selectCouponDetilById', //根据折扣券编号查询折扣券的价格
		showAuth: server.host + '/api/report/showAuth', //号码认证
		AllReportAuth: server.host + '/api/report/AllReportAuth', //查询号码认证全年报表
		selectAuthRecordeByOrderNoAndTel: server.host + '/api/auth/selectAuthRecordeByOrderNoAndTel', //根据订单号 以及 认证号码查询 该条条号码的开通记录集合
		selectAuthItemList: server.host + '/api/auth/selectAuthItemList', //查询所有版本名称
		selectMarkByNumberSouGaou: server.host + '/api/auth/selectMarkByNumberSouGaou', //查询搜狗
		selectMarkByNumberSouGaouHaoMaTong: server.host + '/api/auth/selectMarkByNumberSouGaouHaoMaTong', //查询搜狗号码通
		selectMarkByNumber360w: server.host + '/api/auth/selectMarkByNumber360w', //查询360卫士
		selectMarkByNumber360: server.host + '/api/auth/selectMarkByNumber360', //查询360
		selectMarkByNumberLieB: server.host + '/api/auth/selectMarkByNumberLieB', //查询猎豹
		selectMarkByNumberBaiDuw: server.host + '/api/auth/selectMarkByNumberBaiDuw', //查询百度卫士
		selectMarkByNumberBaidu: server.host + '/api/auth/selectMarkByNumberBaidu', //查询百度
		selectMarkByNumber114: server.host + '/api/auth/selectMarkByNumber114', //查询114
		SearchPay: server.host + '/api/auth/SearchPay', //查询该号码是否支付过
		paySearchMoney: server.host + '/api/auth/paySearchMoney', //查询该号码是否支付过
		// 电信彩铃
		BuyTelecomShow: server.host + '/api/telecom/BuyTelecomShow', //电信购买展示
		selectDetilByTelecomId: server.host + '/api/telecom/selectDetilByTelecomId', //根据电信产品编号查询
		makeTelecomOrder: server.host + '/api/telecom/makeTelecomOrder', //电信下单
		selectTelecomOrderList: server.host + '/api/telecom/selectTelecomOrderList', //查看订单管理
		teaddRangMoneyOne: server.host + '/api/telecom/addRangMoneyOne', //为单个电信产品进行加价设置
		teaddRangMoneyAll: server.host + '/api/telecom/addRangMoneyAll', //批量进行电信产品加价管理
		selectTelecomList: server.host + '/api/telecom/selectTelecomList', //查询电信彩铃集合
		lookTelecomOrderById: server.host + '/api/telecom/lookTelecomOrderById', //查看订单详情 根据编号
		teselectFileReason: server.host + '/api/telecom/selectFileReason', //查看订单详情 根据编号
		addTelecomAppeal: server.host + '/api/telecom/addTelecomAppeal', //添加申诉信息
		teselectChannlName: server.host + '/api/telecom/selectChannlName', //根据订单编号 查询该条订单的用户信息
		teselectTelList: server.host + '/api/telecom/selectTelList', //根据订单编号  查询该条订单下的所有开通手机号
		teinsertOpenTel: server.host + '/api/telecom/insertOpenTel', //订单新加手机信息
		showTelecom: server.host + '/api/report/showTelecom',
		AllReportTelecom: server.host + '/api/report/AllReportTelecom',
		filesdownTelModel: server.host + '/api/common/filesdownTelModel', //协议单下载
		//视频制作、
		selectVedioList: server.host + '/api/vedio/selectVedioList', //查询所有的视频 根据用户等级对应的价格 以及本身的加价
		selectVedioList1: server.host + '/api/vedio/selectVedioList1', //查询所有的视频 根据用户等级对应的价格 以及本身的加价
		addRangMoneyOneVedio: server.host + '/api/vedio/addRangMoneyOneVedio', //视频价格 单个加价
		addRangMoneyAllVedio: server.host + '/api/vedio/addRangMoneyAllVedio', //视频价格 多个批量加价
		selectVedioDetilByItemId: server.host + '/api/vedio/selectVedioDetilByItemId', //根据产品编号查询该条产品的详细信息
		makeVedioOrder: server.host + '/api/vedio/makeVedioOrder', //生成视频订单信息
		addVedioAppeal: server.host + '/api/vedio/addVedioAppeal', //添加申诉信息
		selectVedioOrderList: server.host + '/api/vedio/selectVedioOrderList', //查询用户的视频订单
		AllReportVedio: server.host + '/api/report/AllReportVedio', //查询视频全年报表
		showVedio: server.host + '/api/report/showVedio', //视频
		selectIsOverTime: server.host + '/api/vedio/selectIsOverTime', //根据订单编号查询该条订单是否在可修改范围时间内 48小时 以内可以修改
		selectVedioDetil: server.host + '/api/vedio/selectVedioDetil', //根据订单编号查询 该条订单的详细信息 用于修改回显
		updateOrderMsg: server.host + '/api/vedio/updateOrderMsg', //修改订单信息详情
		aeCodeCase: server.host + '/api/product/aeCodeCase', //视频二维码
		selectVedioTypeList: server.host + '/api/vedio/selectVedioTypeList', //分类列表
	};
	//退出登录
	server.quit = function () {
		layui.use('layer', function () {
			layer.confirm(
				'<p style="text-align:left;padding:0 50px 30px 0;font-size:20px">确定要退出系统吗？</p>', {
					icon: 5,
					title: '退出登录',
					btnAlign: "c",
				},
				function (index) {
					sessionStorage.removeItem("$id");
					sessionStorage.removeItem("$userIdentity");
					sessionStorage.removeItem("$isDevelop");
					sessionStorage.removeItem('$rights'); //权限
					setTimeout(function () {
						window.location.href = "index.html";
					}, 500)
					layer.close(index);
				});
		});
	}
	server.isLogin = function () {
		if (sessionStorage.getItem('$id') == '' || sessionStorage.getItem('$id') == null) {
			window.location.href = "index.html";
			return;
		}
	}
	server.isContact = function () {
		layui.use('layer', function () {
			layer.msg("暂未开发此功能，敬请期待！");
		})
	}
	window.server = server;

})(window);