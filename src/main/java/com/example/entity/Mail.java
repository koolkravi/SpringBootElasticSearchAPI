package com.example.entity;

import java.util.List;

public class Mail {
	String id;
	Id idObj;
	String sender;
	List<String> recipients;
	List<String> cc;
	String text;
	String mid;

	String fpath;
	List<String> bcc;
	List<String> to;
	String replyto;
	String ctype;
	String fname;
	String date;
	String folder;
	String subject;

	public String getId() {
		return id;
	}

	public Id getIdObj() {
		return idObj;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdObj(Id idObj) {
		this.idObj = idObj;
	}

	public String getSender() {
		return sender;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public List<String> getCc() {
		return cc;
	}

	public String getText() {
		return text;
	}

	public String getMid() {
		return mid;
	}

	public String getFpath() {
		return fpath;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public List<String> getTo() {
		return to;
	}

	public String getReplyto() {
		return replyto;
	}

	public String getCtype() {
		return ctype;
	}

	public String getFname() {
		return fname;
	}

	public String getDate() {
		return date;
	}

	public String getFolder() {
		return folder;
	}

	public String getSubject() {
		return subject;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public void setFpath(String fpath) {
		this.fpath = fpath;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public void setReplyto(String replyto) {
		this.replyto = replyto;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "Mail [idStr=" + id + ", id=" + idObj + ", sender=" + sender + ", recipients=" + recipients + ", cc="
				+ cc + ", text=" + text + ", mid=" + mid + ", fpath=" + fpath + ", bcc=" + bcc + ", to=" + to
				+ ", replyto=" + replyto + ", ctype=" + ctype + ", fname=" + fname + ", date=" + date + ", folder="
				+ folder + ", subject=" + subject + "]";
	}

}