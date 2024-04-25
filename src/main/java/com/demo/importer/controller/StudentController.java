package com.demo.importer.controller;

import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addStudent(@RequestBody List<StudentAdditionDto> students) {
        try {
             studentService.saveStudent(students);
             return "Successful";
        } catch (Exception ex) {
            throw new RuntimeException("Failed to add students", ex);
        }
    }

}
