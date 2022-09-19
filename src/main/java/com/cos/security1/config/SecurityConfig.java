package com.cos.security1.config;

/*
  일반적인 순서:
  1.코드받기(인증) 2.액세스토큰(권한)
  3.사용자프로필 정보 등 가져옴
  4.그 정보를 토대로 회원가입을 자동으로 진행시키기도 하고,
  4-2.추가적인 정보가 필요할 경우(예: 쇼핑몰(집주소) 백화점(vip등급) 등..)
  추가정보를 위한 회원가입을 진행
*/

import com.cos.security1.config.oauth.PrincipalOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOAuth2UserService principalOAuth2UserService;

    @Bean // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해줌.
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(authorize -> authorize
                        // 접근권한설정
                        .antMatchers("/user/**").authenticated() // 인증이 이루어진 경우
                        .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                        .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                        .anyRequest().permitAll() // 인증이 이루어지지 않아도 모든 사용자가 접근
                )
                // 인증필터에 의해 로그인하지 않은 경우
                .formLogin()
                // 로그인 페이지로 이동
                .loginPage("/loginForm")
                // .usernameParameter("username")
                .loginProcessingUrl("/login") // /login 주소가 호출되면 시큐리티가 낚아ㅐ서 대신 로그인을 진행
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 코드 받는것이 아님 (액세스토큰+사용자프로필정보 받아옴)
                .userInfoEndpoint()
                .userService(principalOAuth2UserService);

        return http.build();
    }
}
