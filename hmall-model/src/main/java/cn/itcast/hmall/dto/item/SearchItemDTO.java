package cn.itcast.hmall.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author mrchen
 * @date 2022/5/30 15:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchItemDTO {
    private Integer page;
    private Integer size;
    private String name;
    private Date beginDate;
    private Date endDate;
    private Date[] dateRange;
    private String brand;
    private String category;

    public void setDateRange(Date[] dateRange) {
        this.dateRange = dateRange;
        if (dateRange != null && dateRange.length == 2) {
            this.beginDate = dateRange[0];
            this.endDate = dateRange[1];
        }
    }
}
