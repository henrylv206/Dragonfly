package com.alibaba.dragonfly.supernode.dao;

import org.springframework.data.repository.CrudRepository;

import com.alibaba.dragonfly.supernode.common.domain.Task;

public interface TaskDao extends CrudRepository<Task, String> {

}
