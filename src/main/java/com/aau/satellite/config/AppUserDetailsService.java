package com.aau.satellite.config;

import com.aau.satellite.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
  final UserRepository repo;

  public AppUserDetailsService(UserRepository r) {
    repo = r;
  }

  public UserDetails loadUserByUsername(String u) throws UsernameNotFoundException {
    return repo.findByUsername(u)
        .map(
            x ->
                User.withUsername(x.getUsername())
                    .password(x.getPasswordHash())
                    .roles(x.getRole().name())
                    .build())
        .orElseThrow(() -> new UsernameNotFoundException(u));
  }
}
