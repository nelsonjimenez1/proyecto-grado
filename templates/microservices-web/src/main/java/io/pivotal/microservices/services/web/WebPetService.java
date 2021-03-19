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
public class WebPetService {

    @Autowired
    @LoadBalanced
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    protected Logger logger = Logger.getLogger(WebPetService.class.getName());

    public WebPetService(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
    }

    public String prueba(int ownerId) {

        try {
            return restTemplate.getForObject(serviceUrl + "/owners/{ownerId}/pets/prueba", String.class, ownerId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }

    public Owner obtenerOwnerByID(int petId, int ownerId) {
        try {
            return restTemplate.getForObject(serviceUrl + "/owners/{ownerId}/pets/{petId}/owner", Owner.class, ownerId, petId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }

    public Pet processCreationForm(int ownerId, Pet pet) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Pet> request = new HttpEntity<Pet>(pet, headers);
            return restTemplate.postForObject(serviceUrl + "/owners/{ownerId}/pets/new", request, Pet.class, ownerId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }

    }

    public Pet processUpdateForm(int ownerId, int petId, Pet pet) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Pet> request = new HttpEntity<Pet>(pet, headers);
            return restTemplate.postForObject(serviceUrl + "/owners/{ownerId}/pets/{petId}/edit", pet, Pet.class, ownerId, petId);
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }

    public Collection<Pet> processFindForm( int ownerId) {

        try {
            return Arrays.asList(restTemplate.getForObject(serviceUrl + "/owners/{ownerId}/pets", Pet[].class, ownerId));
        } catch (Exception e) {
            logger.severe(e.getClass() + ": " + e.getLocalizedMessage());
            return null;
        }
    }
}
