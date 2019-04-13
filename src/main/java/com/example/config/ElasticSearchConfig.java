package com.example.config;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.es.repository")
public class ElasticSearchConfig {

	@Value("${elasticsearch.host:localhost}")
	public String esHost;
	@Value("${elasticsearch.port:9300}")
	public int esPort;

	@Value("${elasticsearch.clustername}")
	private String esClusterName;

	@Bean
	public Client client() throws Exception {

		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
				new TransportAddress(InetAddress.getByName(AppConstant.ES_HOST_LOCALHOST), AppConstant.ES_PORT_9300));
		return client;
	}

	// @Bean
	/*
	 * public Client client1() throws Exception { // Settings settings =
	 * Settings.builder().put("cluster.name", // esClusterName).build();
	 * Settings settings = Settings.builder().put("client.transport.sniff",
	 * true).build(); TransportClient client = new
	 * PreBuiltTransportClient(settings); client.addTransportAddress(new
	 * TransportAddress(InetAddress.getByName(esHost), esPort)); return client;
	 * }
	 */

	// @Bean
	/*
	 * public ElasticsearchOperations elasticsearchTemplate() throws Exception {
	 * return new ElasticsearchTemplate(client()); }
	 */

}
