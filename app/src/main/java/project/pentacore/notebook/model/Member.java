package project.pentacore.notebook.model;

import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("id") private String id;
    @SerializedName("service_type") private String serviceType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
