package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.response.DashBoardResponse;
import vn.xuanhung.ELearning_Service.service.DashBoardService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dashboards")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DashBoardController {
    DashBoardService dashBoardService;

    @GetMapping("/{year}")
    public ApiResponse<DashBoardResponse> getDashBoard(@PathVariable String year){
        log.info("*Log controller dashboard - get dashboard*");
        return ApiResponse.<DashBoardResponse>builder()
                .result(dashBoardService.getDashBoard(year))
                .build();
    }
}
