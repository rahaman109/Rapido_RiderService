package com.alpha.riderservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alpha.riderservice.entity.LocationCoordinates;
import com.alpha.riderservice.entity.Rider;
import com.alpha.riderservice.entity.Vehicle;
import com.alpha.riderservice.exception.RiderNotFoundException;
import com.alpha.riderservice.repository.RiderRepository;
import com.alpha.riderservice.requestdto.FindNearByRiderRequestDto;
import com.alpha.riderservice.requestdto.RiderCreateRequestDto;
import com.alpha.riderservice.requestdto.StartRideRequestDto;
import com.alpha.riderservice.requestdto.VehicleUpdateRequestDto;
import com.alpha.riderservice.responsedto.ResponseStructure;
import com.alpha.riderservice.responsedto.RiderResponseDto;

@Service
public class RiderService {

	@Autowired
	private RiderRepository riderrepository;

	@Autowired
	private RedisTemplate<String, Object> redistemplate;

	@Autowired
	private RestTemplate resttemplate;

	public ResponseStructure<RiderCreateRequestDto> createRiderAccount(RiderCreateRequestDto ridercreatedto) {

		Rider rider = new Rider();
		Vehicle vehicle = new Vehicle();

		ResponseStructure<RiderCreateRequestDto> responsestructure = new ResponseStructure<RiderCreateRequestDto>();

		rider.setName(ridercreatedto.getName());
		rider.setMobno(ridercreatedto.getMobno());
		rider.setGender(ridercreatedto.getGender());
		rider.setDrivinglicense(ridercreatedto.getDrivinglicense());

		vehicle.setType(ridercreatedto.getVehicleDto().getType());
		vehicle.setVname(ridercreatedto.getVehicleDto().getVname());
		vehicle.setModel(ridercreatedto.getVehicleDto().getModel());
		vehicle.setStatus(ridercreatedto.getVehicleDto().getStatus());
		vehicle.setVehicleno(ridercreatedto.getVehicleDto().getVehicleno());

		rider.setVehicle(vehicle);

		riderrepository.save(rider);

		responsestructure.setStatusCode(HttpStatus.CREATED.value());
		responsestructure.setMessage("RIDER ACCOUNT SUCCESSFULLY CREATED");
		responsestructure.setData(ridercreatedto);

		return responsestructure;
	}

	public ResponseStructure<RiderResponseDto> updateVehicle(int id, VehicleUpdateRequestDto vehicleupdatedto) {

		Rider rider = riderrepository.findById(id).orElseThrow(() -> new RiderNotFoundException());

		rider.getVehicle().setType(vehicleupdatedto.getType());
		rider.getVehicle().setVname(vehicleupdatedto.getVname());
		rider.getVehicle().setModel(vehicleupdatedto.getModel());
		rider.getVehicle().setVehicleno(vehicleupdatedto.getVehicleno());
		rider.getVehicle().setStatus(vehicleupdatedto.getStatus());

		riderrepository.save(rider);

		RiderResponseDto riderResponseDto = new RiderResponseDto();

		riderResponseDto.setId(rider.getId());
		riderResponseDto.setName(rider.getName());
		riderResponseDto.setMobno(rider.getMobno());
		riderResponseDto.setStatus(rider.getStatus());

		return new ResponseStructure<>(HttpStatus.OK.value(), "Update Vehicle", riderResponseDto);
	}

	public ResponseStructure<RiderResponseDto> updateStatus(int id, String newstatus) {

		Rider rider = riderrepository.findById(id).orElseThrow(() -> new RiderNotFoundException());

		rider.setStatus(newstatus);

		riderrepository.save(rider);

		RiderResponseDto riderResponseDto = new RiderResponseDto();

		riderResponseDto.setId(rider.getId());
		riderResponseDto.setName(rider.getName());
		riderResponseDto.setMobno(rider.getMobno());
		riderResponseDto.setStatus(rider.getStatus());

		return new ResponseStructure<>(HttpStatus.OK.value(), "Rider status updated", riderResponseDto);
	}

	// rider current location
	public void riderCurrentLocation(int riderId, String vehicleType, LocationCoordinates locationCordiantes) {

		String key = "RIDER LOCATION:" + vehicleType;

		double latitude = locationCordiantes.getLatitude();
		double longitude = locationCordiantes.getLongitude();

		Point point = new Point(longitude, latitude);

		redistemplate.opsForGeo().add(key, point, String.valueOf(riderId));

	}

