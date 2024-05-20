package com.demo.importer.controller;

import com.demo.importer.dto.LogDisplayDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.exceptions.ResourceAdditionException;
import com.demo.importer.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<LogDisplayDto> addStudent(@RequestBody List<StudentAdditionDto> students) throws InterruptedException, ExecutionException {
        try {
             return studentService.saveStudent(students);
        } catch (ResourceAdditionException ex) {
            throw new ResourceAdditionException();
        }
    }

}
