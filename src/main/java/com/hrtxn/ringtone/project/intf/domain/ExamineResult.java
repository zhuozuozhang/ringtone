package com.hrtxn.ringtone.project.intf.domain;


public class ExamineResult  {

    private String applyNumber;

    private String infoType;

    private ExamineBean data;

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

    public ExamineBean getData() {
        return data;
    }

    public void setData(ExamineBean data) {
        this.data = data;
    }

    public class ExamineBean{
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
