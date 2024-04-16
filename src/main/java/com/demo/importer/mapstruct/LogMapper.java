package com.demo.importer.mapstruct;

import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.entity.LogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {

    public LogEntity mapLogAdditionDtoToLogEntity(LogAddtionDto logAddtionDto);

}
