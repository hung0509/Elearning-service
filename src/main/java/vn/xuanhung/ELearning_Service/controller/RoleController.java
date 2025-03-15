package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.PermissionRequest;
import vn.xuanhung.ELearning_Service.dto.request.RoleRequest;
import vn.xuanhung.ELearning_Service.dto.response.PermissionResponse;
import vn.xuanhung.ELearning_Service.dto.response.RoleResponse;
import vn.xuanhung.ELearning_Service.service.RoleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/roles")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> addPermission(@RequestBody RoleRequest req) {
        log.info("Log controller role - save role");
        return roleService.save(req);
    }

    @GetMapping
    public ApiResponsePagination<List<RoleResponse>> getAll(@ModelAttribute RoleRequest req) {
        log.info("Log controller role - get all role");
        return roleService.findAll(req);
    }
}
