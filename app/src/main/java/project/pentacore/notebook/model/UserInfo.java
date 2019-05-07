package project.pentacore.notebook.model;

public class UserInfo {
    private final static String TAG = UserInfo.class.getSimpleName();

    private String serviceType;
    private String idx;
    private String id;
    private String email;
    private String serviceTypeIcon;
    private String profileURL;

    public static class Builder {
        private String serviceType;
        private String idx;
        private String id;
        private String email;
        private String serviceTypeIcon;
        private String profileURL;

        public Builder serviceType(String serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public Builder idx(String idx) {
            this.idx = idx;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder serviceTypeIcon(String serviceTypeIcon) {
            this.serviceTypeIcon = serviceTypeIcon;
            return this;
        }

        public Builder profileURL(String profileURL) {
            this.profileURL = profileURL;
            return this;
        }

        public UserInfo build() {
            return new UserInfo(this);
        }
    }

    private UserInfo(Builder builder) {
        this.serviceType = builder.serviceType;
        this.email = builder.email;
        this.idx = builder.idx;
        this.id = builder.id;
        this.serviceTypeIcon = builder.serviceTypeIcon;
        this.profileURL = builder.profileURL;
    }

    public String getIdx() {
        return idx;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getServiceTypeIcon() {
        return serviceTypeIcon;
    }

    public String getProfileURL() {
        return profileURL;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserInfo{");
        sb.append("serviceType='").append(serviceType).append('\'');
        sb.append(", idx='").append(idx).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", serviceTypeIcon='").append(serviceTypeIcon).append('\'');
        sb.append(", profileURL='").append(profileURL).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
