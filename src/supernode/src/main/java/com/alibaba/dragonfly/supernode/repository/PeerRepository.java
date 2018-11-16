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
package com.alibaba.dragonfly.supernode.repository;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.dragonfly.supernode.common.domain.PeerInfo;
import com.alibaba.dragonfly.supernode.dao.PeerDao;

@Repository
public class PeerRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(PeerRepository.class);
	
    private static final ConcurrentHashMap<String, PeerInfo> peerMap = new ConcurrentHashMap<String, PeerInfo>();

    @Autowired
	private PeerDao peerDao;
    
    public boolean add(PeerInfo peerInfo) {
        String cid = peerInfo.getCid();
        if (StringUtils.isNotBlank(cid)) {
            peerMap.putIfAbsent(cid, peerInfo); // LV save to memory
            
            // LV save to db
            try {
            	peerDao.save(peerInfo);
            } catch (Exception e) {
            	logger.error("save peer failed.", e);
            }
            
            return true;
        }
        return false;
    }

    public boolean remove(String cid) {
    	// LV delete from db
        try {
        	peerDao.delete(cid);
        } catch (Exception e) {
        	logger.error("delete peer failed.", e);
        }
        
        return cid != null && peerMap.remove(cid) != null;
    }

    public PeerInfo get(String cid) {
        PeerInfo peerInfo = null;
        if (cid != null) {
            peerInfo = peerMap.get(cid);
            
            // LV get from db
            try {
            	if (peerInfo == null) {
            		peerInfo = peerDao.findOne(cid);
            	}
            } catch (Exception e) {
            	logger.error("get peer failed.", e);
            }
        }
        
        return peerInfo;
    }
}
