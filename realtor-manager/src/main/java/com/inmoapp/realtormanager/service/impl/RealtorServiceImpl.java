package com.inmoapp.realtormanager.service.impl;

import com.inmoapp.realtormanager.client.RealEstateClient;
import com.inmoapp.realtormanager.converter.RealtorEntityToRealtorModel;
import com.inmoapp.realtormanager.converter.RealtorModelToRealtorEntity;
import com.inmoapp.realtormanager.entity.RealtorEntity;
import com.inmoapp.realtormanager.model.RealtorModel;
import com.inmoapp.realtormanager.model.exception.RealEstateNotFound;
import com.inmoapp.realtormanager.model.exception.RealtorDniAlReadyExist;
import com.inmoapp.realtormanager.model.exception.RealtorEmailAlReadyExist;
import com.inmoapp.realtormanager.model.exception.RealtorNotFound;
import com.inmoapp.realtormanager.repository.RealtorRepository;
import com.inmoapp.realtormanager.service.RealtorService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RealtorServiceImpl implements RealtorService {

    private final RealtorRepository realtorRepository;

    private final RealtorEntityToRealtorModel realtorEntityToRealtorModel;

    private final RealtorModelToRealtorEntity realtorModelToRealtorEntity;

    private final RealEstateClient realEstateClient;

    public RealtorServiceImpl(RealtorRepository realtorRepository, RealtorEntityToRealtorModel realtorEntityToRealtorModel, RealtorModelToRealtorEntity realtorModelToRealtorEntity,
                              RealEstateClient realEstateClient) {
        this.realtorRepository = realtorRepository;
        this.realtorEntityToRealtorModel = realtorEntityToRealtorModel;
        this.realtorModelToRealtorEntity = realtorModelToRealtorEntity;
        this.realEstateClient = realEstateClient;
    }

    public Set<RealtorModel> findAllRealtors() {

        return realtorRepository.findAll().stream().map(realtorEntityToRealtorModel).collect(Collectors.toSet());
    }

    public RealtorModel findRealtorById(String id) {

        return realtorRepository.findById(id).map(realtorEntityToRealtorModel).orElseThrow(() -> new RealtorNotFound(id));
    }

    public Set<RealtorModel> findRealtorsByRealEstateId(String realEstateId) {
        return realtorRepository.findRealtorByRealEstateId(realEstateId).stream().map(realtorEntityToRealtorModel).collect(Collectors.toSet());
    }

    public RealtorModel addRealtor(RealtorModel realtorModel) {

        Optional.ofNullable(realEstateClient.getRealEstateById(realtorModel.getRealEstateId()))
                .orElseThrow(() -> new RealEstateNotFound(realtorModel.getRealEstateId()));

        realtorExistByDni(realtorModel.getDni());
        realtorExistByEmail(realtorModel.getEmailContact());

        RealtorEntity realtor = realtorRepository.save(realtorModelToRealtorEntity.apply(realtorModel));

        return realtorEntityToRealtorModel.apply(realtor);
    }

    public void removeRealtor(String id) {
        realtorRepository.deleteById(id);
    }

    private void realtorExistByDni(String dni) {
        Optional<RealtorEntity> existByDni = realtorRepository.findRealtorByDni(dni);
        if (existByDni.isPresent()) {
            throw new RealtorDniAlReadyExist();
        }
    }

    private void realtorExistByEmail(String email) {
        Optional<RealtorEntity> existByEmail = realtorRepository.findRealtorByEmailContact(email);
        if (existByEmail.isPresent()) {
            throw new RealtorEmailAlReadyExist();
        }
    }
}