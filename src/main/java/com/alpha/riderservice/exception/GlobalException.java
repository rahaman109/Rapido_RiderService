package com.alpha.riderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpha.riderservice.responsedto.ResponseStructure;

@RestControllerAdvice
public class GlobalException {
	
	@ExceptionHandler(RiderNotFoundException.class)
	public ResponseStructure<String> handleRiderNotFoundException(){
		ResponseStructure<String> rs= new ResponseStructure<String>();
		rs.setStatusCode(HttpStatus.NOT_FOUND.value());
		rs.setMessage("RIDER NOT FOUND");
		rs.setData(null);
		return rs;
		
	}
	
 
}
