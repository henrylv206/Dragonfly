/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dragonfly.supernode.service.impl;

import com.alibaba.dragonfly.supernode.common.Constants;
import com.alibaba.dragonfly.supernode.common.domain.PeerInfo;
import com.alibaba.dragonfly.supernode.common.domain.PeerTask;
import com.alibaba.dragonfly.supernode.common.domain.Task;
import com.alibaba.dragonfly.supernode.common.exception.AssertException;
import com.alibaba.dragonfly.supernode.common.exception.AuthenticationRequiredException;
import com.alibaba.dragonfly.supernode.common.exception.AuthenticationWaitedException;
import com.alibaba.dragonfly.supernode.common.exception.TaskIdDuplicateException;
import com.alibaba.dragonfly.supernode.common.exception.UrlNotReachableException;
import com.alibaba.dragonfly.supernode.common.exception.ValidateException;
import com.alibaba.dragonfly.supernode.common.util.Assert;
import com.alibaba.dragonfly.supernode.common.util.UrlUtil;
import com.alibaba.dragonfly.supernode.common.view.ResultCode;
import com.alibaba.dragonfly.supernode.common.view.ResultInfo;
import com.alibaba.dragonfly.supernode.common.view.TaskRegistryResult;
import com.alibaba.dragonfly.supernode.service.PeerRegistryService;
import com.alibaba.dragonfly.supernode.service.PeerService;
import com.alibaba.dragonfly.supernode.service.PeerTaskService;
import com.alibaba.dragonfly.supernode.service.TaskService;
import com.alibaba.dragonfly.supernode.service.cdn.CdnManager;
import com.alibaba.dragonfly.supernode.service.cdn.util.PathUtil;
import com.alibaba.dragonfly.supernode.service.lock.LockService;
import com.alibaba.dragonfly.supernode.service.scheduler.ProgressService;
import com.alibaba.dragonfly.supernode.service.timer.DataGcService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("peerRegistryService")
public class PeerRegistryServiceImpl implements PeerRegistryService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CdnManager cdnManager;
    @Autowired
    private PeerService peerService;
    @Autowired
    private PeerTaskService peerTaskService;
    @Autowired
    private ProgressService progressService;
    @Autowired
    private DataGcService dataGcService;
    @Autowired
    private LockService lockService;

    @Override
    public ResultInfo registryTask(String sourceUrl, String taskUrl, String md5, String bizId, String port,
        PeerInfo peerInfo, String path, String version, String superNodeIp, String[] headers, boolean dfdaemon)
        throws ValidateException {
        ResultInfo resultInfo = new ResultInfo();
        validateParams(sourceUrl, port, path, peerInfo);
        
        // LV generate current super node's ip and cid
        if (Constants.localIp == null) {
            Constants.localIp = superNodeIp;
            Constants.generateNodeCid();
        }

        // LV create Task and taskId
        Task task = new Task(sourceUrl, taskUrl, md5, bizId, headers, dfdaemon, peerInfo.getIp());
        String taskId = taskService.createTaskId(taskUrl, md5, bizId);

        task.setTaskId(taskId);
        lockService.lockTaskOnRead(taskId); // LV lock read
        
        
        TaskRegistryResult taskRegistryResult = new TaskRegistryResult();
        try {
            task = taskService.add(task); // LV add task
            dataGcService.updateAccessTime(taskId); // LV update task's access time

            taskRegistryResult.setFileLength(task.getHttpFileLen());
            taskRegistryResult.setPieceSize(task.getPieceSize());
            PeerTask peerTask = new PeerTask(peerInfo.getCid(), taskId, Integer.parseInt(port), path,
                task.getPieceSize());
            registryPeerNode(resultInfo, taskRegistryResult, taskId, peerInfo, peerTask);
            if (resultInfo.successCode()) {
                if (!cdnManager.triggerCdnSyncAction(taskId)) {
                    resultInfo.withCode(ResultCode.SYSTEM_ERROR).withMsg("trigger fail!");
                }
            }
        } catch (TaskIdDuplicateException e) {
            resultInfo.withCode(ResultCode.TASK_CONFLICT).withMsg(e.getMessage());
        } catch (UrlNotReachableException e) {
            resultInfo.withCode(ResultCode.URL_NOT_REACH);
        } catch (AuthenticationRequiredException e) {
            resultInfo.withCode(ResultCode.NEED_AUTH);
        } catch (AuthenticationWaitedException e) {
            resultInfo.withCode(ResultCode.WAIT_AUTH);
        } finally {
            lockService.unlockTaskOnRead(taskId); // LV unlock read
        }
        
        return resultInfo;
    }

    /**
     * @param sourceUrl
     * @param port
     * @param path
     * @param peerInfo
     * @throws ValidateException
     */
    private void validateParams(String sourceUrl, String port, String path, PeerInfo peerInfo)
        throws ValidateException {
        try {
            Assert.assertTrue(UrlUtil.isValidUrl(sourceUrl), ResultCode.PARAM_ERROR,
                "source url not startsWith http:// or https://");
            Assert.assertNumeric(port, ResultCode.PARAM_ERROR, "port is not numeric");
            Assert.assertNotEmpty(path, ResultCode.PARAM_ERROR, "path is empty");
            Assert.assertNotNull(peerInfo, ResultCode.PARAM_ERROR, "peerInfo is null");
            Assert.assertTrue(UrlUtil.isValidIp(peerInfo.getIp()), ResultCode.PARAM_ERROR, "ip of peer is illegal");
            Assert.assertNotEmpty(peerInfo.getCid(), ResultCode.PARAM_ERROR, "cid of peer is empty");

        } catch (AssertException e) {
            throw new ValidateException(e.getCode(), e.getMessage());
        }
    }

    private void registryPeerNode(ResultInfo resultInfo, TaskRegistryResult taskRegistryResult, String taskId, PeerInfo peerInfo, PeerTask peerTask) {
        peerService.add(peerInfo); // LV add peer info
        peerTaskService.add(peerTask); // LV add peer task

        ResultInfo initResult = progressService.initProgress(taskId, peerInfo.getCid());
        if (initResult.successCode()) {
            taskRegistryResult.setTaskId(taskId);
            resultInfo.withData(taskRegistryResult);
        } else {
            resultInfo.withCode(initResult.getCode());
            resultInfo.withMsg(initResult.getMsg());
        }
    }

    @Override
    public ResultInfo registryCdnNode(Task task) {
        String taskId = task.getTaskId();

        String cid = Constants.getSuperCid(taskId);
        int port = Constants.PORT;
        String path = PathUtil.getHttpPath(taskId);
        PeerInfo peerInfo = new PeerInfo();
        peerInfo.setIp(Constants.localIp);
        peerInfo.setCid(cid);
        PeerTask peerTask = new PeerTask(cid, taskId, port, path, task.getPieceSize());

        peerService.add(peerInfo);
        peerTaskService.add(peerTask);

        ResultInfo initResult = progressService.initProgress(taskId, cid);
        return initResult;
    }
}
