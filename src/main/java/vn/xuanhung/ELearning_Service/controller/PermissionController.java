package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.PermissionRequest;
import vn.xuanhung.ELearning_Service.dto.response.PermissionResponse;
import vn.xuanhung.ELearning_Service.service.PermissionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/permissions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> addPermission(@RequestBody PermissionRequest req) {
        log.info("Log controller permission - save permission");
        return permissionService.save(req);
    }

    @GetMapping
    public ApiResponsePagination<List<PermissionResponse>> getAll(@ModelAttribute PermissionRequest req) {
        log.info("Log controller permission - get all permission");
        return permissionService.findAll(req);
    }

}
