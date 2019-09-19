package com.hrtxn.ringtone.project.intf.domain;

import java.io.Serializable;

public class ExamineResult implements Serializable {

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
        private String approveStatus ;
        private String approveComment ;
        private String approveTime ;

        public String getApproveStatus() {
            return approveStatus;
        }

        public void setApproveStatus(String approveStatus) {
            this.approveStatus = approveStatus;
        }

        public String getApproveComment() {
            return approveComment;
        }

        public void setApproveComment(String approveComment) {
            this.approveComment = approveComment;
        }

        public String getApproveTime() {
            return approveTime;
        }

        public void setApproveTime(String approveTime) {
            this.approveTime = approveTime;
        }
    }

}
