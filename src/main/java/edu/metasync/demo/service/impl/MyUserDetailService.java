package edu.metasync.demo.service.impl;

import edu.metasync.demo.dto.auth.UserPrincipal;
import edu.metasync.demo.entity.UserEntity;
import edu.metasync.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepository.findByUserName(username);
        if (user == null){
            throw new UsernameNotFoundException("User not found !");
        }
        return new UserPrincipal(user);
    }
}