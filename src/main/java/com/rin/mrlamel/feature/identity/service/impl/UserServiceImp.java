package com.rin.mrlamel.feature.identity.service.impl;

import com.rin.mrlamel.common.dto.PageableDto;
import com.rin.mrlamel.common.mapper.PageableMapper;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImp implements com.rin.mrlamel.feature.identity.service.UserService {

   UserRepository userRepository;
   PageableMapper pageableMapper;

    @Override
    public PageableDto<User> getAllUsers(int page, int size, String sortBy, String sortDirection, String search, String role, String status) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                var loweredSearch = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), loweredSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), loweredSearch)
                ));
            }
            if (role != null && !role.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if(predicates.isEmpty()) {
                // If no filters are applied, return all users
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(page , size, sort);
        Page<User> pageResult = userRepository.findAll(spec, pageable);

        return  pageableMapper.toUserPageableDto(pageResult);
    }
}