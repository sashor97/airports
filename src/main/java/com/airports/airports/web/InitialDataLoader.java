package com.airports.airports.web;

import com.airports.airports.models.ERole;
import com.airports.airports.models.Role;
import com.airports.airports.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // insert data into the table
        Optional<Role> admin = roleRepository.findByName(ERole.ROLE_ADMIN);
        Optional<Role> user = roleRepository.findByName(ERole.ROLE_USER);
        Optional<Role> moderator = roleRepository.findByName(ERole.ROLE_MODERATOR);
        if (admin.isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        } else if (user.isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_USER));

        } else if (moderator.isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));

        }
//        List<Role> roleList = List.of(new Role(ERole.ROLE_ADMIN), new Role(ERole.ROLE_USER), new Role(ERole.ROLE_MODERATOR));
//        roleRepository.saveAll(roleList);
    }
}