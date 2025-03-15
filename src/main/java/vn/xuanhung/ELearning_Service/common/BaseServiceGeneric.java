package vn.xuanhung.ELearning_Service.common;

import java.util.List;

public interface BaseServiceGeneric<ID, R, Response> {
    ApiResponsePagination<List<Response>>  findAll(R request);
    ApiResponse<Response>  findById(ID id);
    ApiResponse<Response> save(R req);
    ApiResponse<String> deleteById(ID id);
}
