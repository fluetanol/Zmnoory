package com.gradation.zmnnoory.common.service;

import com.gradation.zmnnoory.common.jwt.JwtUtil;
import com.gradation.zmnnoory.domain.member.entity.UserDetailsImpl;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(memberRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다.")));
    }
}
