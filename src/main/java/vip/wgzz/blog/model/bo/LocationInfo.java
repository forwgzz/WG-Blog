package vip.wgzz.blog.model.bo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.BaseConstants;

/**
 * @author wgzz
 * @date 2026/8/3 14:23
 * @description ip地址完整信息
 */
@Data
@Accessors(chain = true)
public class LocationInfo {

    /**
     * @param ip ip地址
     * @param errorMsg 错误信息
     * @return 错误地址
     */
    public static LocationInfo error(String ip, String errorMsg) {
        return new LocationInfo().setIp(ip).setErrorMsg(errorMsg);
    }

    /**
     * 国家
     */
    private String nation;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 归属地
     */
    private String isp;

    /**
     * ip
     */
    private String ip;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * @return IP归属
     */
    public String getAddress() {
        if (StrUtil.isNotBlank(province)) {
            return province.replace("省", "");
        }
        if (StrUtil.isNotBlank(nation)) {
            return nation;
        }
        return BaseConstants.UnknowStatus;
    }

    /**
     * @return 完整地址
     */
    public String getAllAddress() {
        if (StrUtil.isNotBlank(errorMsg)) {
            return BaseConstants.UnknowStatus;
        }
        return String.format("%s-%s-%s-%s-%s", nation, province, city, district, isp);
    }
}
