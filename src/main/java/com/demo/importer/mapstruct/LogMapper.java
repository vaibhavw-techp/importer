package com.demo.importer.mapstruct;

import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.dto.LogDisplayDto;
import com.demo.importer.entity.LogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "statusCode", target = "statusCode")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "transferState", target = "transferState")
    @Mapping(source = "currentState", target = "currentState")
    LogEntity mapLogAdditionDtoToLogEntity(LogAddtionDto logAdditionDto);

    @Mapping(source = "id", target = "logId")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "statusCode", target = "statusCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "transferState", target = "transferState")
    @Mapping(source = "currentState", target = "currentState")
    LogDisplayDto mapLogEntityToLogDisplayDto(LogEntity logEntity);

}
