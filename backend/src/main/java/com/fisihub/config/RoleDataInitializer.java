package com.fisihub.config;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.model.Rol;
import com.fisihub.model.RolNombre;
import com.fisihub.repository.RolRepository;

@Component
public class RoleDataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;

    public RoleDataInitializer(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Arrays.stream(RolNombre.values())
                .filter(nombre -> rolRepository.findByNombre(nombre).isEmpty())
                .map(Rol::new)
                .forEach(rolRepository::save);
    }
}

