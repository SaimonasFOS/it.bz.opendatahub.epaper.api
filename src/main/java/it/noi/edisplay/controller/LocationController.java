package it.noi.edisplay.controller;

import it.noi.edisplay.dto.LocationDto;
import it.noi.edisplay.model.Location;
import it.noi.edisplay.repositories.LocationRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to create API for CRUD operations on Locations
 */
@RestController
@RequestMapping("/location")
public class LocationController {

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	ModelMapper modelMapper;

	Logger logger = LoggerFactory.getLogger(LocationController.class);

	@RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
	public ResponseEntity<LocationDto> getLocation(@PathVariable("uuid") String uuid) {
		Location location = locationRepository.findByUuid(uuid);

		if (location == null) {
			logger.debug("Location with uuid: " + uuid + " not found.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		logger.debug("Get location with uuid: " + uuid);
		return new ResponseEntity<>(modelMapper.map(location, LocationDto.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity getAllLocations() {
		List<Location> list = locationRepository.findAll();
		ArrayList<LocationDto> dtoList = new ArrayList<>();
		for (Location location : list)
			dtoList.add(modelMapper.map(location, LocationDto.class));
		logger.debug("All locations requested");
		return new ResponseEntity<>(dtoList, HttpStatus.OK);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity createLocation(@RequestBody LocationDto locationDto) {
		Location location = locationRepository.saveAndFlush(modelMapper.map(locationDto, Location.class));

		logger.debug("Location with uuid:" + location.getUuid() + " created.");
		return new ResponseEntity<>(modelMapper.map(location, LocationDto.class), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/delete/{uuid}", method = RequestMethod.DELETE)
	public ResponseEntity deleteLocation(@PathVariable("uuid") String uuid) {
		Location location = locationRepository.findByUuid(uuid);
		if (location == null) {
			logger.debug("Delete location with uuid:" + uuid + " failed.");
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		locationRepository.delete(location);
		logger.debug("Deleted location with uuid:" + uuid);
		return new ResponseEntity(HttpStatus.OK);

	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity updateLocation(@RequestBody LocationDto locationDto) {
		Location location = locationRepository.findByUuid(locationDto.getUuid());

		if (location == null) {
			logger.debug("Update location with uuid:" + locationDto.getUuid() + " failed.");
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}

		location.setName(locationDto.getName());
		location.setDescription(locationDto.getDescription());
		location.setRoomCode(locationDto.getRoomCode());
		
		locationRepository.saveAndFlush(location);
		logger.debug("Updated location with uuid:" + location.getUuid());
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
}
