package com.alibaba.dragonfly.supernode.dao;

import org.springframework.data.repository.CrudRepository;

import com.alibaba.dragonfly.supernode.common.domain.PeerInfo;

public interface PeerDao extends CrudRepository<PeerInfo, String> {

}