	// get all assigned rides
	public ResponseStructure<Map<Object, Object>> getAssignedRide(int riderId) {
		riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		String riderKey = "rider:" + riderId + ":requests";
		Map<Object, Object> rides = redistemplate.opsForHash().entries(riderKey);

		if (rides.isEmpty()) {
			throw new RuntimeException("No assigned ride found");
		}

		for (Map.Entry<Object, Object> entry : rides.entrySet()) {
			String bookingId = String.valueOf(entry.getKey());

			if (!bookingId.matches("\\d+")) {
				continue;
			}

			String rideKey = String.valueOf(entry.getValue()).replace("\"", "").trim();
			Map<Object, Object> booking = redistemplate.opsForHash().entries(rideKey);

			if (!booking.isEmpty() && "ASSIGNED".equals(String.valueOf(booking.get("status")).replace("\"", ""))) {
				return new ResponseStructure<>(HttpStatus.OK.value(), "Assigned Ride", booking);
			}
		}

		throw new RuntimeException("No assigned ride found");
	}

	// Find nearby riders
	public ResponseStructure<List<Integer>> findNearByRiders(FindNearByRiderRequestDto requestDto) {

		String vehicleType = requestDto.getVehicleType();

		double latitude = requestDto.getLocationCoordinates().getLatitude();

		double longitude = requestDto.getLocationCoordinates().getLongitude();

		String key = "RIDER LOCATION:" + vehicleType;

		Circle searchArea = new Circle(new Point(longitude, latitude), new Distance(5, Metrics.KILOMETERS));

		GeoOperations<String, Object> geoOperations = redistemplate.opsForGeo();

		var results = geoOperations.radius(key, searchArea,
				RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending());

		List<Integer> riderIds = new ArrayList<>();

		if (results != null) {
			results.forEach(result -> {
				Object riderId = result.getContent().getName();

				if (riderId != null) {
					riderIds.add(Integer.parseInt(riderId.toString()));
				}
			});
		}

		return new ResponseStructure<>(HttpStatus.OK.value(), "Nearby riders found", riderIds);
	}

	// rider accepting the ride
	public ResponseStructure<Map<Object, Object>> acceptRide(int riderId) {
		riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		String riderKey = "rider:" + riderId + ":requests";
		Map<Object, Object> rides = redistemplate.opsForHash().entries(riderKey);

		if (rides.isEmpty()) {
			throw new RuntimeException("No Assigned Ride Found");
		}

		String rideKey = null;

		for (Map.Entry<Object, Object> entry : rides.entrySet()) {
			String bookingId = String.valueOf(entry.getKey());

			if (!bookingId.matches("\\d+")) {
				continue;
			}

			String currentRideKey = String.valueOf(entry.getValue()).replace("\"", "").trim();
			Map<Object, Object> booking = redistemplate.opsForHash().entries(currentRideKey);

			if (!booking.isEmpty() && "ASSIGNED".equals(String.valueOf(booking.get("status")).replace("\"", ""))) {
				rideKey = currentRideKey;
				break;
			}
		}

		if (rideKey == null) {
			throw new RuntimeException("No assigned ride found");
		}

		redistemplate.opsForHash().put(rideKey, "status", "ACCEPTED");

		Map<Object, Object> booking = redistemplate.opsForHash().entries(rideKey);

		return new ResponseStructure<>(HttpStatus.OK.value(), "Ride Accepted", booking);
	}

	// rider arrived customer location
	public ResponseStructure<Map<Object, Object>> riderArrived(int riderId) {
		riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		String riderKey = "rider:" + riderId + ":requests";
		Map<Object, Object> rides = redistemplate.opsForHash().entries(riderKey);

		if (rides.isEmpty()) {
			throw new RuntimeException("No Assigned Ride Found");
		}

		String rideKey = null;

		for (Map.Entry<Object, Object> entry : rides.entrySet()) {
			String bookingId = String.valueOf(entry.getKey());

			if (!bookingId.matches("\\d+")) {
				continue;
			}

			String currentRideKey = String.valueOf(entry.getValue()).replace("\"", "").trim();
			Map<Object, Object> booking = redistemplate.opsForHash().entries(currentRideKey);

			if (!booking.isEmpty() && "ACCEPTED".equals(String.valueOf(booking.get("status")).replace("\"", ""))) {
				rideKey = currentRideKey;
				break;
			}
		}

		if (rideKey == null) {
			throw new RuntimeException("No accepted ride found");
		}

		redistemplate.opsForHash().put(rideKey, "status", "ARRIVED");

		Map<Object, Object> booking = redistemplate.opsForHash().entries(rideKey);

		return new ResponseStructure<>(HttpStatus.OK.value(), "Rider Arrived", booking);
	}

