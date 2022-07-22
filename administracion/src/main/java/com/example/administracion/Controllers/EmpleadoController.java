package com.example.administracion.Controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.administracion.Services.EmpleadoService;
import com.example.administracion.Services.TrabajadorService;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmpleadoController {

    @Autowired
    EmpleadoService empleadoService;
    @Autowired
    TrabajadorService trabajadorService;

    @GetMapping
    public ArrayList<Long> getEmpleados() {
        return (ArrayList<Long>) empleadoService.getIdTrabajador();
    }
}
