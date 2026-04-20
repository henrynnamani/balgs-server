package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BalgsUserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return (UserDetails) userRepo.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
        );
    }
}
