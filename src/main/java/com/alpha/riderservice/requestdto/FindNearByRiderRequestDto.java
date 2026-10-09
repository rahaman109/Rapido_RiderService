package com.alpha.riderservice.requestdto;

import com.alpha.riderservice.entity.LocationCoordinates;

public class FindNearByRiderRequestDto {

	private String vehicleType;
	private LocationCoordinates locationCoordinates;

	public FindNearByRiderRequestDto(String vehicleType, LocationCoordinates locationCoordinates) {
		super();
		this.vehicleType = vehicleType;
		this.locationCoordinates = locationCoordinates;
	}

	public FindNearByRiderRequestDto() {
		super();
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public LocationCoordinates getLocationCoordinates() {
		return locationCoordinates;
	}

	public void setLocationCoordinates(LocationCoordinates locationCoordinates) {
		this.locationCoordinates = locationCoordinates;
	}

}
