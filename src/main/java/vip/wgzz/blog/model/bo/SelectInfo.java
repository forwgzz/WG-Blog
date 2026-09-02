package vip.wgzz.blog.model.bo;

import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/8 20:49
 * @description 下拉框信息
 */
@Data
public class SelectInfo {

    public  SelectInfo(){}

    public  SelectInfo(Object value,String label){
        this.value = value;
        this.label = label;
    }

    public  SelectInfo(Object value,String label,Integer number){
        this.value = value;
        this.label = label;
        this.number = number;
    }

    /**
     * 对应id
     */
    private Object value;

    /**
     * 对应名称
     */
    private String label;

    /**
     * 关联文章数量
     */
    private Integer number;
}
