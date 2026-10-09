package com.alpha.riderservice.responsedto;

public class RiderResponseDto {

	private int id;
	private String name;
	private long mobno;
	private String status;

	public RiderResponseDto(int id, String name, long mobno, String status) {
		super();
		this.id = id;
		this.name = name;
		this.mobno = mobno;
		this.status = status;
	}

	public RiderResponseDto() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobno() {
		return mobno;
	}

	public void setMobno(long mobno) {
		this.mobno = mobno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
