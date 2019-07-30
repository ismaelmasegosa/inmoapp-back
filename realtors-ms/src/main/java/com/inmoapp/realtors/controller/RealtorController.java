package com.inmoapp.realtors.controller;

import com.inmoapp.realtors.model.RealtorModel;
import com.inmoapp.realtors.service.RealtorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/realtors")
@CrossOrigin("*")
@Api(tags = "realtors-controller")
public class RealtorController {

    private final RealtorService realtorService;

    public RealtorController(RealtorService realtorService) {
        this.realtorService = realtorService;
    }

    @GetMapping(value = "findById/{id}")
    @ApiOperation(value = "Find realtor by id")
    public RealtorModel getRealtorById(@PathVariable("id") String id) {
        return realtorService.findRealtorById(id);
    }

    @GetMapping(value = "findByRealEstate/{realEstateId}")
    @ApiOperation(value = "Find realtors by real estate ID")
    public Set<RealtorModel> getRealtorsEstateId(@PathVariable("id") String realEstateId) {
        return realtorService.findRealtorsByRealEstateId(realEstateId);
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "Find all realtors")
    public Set<RealtorModel> getAllRealtors() {
        return realtorService.findAllRealtors();
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "Add realtor")
    public ResponseEntity addRealtor(@Valid @RequestBody RealtorModel realtor) {
        return new ResponseEntity(realtorService.addRealtor(realtor), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "Update realtor")
    public ResponseEntity updateRealtor(@Valid @RequestBody RealtorModel realtor) {
        return new ResponseEntity(realtorService.updateRealtor(realtor), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "Delete realtor")
    public ResponseEntity deleteRealtor(@PathVariable("id") String id) {
        realtorService.removeRealtor(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}