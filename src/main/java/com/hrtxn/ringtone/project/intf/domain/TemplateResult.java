package com.hrtxn.ringtone.project.intf.domain;


public class TemplateResult {

    private String applyNumber;

    private String infoType;

    private TemplateBean data;

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

    public TemplateBean getData() {
        return data;
    }

    public void setData(TemplateBean data) {
        this.data = data;
    }

    public class TemplateBean{

        private String corpBusinessScope;
        private String checkCompanySuccess;
        private String regStatus;
        private String legalHandlerAddress;
        private String checkLegalCardFileSuccess;
        private String corpSocietyNo;
        private String legalIdentityId;
        private String legalHandlerName;
        private String legalHandlerIdentityId;
        private String legalAddress;
        private String templateUrl;
        private String legalName;
        private String corpCompanyDetail;
        private String corpOfficeDetail;
        private String checkOperatorCardFileSuccess;
        private String taskId;

        public String getCorpBusinessScope() {
            return corpBusinessScope;
        }

        public void setCorpBusinessScope(String corpBusinessScope) {
            this.corpBusinessScope = corpBusinessScope;
        }

        public String getCheckCompanySuccess() {
            return checkCompanySuccess;
        }

        public void setCheckCompanySuccess(String checkCompanySuccess) {
            this.checkCompanySuccess = checkCompanySuccess;
        }

        public String getRegStatus() {
            return regStatus;
        }

        public void setRegStatus(String regStatus) {
            this.regStatus = regStatus;
        }

        public String getLegalHandlerAddress() {
            return legalHandlerAddress;
        }

        public void setLegalHandlerAddress(String legalHandlerAddress) {
            this.legalHandlerAddress = legalHandlerAddress;
        }

        public String getCheckLegalCardFileSuccess() {
            return checkLegalCardFileSuccess;
        }

        public void setCheckLegalCardFileSuccess(String checkLegalCardFileSuccess) {
            this.checkLegalCardFileSuccess = checkLegalCardFileSuccess;
        }

        public String getCorpSocietyNo() {
            return corpSocietyNo;
        }

        public void setCorpSocietyNo(String corpSocietyNo) {
            this.corpSocietyNo = corpSocietyNo;
        }

        public String getLegalIdentityId() {
            return legalIdentityId;
        }

        public void setLegalIdentityId(String legalIdentityId) {
            this.legalIdentityId = legalIdentityId;
        }

        public String getLegalHandlerName() {
            return legalHandlerName;
        }

        public void setLegalHandlerName(String legalHandlerName) {
            this.legalHandlerName = legalHandlerName;
        }

        public String getLegalHandlerIdentityId() {
            return legalHandlerIdentityId;
        }

        public void setLegalHandlerIdentityId(String legalHandlerIdentityId) {
            this.legalHandlerIdentityId = legalHandlerIdentityId;
        }

        public String getLegalAddress() {
            return legalAddress;
        }

        public void setLegalAddress(String legalAddress) {
            this.legalAddress = legalAddress;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }

        public void setTemplateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
        }

        public String getLegalName() {
            return legalName;
        }

        public void setLegalName(String legalName) {
            this.legalName = legalName;
        }

        public String getCorpCompanyDetail() {
            return corpCompanyDetail;
        }

        public void setCorpCompanyDetail(String corpCompanyDetail) {
            this.corpCompanyDetail = corpCompanyDetail;
        }

        public String getCorpOfficeDetail() {
            return corpOfficeDetail;
        }

        public void setCorpOfficeDetail(String corpOfficeDetail) {
            this.corpOfficeDetail = corpOfficeDetail;
        }

        public String getCheckOperatorCardFileSuccess() {
            return checkOperatorCardFileSuccess;
        }

        public void setCheckOperatorCardFileSuccess(String checkOperatorCardFileSuccess) {
            this.checkOperatorCardFileSuccess = checkOperatorCardFileSuccess;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
    }

}
