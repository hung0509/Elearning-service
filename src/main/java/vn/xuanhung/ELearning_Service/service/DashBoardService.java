package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.dto.response.DashBoardResponse;

public interface DashBoardService {
    public DashBoardResponse getDashBoard(String year);
}
