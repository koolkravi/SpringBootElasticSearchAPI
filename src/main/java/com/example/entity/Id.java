package com.example.entity;

public class Id {
	String $oid;

	public String get$oid() {
		return $oid;
	}

	public void set$oid(String $oid) {
		this.$oid = $oid;
	}

	@Override
	public String toString() {
		return "Id [$oid=" + $oid + "]";
	}

	public Id() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Id(String $oid) {
		super();
		this.$oid = $oid;
	}
}
