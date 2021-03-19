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
public class WebOwnerController {

    @Autowired
    protected WebOwnersService ownersService;

    protected Logger logger = Logger.getLogger(WebOwnerController.class.getName());

    public WebOwnerController(WebOwnersService ownersService) {
        this.ownersService = ownersService;
    }

    @PostMapping(value= "/owners/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> crearOwner(@RequestBody Owner owner) {
        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Owner o = null;
        try {
            this.ownersService.crearOwner(owner);
            o = owner;
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(o);
    }

    @GetMapping(value="/owners/all", produces = MediaType.APPLICATION_JSON_VALUE)
    //@RequestBody Owner owner
    public ResponseEntity<Collection<Owner>> obtenerOwners() {

        HttpStatus codigo = HttpStatus.NOT_FOUND;

        // allow parameterless GET request for /owners to return all records
        /* if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies broadest possible search
        } */

        Collection<Owner> results = null;
        // find owners by last name
        //owner.getLastName()
        try {
            results = this.ownersService.obtenerOwners();
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(results);
    }

    @GetMapping(value="/owners/find/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@RequestBody Owner owner
    public ResponseEntity<Collection<Owner>> obtenerOwnersByLastName(@PathVariable("lastName") String lastName) {

        HttpStatus codigo = HttpStatus.NOT_FOUND;

        // allow parameterless GET request for /owners to return all records
        /* if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies broadest possible search
        } */

        Collection<Owner> results = null;
        // find owners by last name
        //owner.getLastName()
        try {
            results = this.ownersService.obtenerOwnersByLastName(lastName);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }
        return ResponseEntity.status(codigo).body(results);
    }

    @PostMapping(value="/owners/{ownerId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> editarOwner(@RequestBody Owner owner, @PathVariable("ownerId") int ownerId) {

        HttpStatus codigo = HttpStatus.NOT_FOUND;
        Owner o = null;
        try {
            this.ownersService.editarOwner(ownerId, owner);
            o = owner;
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }

        return ResponseEntity.status(codigo).body(o);
    }

    @GetMapping(value="/owners/{ownerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> obtenerOwnerById(@PathVariable("ownerId") int ownerId) {

        Owner o = new Owner();
        HttpStatus codigo = HttpStatus.NOT_FOUND;
        try {
            o = this.ownersService.obtenerOwnerById(ownerId);
            codigo = HttpStatus.OK;
        } catch (Exception e) {

        }

        return ResponseEntity.status(codigo).body(o);
    }

}
