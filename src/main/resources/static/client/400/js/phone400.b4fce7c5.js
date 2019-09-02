(window["webpackJsonp"] = window["webpackJsonp"] || []).push([
    ["phone400"], {
        "5d86": function(e, n, a) {
            "use strict";
            a.r(n);
            var t = function() {
                    var e = this,
                        n = e.$createElement,
                        a = e._self._c || n;
                    return a("el-container", [a("el-header", {
                        staticClass: "main-header",
                        attrs: {
                            height: "65px"
                        }
                    }, [a("TopNav", {
                        attrs: {
                            menu: e.menu
                        }
                    })], 1), a("el-main", [a("router-view")], 1)], 1)
                },
                r = [],
                c = a("cebc"),
                s = a("5200"),
                o = a("2f62"),
                i = {
                    components: {
                        TopNav: s["a"]
                    },
                    computed: Object(c["a"])({}, Object(o["b"])("user", ["privileges"]), {
                        menu: function() {
                            var e = [{
                                name: "业务介绍",
                                src: "/phone400-introduce",
                                hide: !0
                            }, {
                                name: "号码选择",
                                src: "/phone400-select"
                            }, {
                                name: "订单管理",
                                src: "/phone400-order-manage"
                            }, {
                                name: "数据统计",
                                src: "/phone400-statistics",
                                hide: !0
                            }, {
                                name: "价格管理",
                                src: "/phone400-price-manage",
                                hide: !0
                            }];
                            return e
                        }
                    })
                },
                p = i,
                u = a("2877"),
                h = Object(u["a"])(p, t, r, !1, null, "04d52060", null);
            n["default"] = h.exports
        }
    }
]);
//# sourceMappingURL=phone400.b4fce7c5.js.map
