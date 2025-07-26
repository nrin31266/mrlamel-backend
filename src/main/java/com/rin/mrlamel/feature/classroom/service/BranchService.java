package com.rin.mrlamel.feature.classroom.service;

import com.rin.mrlamel.feature.classroom.dto.req.CreateBranchReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateBranchReq;
import com.rin.mrlamel.feature.classroom.model.Branch;

import java.util.List;

public interface BranchService {
    // Define methods for branch-related operations here
    List<Branch> getAllBranches();
    Branch createBranch(CreateBranchReq createBranchReq);
    Branch updateBranch(Long id, UpdateBranchReq updateBranchReq);
    void deleteBranch(Long id);
}
