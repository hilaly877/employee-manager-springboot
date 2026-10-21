package com.example.employee_manager.service;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> findAll(){
        return repository.findAll();
    }

    public List<Employee> findAll(String keyword){
        return keyword == null || keyword.isBlank()
                ? repository.findAll()
                : repository.findByFullNameContainingIgnoreCase(keyword.trim());
    }

    public Optional<Employee> findById(Long id){
        return repository.findById(id);
    }

    public Employee save(Employee employee){
        return repository.save(employee);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public boolean emailExists(String email, Long excludeId){
        return excludeId == null
                ? repository.existsByEmail(email)
                : repository.existsByEmailAndIdNot(email, excludeId);
    }
}
