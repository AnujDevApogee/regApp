package com.apogee.registration.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Key_person_id")
    val keyPersonId: String,
    @SerializedName("Login_id")
    val loginId: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Organization_id")
    val organizationId: String,
    @SerializedName("Organization_name")
    val organizationName: String,
    @SerializedName("Password")
    val password: String,
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("Project_id")
    val projectId: String,
    @SerializedName("Project_name")
    val projectName: String,
    @SerializedName("Project_organization_map_id")
    val projectOrganizationMapId: String,
    @SerializedName("Username")
    val username: String
)