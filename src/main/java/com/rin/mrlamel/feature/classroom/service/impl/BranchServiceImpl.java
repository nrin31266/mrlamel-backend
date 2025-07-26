package com.rin.mrlamel.feature.classroom.service.impl;

import com.rin.mrlamel.common.exception.AppException;
import com.rin.mrlamel.feature.classroom.dto.req.CreateBranchReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateBranchReq;
import com.rin.mrlamel.feature.classroom.mapper.BranchMapper;
import com.rin.mrlamel.feature.classroom.model.Branch;
import com.rin.mrlamel.feature.classroom.repository.BranchRepository;
import com.rin.mrlamel.feature.classroom.service.BranchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BranchServiceImpl implements BranchService {
    BranchRepository branchRepository;
    BranchMapper branchMapper;

    @Override
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Override
    public Branch createBranch(CreateBranchReq createBranchReq) {
        Branch branch = branchMapper.toBranch(createBranchReq);
        if (branchRepository.existsByName(branch.getName())) {
            throw new AppException("Branch with name " + branch.getName() + " already exists.");
        }
        return branchRepository.save(branch);
    }

    @Override
    public Branch updateBranch(Long id, UpdateBranchReq updateBranchReq) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new AppException("Branch not found with id: " + id));

        branchMapper.updateBranchFromReq(updateBranchReq, branch);

        if (branchRepository.existsByNameAndIdNot(branch.getName(), id)) {
            throw new AppException("Branch with name " + branch.getName() + " already exists.");
        }

        return branchRepository.save(branch);
    }

    @Override
    public void deleteBranch(Long id) {
        if (branchRepository.existsById(id)) {
            branchRepository.deleteById(id);
        } else {
            throw new AppException("Branch not found with id: " + id);
        }
    }
}
