package com.example.monolithic.common.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    @PostConstruct
    private void init() {
        System.out.println(">>> JwtFilter init jwt secret : " + secret);
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        System.out.println(">>> JwtAuthenticationFilter doFilterInternal");

        String endPoint = request.getRequestURI();
        System.out.println(">>> JwtFilter User Endpoint : " + endPoint);
        String method = request.getMethod();
        System.out.println(">>> JwtFilter User Method : " + method);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // config 처리할 예정
            // response.setStatus(HttpServletResponse.SC_OK);
            // response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            // response.setHeader("Access-Control-Allow-Methods",
            // "GET,POST,PUT,DELETE,OPTIONS");
            // response.setHeader("Access-Control-Allow-Headers", "Authorization,
            // Content-Type");
            // response.setHeader("Access-Control-Allow-Credentials", "true");

            chain.doFilter(request, response); // controller와 연결
            return;
        }
        String authHeader = request.getHeader("Authorization");
        System.out.println(">>> JwtFilter Authorization : " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            System.out.println(">>> JwtFilter Not Authorization : ");
            chain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        System.out.println(">>> JwtFilter token : " + token);
        System.out.println(">>> JwtFilter token validation");
        try {
            // Claims = JWT 데이터
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 누가 이 Subject를 만들었는지 (누구인지에 대한 정보를 꺼내는 것)
            String email = claims.getSubject();
            System.out.println(">>> JwtAuthenticationFilter claims get email : " + email);

            // JWTProvider 의해서 Role 입력된 경우에만 해당
            String role = claims.get("role", String.class);
            System.out.println(">>> JwtAuthenticationFilter claims get role : " + role);

            // Spring Securoty 인증 정보를 담는 객체(Principal, credential, authorities(권한 리스트))
            // UsernamePasswordAuthenticationToken -> 보편적으로 가장 많이 씀
            // SecurityContextHolder에 심어버림 -> 어떤 사용자의 요청이냐를 Controller가 심어주고 꺼내는것?

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    role != null ? java.util.List.of(() -> "ROLE_" + role) : java.util.List.of());

            // 사용자의 요청과 인증정보객체를 연결
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Spring SecurityContext 저장 -> ctrl에서 필요할 때 꺼낼 수 있음
            // 사용자의 상태 정보를 확인할 수 있다는 것임
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }

}
