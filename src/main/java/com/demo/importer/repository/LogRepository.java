package com.demo.importer.repository;

import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LogRepository {
    void saveStudentLog(@Param("log") LogEntity log);
    void updateTransferStateAndStatusCode(Long id, String transferState, Integer statusCode);
    void updateCurrentState(Long id, String currentState);
}
