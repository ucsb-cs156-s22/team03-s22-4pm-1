package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(description = "UCSBOrganization")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController{
    
    @Autowired
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @ApiOperation(value = "List all organizations in the database")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganization> allUCSBOrganizaions(){
        Iterable<UCSBOrganization> orgs = ucsbOrganizationRepository.findAll();
        return orgs;
    }

    @ApiOperation(value = "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganization getById(
            @ApiParam("orgCode") @RequestParam String orgCode) {
        UCSBOrganization ucsbOrg = ucsbOrganizationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

        return ucsbOrg;
    }

    @ApiOperation(value = "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganization postUCSBOrganization(
        @ApiParam("Organization Code") @RequestParam String orgCode,
        @ApiParam("Abbreviated name of organization") @RequestParam String orgTranslationShort,
        @ApiParam("Name of organization") @RequestParam String orgTranslation,
        @ApiParam("Whether the organization is inactive or not") @RequestParam boolean inactive)
        throws JsonProcessingException{

        UCSBOrganization ucsbOrg = new UCSBOrganization();

        ucsbOrg.setOrgCode(orgCode);
        ucsbOrg.setOrgTranslationShort(orgTranslationShort);
        ucsbOrg.setOrgTranslation(orgTranslation);
        ucsbOrg.setInactive(inactive);

        UCSBOrganization savedUcsbOrg = ucsbOrganizationRepository.save(ucsbOrg);

        return savedUcsbOrg;
        
    }

    @ApiOperation(value = "Delete a UCSBOrganization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBOrganization(
        @ApiParam("orgCode") @RequestParam String orgCode) {
        UCSBOrganization ucsbOrg = ucsbOrganizationRepository.findById(orgCode)
            .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

        ucsbOrganizationRepository.delete(ucsbOrg);
        return genericMessage("UCSBOrganization with id %s deleted".formatted(orgCode));
    }

    @ApiOperation(value = "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganization updateUCSBOrg(
            @ApiParam ("orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganization incoming) {

        UCSBOrganization ucsbOrg = ucsbOrganizationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

        ucsbOrg.setOrgTranslation(incoming.getOrgTranslation());
        ucsbOrg.setOrgTranslationShort(incoming.getOrgTranslationShort());
        ucsbOrg.setInactive(incoming.getInactive());

        ucsbOrganizationRepository.save(ucsbOrg);

        return ucsbOrg;
    }
}
