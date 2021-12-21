package com.platon.metis.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.platon.metis.admin.common.context.LocalOrgCache;
import com.platon.metis.admin.common.exception.IdentityIdMissing;
import com.platon.metis.admin.common.exception.OrgInfoNotFound;
import com.platon.metis.admin.dao.*;
import com.platon.metis.admin.dao.entity.*;
import com.platon.metis.admin.dao.enums.TaskStatusEnum;
import com.platon.metis.admin.service.TaskService;
import com.platon.metis.admin.service.constant.ServiceConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

       @Resource
        private TaskMapper taskMapper;

       @Resource
       private TaskEventMapper taskEventMapper;

       @Resource
       private TaskOrgMapper taskOrgMapper;

        @Resource
        private TaskAlgoProviderMapper taskAlgoConsumerMapper;

       @Resource
       private TaskResultConsumerMapper taskResultConsumerMapper;

       @Resource
       private TaskDataProviderMapper taskDataProviderMapper;

       @Resource
       private TaskPowerProviderMapper taskPowerProviderMapper;



        @Override
        public Page<Task> listTaskByIdentityIdWithRole(String identityId, Integer statusFilter, Integer roleFilter, Long startTimestamp, Long endTimestamp, int pageNumber, int pageSize) {

            //String roleParam = RoleEnum.getMessageByCode(role);
            Timestamp startTimestampParam = (startTimestamp == 0) ? null : new Timestamp(startTimestamp);
            Timestamp endTimestampParam = (endTimestamp == 0) ? null : new Timestamp(endTimestamp);

            Page<Task> taskPage = PageHelper.startPage(pageNumber, pageSize);
            List<Task> taskList = taskMapper.listTaskByIdentityIdWithRole(identityId, statusFilter, roleFilter, startTimestampParam, endTimestampParam);
            taskList.forEach(task -> {
                if(checkEndAtOver72Hour(task)){
                    task.setReviewed(true);
                }
            });

            return taskPage;
        }

    @Override
    public Page<Task> listTaskByIdentityIdAndMetaDataIdWithRole(String identityId, String metaDataId, int pageNumber, int pageSize) {
        Page<Task> taskPage = PageHelper.startPage(pageNumber, pageSize);
        taskMapper.listTaskByIdentityIdAndMetaDataIdWithRole(identityId, metaDataId);
        return taskPage;
    }

    @Override
    public Page<Task> listRunningTaskByPowerNodeId(String powerNodeId, int pageNumber, int pageSize) {
        Page<Task> taskPage = PageHelper.startPage(pageNumber, pageSize);
        taskMapper.listRunningTaskByPowerNodeId(powerNodeId);
        return taskPage;
    }

    @Override
    public Integer selectAllTaskCount() {
        return taskMapper.selectAllTaskCount();
    }

    @Override
    public Integer selectTaskRunningCount() {
        return taskMapper.selectTaskRunningCount();
    }

    @Override
    public TaskStatistics taskStatistics() {
        //String identityId = LocalOrgIdentityCache.getIdentityId();
        LocalOrg localOrg = (LocalOrg)LocalOrgCache.getLocalOrgInfo();
        if(localOrg == null){
            throw new OrgInfoNotFound();
        }else if(StringUtils.isBlank(localOrg.getIdentityId())){
            throw new IdentityIdMissing();
        }

        return taskMapper.taskStatistics(localOrg.getIdentityId());
    }

    @Override
    public Task getTaskDetails(String taskId) {
        Task task = taskMapper.selectTaskByTaskId(taskId,null);
         //任务发起方身份信息
        TaskOrg owner = taskOrgMapper.findOrgByIdentityId(task.getOwnerIdentityId());
        task.setOwner(owner);

        LocalOrg localOrg = (LocalOrg)LocalOrgCache.getLocalOrgInfo();
        if(localOrg == null){
            throw new OrgInfoNotFound();
        }else if(StringUtils.isBlank(localOrg.getIdentityId())){
            throw new IdentityIdMissing();
        }

        //本组织在此任务中的身份
        Map<String, Boolean> roleMap = taskMapper.listRoleByTaskIdAndIdentityId(taskId, localOrg.getIdentityId());
        task.setDynamicFields(roleMap);

        //算法提供方
        TaskAlgoProvider taskAlgoProvider = taskAlgoConsumerMapper.selectTaskAlgoProviderWithOrgNameByTaskId(taskId);
        task.setAlgoSupplier(taskAlgoProvider);

        //算力提供方
        List<TaskPowerProvider> powerSupplierList = taskPowerProviderMapper.selectTaskPowerWithOrgByTaskId(taskId);
        task.setPowerSupplier(powerSupplierList);

        //数据提供方
        List<TaskDataProvider> dataSupplierList = taskDataProviderMapper.selectTaskDataWithOrgByTaskId(taskId);
        task.setDataSupplier(dataSupplierList);

        //结果接收方
        List<TaskResultConsumer> resultReceiverList = taskResultConsumerMapper.selectTaskResultWithOrgByTaskId(taskId);
        task.setReceivers(resultReceiverList);

        //更新task查看状态
        taskMapper.updateTaskReviewedById(taskId,true);
        //是否任务结束超过72小时，非最新(已查看)
        if(checkEndAtOver72Hour(task)){
            task.setReviewed(true);
        }
        return task;
    }

    @Override
    public Task selectTaskByTaskId(String taskId) {
        return taskMapper.selectTaskByTaskId(taskId,null);
    }

    @Override
    public List<TaskEvent> listTaskEventWithOrgName(String taskId) {
        List<TaskEvent> taskEventList = taskEventMapper.listTaskEventWithOrgNameByTaskId(taskId);
        if(!CollectionUtils.isEmpty(taskEventList)){
            //更新task查看状态
            taskMapper.updateTaskReviewedById(taskId,true);
        }
        return taskEventList;
    }


    /**
     * 是否未查看task
     * @param task
     * @return
     */
    private boolean isTaskSucceeFailUnRead(Task task){
        int status = task.getStatus();
        return (TaskStatusEnum.SUCCESS.getValue() == status || TaskStatusEnum.FAILED.getValue() == status) && !task.getReviewed();
    }

    /**
     * 任务状态是否为PENDING/RUNNING
     * @param task
     * @return
     */
    private boolean isTaskStatusPendAndRunning(Task task){
        int status = task.getStatus();
        return TaskStatusEnum.PENDING.getValue() == status || TaskStatusEnum.RUNNING.getValue() == status;
    }

    /**
     * 检查任务结束时间是否超过72小时
     * @param task
     * @return
     */
    private boolean checkEndAtOver72Hour(Task task){
        boolean isOver = false;
        if(!Objects.isNull(task.getStatus()) && isTaskSucceeFailUnRead(task) && !Objects.isNull(task.getEndAt())){
            long currentTime = new Date().getTime();
            long endAtTime = task.getEndAt().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            isOver = (currentTime - endAtTime) > ServiceConstant.TIME_HOUR_72;
            return isOver;
        }
        return isOver;
    }
}
