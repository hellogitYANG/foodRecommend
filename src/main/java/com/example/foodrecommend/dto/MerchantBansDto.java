package com.example.foodrecommend.dto;

import com.example.foodrecommend.beans.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchantBansDto extends Merchant {
    /**
     * 近一小时订单
     */
    private Long orderNum;

    /**
     * 近一小时评价数
     */
    private Long commentNum;

    /**
     * 近一小时好评率
     */
    private Double goodReputation;
}
