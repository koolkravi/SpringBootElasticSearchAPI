package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.service.MailService;

@SpringBootApplication(scanBasePackages = { "com.example" })
public class SearchApplication implements ApplicationRunner {
	private final Logger LOGGER = LoggerFactory.getLogger(SearchApplication.class);

	@Autowired
	MailService mailService;
	@Value("${elasticsearch.index}")
	private String index;

	@Value("${elasticsearch.type}")
	private String type;

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}

	/*
	 * This method is called when running application jar from command line with
	 * input parameter and value as json file e.g. java -jar searchmail-1.0.jar
	 * --input=D:\\enron_small.json
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.boot.ApplicationRunner#run(org.springframework.boot.
	 * ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		boolean containsOption = args.containsOption("input");
		if (containsOption) {
			String jsonFilePath = args.getOptionValues("input").get(0);
			LOGGER.info("jsonFilePath=", jsonFilePath);
			LOGGER.info("indexName, indexType= {},{} ", index, type);

			String out = mailService.bulkload(jsonFilePath, index, type);
			LOGGER.info("Index completed={}", out);
		}
	}
}
