package io.pivotal.microservices.services.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Hide the access to the microservice inside this local service.
 * 
 * @author Paul Chapman
 */
@Service
public class WebOwnersService {

    @Autowired
    @LoadBalanced
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    protected Logger logger = Logger.getLogger(WebOwnersService.class.getName());

    public WebOwnersService(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
    }

    public Owner crearOwner(Owner owner) {
        //logger.info("findByNumber() invoked: for " + accountNumber);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Owner> request = new HttpEntity<Owner>(owner, headers);
            return restTemplate.postForObject(serviceUrl + "/owners/new", owner, Owner.class);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }

    public Collection<Owner> obtenerOwners() {
        //logger.info("findByNumber() invoked: for " + accountNumber);
        Owner[] ows = null;
        try {
            ows = restTemplate.getForObject(serviceUrl + "/owners/all", Owner[].class);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
        return Arrays.asList(ows);
    }

    public Collection<Owner> obtenerOwnersByLastName(String lastName) {
        //logger.info("findByNumber() invoked: for " + accountNumber);
        Owner[] ows = null;
        try {
            ows = restTemplate.getForObject(serviceUrl + "/owners/find/{lastName}", Owner[].class, lastName);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
        return Arrays.asList(ows);
    }

    public Owner editarOwner(int ownerId, Owner owner) {
        //logger.info("findByNumber() invoked: for " + accountNumber);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Owner> request = new HttpEntity<Owner>(owner, headers);
            return restTemplate.postForObject(serviceUrl + "/owners/{ownerId}/edit/", owner, Owner.class, ownerId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }

    public Owner obtenerOwnerById(int ownerId) {
        //logger.info("findByNumber() invoked: for " + accountNumber);
        try {
            return restTemplate.getForObject(serviceUrl + "/owners/{ownerId}/", Owner.class, ownerId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }
}
