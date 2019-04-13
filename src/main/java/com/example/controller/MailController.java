package com.example.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Mail;
import com.example.service.MailService;

//@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:4200" })
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mails")
public class MailController {

	@Autowired
	MailService mailService;

	@Autowired
	Client client;

	/**
	 * This method searches mails by Id
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/{id}")
	Mail searchById(@PathVariable String id) throws IOException {
		return mailService.serachById(id);
	}

	/**
	 * This method searches mails by multiple fields value
	 * 
	 * @param subject
	 * @return
	 * @throws IOException
	 */

	@GetMapping("/search/{freeText}/{from}/{size}")
	ResponseEntity<List<Mail>> serach(@PathVariable String freeText, @PathVariable Integer from,
			@PathVariable Integer size) throws IOException {
		ResponseEntity<List<Mail>> list = mailService.serach(freeText, from, size);
		return list;
	}

	/**
	 * This method returns all mails
	 * 
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/{from}/{size}")
	@ResponseBody
	public ResponseEntity<List<Mail>> getAllMails(@PathVariable Integer from, @PathVariable Integer size)
			throws IOException {
		return mailService.serach(null, from, size);
	}

	/**
	 * This method creates mails
	 * 
	 * @param mail
	 * @return
	 * @throws IOException
	 */
	@PostMapping()
	public String create(@RequestBody Mail mail) throws IOException {
		return mailService.create(mail);
	}

	@PutMapping("/{id}")
	Map<String, Object> update(@RequestBody Mail mail, @PathVariable String id) {

		return mailService.update(id, mail);
	}

	/**
	 * This method deletes mail by ID
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@DeleteMapping("/{id}")
	public String delete(@PathVariable String id) throws IOException {
		return mailService.delete(id);
	}

	/*
	 * @GetMapping("/bulkload/{id}") public String bulkload(@PathVariable String
	 * id) throws IOException, ExecutionException, InterruptedException { return
	 * null;// mailService.bulkload( filePath, fileName); }
	 */

}
