package com.platon.rosettanet.admin.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author houzhuang
 * 停用算力请求参数
 */
@Data
@ApiModel(value = "停用算力请求参数")
public class PowerSwitchReq {

    @NotNull(message = "计算节点ID不能为空")
    @ApiModelProperty(value = "计算节点ID", example = "", required = true)
    private String powerNodeId;

    @NotNull(message = "计算节点状态不能为空")
    @ApiModelProperty(value = "计算节点状态", example = "", required = true)
    private String status;

}
