package com.example.util;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.config.AppConstant;
import com.example.entity.Mail;
import com.example.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@Component
public class BulkLoadUtil {
	private final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	@Autowired
	Client client;

	private static int numberOfRecords = 1;
	private static int count = 0;
	private static int noOfBatch = 1;

	public void JsonBulkImport(String jsonFilePath, String indexName, String indexTypeName)
			throws IOException, ExecutionException, InterruptedException {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Stream<String> lines = Files.lines(Paths.get(jsonFilePath));
			LOGGER.info("<!-----Read all lines as a Stream-----!>");
			lines.forEach(s -> {
				try {
					s = s.replace("_id", "idObj");
					Mail document = mapper.readValue(s, Mail.class);
					createIndexHelper(document, bulkRequest, indexName, indexTypeName);
					// System.out.println(s);

				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			lines.close();
		} catch (IOException io) {
			io.printStackTrace();
		}

		if (count != 0) { // add remaining documents to ES
			addDocumentToESCluser(bulkRequest, noOfBatch, count);
		}

		LOGGER.info("Total Document Indexed : {}", numberOfRecords);
	}

	private void createIndexHelper(Mail document, BulkRequestBuilder bulkRequest, String indexName,
			String indexTypeName) {
		try {
			XContentBuilder xContentBuilder = jsonBuilder().startObject()
					.field(AppConstant.ID, UUID.randomUUID().toString()).field(AppConstant.SENDER, document.getSender())
					.field(AppConstant.RECIPIENTS, document.getRecipients()).field(AppConstant.CC, document.getCc())
					.field(AppConstant.TEXT, document.getText()).field(AppConstant.MID, document.getMid())
					.field(AppConstant.F_PATH, document.getFpath()).field(AppConstant.BCC, document.getBcc())
					.field(AppConstant.TO, document.getTo()).field(AppConstant.REPLY_TO, document.getReplyto())
					.field(AppConstant.C_TYPE, document.getCtype()).field(AppConstant.F_NAME, document.getFname())
					.field(AppConstant.DATE, document.getDate()).field(AppConstant.FOLDER, document.getFolder())
					.field(AppConstant.SUBJECT, document.getSubject()).endObject();
			bulkRequest.add(client.prepareIndex(indexName, indexTypeName, String.valueOf(numberOfRecords))
					.setSource(xContentBuilder));
			if (count == 50_000) {
				addDocumentToESCluser(bulkRequest, noOfBatch, count);
				noOfBatch++;
				count = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// skip records if wrong date in input file
		}
		numberOfRecords++;
		count++;
	}

	public void JsonBulkImport1(File jsonFilePath, String indexName, String indexTypeName)
			throws IOException, ExecutionException, InterruptedException {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		int count = 0, noOfBatch = 1;
		// initialize jsonReader class by passing reader
		JsonReader jsonReader = new JsonReader(
				new InputStreamReader(new FileInputStream(jsonFilePath), StandardCharsets.UTF_8));
		Gson gson = new GsonBuilder().create();
		jsonReader.beginArray(); // start of json array
		int numberOfRecords = 1;
		while (jsonReader.hasNext()) { // next json array element
			Mail document = gson.fromJson(jsonReader, Mail.class);
			// do something real
			try {
				XContentBuilder xContentBuilder = jsonBuilder().startObject().field(AppConstant.ID, document.getId())
						.field(AppConstant.SENDER, document.getSender())
						.field(AppConstant.RECIPIENTS, document.getRecipients()).field(AppConstant.CC, document.getCc())
						.field(AppConstant.TEXT, document.getText()).field(AppConstant.MID, document.getMid())
						.field(AppConstant.F_PATH, document.getFpath()).field(AppConstant.BCC, document.getBcc())
						.field(AppConstant.TO, document.getTo()).field(AppConstant.REPLY_TO, document.getReplyto())
						.field(AppConstant.C_TYPE, document.getCtype()).field(AppConstant.F_NAME, document.getFname())
						.field(AppConstant.DATE, document.getDate()).field(AppConstant.FOLDER, document.getFolder())
						.field(AppConstant.SUBJECT, document.getSubject()).endObject();

				// bulkRequest.add(new IndexRequest(INDEX, TYPE,
				// String.valueOf(numberOfRecords)).source(xContentBuilder));
				bulkRequest.add(client.prepareIndex(indexName, indexTypeName, String.valueOf(numberOfRecords))
						.setSource(xContentBuilder));
				if (count == 50_000) {
					addDocumentToESCluser(bulkRequest, noOfBatch, count);
					noOfBatch++;
					count = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// skip records if wrong date in input file
			}
			numberOfRecords++;
			count++;
		}
		jsonReader.endArray();
		if (count != 0) { // add remaining documents to ES
			addDocumentToESCluser(bulkRequest, noOfBatch, count);
		}
		LOGGER.info("Total Document Indexed : {} " + numberOfRecords);
	}

	public void addDocumentToESCluser(BulkRequestBuilder bulkRequest, int noOfBatch, int count) {
		if (count == 0) {
			// org.elasticsearch.action.ActionRequestValidationException:
			// Validation Failed: 1: no requests added;
			return;
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			LOGGER.info("Bulk Indexing failed for Batch : " + noOfBatch);
			// process failures by iterating through each bulk response item
			int numberOfDocFailed = 0;
			Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
			while (iterator.hasNext()) {
				BulkItemResponse response = iterator.next();
				if (response.isFailed()) {
					// System.out.println("Failed Id : "+response.getId());
					numberOfDocFailed++;
				}
			}
			LOGGER.info("Out of {} documents, {} + documents failed", count, numberOfDocFailed);
			LOGGER.info(bulkResponse.buildFailureMessage());
		} else {
			LOGGER.info("Bulk Indexing Completed for batch : {}", noOfBatch);
		}
	}

	public void refreshIndices(String indexName) {
		// Refresh before search, so you will get latest indices result
		client.admin().indices().prepareRefresh(indexName).get();
	}

	public Long search(String indexName, String indexTypeName) {
		SearchResponse response = client.prepareSearch(indexName).setTypes(indexTypeName).get();
		// MatchAllDocQuery
		Long hits = response.getHits().getTotalHits();
		LOGGER.info("Total Hits : {} ", hits);
		// System.out.println(response);
		return hits;
	}
}
