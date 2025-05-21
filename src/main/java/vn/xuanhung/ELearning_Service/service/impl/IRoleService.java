package vn.xuanhung.ELearning_Service.service.impl;

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
import vn.xuanhung.ELearning_Service.dto.request.RoleRequest;
import vn.xuanhung.ELearning_Service.dto.response.PermissionResponse;
import vn.xuanhung.ELearning_Service.dto.response.RoleResponse;
import vn.xuanhung.ELearning_Service.entity.Permission;
import vn.xuanhung.ELearning_Service.entity.Role;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.PermissionRepository;

import vn.xuanhung.ELearning_Service.repository.RoleRepository;
import vn.xuanhung.ELearning_Service.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IRoleService implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    ModelMapper modelMapper;

    @Override
    public ApiResponsePagination<List<RoleResponse>> findAll(RoleRequest request) {
        log.info("***Log role service - get all role***");
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        Page<Role> rolePage = roleRepository.findAll(pageable);

        List<Role> roles = rolePage.getContent();

        return  ApiResponsePagination.<List<RoleResponse>>builder()
                .result(roles.stream().map(role -> modelMapper.map(role, RoleResponse.class)).toList())
                .build();
    }

    @Override
    public ApiResponse<RoleResponse> findById(String s) {
        return null;
    }

    @Override
    public ApiResponse<RoleResponse> save(RoleRequest req) {
        log.info("***Log role service - save role***");
        Role role = modelMapper.map(req, Role.class);

        Set<Permission> permissions = new HashSet<>();
        if(req.getListOfPermissions() != null && !req.getListOfPermissions().isEmpty()){
            req.getListOfPermissions().forEach(permission -> {
                Permission permission1 = permissionRepository.findById(permission)
                        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXIST));
                permissions.add(permission1);
            });
        }

        role.setPermissions(permissions);
        role = roleRepository.save(role);

        return  ApiResponse.<RoleResponse>builder()
                .result(modelMapper.map(role, RoleResponse.class))
                .build();
    }

    @Override
    public ApiResponse<String> deleteById(String s) {
        return null;
    }
}
