/**
 * Created by tgwang on 2016/11/29.
 */

; var messengerClient = (function (global) {
    var version = "1.0.0";

    function Queue() {
        var items = [];
        this.enqueue = function (element) {
            items.push(element);
        };
        this.dequeue = function () {
            return items.shift();
        };
        this.front = function () {
            return items.length == 0 ? null : items[0];
        };
        this.behind = function () {
            return items.length == 0 ? null : items[items.length - 1];
        };
        this.isEmpty = function () {
            return items.length == 0;
        };
        this.clear = function () {
            items = [];
        };
        this.size = function () {
            return items.length;
        };
        this.print = function () {
            console.log(items.toString());
        };
    }

    var _callbackId = Math.floor(Math.random() * 2000000000);

    /**
     * 获取回调ID
     * @returns {number}
     */
    function getCallbackId() {
        return _callbackId++;
    }

    var pageReady = false;  // 页面是否初始化完成
    var procHandler = null; // 处理proc的handler
    var procStatus = '';
    var procStatusFlag = {
        END: '0',
        LOADING: '1',
        INIT: '2'
    };
    var miguModalOpenId = '__migu-modal-open__';

    var queue = new Queue();

    global.messenger = new Messenger('parent', 'shring');
    global.messenger.listen(function (msg) {
        var msgObj = JSON.parse(msg);

        // 如果初始化完成
        if (msgObj.cmd == 'init' && msgObj.ret == true) {
            debugLog("event -> init");
            pageReady = true;
            clearTimeout(procHandler);
            procHandler = setTimeout(proc, 0);// 初始化完成之后进行消息处理
            return;
            return;
        }
        debugLog("event -> callback");

        var cbObj = null;
        try {
            if (queue.size() > 0) {
                for (var i = 0; i < queue.size() ; i++) {
                    cbObj = queue.front();

                    if (cbObj.callbackId != msgObj.callbackId || cbObj.cmd != msgObj.cmd) {
                        cbObj = null;
                        queue.dequeue();
                        continue;
                    }

                    break;
                }

                if (!cbObj) {
                    return;
                }

                // 删除key cmd， 不暴露在最外层
                delete msgObj['cmd'];
                delete msgObj['callbackId'];

                // 删除生成的iframe
                var _iframe = document.getElementById(miguModalOpenId);
                _iframe && _iframe.parentNode.removeChild(_iframe);

                if (cbObj.doneCallback) {
                    cbObj.doneCallback(msgObj);
                    return;
                }

                if (cbObj.failCallback) {
                    cbObj.failCallback(msgObj);
                    return;
                }
            }
        } finally {
            if (cbObj) {
                queue.dequeue();
            }

            procStatus = procStatusFlag.END;
            // 进入下一个处理循环
            clearTimeout(procHandler);
            procHandler = setTimeout(proc, 0);
        }
    });

    /**
     * 创建回调对象
     * @param cmd
     * @param args
     * @param done
     * @param fail
     * @returns {{callbackId: number, cmd: string, args: *, doneCallback: *, failCallback: *}}
     */
    function createCallbackObj(cmd, args, done, fail) {
        return {
            callbackId: getCallbackId(),
            cmd: cmd,
            args: args,
            doneCallback: done,
            failCallback: fail
        };
    }

    /**
     * 执行命令
     * @param cmd
     * @param args
     * @param done
     * @param fail
     */
    function exec(cbObj) {
        global.messenger.targets['MiguRing'].send(JSON.stringify(cbObj));
    }

    function proc() {
        // 如果已经在记载中状态，不再执行proc方法
        if (procStatus == procStatusFlag.LOADING) {
            return;
        }

        if (pageReady && queue.size() > 0) {
            procStatus = procStatusFlag.LOADING;
            var cbObj = queue.front();

            exec(cbObj);
            return;
        }

        clearTimeout(procHandler);
        procHandler = setTimeout(proc, 500);
    }

    /**
     * 显示在某个容器中
     * @param parentId
     * @param iframeUrl
     */
    function init(parentId, iframeUrl) {
        debugLog("call -> init");
        pageReady = false;
        procStatus = procStatusFlag.INIT;

        var iframe = document.createElement("iframe");
        iframe.src = iframeUrl;
        iframe.style.width = '100%';
        iframe.style.height = '100%';
        iframe.style.border = 'none';
        iframe.id = miguModalOpenId;

        document.getElementById(parentId).appendChild(iframe);
        global.messenger.addTarget(iframe.contentWindow, 'MiguRing');
    }

    function openRing(args, done, fail) {
        debugLog("call -> openRing");
        // 生成回调对象，并入队
        var cbObj = createCallbackObj("openRing", args, done, fail);
        queue.enqueue(cbObj);
        // 此处值加入到队列，不处理消息
    }

    function openBiz(args, done, fail) {
        debugLog("call -> openBiz");
        // 生成回调对象，并入队
        var cbObj = createCallbackObj("openBiz", args, done, fail);
        queue.enqueue(cbObj);
    }

    function debugLog(logText) {
        try {
            if (window["console"]) {
                console.log(logText);
            }
        } catch (e) {
        }
    }

    return {
        init: init,
        openRing: openRing,
        openBiz: openBiz
    }
})(window);