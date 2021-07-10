package com.platon.rosettanet.admin.controller.node;

import com.github.pagehelper.Page;
import com.platon.rosettanet.admin.constant.ControllerConstants;
import com.platon.rosettanet.admin.dao.entity.DataNode;
import com.platon.rosettanet.admin.dto.JsonResponse;
import com.platon.rosettanet.admin.dto.req.*;
import com.platon.rosettanet.admin.dto.resp.AvailableStatusResp;
import com.platon.rosettanet.admin.service.DataNodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lyf
 * @Description 数据节点控制层
 * @date 2021/7/8 17:10
 */
@RequestMapping("/api/v1/node/datanode/")
@RestController
public class DataNodeController {

    @Autowired
    private DataNodeService dataNodeService;

    /**
     * @param req 节点分页查询请求类
     * @return
     */
    @PostMapping("listNode")
    public JsonResponse listNode(@Validated @RequestBody NodePageReq req) {
        Page<DataNode> dataNodes = dataNodeService.listNode(req.getPageNumber(), req.getPageSize(), req.getKeyword());
        return JsonResponse.page(dataNodes);
    }

    @PostMapping("addDataNode")
    public JsonResponse addDataNode(@Validated @RequestBody DataNodeAddReq dataNodeAddReq) {
        DataNode dataNode = new DataNode();
        BeanUtils.copyProperties(dataNodeAddReq, dataNode);
        dataNode.setHostName(dataNodeAddReq.getNodeName());
        if (!dataNodeService.checkDataNodeName(dataNode)) {
            return JsonResponse.fail("保存失败，节点名称已存在！");
        }
        ;
        return JsonResponse.success(dataNodeService.addDataNode(dataNode));
    }

    @PostMapping("checkDataNodeName")
    public JsonResponse checkDataNodeName(@Validated @RequestBody CheckDataNodeNameReq checkDataNodeNameReq) {
        DataNode dataNode = new DataNode();
        dataNode.setHostName(checkDataNodeNameReq.getNodeName());
        AvailableStatusResp resp = new AvailableStatusResp();
        if (dataNodeService.checkDataNodeName(dataNode)) {
            resp.setStatus(ControllerConstants.STATUS_AVAILABLE);
        } else {
            resp.setStatus(ControllerConstants.STATUS_NOT_AVAILABLE);
        }
        return JsonResponse.success(resp);
    }

    @PostMapping("updateDataNode")
    public JsonResponse updateDataNode(@Validated @RequestBody DataNodeUpdateReq dataNodeUpdateReq) {
        DataNode dataNode = new DataNode();
        BeanUtils.copyProperties(dataNodeUpdateReq, dataNode);
        dataNodeService.updateDataNode(dataNode);
        return JsonResponse.success();
    }

    @PostMapping("deleteDataNode")
    public JsonResponse deleteDataNode(@Validated @RequestBody DataNodeDeleteReq dataNodeDeleteReq) {
        dataNodeService.deleteDataNode(dataNodeDeleteReq.getNodeId());
        return JsonResponse.success();
    }

}
