package com.rin.mrlamel.feature.classroom.mapper;

import com.rin.mrlamel.feature.classroom.dto.req.CreateBranchReq;
import com.rin.mrlamel.feature.classroom.dto.req.UpdateBranchReq;
import com.rin.mrlamel.feature.classroom.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    Branch toBranch(CreateBranchReq createBranchReq);
    void updateBranchFromReq(UpdateBranchReq createBranchReq, @MappingTarget Branch branch);
}
