package org.dromara.huahao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("h_acceptance_rc_pallet")
public class HAcceptanceRcPallet {
    private Long id;
    private String rcId;
    private String palletId;
}


