package com.example.monolithic.product.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.monolithic.product.domain.dto.ProductRequestDto;
import com.example.monolithic.product.domain.dto.ProductResponseDto;
import com.example.monolithic.product.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> create(ProductRequestDto request) {

        System.out.println("=== prodyct ctrl path : /create");
        System.out.println("=== params : " + request);

        ProductResponseDto response = productService.productCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
