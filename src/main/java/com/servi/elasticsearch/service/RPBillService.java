package com.servi.elasticsearch.service;

import com.servi.elasticsearch.document.RPBillDocument;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import static com.servi.elasticsearch.util.Constant.INDEX;
import static com.servi.elasticsearch.util.Constant.TYPE;

@Service
@Slf4j
public class RPBillService implements IRPBillService {
    private RestHighLevelClient client;

    @Autowired
    public RPBillService(RestHighLevelClient client) {
        this.client = client;
    }

    public String createRPBillDocument(RPBillDocument document) throws Exception {
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, document.getPk_bill())
                .source(convertProfileDocumentToMap(document));
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult().name();
    }

    public RPBillDocument findById(String id) throws Exception {
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();
        return convertMapToRPBillDocument(resultMap);
    }

    public String updateRPBill(RPBillDocument document) throws Exception {
        RPBillDocument resultDocument = findById(document.getPk_bill());
        UpdateRequest updateRequest = new UpdateRequest(
                INDEX,
                TYPE,
                resultDocument.getPk_bill());
        updateRequest.doc(convertProfileDocumentToMap(document));
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        return updateResponse
                .getResult()
                .name();
    }

    public List<RPBillDocument> findAll() throws Exception {
        SearchRequest searchRequest = buildSearchRequest(INDEX, TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }


    public List<RPBillDocument> findRPBillByBillno(String billno) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        searchRequest.types(TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("billno", billno)
                .operator(Operator.AND);
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        log.error(searchRequest.source().toString());
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    public String deleteRPBillDocument(String id) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        return response
                .getResult()
                .name();
    }

    @Override
    public List<RPBillDocument> boolQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        searchRequest.types(TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("pk_org.keyword", "0001Q110000000001NDN");
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("pk_org.keyword", "0001Q110000000001NDN");
        boolQueryBuilder1.should(termQueryBuilder1).should(termQueryBuilder2);
        boolQueryBuilder1.minimumShouldMatch(1);

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder3 = QueryBuilders.termQuery("pk_tradetype.keyword", "0001Q1100000000018TU");
        TermQueryBuilder termQueryBuilder4 = QueryBuilders.termQuery("pk_tradetype.keyword", "0001Q1100000000018TU");
        boolQueryBuilder2.should(termQueryBuilder3).should(termQueryBuilder4);
        boolQueryBuilder2.minimumShouldMatch(1);

        BoolQueryBuilder boolFilterBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder5 = QueryBuilders.termQuery("billno.keyword", "ZDYDJ202010100007");
        TermQueryBuilder termQueryBuilder6 = QueryBuilders.termQuery("billmaker.keyword", "1001Q1100000000006LD");
        boolFilterBuilder.must(termQueryBuilder5).must(termQueryBuilder6);

        RangeQueryBuilder amountRangeQueryBuilder = QueryBuilders.rangeQuery("amount").from(21).to(23);
        RangeQueryBuilder billdateRangeQueryBuilder = QueryBuilders.rangeQuery("billdate").from("2020-10-10 09:59:21").to("2020-10-10 09:59:23");

        MatchQueryBuilder likeQuery = QueryBuilders.matchQuery("pk_tradetype_name", "自定义工单");
        BoolQueryBuilder query = boolQueryBuilder
                .must(boolQueryBuilder1)
                .must(boolQueryBuilder2)
                .must(amountRangeQueryBuilder)
                .must(billdateRangeQueryBuilder)
                .filter(boolFilterBuilder)
                .must(likeQuery);

        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        log.error(searchRequest.source().toString());
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    private Map<String, Object> convertProfileDocumentToMap(RPBillDocument document) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = RPBillDocument.class.getDeclaredFields();

        String name = null;
        for (Field field : fields) {
            name = field.getName();
            if ("detail".equals(field.getName())) {
                map.putAll(document.getDetail());
            } else {
                field.setAccessible(true);
                map.put(name, field.get(document));
            }
        }
        return map;
    }

    private RPBillDocument convertMapToRPBillDocument(Map<String, Object> map) throws Exception {
        RPBillDocument document = new RPBillDocument();
        Field[] fields = RPBillDocument.class.getDeclaredFields();
        String name = null;
        for (Field field : fields) {
            name = field.getName();
            if (map.containsKey(name)) {
                field.setAccessible(true);
                field.set(document, map.get(name));
            }
        }
        return document;
    }

    private List<RPBillDocument> getSearchResult(SearchResponse response) throws Exception {
        SearchHit[] searchHit = response.getHits().getHits();
        List<RPBillDocument> profileDocuments = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            profileDocuments
                    .add(convertMapToRPBillDocument(hit
                            .getSourceAsMap()));
        }
        return profileDocuments;
    }

    private SearchRequest buildSearchRequest(String index, String type) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        return searchRequest;
    }
}