package com.instagram.demo.security;

import com.instagram.demo.data.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository
                .findFirstByUsername(username)
                .map(person -> new CustomUserDetails(person.getEmail(), person.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
