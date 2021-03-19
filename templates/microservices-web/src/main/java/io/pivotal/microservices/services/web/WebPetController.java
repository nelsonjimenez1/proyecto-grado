package io.pivotal.microservices.services.web;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.*;

/**
 * Client controller, fetches Account info from the microservice via
 * {@link WebAccountsService}.
 * 
 * @author Paul Chapman
 */
@RestController
@RequestMapping("/owners/{ownerId}")
public class WebPetController {

    @Autowired
    protected WebPetService service;

    protected Logger logger = Logger.getLogger(WebPetController.class.getName());

    public WebPetController(WebPetService service) {
        this.service = service;
    }

    @GetMapping(value= "/pets/prueba", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> prueba(@PathVariable("ownerId") int ownerId) {

        HttpStatus codigo = HttpStatus.NOT_FOUND;
        String aux = null;
        try {
            aux = this.service.prueba(ownerId);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(aux);

    }

    @GetMapping(value= "/pets/{petId}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> obtenerOwnerByID(@PathVariable("petId") int petId, @PathVariable("ownerId") int ownerId) {

        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Owner aux = null;
        try {
            aux = this.service.obtenerOwnerByID(petId, ownerId);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(aux);

    }

    @PostMapping(value= "/pets/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pet> processCreationForm(@PathVariable("ownerId") int ownerId, @RequestBody Pet pet) {
        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Pet aux = null;
        try {
            aux = this.service.processCreationForm(ownerId, pet);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(aux);
    }

    @PostMapping(value= "/pets/{petId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pet> processUpdateForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, @RequestBody Pet pet) {
        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Pet aux = null;
        try {
            aux = this.service.processUpdateForm(ownerId, petId, pet);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(aux);
    }

    @GetMapping(value="/pets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Pet>> processFindForm(@PathVariable("ownerId") int ownerId) {
        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Collection<Pet> aux = null;
        try {
            aux = this.service.processFindForm(ownerId);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(aux);
    }

}
