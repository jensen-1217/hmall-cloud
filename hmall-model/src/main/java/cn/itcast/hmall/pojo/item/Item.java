package cn.itcast.hmall.pojo.item;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tb_item")
public class Item {
    @TableId(type = IdType.AUTO)
    private Long id;//商品id
    private String name;//商品名称
    private Long price;//价格（分）
    private Integer stock;//库存数量
    private String image;//商品图片
    private String category;//分类名称
    private String brand;//品牌名称
    private String spec;//规格
    private Integer sold;//销量
    private Integer commentCount;//评论数
    private Integer status;//商品状态 1-正常，2-下架
    @TableField("isAD")
    private Boolean isAD;//商品状态 1-正常，2-下架
    //指定插入时自动填充的字段
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;//创建时间
    //自定插入或者更新时自动填充的字段
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;//更新时间


}
