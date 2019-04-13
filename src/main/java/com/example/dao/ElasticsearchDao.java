package com.example.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpHost;
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
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.config.AppConstant;
import com.example.entity.Mail;
import com.example.util.BulkLoadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ElasticsearchDao {
	private final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDao.class);

	private final ObjectMapper mapper;
	private final RestHighLevelClient restHighLevelClient;

	@Autowired
	BulkLoadUtil bulkLoadUtil;

	@Value("${elasticsearch.index}")
	private String index;

	@Value("${elasticsearch.type}")
	private String type;

	public ElasticsearchDao(ObjectMapper mapper) {
		this.restHighLevelClient = new RestHighLevelClient(
				RestClient.builder(new HttpHost(AppConstant.ES_HOST_LOCALHOST, AppConstant.ES_PORT_9200, "http")));
		this.mapper = mapper;
	}

	/**
	 * This method is generic method to search document on any fields
	 * 
	 * @param query
	 * @param from
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public SearchResponse search(QueryBuilder query, Integer from, Integer size) throws IOException {
		SearchRequest searchRequest = new SearchRequest(index);

		if (null != query) {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			// searchSourceBuilder.query(QueryBuilders.matchAllQuery()).from(from).size(size).timeout(new
			// TimeValue(60, TimeUnit.SECONDS));
			searchSourceBuilder.query(query).from(from).size(size);
			searchRequest.source(searchSourceBuilder);
		}
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		return searchResponse;
	}

	public GetResponse serachById(String id) throws IOException {
		GetRequest getRequest = new GetRequest(index, type, id);
		GetResponse getResponse = null;
		getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		return getResponse;
	}

	public String create(Mail mail) throws IOException {
		Map<?, ?> dataMap = mapper.convertValue(mail, Map.class);
		IndexRequest indexRequest = new IndexRequest(index, type, mail.getId()).source(dataMap);
		IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		String id = response.getId();
		return id;
	}

	public Map<String, Object> update(String id, Mail mail) {

		// Fetch Object after its update
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).fetchSource(true);
		Map<String, Object> error = new HashMap<>();
		error.put("Error", "Unable to update mail");

		try {
			String mailJson = mapper.writeValueAsString(mail);
			updateRequest.doc(mailJson, XContentType.JSON);
			UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
			Map<String, Object> sourceAsMap = updateResponse.getGetResult().sourceAsMap();
			return sourceAsMap;
		} catch (JsonProcessingException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return error;
	}

	public String delete(String id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
		DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		String deletedId = deleteResponse.getId();
		LOGGER.info("Delete query id: {} " + id);
		return deletedId;
	}

	/**
	 * This method is used to index documents from a file as input parameter.
	 * 
	 * @param jsonFilePath
	 * @param indexName
	 * @param indexTypeName
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Long bulkload(String jsonFilePath, String indexName, String indexTypeName)
			throws IOException, ExecutionException, InterruptedException {

		// index multiple document
		bulkLoadUtil.JsonBulkImport(jsonFilePath, indexName, indexTypeName);

		// refresh indices
		bulkLoadUtil.refreshIndices(indexName);

		Long hits = bulkLoadUtil.search(indexName, indexTypeName);
		return hits;
	}
}
