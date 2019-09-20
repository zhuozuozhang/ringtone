package com.hrtxn.ringtone.project.intf.domain;


public class PreementionResult   {

    private String applyNumber;

    private String infoType;

    private PreementionBean data;

    public String getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(String applyNumber) {
        this.applyNumber = applyNumber;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public PreementionBean getData() {
        return data;
    }

    public void setData(PreementionBean data) {
        this.data = data;
    }

    public class PreementionBean{
        private String occupyCompany;
        private String auditStatus;
        private String applyDate;
        private String platform;

        public String getOccupyCompany() {
            return occupyCompany;
        }

        public void setOccupyCompany(String occupyCompany) {
            this.occupyCompany = occupyCompany;
        }

        public String getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(String auditStatus) {
            this.auditStatus = auditStatus;
        }

        public String getApplyDate() {
            return applyDate;
        }

        public void setApplyDate(String applyDate) {
            this.applyDate = applyDate;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }

}
