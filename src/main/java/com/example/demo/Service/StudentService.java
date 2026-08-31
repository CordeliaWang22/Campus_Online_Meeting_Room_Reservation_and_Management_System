package com.example.demo.Service;

import com.example.demo.Repo.Student;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StudentService {
    public Optional<Student> findStudentById(int id);
}
