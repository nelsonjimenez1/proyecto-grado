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
 */
@RequestMapping(value= "/api")
@RestController
public class TestMethod{

    @Autowired
    protected WebOwnerService service;

    public WebOwnerController(WebOwnerService service) {
        this.service = service;
    }

    @PostMapping(value = "/owners/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> processCreationForm(@RequestBody Owner owner) {
        HttpStatus code = HttpStatus.NOT_FOUND;
        Owner aux = null;
        try {
            aux = this.service.processCreationForm(owner);
            code = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(code).body(aux);
    }

    @GetMapping(value = "/owners", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Owner>> processFindForm(@RequestBody Owner owner) {
        HttpStatus code = HttpStatus.NOT_FOUND;
        Collection<Owner> aux = null;
        try {
            aux = this.service.processFindForm(owner);
            code = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(code).body(aux);
    }

    @PostMapping(value = "/owners/{ownerId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> processUpdateOwnerForm(@RequestBody Owner owner, @PathVariable("ownerId") int ownerId) {
        HttpStatus code = HttpStatus.NOT_FOUND;
        Owner aux = null;
        try {
            aux = this.service.processUpdateOwnerForm(owner, ownerId);
            code = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(code).body(aux);
    }

    @GetMapping(value = "/owners/{ownerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Owner> showOwner(@PathVariable("ownerId") int ownerId) {
        HttpStatus code = HttpStatus.NOT_FOUND;
        Owner aux = null;
        try {
            aux = this.service.showOwner(ownerId);
            code = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(code).body(aux);
    }
}
