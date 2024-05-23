package com.demo.importer.mapstruct;

import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.dto.StudentEventLogDto;
import com.demo.importer.entity.LogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "email", source = "student.email")
    @Mapping(target = "age", source = "student.age")
    StudentDisplayDto mapStudentAdditionDtoToStudentDisplayDto(StudentAdditionDto student);

    @Mapping(source = "student.name", target = "name")
    @Mapping(source = "student.email", target = "email")
    @Mapping(source = "student.age", target = "age")
    @Mapping(source = "logEntity.id", target = "logId")
    StudentEventLogDto mapStudentAdditionDtoAndLogIdToStudentEventLogDto(StudentAdditionDto student, LogEntity logEntity);
}
