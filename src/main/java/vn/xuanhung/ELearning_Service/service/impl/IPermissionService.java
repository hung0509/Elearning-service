package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.PermissionRequest;
import vn.xuanhung.ELearning_Service.dto.response.PermissionResponse;
import vn.xuanhung.ELearning_Service.entity.Permission;
import vn.xuanhung.ELearning_Service.repository.PermissionRepository;
import vn.xuanhung.ELearning_Service.service.PermissionService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IPermissionService implements PermissionService {
    PermissionRepository permissionRepository;
    ModelMapper modelMapper;

    @Override
    public ApiResponsePagination<List<PermissionResponse>> findAll(PermissionRequest request) {
        log.info("***Log permission service - get all permission***");
        Pageable page = PageRequest.of(request.getPage(), request.getPageSize());

        Page<Permission> permissionPage = permissionRepository.findAll(page);

        List<Permission> permissions = permissionPage.getContent();

        return ApiResponsePagination.<List<PermissionResponse>>builder()
                .result(
                        permissions.stream().map(
                            permission -> modelMapper.map(permission, PermissionResponse.class)
                    ).toList()
                )
                .totalItems(permissionPage.getTotalElements())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalPages(permissionPage.getTotalPages())
                .build();
    }

    @Override
    public ApiResponse<PermissionResponse> findById(String s) {
        return null;
    }

    @Override
    @Transactional
    public ApiResponse<PermissionResponse> save(PermissionRequest req) {
        log.info("***Log permission service - save permission***");
        log.info("{dto} :" + req);
        Permission permission = modelMapper.map(req, Permission.class);
        log.info("{entity} :" + permission.getPermissionName());

        permission = permissionRepository.save(permission);

        return  ApiResponse.<PermissionResponse>builder()
                .result(modelMapper.map(permission, PermissionResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(String s) {
        return null;
    }
}
