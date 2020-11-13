package com.servi.elasticsearch.service;

import com.servi.elasticsearch.document.RPBillDocument;

import java.util.List;

public interface IRPBillService {

    public String createRPBillDocument(RPBillDocument document) throws Exception;

    public RPBillDocument findById(String id) throws Exception;

    public String updateRPBill(RPBillDocument document) throws Exception;

    public List<RPBillDocument> findAll() throws Exception;

    public List<RPBillDocument> findRPBillByBillno(String billno) throws Exception;

    public String deleteRPBillDocument(String id) throws Exception;

    public List<RPBillDocument> boolQuery() throws Exception;
}
