const FrontUtilsMixin = {
    mixins: [AppUtilsMixin],
    data: {
        // 图片预览
        imagePreview: {
            url: "",
            urls: [],
            index: 0
        },
        // 搜索框显示
        searchInputVisible: false,
        // 搜索框内容
        searchValue: "",
        // 手机平面最大宽度
        mobileMaxWidth: 768,
        // 是否是移动端
        isMobile: window.innerWidth <= 768,
        // 左侧抽屉
        mobileLeftDrawerVisible: false,
        // 右侧抽屉
        mobileRightDrawerVisible: false,
        // 顶部隐藏
        headerHidden: false,
        // 顶部颜色
        headerColor: false,
        // 当前滚动条y值
        scrollY: 0,
        // 分类弹窗
        categoryDialogVisible: false,
        // 标签弹窗
        tagDialogVisible: false,
        // 目录链接
        catalogLinks: null,
        // 运行时间
        webStartTimeFormat: null,
        // 发布评论弹窗
        commentDialog: {
            articleId: null,
            userName: null,
            userEmail: null,
            visible: false,
            toCommentId: null,
            rootCommentId: null,
            commentContent: "",
            captcha: null,
            emailNotify: true,
            captchaUrl: null,
        },
        // 回复评论信息
        toComment: null,
        // 评论分页数据
        commentPage: {},
        // 评论id索引
        commentIdIndex: 0,
        rules: {
            userName: [{required: true, message: '昵称不能为空', trigger: 'blur'}],
            userEmail: [
                {required: true, message: '邮箱不能为空', trigger: 'blur'},
                {
                    validator: (rule, value, callback) => {
                        const regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
                        if (!regEmail.test(value)) {
                            callback(new Error('请输入合法的邮箱'));
                        } else {
                            callback();
                        }
                    },
                    trigger: ['blur', 'change']  // 实时校验
                }
            ],
            captcha: [{required: true, message: '验证码不能为空', trigger: 'blur'}],
            commentContent: [{required: true, message: '评论内容不能为空', trigger: 'blur'},]
        }
    },

    created() {
        // 初始化监听
        window.addEventListener('resize', this.handleResize);
        window.addEventListener('scroll', this.handleScroll);

        // 每秒更新一次时间
        setInterval(this.updateWebStartTime, 1000);
    },
    mounted() {
        this.initShowText();
        this.initCatalogLinks();
    },
    methods: {
        // 代码高亮
        initCodeFormat() {
            document.querySelectorAll('pre code').forEach(block => {
                //高亮
                hljs.highlightBlock(block);
                //行号
                hljs.lineNumbersBlock(block);
                //复制按钮
                const button = document.createElement('i');
                button.className = 'copy-button';
                button.classList.add('el-icon-document-copy');
                //绑定点击事件
                button.onclick = () => {
                    try {
                        //提取原始代码
                        let originalCode = '';
                        if (block.childNodes.length > 0) {
                            const targetElements = block.querySelectorAll(".hljs-ln-code");
                            //拼接
                            originalCode = Array.from(targetElements).map(node => node.innerText).join('\n');
                        } else {
                            originalCode = block.innerText || block.textContent || '';
                        }
                        originalCode = originalCode.trim();

                        //复制到剪贴板
                        navigator.clipboard.writeText(originalCode).then(() => {
                            button.classList.add('active');
                            button.classList.replace('el-icon-document-copy', 'el-icon-check');
                            setTimeout(() => {
                                button.classList.remove('active');
                                button.classList.replace('el-icon-check', 'el-icon-document-copy');
                            }, 1500);
                        }).catch(err => {
                            console.error('复制失败:', err);
                        });
                    } catch (error) {
                        console.error('复制操作异常:', error);
                    }
                };
                //将按钮添加到代码块的父元素中
                block.parentNode.appendChild(button);
            });
        },
        // 图片预览初始化
        initImagePreview() {
            // 获取文章内容中的图片
            let imgs = document.querySelectorAll('.article-info-content img');
            if (imgs) {
                const self = this;
                imgs.forEach(img => {
                    // 排除emoji
                    if (!img.classList.contains('emoji')) {
                        self.imagePreview.urls.push(img.getAttribute("src"));
                        // 监听图片点击事件
                        img.addEventListener('click', function () {
                            self.imagePreview.index = self.imagePreview.urls.findIndex(t => t === this.getAttribute("src"))
                            // 触发el-image的预览功能
                            self.outVue.$nextTick(() => {
                                let imageViewer = document.getElementById('image-preview');
                                if (imageViewer) {
                                    imageViewer.click();
                                }
                            });
                        })
                    }
                })
                if (self.imagePreview.urls.length > 0) self.imagePreview.url = self.imagePreview.urls[0];
            }
        },
        // 初始化评论用户信息
        initCommentUser() {
            this.commentDialog.userName = document.getElementById("userName").getAttribute("data-target");
            this.commentDialog.userEmail = document.getElementById("userEmail").getAttribute("data-target");
            this.commentDialog.articleId = document.getElementById("articleId").getAttribute("data-target");
            this.getCommentPage(1)
        },
        // 获取评论分页数据
        getCommentPage(page) {
            if (!this.commentDialog.articleId || !page) {
                return
            }
            this.$post('/comment/page', {articleId: this.commentDialog.articleId, page: page}).then(re => {
                if (re.data && re.data.code === 200) {
                    this.commentPage = re.data.data;
                } else {
                    this.errorMsg(re.data.msg);
                }
            });
        },
        // 添加评论
        addComment() {
            this.$refs.commentDialog.validate(valid => {
                if (valid) {
                    this.$post("/comment/add", this.commentDialog).then(re => {
                        if (re.data && re.data.code === 200) {
                            this.closeCommentDialog();
                            this.getCommentPage(1);
                            this.successMsg("提交成功，等待审核");
                        } else {
                            this.errorMsg(re.data.msg);
                        }
                    });
                }
            })
        },
        // 关闭开评论弹窗
        closeCommentDialog() {
            this.commentDialog.visible = false;
            this.commentDialog.toCommentId = null;
            this.commentDialog.rootCommentId = null;
            this.commentDialog.commentContent = "";
            this.commentDialog.captcha = null;
            this.toComment = null;
        },
        // 打开评论弹窗
        openCommentDialog(comment) {
            if (comment) {
                this.toComment = comment;
                this.commentDialog.toCommentId = comment.id;
                this.commentDialog.rootCommentId = comment.rootCommentId === 0 ? comment.id : comment.rootCommentId;
            }
            this.commentDialog.visible = true;
            this.refreshCaptcha();
        },
        // 刷新验证码
        refreshCaptcha() {
            this.commentDialog.captchaUrl = '/comment/getCaptcha?t=' + Date.now();
        },
        // 更新网站运行时间
        updateWebStartTime() {
            if (!this.__webStartTime) {
                const runTime = document.getElementById('run-time');
                if (runTime) {
                    let time = runTime.getAttribute('alt');
                    this.__webStartTime = new Date(time).getTime();
                }
            }
            if (this.__webStartTime) {
                const now = Date.now();
                const diff = Math.floor((now - this.__webStartTime) / 1000); // 时间差，单位为秒

                if (diff < 60) {
                    this.webStartTimeFormat = `${diff} 秒`;
                } else if (diff < 3600) {
                    const minutes = Math.floor(diff / 60);
                    const seconds = diff % 60;
                    this.webStartTimeFormat = `${minutes} 分 ${seconds} 秒`;
                } else if (diff < 86400) {
                    const hours = Math.floor(diff / 3600);
                    const minutes = Math.floor((diff % 3600) / 60);
                    const seconds = diff % 60;
                    this.webStartTimeFormat = `${hours} 小时 ${minutes} 分 ${seconds} 秒`;
                } else {
                    const days = Math.floor(diff / 86400);
                    const hours = Math.floor((diff % 86400) / 3600);
                    const minutes = Math.floor((diff % 3600) / 60);
                    const seconds = diff % 60;
                    this.webStartTimeFormat = `${days} 天 ${hours} 小时 ${minutes} 分 ${seconds} 秒`;
                }
            }
        },
        // 归档点击
        archiveClick(month) {
            let hash = month.replace(/(\d{4})年0*(\d{1,2})月/, '$1-$2')
            window.location.href = "/archive#" + hash;
        },
        // 目录初始化
        initCatalogLinks() {
            this.catalogLinks = document.querySelectorAll('.catalog-content ul li a')
            if (!this.catalogLinks) return;
            const self = this;
            this.catalogLinks.forEach(link => {
                link.addEventListener('click', function () {
                    // 移除所有链接的 active 类
                    self.catalogLinks.forEach(l => l.classList.remove('active'));
                    // 为当前点击的链接添加 active 类
                    this.classList.add('active');
                    // 获取目标元素并滚动到目标位置
                    const targetId = this.getAttribute('data-target');
                    const targetElement = document.getElementById(targetId);
                    if (targetElement) {
                        targetElement.scrollIntoView({
                            behavior: 'smooth',
                            block: 'start'
                        });
                    }
                });
            });
        },
        // 激活目录链接
        activeCatalogLinks() {
            if (!this.catalogLinks) return;

            for (let link of this.catalogLinks) {
                const id = link.getAttribute('data-target');
                const content = document.getElementById(id);
                if (content) {
                    const rect = content.getBoundingClientRect();
                    if (rect.bottom > 0) {
                        if (id === this.__catalogActiveId) {
                            break;
                        }
                        this.catalogLinks.forEach(l => l.classList.remove('active'));
                        link.classList.add('active');
                        link.scrollIntoView({behavior: 'smooth', block: 'nearest'});
                        this.__catalogActiveId = id;
                        break
                    }
                }
            }

        },
        // 初始化逐字显示
        initShowText() {
            // 获取需要逐字显示的元素
            this.__elements = [
                document.getElementById('show-text-title'),
                document.getElementById('show-text-desc'),
            ];
            // 取出第一个元素
            this.__currentElement = this.__elements.shift();
            this.__currentIndex = 0;
            // 启动逐字显示
            this.showText(this.__currentElement.getAttribute('data-text'));
        },
        // 逐字显示
        showText(fullText) {
            if (!this.isBlank(fullText) && this.__currentIndex < fullText.length) {
                //更新
                this.__currentElement.textContent = fullText.substring(0, this.__currentIndex + 1); //
                this.__currentIndex++;
                setTimeout(() => this.showText(fullText), 80);
            } else {
                //当前元素显示完成后，检查是否还有下一个元素
                if (this.__elements.length > 0) {
                    this.__currentIndex = 0;
                    // 取出下一个元素
                    this.__currentElement = this.__elements.shift();
                    const nextText = this.__currentElement.getAttribute('data-text');
                    this.showText(nextText);
                }
            }
        },
        // 搜索页
        handledSearch() {
            if (this.searchValue) {
                window.location.href = "/search/" + this.searchValue;
            } else {
                this.warnMsg("请输入搜索内容");
            }
        },
        // 开关搜索框
        handleSearchInput() {
            this.searchInputVisible = !this.searchInputVisible;
        },
        // 开关左侧抽屉
        handleMobileLeftDrawer() {
            if (this.isMobile) {
                this.mobileLeftDrawerVisible = !this.mobileLeftDrawerVisible;
            }
        },
        // 开关右侧抽屉
        handleMobileRightDrawer() {
            if (this.isMobile) {
                this.mobileRightDrawerVisible = !this.mobileRightDrawerVisible;
            }
        },

        // 窗口监听
        handleResize() {
            this.isMobile = window.innerWidth <= this.mobileMaxWidth;
            this.searchInputVisible = false;
            this.mobileLeftDrawerVisible = false;
        },
        // 滚动监听
        handleScroll() {
            if (this.__scrollTicking) return;
            this.__scrollTicking = true;
            requestAnimationFrame(() => {
                // 下滑隐藏
                this.headerHidden = window.pageYOffset > this.scrollY;
                this.scrollY = window.pageYOffset;
                this.headerColor = !this.headerHidden && window.pageYOffset>80;
                // 目录回显
                this.activeCatalogLinks();

                this.__scrollTicking = false;
            });
        },
    },
    beforeDestroy() {
        // 移除监听器
        window.removeEventListener('resize', this.handleResize);
        window.removeEventListener('scroll', this.handleScroll);
    },
}