package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EmployeeController {
    private static final List<String> DEPARTMENTS = List.of(
            "Human Resources", "Engineering", "Finance",
            "Sales", "Operations");

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @ModelAttribute("departments")
    public List<String>departments(){
        return DEPARTMENTS;
    }

    @GetMapping("/")
    public String home(){
        return "redirect:/employees";
    }

    //READ: show the form and the table of all employees
    @GetMapping("/employees")
    public String list(@RequestParam(required = false) String keyword, Model model){
        model.addAttribute("employee",new Employee());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("employees",service.findAll(keyword));
        return "employees";
    }

    //CREATE: save a new employee
    @PostMapping("/employees")
    public String create(@Valid @ModelAttribute("employee") Employee employee,
    BindingResult result, Model model,
    RedirectAttributes redirect){
        if(service.emailExists(employee.getEmail(),null)){
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll());
            return "employees";
        }
        service.save(employee);
        redirect.addFlashAttribute("success", "Employee added successfully.");
        return "redirect:/employees";
    }

    // UPDATE (step 1): show the edit form filled with existing data
    @GetMapping("/employees/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return service.findById(id)
                .map(employee -> {
                    model.addAttribute("employee", employee);
                    return "edit-employee";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Employee not found.");
                    return "redirect:/employees";
                });
    }

    // UPDATE (step 2): save the changes
    @PostMapping("/employees/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employee") Employee employee,
                         BindingResult result, RedirectAttributes redirect) {
        employee.setId(id);
        if (service.emailExists(employee.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) {
            return "edit-employee";
        }
        service.save(employee);
        redirect.addFlashAttribute("success", "Employee updated successfully.");
        return "redirect:/employees";
    }

    // DELETE
    @PostMapping("/employees/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.deleteById(id);
        redirect.addFlashAttribute("success", "Employee deleted successfully.");
        return "redirect:/employees";
    }

}
