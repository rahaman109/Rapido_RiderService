package com.alpha.riderservice.requestdto;

public class StartRideRequestDto {

	private int riderId;
	private String otp;

	public StartRideRequestDto(int riderId, String otp) {
		super();
		this.riderId = riderId;
		this.otp = otp;
	}

	public int getRiderId() {
		return riderId;
	}

	public void setRiderId(int riderId) {
		this.riderId = riderId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public StartRideRequestDto() {
		super();
	}

}
