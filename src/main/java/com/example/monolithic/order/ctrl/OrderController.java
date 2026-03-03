package com.example.monolithic.order.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.monolithic.order.domain.dto.OrderRequestDto;
import com.example.monolithic.order.domain.dto.OrderResponseDto;
import com.example.monolithic.order.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrderRequestDto request) {
        System.out.println("=== order ctrl path : /create");
        System.out.println("=== params : " + request);
        OrderResponseDto response = orderService.orderCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
