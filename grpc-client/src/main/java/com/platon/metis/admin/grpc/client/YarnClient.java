package com.platon.metis.admin.grpc.client;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.Empty;
import com.platon.metis.admin.common.exception.ApplicationException;
import com.platon.metis.admin.common.exception.BizException;
import com.platon.metis.admin.common.exception.CallGrpcServiceFailed;
import com.platon.metis.admin.dao.entity.DataNode;
import com.platon.metis.admin.dao.enums.FileTypeEnum;
import com.platon.metis.admin.grpc.channel.SimpleChannelManager;
import com.platon.metis.admin.grpc.common.CommonBase;
import com.platon.metis.admin.grpc.entity.RegisteredNodeResp;
import com.platon.metis.admin.grpc.entity.YarnAvailableDataNodeResp;
import com.platon.metis.admin.grpc.entity.YarnGetNodeInfoResp;
import com.platon.metis.admin.grpc.entity.YarnQueryFilePositionResp;
import com.platon.metis.admin.grpc.service.YarnRpcMessage;
import com.platon.metis.admin.grpc.service.YarnServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.platon.metis.admin.grpc.constant.GrpcConstant.GRPC_SUCCESS_CODE;

/**
 * @Author liushuyu
 * @Date 2021/7/8 18:27
 * @Version
 * @Desc 系统服务客户端
 * java服务类：YarnServiceGrpc
 * proto文件：sys_rpc_api.proto
 */

@Component
@Slf4j
public class YarnClient {
    @Resource
    private SimpleChannelManager channelManager;

    /**
     * 调度新增数据节点
     * @param dataNode           数据节点实体类
     * @return
     */
    public RegisteredNodeResp setDataNode(DataNode dataNode) {
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        YarnRpcMessage.SetDataNodeRequest setDataNodeRequest = YarnRpcMessage.SetDataNodeRequest.newBuilder().
                setInternalIp(dataNode.getInternalIp()).
                setInternalPort(String.valueOf(dataNode.getInternalPort())).
                setExternalIp(dataNode.getExternalIp()).
                setExternalPort(String.valueOf(dataNode.getExternalPort())).build();
        log.debug("调用新增数据节点调度服务,参数:{}",setDataNodeRequest);
        //3.调用rpc,获取response
        YarnRpcMessage.SetDataNodeResponse response = YarnServiceGrpc.newBlockingStub(channel).setDataNode(setDataNodeRequest);
        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
            throw new CallGrpcServiceFailed(response.getMsg());
        }

