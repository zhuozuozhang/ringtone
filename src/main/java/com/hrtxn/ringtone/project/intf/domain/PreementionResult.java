package com.hrtxn.ringtone.project.intf.domain;

import java.io.Serializable;

public class PreementionResult  implements Serializable {

    private String applyNumber;

    private String infoType;

    private resultBean data;

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

    public resultBean getData() {
        return data;
    }

    public void setData(resultBean data) {
        this.data = data;
    }

    public class resultBean{
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