	// after otp validation -- start the ride
	public ResponseStructure<Map<Object, Object>> startRide(StartRideRequestDto startRideRequestDto) {
		int riderId = startRideRequestDto.getRiderId();
		String otp = startRideRequestDto.getOtp();

		riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		String riderKey = "rider:" + riderId + ":requests";
		Map<Object, Object> rides = redistemplate.opsForHash().entries(riderKey);

		if (rides.isEmpty()) {
			throw new RuntimeException("No Assigned Ride Found");
		}

		String rideKey = null;
		Map<Object, Object> booking = null;

		for (Map.Entry<Object, Object> entry : rides.entrySet()) {
			String bookingId = String.valueOf(entry.getKey());

			if (!bookingId.matches("\\d+")) {
				continue;
			}

			String currentRideKey = String.valueOf(entry.getValue()).replace("\"", "").trim();
			Map<Object, Object> currentBooking = redistemplate.opsForHash().entries(currentRideKey);

			if (!currentBooking.isEmpty()
					&& "ARRIVED".equals(String.valueOf(currentBooking.get("status")).replace("\"", "").trim())) {
				rideKey = currentRideKey;
				booking = currentBooking;
				break;
			}
		}

		if (booking == null) {
			throw new RuntimeException("No assigned ride found");
		}

		Object customerIdValue = booking.get("customerId");

		if (customerIdValue == null) {
			throw new RuntimeException("Customer ID not found in assigned ride");
		}

		String customerIdString = String.valueOf(customerIdValue).replace("\"", "").trim();

		if (customerIdString.isEmpty() || customerIdString.equalsIgnoreCase("null")) {
			throw new RuntimeException("Customer ID not found in assigned ride");
		}

		int customerId = Integer.parseInt(customerIdString);

		String url = "http://localhost:8082/customer/otp?customerId=" + customerId + "&otp=" + otp;

		ResponseEntity<ResponseStructure> responseEntity = resttemplate.postForEntity(url, null,
				ResponseStructure.class);

		boolean isOtpValid = Boolean.parseBoolean(String.valueOf(responseEntity.getBody().getData()));

		if (!isOtpValid) {
			throw new RuntimeException("Invalid Otp");
		}

		redistemplate.opsForHash().put(rideKey, "status", "STARTED");
		redistemplate.opsForHash().put(rideKey, "pickupTime", java.time.LocalDateTime.now().toString());

		booking = redistemplate.opsForHash().entries(rideKey);

		return new ResponseStructure<>(HttpStatus.OK.value(), "Ride Started", booking);
	}

	// complete ride
	public ResponseStructure<Map<Object, Object>> completeRide(int riderId) {
		riderrepository.findById(riderId).orElseThrow(() -> new RiderNotFoundException());

		String riderKey = "rider:" + riderId + ":requests";
		Map<Object, Object> rides = redistemplate.opsForHash().entries(riderKey);

		if (rides.isEmpty()) {
			throw new RuntimeException("No Assigned Ride Found");
		}

		String rideKey = null;

		for (Map.Entry<Object, Object> entry : rides.entrySet()) {
			String bookingId = String.valueOf(entry.getKey());

			if (!bookingId.matches("\\d+")) {
				continue;
			}

			String currentRideKey = String.valueOf(entry.getValue()).replace("\"", "").trim();
			Map<Object, Object> currentBooking = redistemplate.opsForHash().entries(currentRideKey);

			if (!currentBooking.isEmpty()
					&& "STARTED".equals(String.valueOf(currentBooking.get("status")).replace("\"", ""))) {
				rideKey = currentRideKey;
				break;
			}
		}

		if (rideKey == null) {
			throw new RuntimeException("No started ride found for this rider");
		}

		redistemplate.opsForHash().put(rideKey, "status", "COMPLETED");
		redistemplate.opsForHash().put(rideKey, "dropTime", java.time.LocalDateTime.now().toString());

		Map<Object, Object> booking = redistemplate.opsForHash().entries(rideKey);

		return new ResponseStructure<>(HttpStatus.OK.value(), "Ride Completed", booking);
	}
}
