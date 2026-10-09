package com.alpha.riderservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alpha.riderservice.entity.LocationCoordinates;
import com.alpha.riderservice.requestdto.FindNearByRiderRequestDto;
import com.alpha.riderservice.requestdto.RiderCreateRequestDto;
import com.alpha.riderservice.requestdto.StartRideRequestDto;
import com.alpha.riderservice.requestdto.VehicleUpdateRequestDto;
import com.alpha.riderservice.responsedto.ResponseStructure;
import com.alpha.riderservice.responsedto.RiderResponseDto;
import com.alpha.riderservice.service.RiderService;

@RestController
public class RiderController {
	@Autowired
	private RiderService riderservice;

	@PostMapping("/rider/createaccount")
	public ResponseStructure<RiderCreateRequestDto> createRiderAccount(
			@RequestBody RiderCreateRequestDto ridercreatedto) {
		return riderservice.createRiderAccount(ridercreatedto);
	}

	@PatchMapping("/rider/{id}/updatevehicle")
	public ResponseStructure<RiderResponseDto> updateVehicle(@PathVariable int id,
			@RequestBody VehicleUpdateRequestDto vehicleupdatedto) {
		return riderservice.updateVehicle(id, vehicleupdatedto);
	}

	@PatchMapping("/rider/{id}/{newstatus}/updatestatus")
	public ResponseStructure<RiderResponseDto> updateStatus(@PathVariable int id, @PathVariable String newstatus) {
		return riderservice.updateStatus(id, newstatus);
	}

	@PostMapping("/rider/ridercurrentlocation")
	public void riderCurrentLocation(@RequestParam int riderId, @RequestParam String vehicleType,
			@RequestBody LocationCoordinates locationCordiantes) {
		riderservice.riderCurrentLocation(riderId, vehicleType, locationCordiantes);
	}

	@PostMapping("/rider/findNearByRiders")
	public ResponseStructure<List<Integer>> findNearByRiders(@RequestBody FindNearByRiderRequestDto requestDto) {

		return riderservice.findNearByRiders(requestDto);
	}

	@GetMapping("/rider/assignedrider")
	public ResponseStructure<Map<Object, Object>> getAssignedRide(@RequestParam int riderId) {
		return riderservice.getAssignedRide(riderId);
	}

	@PatchMapping("/rider/acceptRide")
	public ResponseStructure<Map<Object, Object>> acceptRide(@RequestParam int riderId) {
		return riderservice.acceptRide(riderId);
	}

	@PatchMapping("/rider/arrived")
	public ResponseStructure<Map<Object, Object>> riderArrived(@RequestParam int riderId) {
		return riderservice.riderArrived(riderId);
	}

	@PatchMapping("/rider/startride")
	public ResponseStructure<Map<Object, Object>> startRide(@RequestBody StartRideRequestDto startRideRequestDto) {
		return riderservice.startRide(startRideRequestDto);
	}

	@PatchMapping("rider/completeride")
	public ResponseStructure<Map<Object, Object>> completeRide(@RequestParam int riderId) {
		return riderservice.completeRide(riderId);
	}
}
