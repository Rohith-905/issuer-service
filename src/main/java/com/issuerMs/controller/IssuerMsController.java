package com.issuerMs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.issuerMs.Entity.IssuerMsEntity;
import com.issuerMs.Entity.LogRequest;
import com.issuerMs.service.IssuerMsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/issuerService")
public class IssuerMsController {
	
	@Autowired
	private IssuerMsService issuerMsService;
	
	@Autowired
	private RestTemplate template;
	
	public void logMessage(String level, String message) {
		String url = "http://cross-cutting-service/crossCutting/log"; 
		LogRequest logRequest = new LogRequest(level,message);
		try {
			template.postForObject(url,logRequest,LogRequest.class);
		}
		catch (Exception e ) {
			throw e;
		}
	}

	@Operation(summary = "To fetch book details and and issue copies")
	@ApiResponses(value = {
			@ApiResponse(responseCode ="200", description ="Successfully fetched and updated issuer"),
			@ApiResponse(responseCode = "404", description = "Book not found"),
	        @ApiResponse(responseCode = "500", description = "Internal server error")
			})
	@GetMapping("/getBook/{custId}/{isbn}/noOfCopies/{noOfCopies}")
	public IssuerMsEntity fetchBookDetails(@PathVariable Long custId, @PathVariable Integer isbn, @PathVariable Integer noOfCopies) throws Exception {
		 try {
			 logMessage("INFO","Entered into fetchBookDetails method with isbn : "+isbn +" and no of copies as : "+noOfCopies);
			return issuerMsService.getBookById(custId,isbn,noOfCopies);
		} catch (Exception e) {
			logMessage("ERROR", "Caught exception in fetchBookDetailsController "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	@Operation(summary = "Fetch all issuer details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully fetched all issuer details"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@GetMapping("/getAllIssuers")
	public List<IssuerMsEntity> fetchIssuerDetails() throws Exception{
		try {
			logMessage("INFO", "Entered in to fetchIssuerDetails");
			return issuerMsService.fetchIssuerDetails();
		}
		catch(Exception e) {
			logMessage("ERROR", "Caught excption in getAllIssuers Controller "+e.getMessage());
			throw e;
		}
	}
	
	 @Operation(summary = "Fetch issuer details by customer ID")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successfully fetched issuer details by customer ID"),
	        @ApiResponse(responseCode = "404", description = "Issuer not found"),
	        @ApiResponse(responseCode = "500", description = "Internal server error")
	    })
	@GetMapping("/getIssuerById/{custId}")
	public List<IssuerMsEntity> fetchIssuerById(@PathVariable Long custId) throws Exception{
		try {
			logMessage("INFO","Entered into fetchIssuerByID with custID : "+custId);
			return issuerMsService.fetchIssuerById(custId);
		}
		catch(Exception e){
			logMessage("ERROR","Caught Exception in fetchIssuerById");
			throw e;
		}
		
	}
	@Operation(summary = "To delete details of customer by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully deleted issuer details by customer ID"),
	        @ApiResponse(responseCode = "404", description = "Issuer not found"),
	        @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@DeleteMapping("/deleteByIssuerId/{custId}")
	public List<IssuerMsEntity> deleteIssuerById(@PathVariable Long custId) throws Exception{
		try {
			logMessage("INFO","Entered into deleteIssuerById with custID : "+custId);
			return issuerMsService.deleteIssuerById(custId);
		}
		catch(Exception e){
			logMessage("ERROR","Caught Exception in fetchIssuerById");
			throw e;
		}
		
	}
	 
}
