package com.rin.mrlamel.feature.classroom.controller;

import com.rin.mrlamel.common.dto.response.ApiRes;
import com.rin.mrlamel.feature.classroom.dto.req.CreateBranchReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateBranchReq;
import com.rin.mrlamel.feature.classroom.model.Branch;
import com.rin.mrlamel.feature.classroom.service.BranchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branches")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BranchController {
    BranchService branchService;

    // Define endpoints for branch operations here, e.g., getAllBranches, createBranch, updateBranch, deleteBranch
    @GetMapping
    public ApiRes<List<?>> getAllBranches() {
        List<?> branches = branchService.getAllBranches();
        return ApiRes.success(branches);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiRes<Branch> createBranch(@Validated CreateBranchReq createBranchReq) {
        var branch = branchService.createBranch(createBranchReq);
        return ApiRes.success(branch);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiRes<Branch> updateBranch(@PathVariable("id") Long id, @Validated UpdateBranchReq updateBranchReq) {
        var branch = branchService.updateBranch(id, updateBranchReq);
        return ApiRes.success(branch);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiRes<Void> deleteBranch(@PathVariable("id") Long id) {
        branchService.deleteBranch(id);
        return ApiRes.<Void>builder()
                .message("Branch deleted successfully")
                .build();
    }
}
