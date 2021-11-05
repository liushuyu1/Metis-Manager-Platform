package com.platon.metis.admin.dto.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "任务详情实体类")
public class TaskDataDetailResp {


    @ApiModelProperty(name = "id",value = "序号")
    private Integer id;
    @ApiModelProperty(name = "taskId",value = "任务id")
    private String taskId;
    @ApiModelProperty(name = "taskName",value = "任务名称")
    private String taskName;
    @ApiModelProperty(name = "createAt",value = "任务发起时间 (时间戳)，单位ms")
    private Long createAt;
    @ApiModelProperty(name = "startAt",value = "任务开始计算时间 (时间戳)，单位ms")
    private Long startAt;
    @ApiModelProperty(name = "endAt",value = "任务结束时间 (时间戳)，单位ms")
    private Long endAt;
    @ApiModelProperty(name = "status",value = "任务状态 (0:unknown未知、1:pending等在中、2:running计算中、3:failed失败、4:success成功)")
    private Integer status;
    @ApiModelProperty(name = "duration",value = "任务所需资源声明，任务运行耗时时长 (单位: ms)")
    private Long duration;
    @ApiModelProperty(name = "costCore",value = "任务所需的CPU资源 (单位: 个)")
    private Long costCore;
    @ApiModelProperty(name = "costMemory",value = "任务所需的内存资源 (单位: byte)")
    private Long costMemory;
    @ApiModelProperty(name = "costBandwidth",value = "任务所需的带宽资源 (单位: bps)")
    private Long costBandwidth;
    @ApiModelProperty(name = "applyUser",value = "任务发起的账户")
    private String applyUser;
    @ApiModelProperty(name = "userType",value = "发起任务用户类型 (0: 未定义; 1: 以太坊地址; 2: Alaya地址; 3: PlatON地址)")
    private Integer userType;
    @ApiModelProperty(name = "reviewed",value = "任务是否被查看过，默认为false(0)")
    private Boolean reviewed;
    @ApiModelProperty(name = "role",value = "我在任务中的角色 (0：unknown 未知、1： owner  任务发起方、2：dataSupplier  数据提供方、 3: powerSupplier  算力提供方、 4： receiver  结果接收方、5：algoSupplier 算法提供方)")
    private Integer role;
    @ApiModelProperty(name = "owner",value = "任务发起方身份信息")
    //任务发起方身份信息
    private CommonTaskOrg owner;
    @ApiModelProperty(name = "algoSupplier",value = "算法提供方身份信息")
    //算法提供方
    private CommonTaskOrg algoSupplier;
    @ApiModelProperty(name = "receivers",value = "结果接收方身份信息")
    //结果接收方
    private List<CommonTaskOrg> receivers;
    @ApiModelProperty(name = "dataSupplier",value = "数据提供方身份信息")
    //数据提供方
    private List<DataSupplier> dataSupplier;
    @ApiModelProperty(name = "powerSupplier",value = "算力提供方")
    //算力提供方
    private List<PowerSupplier> powerSupplier;



     @Data
     public static class CommonTaskOrg{
         @ApiModelProperty(name = "carrierNodeId",value = "组织中调度服务的 nodeId")
         String carrierNodeId;
         @ApiModelProperty(name = "identityId",value = "组织身份标识ID")
         String identityId;
         @ApiModelProperty(name = "orgName",value = "组织名称")
         String orgName;
    }

    @Data
    public static class DataSupplier extends CommonTaskOrg{
        @ApiModelProperty(name = "metaDataId",value = "参与任务的元数据ID")
        private String metaDataId;
        @ApiModelProperty(name = "metaDataName",value = "元数据名称")
        private String metaDataName;
    }


   @Data
    public static class PowerSupplier extends CommonTaskOrg{
        @ApiModelProperty(name = "totalBandwidth",value = "任务总带宽信息")
        private Long totalBandwidth;
        @ApiModelProperty(name = "usedBandwidth",value = "任务占用带宽信息")
        private Long usedBandwidth;
        @ApiModelProperty(name = "totalCore",value = "任务总CPU信息")
        private Long totalCore;
        @ApiModelProperty(name = "usedCore",value = "任务占用CPU信息")
        private Long usedCore;
        @ApiModelProperty(name = "totalMemory",value = "任务总内存信息")
        private Long totalMemory;
        @ApiModelProperty(name = "usedMemory",value = "任务占用内存信息")
        private Long usedMemory;

    }









}
