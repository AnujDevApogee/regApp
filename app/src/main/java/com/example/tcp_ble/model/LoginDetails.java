package com.example.tcp_ble.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginDetails {

    @SerializedName("Login_id")
    @Expose
    private String loginId;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("Username")
    @Expose
    private String username;

    @SerializedName("Phone")
    @Expose
    private String phone;

    @SerializedName("Password")
    @Expose
    private String password;

    @SerializedName("Project_organization_map_id")
    @Expose
    private String projectOrganizationMapId;

    @SerializedName("Key_person_id")
    @Expose
    private String keyPersonId;

    @SerializedName("Project_id")
    @Expose
    private String projectId;

    @SerializedName("Organization_id")
    @Expose
    private String organizationId;

    @SerializedName("Organization_name")
    @Expose
    private String organizationName;

    @SerializedName("Project_name")
    @Expose
    private String projectName;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectOrganizationMapId() {
        return projectOrganizationMapId;
    }

    public void setProjectOrganizationMapId(String projectOrganizationMapId) {
        this.projectOrganizationMapId = projectOrganizationMapId;
    }

    public String getKeyPersonId() {
        return keyPersonId;
    }

    public void setKeyPersonId(String keyPersonId) {
        this.keyPersonId = keyPersonId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
