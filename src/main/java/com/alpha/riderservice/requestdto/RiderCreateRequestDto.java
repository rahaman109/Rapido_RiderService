package com.alpha.riderservice.requestdto;

public class RiderCreateRequestDto {

	private String name;
	private long mobno;
	private String gender;
	private String email;
	private String drivinglicense;
	private VehicleRequestDto vehicleRequestDto;

	public RiderCreateRequestDto() {
		super();
	}

	public RiderCreateRequestDto(String name, long mobno, String gender, String email, String drivinglicense,
			VehicleRequestDto vehicleRequestDto) {
		super();
		this.name = name;
		this.mobno = mobno;
		this.gender = gender;
		this.email = email;
		this.drivinglicense = drivinglicense;
		this.vehicleRequestDto = vehicleRequestDto;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDrivinglicense() {
		return drivinglicense;
	}

	public void setDrivinglicense(String drivinglicense) {
		this.drivinglicense = drivinglicense;
	}

	public VehicleRequestDto getVehicleDto() {
		return vehicleRequestDto;
	}

	public void setVehicleDto(VehicleRequestDto vehicleRequestDto) {
		this.vehicleRequestDto = vehicleRequestDto;
	}
}