package com.platon.metis.admin.dao;

import com.platon.metis.admin.dao.entity.LocalPowerNode;
import com.platon.metis.admin.dao.entity.PowerLoad;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author houz
 * 业务节点实体类
 */
@Repository
public interface LocalPowerNodeMapper {

    /**
     * 插入计算节点数据
     * @param localPowerNode
     * @return
     */
    int insertPowerNode(LocalPowerNode localPowerNode);

    /**
     * 根据节点id修改计算节点
     * @param localPowerNode
     * @return
     */
    int updatePowerNodeByNodeId(LocalPowerNode localPowerNode);

    /**
     * 批量replace计算节点的基本信息，以及和调度服务的连接情况
     * @param localPowerNodeList
     * @return
     */
    int replaceBasicInfoExcludingNameBatch(List<LocalPowerNode> localPowerNodeList);

    /**
     * 批量修改计算节点的资源信息
     * @param localPowerNodeList
     * @return
     */
    int updateResourceInfoBatchByNodeId(List<LocalPowerNode> localPowerNodeList);


    /**
     * 根据计算id删除计算节点
     * @param nodeId
     * @return
     */
    int deletePowerNode(@Param("nodeId") String nodeId);

    /**
     * 查询计算节点详情
     * @param nodeId
     * @return
     */
    LocalPowerNode queryPowerNodeDetails(@Param("nodeId") String nodeId);

    /**
     * 查询计算节点列表
     * @param keyword
     * @return
     */
    List<LocalPowerNode> queryPowerNodeList(@Param("keyword") String keyword);


    PowerLoad getCurrentLocalPowerLoadByNodeId(@Param("nodeId") String nodeId);

    LocalPowerNode findLocalPowerNodeByName(@Param("nodeName") String nodeName);

    void updateLocalPowerNodeName(@Param("nodeId") String nodeId, @Param("nodeName") String nodeName);
}