const HeatmapUtilsMixin = {
    data :{
        currentYear: null,
        currentMonth: null,
        visibleWeeks: 26,
        heatmapDataMap:{},
        resizeObserver: null,
        colors: ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39']
    },
    computed: {
        // 可见数据
        visibleData() {
            let lastDayOfMonth = new Date(this.currentYear, this.currentMonth + 1, 0);
            let endDayOfWeek = lastDayOfMonth.getDay();
            let endDate = new Date(lastDayOfMonth);
            endDate.setDate(endDate.getDate() + (6 - endDayOfWeek));

            let totalDays = this.visibleWeeks * 7;
            let startDate = new Date(endDate);
            startDate.setDate(startDate.getDate() - totalDays + 1);

            let result = [];
            let current = new Date(startDate);
            while (current <= endDate) {
                let dateStr = this.formatDate(current);
                result.push({date: dateStr, count: this.heatmapDataMap[dateStr] || 0});
                current.setDate(current.getDate() + 1);
            }
            return result;
        },
        // 可见月份
        visibleMonthLabels() {
            if (!this.visibleData.length) return [];
            let labels = [];
            let currentMonth = -1;
            let cnMonths = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];

            this.visibleData.forEach(function (item, idx) {
                let col = Math.floor(idx / 7);
                let month = parseInt(item.date.slice(5, 7)) - 1;
                if (month !== currentMonth) {
                    labels.push({name: cnMonths[month], span: 1, startCol: col});
                    currentMonth = month;
                } else {
                    labels[labels.length - 1].span = col - labels[labels.length - 1].startCol + 1;
                }
            })
            return labels;
        },
        canNext() {
            let now = new Date();
            return !(this.currentYear === now.getFullYear() && this.currentMonth === now.getMonth());
        }
    },
    created() {
    },
    mounted () {
        this.getCurrentTime();
        this.getHeatmapData();
    },
    beforeDestroy () {
        if (this.resizeObserver) this.resizeObserver.disconnect();
    },
    methods: {
        // 初始化热力图
        initHeatmap(){
            // 当前时间
            this.getCurrentTime();
            // 获取数据
            this.getHeatmapData();
            // 计算可见周数
            this.calcVisibleWeeks()
            let self = this
            this.resizeObserver = new ResizeObserver(function () {
                self.calcVisibleWeeks()
            })
            this.resizeObserver.observe(this.$refs.heatmap)
        },
        // 获取当前时间
        getCurrentTime() {
            this.currentYear = new Date().getFullYear();
            this.currentMonth = new Date().getMonth();
        },
        // 由调用方重写
        getHeatmapData() {

        },
        // 计算可见周数
        calcVisibleWeeks() {
            if(this.heatmapDataMap.size >0){
                let containerWidth = this.$refs.heatmap.clientWidth;
                let availableWidth = containerWidth - 30 - 6;
                this.visibleWeeks = Math.max(4, Math.floor(availableWidth / 17));
            }
        },

        // 获取 tooltip 显示位置
        getPlacement: function (idx) {
            let col = Math.floor(idx / 7);
            let totalCols = Math.ceil(this.visibleData.length / 7);
            return col < totalCols / 2 ? 'top-start' : 'top-end';
        },

        // 切换月份
        shiftMonth(delta) {
            if (delta === 0) {
                this.getCurrentTime();
                return;
            }
            let m = this.currentMonth + delta;
            let y = this.currentYear;
            if (m < 0) {
                m = 11;
                y--;
            }
            if (m > 11) {
                m = 0;
                y++;
            }
            this.currentYear = y;
            this.currentMonth = m;
        },
        // 获取颜色
        getColor(count) {
            if (count === 0) return this.colors[0];
            if (count <= 2) return this.colors[1];
            if (count <= 4) return this.colors[2];
            if (count <= 8) return this.colors[3];
            return this.colors[4];
        },

        // 格式化日期
        formatDate(d) {
            let y = d.getFullYear();
            let m = String(d.getMonth() + 1).padStart(2, '0');
            let day = String(d.getDate()).padStart(2, '0');
            return y + '-' + m + '-' + day;
        }
    }
}
