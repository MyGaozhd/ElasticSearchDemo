package com.servi.elasticsearch.document;

import lombok.Data;

import java.util.Map;

@Data
public class RPBillDocument {
    private String busiid;
    private long amount;
    private String billdate;
    private String billmaker;
    private String billno;
    private String billtypecode;
    private String creationtime;
    private String creator;
    private String defitem1;
    private String defitem2;
    private String defitem3;
    private String defitem4;
    private String defitem5;
    private String defitem6;
    private String defitem7;
    private String defitem8;
    private String defitem9;
    private String defitem10;
    private String defitem11;
    private String defitem12;
    private String defitem13;
    private String defitem14;
    private String defitem15;
    private String defitem16;
    private String defitem17;
    private String defitem18;
    private String defitem19;
    private String defitem20;
    private int dr;
    private String modifiedtime;
    private String modifier;
    private String operator;
    private String orgamount;
    private String pk_bill;
    private String pk_currtype;
    private String pk_group;
    private String pk_org;
    private String pk_tradetype;
    private String remark;
    private String rescan;
    private String transtypecode;
    private String ts;
    private Map<String, Object> detail;
}
