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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.dragonfly.supernode.common.domain.PeerInfo;
import com.alibaba.dragonfly.supernode.dao.PeerDao;

@Repository
public class PeerRepository {
    private static final ConcurrentHashMap<String, PeerInfo> peerMap = new ConcurrentHashMap<String, PeerInfo>();

    @Autowired
	private PeerDao peerDao;
    
    public boolean add(PeerInfo peerInfo) {
        String cid = peerInfo.getCid();
        if (StringUtils.isNotBlank(cid)) {
            peerMap.putIfAbsent(cid, peerInfo); // LV save to memory
            return true;
        }
        return false;
    }

    public boolean remove(String cid) {
        return cid != null && peerMap.remove(cid) != null;
    }

    public PeerInfo get(String cid) {
        PeerInfo peerInfo = null;
        if (cid != null) {
            peerInfo = peerMap.get(cid);
        }
        return peerInfo;
    }
}
