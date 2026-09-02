const tagSelectsKey = "tagSelects";
const categorySelectsKey = "categorySelects";
const fileTypeSelectsKey = "fileTypeSelects";

// 后台管理全局变量
const AdminUtilsMixin = {
    mixins: [AppUtilsMixin],
    data: {
        // 分页查询条件
        pageQuery: {
            size: 10,
            page: 1,
            searchStr: "",
        },
        // 分页数据
        pageData:{
            current: 1,
            total: 0,
            size: 10,
            pages: 0,
            records:[]
        },
        multipleSelection: [], // 多选框
        categorySelects: [],// 分类下拉框
        tagSelects: [], // 标签下拉框
        fileTypeSelects: [], // 文件类型下拉框
        adminPath: '/admin', // 后端管理路径
        nowPath: '', // 当前页面路径
        fileUploadUrl: '',// 文件上传地址
        asideExpand: true, // 侧边栏展开
        loginUser:{},// 登录用户信息
        // 素材库分页条件
        filePageQuery:{
            visible: false,
            tab:'image',
            fileTypes:['image'],
            size: 10,
            page: 1,
            searchStr: "",
        },
        // 素材库分页数据
        filePageData: {
            current: 1,
            total: 0,
            size: 10,
            pages: 0,
            records:[]
        },
        // 用户详情弹窗
        userDialog: {
            visible: false,
            userId: null,
            userName: null,
            userCode: null,
            userQq: null,
            userEmail: null,
            userAvatar: null,
        },
        // 密码修改弹窗
        passwordDialog:{
            visible: false,
            userId: null,
            oldPassword: null,
            newPassword: null,
            confirmPassword: null
        },
        // 用户表单验证规则
        userRules: {
            userName: [
                {required: true, message: '请输入用户名', trigger: 'blur'},
                {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
            ],
            userCode: [
                {required: true, message: '请输入用户编号', trigger: 'blur'},
                {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
            ],
            userQq: [
                {min: 5, max: 11, message: '长度在 5 到 11 个字符', trigger: 'blur'}
            ],
            userEmail: [
                {required: true, message: '请输入用户邮箱', trigger: 'blur'},
                {min: 4, max: 100, message: '长度在 4 到 100 个字符', trigger: 'blur'},
                {pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱地址', trigger: 'blur'}
            ],
            userAvatar: [
                {max: 50, message: '长度最大为 50  个字符', trigger: 'blur'}
            ],
        },
    },
    computed: {
        // 密码修改表单验证规则 data中获取不到this
        passwordRules(){
            return {
                oldPassword: [
                    {required: true, message: '请输入原密码', trigger: 'blur'},
                    {
                        pattern: /^[A-Za-z0-9!@#$%^&*()_+\-=;'":|,.<>/?~]{6,20}$/,
                        message: '密码只能包含字母、数字和!@#$%^&*()_+-=;\'\":|,.<>/?~，长度6-20位', trigger: 'blur'
                    }
                ],
                newPassword: [
                    {required: true, message: '请输入新密码', trigger: 'blur'},
                    {
                        pattern: /^[A-Za-z0-9!@#$%^&*()_+\-=;'":|,.<>/?~]{6,20}$/,
                        message: '密码只能包含字母、数字和!@#$%^&*()_+-=;\'\":|,.<>/?~，长度6-20位', trigger: 'blur'
                    }
                ],
                confirmPassword: [
                    {required: true, message: '请输入确认密码', trigger: 'blur'},
                    {
                        pattern: /^[A-Za-z0-9!@#$%^&*()_+\-=;'":|,.<>/?~]{6,20}$/,
                        message: '密码只能包含字母、数字和!@#$%^&*()_+-=;\'\":|,.<>/?~，长度6-20位', trigger: 'blur'
                    },
                    {
                        validator: (rule, value, callback) => {
                            if (value !== this.passwordDialog.newPassword) {
                                callback(new Error('两次输入的密码不一致'));
                            } else {
                                callback();
                            }
                        }, trigger: 'blur'
                    }
                ],
            }
        },
    },
    created() {
        // 后端管理路径
        this.initAdminPath();
    },
    mounted() {
        this.getLoginUser();
        window.addEventListener('resize', this.asideListener)
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.asideListener)
    },

    methods: {

        // 头像上传检测
        beforeAvatarUpload(file){
            const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
            const isLt2M = file.size / 1024 / 1024 < 3;

            if (!isJPG) {
                this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
            }
            if (!isLt2M) {
                this.$message.error('上传图片大小不能超过 3MB!');
            }
            return isJPG && isLt2M;
        },
        // 头像上传成功
        handleAvatarSuccess(res, file){},
        // 打开素材库
        handleFileOpen() {
            this.filePageQuery.visible = true;
            this.handleFilePageGet();
        },
        // 文件分页
        handleFilePageGet(){
            this.$post(this.adminPath + "/file/page", this.filePageQuery).then(re => {
                if (re.data && re.data.code === 200) {
                    this.filePageData = re.data.data;
                } else {
                    this.errorMsg(re.data.msg);
                }
            });
        },
        // 素材tab切换
        fileTabClick() {
            this.filePageQuery.fileTypes = [];
            if ("image" === this.filePageQuery.tab) {
                // 固定图片类型
                this.filePageQuery.fileTypes.push("image");
            }
            this.handleFilePageGet();
        },
        // 插入文件 由使用对象重写
        insertFile(file){

        },
        // 插入头像
        insertUserAvatar(file){
            this.userDialog.userAvatar =  this.fileUrlPrefix + file.id;
            this.filePageQuery.visible = false;
        },
        // 页大小
        handleFileSizeChange(val) {
            this.filePageQuery.size = val;
            this.handleFilePageGet();
        },
        // 当前页
        handleFileCurrentChange(val) {
            this.filePageQuery.page = val;
            this.handleFilePageGet();
        },
        // 获取当前登录用户
        getLoginUser(){
            this.$get(this.adminPath + "/user").then(re => {
                if (re.data && re.data.code === 200) {
                    this.loginUser = re.data.data;
                    this.userDialog.userName = this.loginUser.userName;
                    this.userDialog.userCode = this.loginUser.userCode;
                    this.userDialog.userQq = this.loginUser.userQq;
                    this.userDialog.userEmail = this.loginUser.userEmail;
                    this.userDialog.userAvatar = this.loginUser.userAvatar;
                    this.passwordDialog.userId = this.loginUser.userId;
                }
            });
        },
        // 关闭密码修改表单
        closePasswordForm(){
            this.passwordDialog.visible = false;
            this.passwordDialog.oldPassword = null;
            this.passwordDialog.newPassword = null;
            this.passwordDialog.confirmPassword = null;
        },
        // 提交密码修改表单
        submitPasswordForm() {
            this.$refs.passwordForm.validate(valid => {
                if (valid) {
                    this.$post(this.adminPath + "/password", this.passwordDialog).then(re => {
                        if (re.data && re.data.code === 200) {
                            // 退出登录
                            window.location.href = this.adminPath + "/logout";
                        } else {
                            this.errorMsg(re.data.msg);
                        }
                    });
                }
            });
        },
        // 关闭用户修改表单
        closeUserForm(){
            this.userDialog.visible = false;
            this.userDialog.userName = this.loginUser.userName;
            this.userDialog.userCode = this.loginUser.userCode;
            this.userDialog.userQq = this.loginUser.userQq;
            this.userDialog.userEmail = this.loginUser.userEmail;
            this.userDialog.userAvatar = this.loginUser.userAvatar;
        },
        // 提交用户修改表单
        submitUserForm() {
            this.$refs.userForm.validate(valid => {
                if (valid) {
                    this.$post(this.adminPath + "/user", this.userDialog).then(re => {
                        if (re.data && re.data.code === 200) {
                            // 退出登录
                            window.location.href = this.adminPath + "/logout";
                        } else {
                            this.errorMsg(re.data.msg);
                        }
                    });
                }
            });
        },
        // 获取路径
        initAdminPath() {
            // 当前路径
            this.nowPath = window.location.pathname;
            // 截取前缀
            this.adminPath = this.nowPath.substring(0, this.nowPath.indexOf('/', 1));
            this.fileUploadUrl = this.adminPath + "/file/upload";
        },

        // 通用分页
        handlePageGet() {
            this.$post(this.nowPath + "/page", this.pageQuery).then(re => {
                if (re.data && re.data.code === 200) {
                    this.pageQuery.total = re.data.data.total;
                    this.pageQuery.page = re.data.data.current
                    this.pageData = re.data.data;
                } else {
                    this.errorMsg(re.data.msg);
                }
            }).catch(e => {
                this.errorMsg("获取分页失败:" + e.message);
            });
        },
        // 通用置顶
        handleTop(row) {
            this.$post(this.nowPath + "/top", {"id": row.id}).then(re => {
                if (re.data && re.data.code === 200) {
                    // 刷新
                    this.handlePageGet();
                    this.successMsg("置顶成功");
                } else {
                    this.errorMsg(re.data.msg);
                }
            }).catch(e => {
                this.errorMsg("置顶失败:" + e.message);
            });
        },
        // 通用取消置顶
        handleCancelTop(row) {
            this.$post(this.nowPath + "/top", {id: row.id, sort: 0}).then(re => {
                if (re.data && re.data.code === 200) {
                    //刷新
                    this.handlePageGet();
                    this.successMsg("取消置顶");
                } else {
                    this.errorMsg(re.data.msg);
                }
            }).catch(e => {
                this.errorMsg("取消置顶失败:" + e.message);
            });
        },
        // 通用删除
        handleDelete(row) {
            // 收集ids
            const ids = row ? [row.id] : this.multipleSelection.map(item => item.id);
            // 2. 校验选中状态（仅批量删除时校验）
            if (!row && ids.length === 0) {
                this.warnMsg('请先勾选');
            }
            //确认框
            this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$post(this.nowPath + "/delete", ids).then(re => {
                    if (re.data && re.data.code === 200) {
                        this.successMsg("删除成功");
                        // 删除缓存
                        if (this.nowPath.includes('/tag')) {
                            this.removeStorage(tagSelectsKey)
                        } else if (this.nowPath.includes('/category')) {
                            this.removeStorage(categorySelectsKey)
                        }
                        // 刷新
                        this.handlePageGet();
                    } else {
                        this.errorMsg(re.data.msg);
                    }
                }).catch(e => {
                    this.errorMsg("删除失败:" + e.message);
                });
            }).catch(() => {
                this.infoMsg('已取消删除');
            });
        },
        // 选中
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        // 页大小
        handleSizeChange(val) {
            this.pageQuery.size = val;
            this.handlePageGet();
        },
        // 当前页
        handleCurrentChange(val) {
            this.pageQuery.page = val;
            this.handlePageGet();
        },

        // 获取分类下拉框
        getCategorySelects() {
            // 缓存
            const cache = this.getStorage(categorySelectsKey);
            if (cache) {
                this.categorySelects = cache;
                return;
            }
            // 查询接口
            this.$post("/category/select").then(re => {
                if (re.data && re.data.code === 200) {
                    this.categorySelects = re.data.data;
                    this.setStorage(categorySelectsKey, re.data.data);
                } else {
                    this.errorMsg(re.data.msg);
                }
            }).catch(e => {
                this.errorMsg("获取分类下拉框失败:"+e.message);
            });
        },
        // 获取标签下拉框
        getTagSelects() {
            // 缓存
            const cache = this.getStorage(tagSelectsKey);
            if (cache) {
                this.tagSelects = cache;
                return;
            }
            // 查询接口
            this.$post("/tag/select").then(re => {
                if (re.data && re.data.code === 200) {
                    this.tagSelects = re.data.data;
                    this.setStorage(tagSelectsKey, re.data.data);
                } else {
                    this.errorMsg(re.data.msg);
                }
            }).catch(e => {
                this.errorMsg("获取标签下拉框失败:"+e.message)
            });
        },
        // 获取文件类型下拉框
        getFileTypeSelects() {
            // 缓存
            const cache = this.getStorage(fileTypeSelectsKey);
            if (cache) {
                this.fileTypeSelects = cache;
                return;
            }
            // 获取接口
            this.$post("/file/type/select").then(re => {
                if (re.data && re.data.code === 200) {
                    this.fileTypeSelects = re.data.data;
                    this.setStorage(fileTypeSelectsKey, re.data.data);
                }
            }).catch(e => {
                this.errorMsg("获取文件类型下拉框失败:"+e.message)
            });
        },
        // Storage读取 默认sessionStorage
        setStorage(key, value, persistent) {
            try {
                const store = persistent ? localStorage : sessionStorage;
                store.setItem(key, JSON.stringify(value));
            } catch (e) {
                console.error('Storage存储错误:', key, e);
            }
        },
        getStorage(key, defaultValue, persistent) {
            try {
                const store = persistent ? localStorage : sessionStorage;
                const raw = store.getItem(key);
                if (raw === null) return defaultValue !== undefined ? defaultValue : null;
                return JSON.parse(raw);
            } catch (e) {
                return defaultValue !== undefined ? defaultValue : null;
            }
        },
        removeStorage(key, persistent) {
            const store = persistent ? localStorage : sessionStorage;
            store.removeItem(key);
        },
        clearStorage(persistent) {
            const store = persistent ? localStorage : sessionStorage;
            store.clear();
        },

        // 开关侧边栏
        handleAsideStatus() {
            this.asideExpand = !this.asideExpand;
        },
        // 浏览器宽度监听修改侧边栏状态
        asideListener() {
            this.asideExpand = window.innerWidth >= 600;
        }
    }
};