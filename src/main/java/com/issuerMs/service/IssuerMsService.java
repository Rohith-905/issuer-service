package com.issuerMs.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.issuerMs.Entity.Book;
import com.issuerMs.Entity.IssuerMsEntity;
import com.issuerMs.Entity.LogRequest;
import com.issuerMs.common.DataNotFoundException;
import com.issuerMs.common.UpdateFailedException;
import com.issuerMs.repository.IssuerMsRepo;


@Service
public class IssuerMsService {

	@Autowired
	private RestTemplate template;
	
	@Autowired
	private IssuerMsRepo issuerMsRepo;
	
	public void logMessage(String level,String message) {
		
		 String url = "http://cross-cutting-service/crossCutting/log";
		 LogRequest logRequest = new LogRequest(level,message);
		 try {
			 template.postForObject(url,logRequest, LogRequest.class);
		 }
		 catch(Exception e) {
			 throw e;
		 }
		
		
	}
	
	public List<IssuerMsEntity> fetchIssuerDetails() {
		List<IssuerMsEntity> issuerDetails = null;
		try {
			issuerDetails = issuerMsRepo.findAll();
			if(issuerDetails.isEmpty()) {
				throw new DataNotFoundException(new Date(),"No issuers present");
			}
		}
		catch(Exception e) {
			throw e;
		}
		return issuerDetails;
	}
	
	
	public List<IssuerMsEntity> fetchIssuerById(Long custId) throws Exception{
		List<IssuerMsEntity> issuerDetails = null;
		try {
			issuerDetails = issuerMsRepo.findByCustId(custId);
			if(issuerDetails.isEmpty()) {
				throw new DataNotFoundException(new Date(),"Issuer not found with the ID: "+custId);
			}
		}
		catch(Exception e) {
			throw e;
		}
		  
		 return issuerDetails;
	}
	
	public List<IssuerMsEntity> deleteIssuerById(Long custId) throws Exception{
		List<IssuerMsEntity> issuerDetails = null;
		try {
			issuerDetails = issuerMsRepo.findByCustId(custId);
			if(issuerDetails.isEmpty()) {
				logMessage("ERROR","issuer not found for the id: "+custId);
				throw new DataNotFoundException(new Date(),"Issuer details not found for custId: " + custId);
			}
		}
		catch(Exception e) {
			throw e;
		}
		  
		 return issuerDetails;
	}
	
	public IssuerMsEntity getBookById(Long custId, Integer isbn, Integer noOfCopies) throws Exception {
		IssuerMsEntity updatedIssuer = new IssuerMsEntity();
		try {
			
			logMessage("INFO", "Issuer Service - Trying to fetch book details making rest call with isbn: "+isbn);
			
			Book bookDetails =  template.getForObject("http://book-service/bookService/getBook/{isbn}", Book.class,isbn);
			
			// Validate book details
            if (bookDetails == null) {
            	logMessage("ERROR", "Issuer Service - Book details not found for isbn : "+ isbn);
                throw new DataNotFoundException(new Date(),"Book details not found for ISBN: " + isbn);
            }

            Integer availableCopies = bookDetails.getTotalCopies() - bookDetails.getIssuedCopies();
            
            if (availableCopies <= 0 || availableCopies < noOfCopies) {
            	logMessage("ERROR","Not enough copies availabe, available copies :"+ availableCopies);
                throw new DataNotFoundException(new Date(),"Not enough copies available. Available: " + availableCopies);
            }
            
            bookDetails.setIssuedCopies(bookDetails.getIssuedCopies()+noOfCopies);
			// Create HttpHeaders if you need to specify any headers (e.g., Content-Type)
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Type", "application/json");
	        
	        // Wrap the bookDetails and headers in HttpEntity
	        HttpEntity<Book> requestEntity = new HttpEntity<>(bookDetails, headers);
	        
	        // Make the POST request
	        logMessage("INFO","Making a rest call to updateBook with isbn : "+isbn );
	        ResponseEntity<Book> responseEntity = template.exchange(
	        	"http://book-service/bookService/updateBook",
	            HttpMethod.PUT,
	            requestEntity,
	            Book.class,
	            isbn
	        );
	        
	        Book updatedBook = responseEntity.getBody();
	        
	        if (updatedBook == null) {
                throw new UpdateFailedException(new Date(),"Failed to update book details for ISBN: " + isbn);
            }
	        
	        IssuerMsEntity issued = new IssuerMsEntity();
	        issued.setCustId(custId);
	       	issued.setIsbn(updatedBook.getIsbn());
	        issued.setNoOfCopies(noOfCopies);
	        logMessage("INFO","Storing issuer info in DB");	
	        updatedIssuer = issuerMsRepo.save(issued); 	
			
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return updatedIssuer;
		
	}
}
