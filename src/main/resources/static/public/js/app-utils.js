// Axios封装
// ========== 私有的 Loading 管理器 ==========
let loadingInstance = null;
let requestCount = 0;
function showLoading() {
    if (requestCount === 0) {
        loadingInstance = ELEMENT.Loading.service({
            lock: true,
            text: '加载中...',
            background: 'rgba(255, 255, 255, 0.7)'
        });
    }
    requestCount++;
}
function hideLoading() {
    requestCount--;
    if (requestCount <= 0) {
        requestCount = 0;
        if (loadingInstance) {
            loadingInstance.close();
            loadingInstance = null;
        }
    }
}
// ========== 创建共享的 Axios 实例 ==========
let service = axios.create({
    baseURL: '',
    timeout: 15000
});
service.interceptors.request.use(function (config) {
    if (!config.silent) showLoading();
    return config;
}, function (error) {
    hideLoading();
    return Promise.reject(error);
});
service.interceptors.response.use(function (response) {
    hideLoading();
    return response;
}, function (error) {
    hideLoading();
    var msg = (error.response && error.response.data && error.response.data.msg)
        ? error.response.data.msg
        : '网络异常，请稍后重试';
    ELEMENT.Message.error(msg);
    return Promise.reject(error);
});

// 全局通用工具混入
const AppUtilsMixin = {
    data:{
        // 默认头像
        defaultAvatar: "/public/pic/tx.png",
        // 文件地址前缀
        fileUrlPrefix: "/file/p/",
        // 文章地址前缀
        articleUrlPrefix: "/article/",
    },
    created() {
    },
    mounted() {
    },
    methods: {

        // 获取头像背景色
        getCommentUserAvatarColor(comment) {
            if (!comment || !comment.id) return;
            const name = comment.userName;
            let hash = 0;
            for (let i = 0; i < name.length; i++) {
                hash = name.charCodeAt(i) + ((hash << 5) - hash);
            }
            let color = "#";
            for (let i = 0; i < 3; i++) {
                const value = (hash >> (i * 8)) & 0xff;
                color += (`00${value.toString(16)}`).substr(-2);
            }
            comment.userAvatarColor = color;
            return color;
        },
        // 判空
        isBlank(val) {
            return val === null || val === undefined || String(val).trim().length === 0;
        },
        // element 弹窗
        successMsg(message, duration) {
            ELEMENT.Message({message: message, type: 'success', duration: duration || 2000});
        },
        errorMsg(message, duration) {
            ELEMENT.Message({message: message, type: 'error', duration: duration || 3000});
        },
        warnMsg(message, duration) {
            ELEMENT.Message({message: message, type: 'warning', duration: duration || 2500});
        },
        infoMsg(message, duration) {
            ELEMENT.Message({message: message, type: 'info', duration: duration || 2000});
        },
        // 通用请求方法
        $request: function (config) {
            return service(config);
        },
        $get: function (url, params, options) {
            return service.get(url, Object.assign({ params: params }, options));
        },
        $post: function (url, data, options) {
            return service.post(url, data, options);
        }
    }
};