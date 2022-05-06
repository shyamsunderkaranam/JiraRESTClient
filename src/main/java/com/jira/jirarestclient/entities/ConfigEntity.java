package com.jira.jirarestclient.entities;

import javax.persistence.*;

@Entity
@Table(name="AppConfigs")
public class ConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="CONFIG_KEY")
    private String configKey;

    @Column(name="CONFIG_VALUE" , length = 3000)
    private String configValue;

    public ConfigEntity(long id, String configKey, String configValue) {
        this.id = id;
        this.configKey = configKey;
        this.configValue = configValue;
    }
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }


    public String toString(){
        return "{"+"\""+getConfigKey()+"\":"+"\""+getConfigValue()+"\"}" ;
    }

}
