package com.example.demo.Service;

import com.example.demo.Repo.Student;
import com.example.demo.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImple implements StudentService{
    @Autowired
    private StudentRepo studentRepo;
    @Override
    public Optional<Student> findStudentById(int id) {
        return studentRepo.findById(id);
    }
}
