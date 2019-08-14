$(function () {
	$.MsgBox = {
		diyload: function () {
			var html = "<div id='loading'>";
			html += "<img id='loadimg' src='../img/fine.gif'/>";
			html += "</div>";
			html += "<script type='text/javascript'>";
			// html += "var left=(document.documentElement.clientWidth-198)/2;";
			// html += "var top1=(document.documentElement.clientHeight-92)/2;";
			// html += "$('#loadimg').css({'top':top1,'left':left,'position':'absolute'})";
			html += "</script>";
			$("body").append(html);
		},
		Alter: function (title, msg) {
			this.createHtml(title, msg, "alter", "关闭", "");
			this.btn1fun("alter");
			this.btnImgfun("alter");
			$("#msgboxalter").css("zIndex", "999999");
		},
		Altersure: function (title, msg, callback) {
			this.createHtml(title, msg, "altersure", "关闭", "");
			this.btn1fun("altersure", callback);
			this.btnImgfun("altersure");
		},
		Altersureok: function (title, msg, callback) {
			this.createHtml(title, msg, "altersure", "", "确定");
			this.btn2fun("altersure", callback);
			this.btnImgfun("altersure");
		},
		Confirm: function (title, msg, callback) {
			this.createHtml(title, msg, "confirm", "取消", "确认");
			this.btn1fun("confirm", callback);
			this.btn2fun("confirm", callback);
			this.btnImgfun("confirm");
		},
		Diyconfirm: function (title, msg, btn1, btn2, callback) {
			this.createHtml(title, msg, "diyconfirm", btn1, btn2);
			this.btn1fun("diyconfirm", callback);
			this.btn2fun("diyconfirm", callback);
			this.btnImgfun("diyconfirm");
		},
		Load: function () {
			this.diyload();
		},
		RemoveHtml: function (id) {
			$("#loading").remove();
		},
		linkTo: function (url) {
			this.diyload();
			location.href = url;
		},
		createHtml: function (title, msg, id, btn1, btn2) {
			var html = "<div id=\"msgbox" + id + "\" class=\"popAlert\">" +
				"<div id=\"msgcontent" + id + "\" class=\"popcontent\">";
			if (id != "load") {
				html = html + "<div class=\"popcontentmsg\">" +
					"<img src=\"static/channelthree/images/detaily-close.png\" alt=\"\">" +
					"</div>";
			}
			html = html + "<p id=\"msgtext" + id + "\">" + msg + "</p>" +
				"<div class=\"clearfix\">";
			if (btn1 != "") {
				if (btn2 == "") {
					html = html + "<span id=\"msgbtn1" + id + "\" style=\"float: none;margin-left: auto;\">" + btn1 + "</span>";
				} else {
					html = html + "<span id=\"msgbtn1" + id + "\">" + btn1 + "</span>";
				}
			}
			if (btn2 != "") {
				if (btn1 == "") {
					html = html + "<a id=\"msgbtn2" + id + "\" style=\"float: none;margin-left: auto;\" href=\"javascript:void(0);\" >" + btn2 + "</a>";;
				} else {
					html = html + "<a id=\"msgbtn2" + id + "\" href=\"javascript:void(0);\" >" + btn2 + "</a>";
				}


			}
			html = html + "</div>" +
				"</div>" +
				"</div>";
			$(html).appendTo("body");
			$("#msgbox" + id).show();
			if ($("#msgtextalter").length > 0) {
				$("#msgtextalter").css({
					"paddingTop": "22px",
					"lineHeight": "44px"
				});
			}
			var left = (document.documentElement.clientWidth - $("#msgcontent" + id).get(0).offsetWidth) / 2;
			var top = (document.documentElement.clientHeight - $("#msgcontent" + id).get(0).offsetHeight) / 2;
			$("#msgcontent" + id).css({
				"left": left + "px",
				"top": top + "px"
			});
			if (id == "altersure") {
				$(".popcontentmsg img").remove();
			}

		},
		btn1fun: function (id, callback) {
			$("#msgbtn1" + id).click(function () {
				if (typeof (callback) == 'function') {
					$ss = $("#msgbtn2" + id);
					if ($ss.length > 0) {
						callback(false);
					} else {
						callback(true);
					}
				}
				$("#msgbox" + id).remove();
			});
		},
		btn2fun: function (id, callback) {
			$("#msgbtn2" + id).click(function () {
				if (typeof (callback) == 'function') {
					callback(true);
				}
				$("#msgbox" + id).remove();
			});
		},
		btnImgfun: function (id, callback) {
			$("#msgcontent" + id + " .popcontentmsg img").click(function () {
				$("#msgbox" + id).remove();
				if (typeof (callback) == 'function') {
					callback(false);
				}
			});
		}
	};
});