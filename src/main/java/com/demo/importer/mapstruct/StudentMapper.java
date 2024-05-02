package com.demo.importer.mapstruct;

import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "email", source = "student.email")
    @Mapping(target = "age", source = "student.age")
    StudentDisplayDto mapStudentAdditionDtoToStudentDisplayDto(StudentAdditionDto student);

    List<StudentDisplayDto> mapStudentAdditionDtosToStudentDisplayDtos(List<StudentAdditionDto> students);

}