        YarnRpcMessage.YarnRegisteredPeerDetail resDataNode = response.getNode();
        if (resDataNode != null) {
            RegisteredNodeResp nodeResp = new RegisteredNodeResp();
            nodeResp.setNodeId(resDataNode.getId());
            nodeResp.setConnStatus(resDataNode.getConnState().getNumber());
            return nodeResp;
        }
        return null;

    }

    /**
     * 调度修改数据节点
     * @param dataNode           数据节点实体类
     * @return
     */
    public RegisteredNodeResp updateDataNode(DataNode dataNode) {
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        YarnRpcMessage.UpdateDataNodeRequest request = YarnRpcMessage.UpdateDataNodeRequest
                .newBuilder().setId(dataNode.getNodeId()).
                        setInternalIp(dataNode.getInternalIp()).
                        setInternalPort(String.valueOf(dataNode.getInternalPort())).
                        setExternalIp(dataNode.getExternalIp()).
                        setExternalPort(String.valueOf(dataNode.getExternalPort())).build();
        log.debug("调用修改数据节点调度服务,参数:{}",request);
        //3.调用rpc,获取response
        YarnRpcMessage.SetDataNodeResponse response = YarnServiceGrpc.newBlockingStub(channel).updateDataNode(request);
        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
            throw new CallGrpcServiceFailed(response.getMsg());
        }

        YarnRpcMessage.YarnRegisteredPeerDetail resDataNode = response.getNode();
        if (resDataNode != null) {
            RegisteredNodeResp nodeResp = new RegisteredNodeResp();
            nodeResp.setNodeId(resDataNode.getId());
            nodeResp.setConnStatus(resDataNode.getConnState().getNumber());
            return nodeResp;
        }
        return null;

    }

    /**
     * 调度删除数据节点
     * @return
     */
    public void deleteDataNode(String id) {
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        YarnRpcMessage.DeleteRegisteredNodeRequest request = YarnRpcMessage.DeleteRegisteredNodeRequest.newBuilder().setId(id).build();
        log.debug("调用删除数据节点调度服务,参数:{}",request);
        //3.调用rpc,获取response
        CommonBase.SimpleResponse response = YarnServiceGrpc.newBlockingStub(channel).deleteDataNode(request);
        log.debug("调用删除数据节点调度服务,响应结果:{}",response);
        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
            throw new CallGrpcServiceFailed(response.getMsg());
        }
    }

    /**
     * 调度获取数据节点列表
     * @return
     */
    public List<RegisteredNodeResp> getDataNodeList() {
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        Empty emptyGetParams = Empty.newBuilder().build();
        log.debug("调用获取数据节点列表调度服务");
        //3.调用rpc,获取response
        YarnRpcMessage.GetRegisteredNodeListResponse response = YarnServiceGrpc.newBlockingStub(channel).getDataNodeList(emptyGetParams);

        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
            throw new CallGrpcServiceFailed(response.getMsg());
        }
        List<RegisteredNodeResp> nodeRespList = new ArrayList<>();

        response.getNodesList().forEach(item -> {
            RegisteredNodeResp nodeResp = new RegisteredNodeResp();
            nodeResp.setNodeId(item.getNodeDetail().getId());
            nodeResp.setInternalIp(item.getNodeDetail().getInternalIp());
            String internalPort = item.getNodeDetail().getInternalPort();
            nodeResp.setInternalPort(internalPort == null ? null : Integer.valueOf(internalPort));
            nodeResp.setExternalIp(item.getNodeDetail().getExternalIp());
            String externalPort = item.getNodeDetail().getExternalPort();
            nodeResp.setExternalPort(externalPort == null ? null : Integer.valueOf(externalPort));
            nodeResp.setConnStatus(item.getNodeDetail().getConnState().getNumber());
            nodeRespList.add(nodeResp);
        });

        return nodeRespList;
    }


    /**
     * 根据需要上传的文件大小和类型，获取可用的数据节点信息
     */
    public YarnAvailableDataNodeResp getAvailableDataNode(long fileSize, FileTypeEnum fileType){
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        YarnRpcMessage.QueryAvailableDataNodeRequest request = YarnRpcMessage.QueryAvailableDataNodeRequest
                .newBuilder()
                .setFileSize(fileSize)
                .setFileTypeValue(fileType.getValue())
                .build();
        //3.调用rpc,获取response
        YarnRpcMessage.QueryAvailableDataNodeResponse response = YarnServiceGrpc.newBlockingStub(channel).queryAvailableDataNode(request);
        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }
        if (StrUtil.isEmpty(response.getIp()) || StrUtil.isEmpty(response.getPort())) {
            throw new BizException(StrUtil.format("获取可用数据节点信息失败：ip:{},port:{}",
                    response.getIp(),
                    response.getPort()));
        }
        /**
         * 由于调度服务rpc接口也在开发阶段，如果直接返回调度服务的response，一旦response发生变化，则调用该方法的地方都需要修改
         * 故将response转换后再放给service类使用
         */
        YarnAvailableDataNodeResp node = new YarnAvailableDataNodeResp();
        node.setIp(response.getIp());
        node.setPort(Integer.parseInt(response.getPort()));
        return node;

    }

    /**
     * 查询需要下载的目标原始文件所在的 数据服务信息和文件的完整相对路径
     */
    public YarnQueryFilePositionResp queryFilePosition(String fileId) throws ApplicationException{
        //1.获取rpc连接
        Channel channel = channelManager.getCarrierChannel();
        //2.拼装request
        YarnRpcMessage.QueryFilePositionRequest request = YarnRpcMessage.QueryFilePositionRequest
                .newBuilder()
                .setOriginId(fileId)
                .build();
        //3.调用rpc,获取response
        YarnRpcMessage.QueryFilePositionResponse response = YarnServiceGrpc.newBlockingStub(channel).queryFilePosition(request);
        //4.处理response
        //4.处理response
        if (response == null) {
            throw new CallGrpcServiceFailed();
        }

        if (StrUtil.isEmpty(response.getIp())
                || StrUtil.isEmpty(response.getPort())
                || StrUtil.isEmpty(response.getFilePath())) {
            throw new BizException(StrUtil.format("获取可用数据节点信息失败：ip:{},port:{},filePath:{}",
                    response.getIp(),
                    response.getPort(),
                    response.getFilePath()));
        }
        /**
         * 由于调度服务rpc接口也在开发阶段，如果直接返回调度服务的response，一旦response发生变化，则调用该方法的地方都需要修改
         * 故将response转换后再放给service类使用
         */
        YarnQueryFilePositionResp resp = new YarnQueryFilePositionResp();
        resp.setIp(response.getIp());
        resp.setPort(Integer.parseInt(response.getPort()));
        resp.setFilePath(response.getFilePath());
        return resp;

    }


    /**
     * 尝试连接调度服务,连通则返回true，否则返回false
     */
    public boolean connectScheduleServer(String scheduleIP,int schedulePort){
        //1.获取rpc连接
        ManagedChannel channel = null;
        try{
            channel = channelManager.buildChannel(scheduleIP, schedulePort);
            //2.拼装request
            Empty request = Empty.newBuilder().build();
            //3.调用rpc,获取response
            YarnRpcMessage.GetNodeInfoResponse response = YarnServiceGrpc.newBlockingStub(channel).getNodeInfo(request);

            //4.处理response
            if (response == null) {
                throw new CallGrpcServiceFailed();
            }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
                throw new CallGrpcServiceFailed(response.getMsg());
            }

        } catch (Throwable throwable) {
            return false;
        } finally {
            channelManager.closeChannel(channel);
        }
        return true;

    }

    /**
     * 查看自身调度服务信息
     * @param scheduleIP 调度服务ip
     * @param schedulePort 调度服务端口
     */
    public YarnGetNodeInfoResp getNodeInfo(String scheduleIP,int schedulePort){
        YarnGetNodeInfoResp resp = new YarnGetNodeInfoResp();
        //1.获取rpc连接
        ManagedChannel channel = null;
        try{
            channel = channelManager.buildChannel(scheduleIP, schedulePort);
            //2.拼装request
            Empty request = Empty.newBuilder().build();
            //3.调用rpc,获取response
            //3.调用rpc,获取response
            YarnRpcMessage.GetNodeInfoResponse response = YarnServiceGrpc.newBlockingStub(channel).getNodeInfo(request);

            //4.处理response
            if (response == null) {
                throw new CallGrpcServiceFailed();
            }else if(response.getStatus() != GRPC_SUCCESS_CODE) {
                throw new CallGrpcServiceFailed(response.getMsg());
            }
            /**
             * 由于调度服务rpc接口也在开发阶段，如果直接返回调度服务的response，一旦response发生变化，则调用该方法的地方都需要修改
             * 故将response转换后再放给service类使用
             */
            YarnRpcMessage.YarnNodeInfo information = response.getInformation();
            resp.setNodeId(information.getNodeId());
            resp.setInternalIp(information.getInternalIp());
            resp.setInternalPort(information.getInternalPort());
            resp.setExternalIp(information.getExternalIp());
            resp.setExternalPort(information.getExternalPort());
            resp.setIdentityType(information.getIdentityType());
            resp.setIdentityId(information.getIdentityId());
            resp.setState(information.getState().getNumber());
            resp.setName(information.getName());
            resp.setLocalBootstrapNode(information.getLocalBootstrapNode());
            resp.setLocalMultiAddr(information.getLocalMultiAddr());
            resp.setStatus(GRPC_SUCCESS_CODE);
            resp.setConnCount(information.getRelatePeers());
            resp.setMsg("成功");
        } catch (Throwable e) {
            resp.setStatus(1);
            resp.setMsg(e.getMessage());
        } finally {
            channelManager.closeChannel(channel);
        }
        return resp;
    }
}
