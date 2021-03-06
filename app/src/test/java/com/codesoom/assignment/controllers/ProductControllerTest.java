package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@DisplayName("ProductController 클래스")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private final List<Product> products = new ArrayList<>();
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("name 1")
                .imageUrl("imageURL 1")
                .maker("brand 1")
                .price(100)
                .build();
    }

    public void generateProducts() {
        Product product1 = Product.builder()
                .id(1L)
                .name("name 1")
                .imageUrl("imageURL 1")
                .maker("brand 1")
                .price(100)
                .build();
        Product product2 = Product.builder()
                .id(2L)
                .name("name 2")
                .imageUrl("imageURL 2")
                .maker("brand 2")
                .price(200)
                .build();

        products.add(product1);
        products.add(product2);
    }

    @Nested
    @DisplayName("POST /products 요청은")
    class Describe_POST_product {
        @Nested
        @DisplayName("생성할 상품의 정보가 주어지면")
        class Context_with_product {
            @BeforeEach
            void setUp() {
                Product productResponse = Product.builder()
                        .id(1L)
                        .name("name 1")
                        .imageUrl("imageURL 1")
                        .maker("brand 1")
                        .price(100)
                        .build();
                given(productService.createProduct(any(Product.class)))
                        .willReturn(productResponse);
            }

            @DisplayName("201 코드와 생성된 상품을 응답한다")
            @Test
            void it_returns_201_with_created_product() throws Exception {
                mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                        .andExpect(jsonPath("name").exists())
                        .andExpect(jsonPath("maker").exists())
                        .andExpect(jsonPath("price").exists())
                        .andExpect(jsonPath("imageUrl").exists())
                        .andExpect(status().isCreated());
            }
        }
    }

    @Nested
    @DisplayName("GET /products 요청은")
    class Describe_GET_products {
        @Nested
        @DisplayName("저장된 상품이 여러개 있다면")
        class Context_with_products {

            @BeforeEach
            void setUp() {
                generateProducts();
                given(productService.getProducts()).willReturn(products);
            }

            @DisplayName("200코드와 모든 상품 목록을 응답한다")
            @Test
            void it_responses_200_with_all_products() throws Exception {
                mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("저장된 상품이 없다면")
        class Context_without_products {

            @BeforeEach
            void setUp() {
                given(productService.getProducts()).willReturn(new ArrayList<>());
            }

            @DisplayName("200코드와 빈 목록을 응답한다")
            @Test
            void it_responses_200_with_all_products() throws Exception {
                mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(0)))
                        .andExpect(status().isOk());
            }
        }
    }
}
