package com.alibaba.dragonfly.supernode.dao;

import org.springframework.data.repository.CrudRepository;

import com.alibaba.dragonfly.supernode.common.domain.PeerTask;

public interface PeerTaskDao extends CrudRepository<PeerTask, Integer> {

}
