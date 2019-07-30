package com.inmoapp.realtors.service.impl;

import com.inmoapp.realtors.client.RealEstateClient;
import com.inmoapp.realtors.converter.RealtorEntityToRealtorModel;
import com.inmoapp.realtors.converter.RealtorModelToRealtorEntity;
import com.inmoapp.realtors.entity.RealtorEntity;
import com.inmoapp.realtors.model.RealtorModel;
import com.inmoapp.realtors.model.exception.RealEstateNotFound;
import com.inmoapp.realtors.model.exception.RealtorEmailAlReadyExist;
import com.inmoapp.realtors.model.exception.RealtorNicknameAlReadyExist;
import com.inmoapp.realtors.model.exception.RealtorNotFound;
import com.inmoapp.realtors.repository.RealtorRepository;
import com.inmoapp.realtors.service.RealtorService;
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
        realtorModel.setId("");

        Optional.ofNullable(realEstateClient.getRealEstateById(realtorModel.getRealEstateId()))
                .orElseThrow(() -> new RealEstateNotFound(realtorModel.getRealEstateId()));

        realtorExistByNickname(realtorModel.getNickname(), null);
        realtorExistByEmail(realtorModel.getEmailContact(), null);

        RealtorEntity realtor = realtorRepository.save(realtorModelToRealtorEntity.apply(realtorModel));

        return realtorEntityToRealtorModel.apply(realtor);
    }

    public RealtorModel updateRealtor(RealtorModel realtorModel) {

        existRealtorById(realtorModel.getId());

        Optional.ofNullable(realEstateClient.getRealEstateById(realtorModel.getRealEstateId()))
                .orElseThrow(() -> new RealEstateNotFound(realtorModel.getRealEstateId()));

        realtorExistByNickname(realtorModel.getNickname(), realtorModel.getId());
        realtorExistByEmail(realtorModel.getEmailContact(), realtorModel.getId());

        RealtorEntity realtor = realtorRepository.save(realtorModelToRealtorEntity.apply(realtorModel));

        return realtorEntityToRealtorModel.apply(realtor);
    }


    public void removeRealtor(String id) {
        realtorRepository.deleteById(id);
    }

    private void realtorExistByNickname(String nickname, String realtorId) {
        if (realtorId == null) {
            Optional<RealtorEntity> existByNickname = realtorRepository.findRealtorByNickname(nickname);
            if (existByNickname.isPresent()) {
                throw new RealtorNicknameAlReadyExist();
            }
        } else {
            Optional<RealtorEntity> existByNickname = realtorRepository.findRealtorByNickname(nickname);
            if (existByNickname.isPresent()) {
                if (!existByNickname.get().getId().equals(realtorId)) {
                    throw new RealtorEmailAlReadyExist();
                }
            }
        }
    }

    private void realtorExistByEmail(String email, String realtorId) {
        if (realtorId == null) {
            Optional<RealtorEntity> existByEmail = realtorRepository.findRealtorByEmailContact(email);
            if (existByEmail.isPresent()) {
                throw new RealtorEmailAlReadyExist();
            }
        } else {
            Optional<RealtorEntity> existByEmail = realtorRepository.findRealtorByEmailContact(email);
            if (existByEmail.isPresent()) {
                if (!existByEmail.get().getId().equals(realtorId)) {
                    throw new RealtorEmailAlReadyExist();
                }
            }
        }
    }

    private void existRealtorById(String realtorId) {
        Optional<RealtorEntity> existById = realtorRepository.findById(realtorId);
        if (!existById.isPresent()) {
            throw new RealtorNotFound(realtorId);
        }
    }
}