package com.example.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.dao.ElasticsearchDao;
import com.example.entity.Id;
import com.example.entity.Mail;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MailService {

	private final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	@Autowired
	ElasticsearchDao elasticsearchDao;

	/**
	 * This method searches all mails bases on free test matching multiple
	 * fields value
	 * 
	 * @param freeText
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<List<Mail>> serach(String freeText, Integer from, Integer size) throws IOException {
		LOGGER.info("from {} ,size {} ", from, size);
		QueryBuilder multiMatchQueryBuilder = null;
		if (null != freeText) {
			multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(freeText, "id", "sender", "subject", "text", "date");
		}
		SearchResponse searchResponse = elasticsearchDao.search(multiMatchQueryBuilder, from, size);
		SearchHits hits = searchResponse.getHits();
		Long totalHits = hits.getTotalHits();
		LOGGER.info("{} totalHits ", totalHits);

		List<SearchHit> searchHits = Arrays.asList(hits.getHits());
		LOGGER.info("{} result hits ", searchHits.size());
		List<Mail> results = new ArrayList<Mail>();
		ObjectMapper mapper = new ObjectMapper();
		searchHits.forEach(hit -> {
			try {
				String responseSource = hit.getSourceAsString();
				Mail mail = mapper.readValue(responseSource, Mail.class);
				setIdToMailObject(hit.getId(), mail);
				results.add(mail);
			} catch (JsonParseException e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			} catch (JsonMappingException e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
		});
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("TotalHits", String.valueOf(totalHits));

		return ResponseEntity.ok().headers(responseHeaders).body(results);
	}

	/**
	 * This method is used to search mail by Id field
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public Mail serachById(String id) throws IOException {
		GetResponse response = elasticsearchDao.serachById(id);
		ObjectMapper mapper = new ObjectMapper();
		String responseSource = response.getSourceAsString();
		Mail mail = mapper.readValue(responseSource, Mail.class);
		setIdToMailObject(response.getId(), mail);
		return mail;

	}

	/**
	 * This method is used to build index document using command line JSON file
	 * input .
	 * 
	 * @param jsonFilePath
	 * @param indexName
	 * @param indexTypeName
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public String bulkload(String jsonFilePath, String indexName, String indexTypeName)
			throws IOException, ExecutionException, InterruptedException {
		Long hits = elasticsearchDao.bulkload(jsonFilePath, indexName, indexTypeName);
		return hits + "";
	}

	/**
	 * @param hit
	 * @param mail
	 */
	private void setIdToMailObject(String id, Mail mail) {
		mail.setIdObj(new Id(mail.getId()));
		mail.setId(id);
	}

	public String create(Mail mail) throws IOException {
		String id = elasticsearchDao.create(mail);
		return "created  " + id;
	}

	public Map<String, Object> update(String id, Mail mail) {
		return elasticsearchDao.update(id, mail);
	}

	public String delete(String id) throws IOException {
		String deletedId = elasticsearchDao.delete(id);
		return "deleted " + deletedId;
	}

}
