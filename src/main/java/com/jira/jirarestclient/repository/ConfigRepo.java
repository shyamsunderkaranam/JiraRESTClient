package com.jira.jirarestclient.repository;

import com.jira.jirarestclient.entities.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends JpaRepository<ConfigEntity,Long> {
}