package com.alpha.riderservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/rider")
public class RiderController {
	@Autowired
	private RiderService riderservice;

	@PostMapping("/createaccount")
	public ResponseStructure<RiderCreateRequestDto> createRiderAccount(
			@RequestBody RiderCreateRequestDto ridercreatedto) {
		return riderservice.createRiderAccount(ridercreatedto);
	}

	@PatchMapping("/{id}/updatevehicle")
	public ResponseStructure<RiderResponseDto> updateVehicle(@PathVariable int id,
			@RequestBody VehicleUpdateRequestDto vehicleupdatedto) {
		return riderservice.updateVehicle(id, vehicleupdatedto);
	}

	@PatchMapping("/{id}/{newstatus}/updatestatus")
	public ResponseStructure<RiderResponseDto> updateStatus(@PathVariable int id, @PathVariable String newstatus) {
		return riderservice.updateStatus(id, newstatus);
	}

	@PostMapping("/ridercurrentlocation")
	public void riderCurrentLocation(@RequestParam int riderId, @RequestParam String vehicleType,
			@RequestBody LocationCoordinates locationCordiantes) {
		riderservice.riderCurrentLocation(riderId, vehicleType, locationCordiantes);
	}

	@PostMapping("/findNearByRiders")
	public ResponseStructure<List<Integer>> findNearByRiders(@RequestBody FindNearByRiderRequestDto requestDto) {

		return riderservice.findNearByRiders(requestDto);
	}

	@GetMapping("/assignedrider")
	public ResponseStructure<Map<Object, Object>> getAssignedRide(@RequestParam int riderId) {
		return riderservice.getAssignedRide(riderId);
	}

	@PatchMapping("/acceptRide")
	public ResponseStructure<Map<Object, Object>> acceptRide(@RequestParam int riderId) {
		return riderservice.acceptRide(riderId);
	}

	@PatchMapping("/arrived")
	public ResponseStructure<Map<Object, Object>> riderArrived(@RequestParam int riderId) {
		return riderservice.riderArrived(riderId);
	}

	@PatchMapping("/startride")
	public ResponseStructure<Map<Object, Object>> startRide(@RequestBody StartRideRequestDto startRideRequestDto) {
		return riderservice.startRide(startRideRequestDto);
	}

	@PatchMapping("/completeride")
	public ResponseStructure<Map<Object, Object>> completeRide(@RequestParam int riderId) {
		return riderservice.completeRide(riderId);
	}
}
