package com.gradation.zmnnoory.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.common.jwt.JwtAccessDeniedHandler;
import com.gradation.zmnnoory.domain.member.entity.Gender;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.entity.Role;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import com.gradation.zmnnoory.domain.product.dto.request.ProductCreateRequest;
import com.gradation.zmnnoory.domain.product.dto.request.ProductUpdateRequest;
import com.gradation.zmnnoory.domain.product.dto.response.ProductResponse;
import com.gradation.zmnnoory.domain.product.entity.Category;
import com.gradation.zmnnoory.domain.product.entity.Product;
import com.gradation.zmnnoory.domain.product.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private MemberService memberService;

	@MockitoBean
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;  // AccessDeniedHandler mock 추가



	private final String ADMIN_EMAIL = "admin@test.com";
    private final String USER_EMAIL = "user@test.com";
    private final String BASE_URL = "/api/products";

    private Member adminMember;
    private Member userMember;
    private Product testProduct;
    private ProductResponse testProductResponse;
    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        // 관리자 회원 설정
        adminMember = Member.builder()
                .email(ADMIN_EMAIL)
                .password("password")
                .gender(Gender.MALE)
                .nickname("adminUser")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .build();

        // 일반 회원 설정
        userMember = Member.builder()
                .email(USER_EMAIL)
                .password("password")
                .gender(Gender.FEMALE)
                .nickname("regularUser")
                .birthday(LocalDate.of(1995, 5, 5))
                .role(Role.USER)
                .build();

        // 테스트용 상품 설정
        testProduct = Product.builder()
                .title("테스트 상품")
                .category(Category.CAFE)
                .price(5000L)
                .thumbnail("https://example.com/test.jpg")
                .build();
        
        // ID 필드 설정을 위한 리플렉션 사용
        try {
            java.lang.reflect.Field idField = testProduct.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testProduct, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 테스트용 상품 응답 설정
        testProductResponse = ProductResponse.builder()
                .title(testProduct.getTitle())
                .category(testProduct.getCategory().getDesc())
                .price(testProduct.getPrice())
                .thumbnail(testProduct.getThumbnail())
                .build();

        // 상품 생성 요청 설정
        createRequest = new ProductCreateRequest(
                "새 상품",
                Category.CAFE,
                4500L,
                "https://example.com/new.jpg"
        );

        // 상품 수정 요청 설정
        updateRequest = new ProductUpdateRequest(
                "수정된 상품",
                Category.GIFT,
                6000L,
                "https://example.com/updated.jpg"
        );

        // MemberService mock 설정
        given(memberService.findByEmail(ADMIN_EMAIL)).willReturn(adminMember);
        given(memberService.findByEmail(USER_EMAIL)).willReturn(userMember);

        // ProductService mock 설정
        given(productService.getAllProducts()).willReturn(List.of(testProductResponse));
        given(productService.getProductById(1L)).willReturn(testProductResponse);
        given(productService.createProduct(any(ProductCreateRequest.class))).willReturn(testProductResponse);
        given(productService.updateProduct(eq(1L), any(ProductUpdateRequest.class))).willReturn(testProductResponse);

	    // AccessDeniedHandler 동작 설정
	    doAnswer(invocation -> {
		    HttpServletResponse response = invocation.getArgument(1);
		    response.setStatus(HttpStatus.FORBIDDEN.value());
		    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		    response.getWriter().write(objectMapper.writeValueAsString(
				    BaseResponse.fail("접근 권한이 없습니다.", HttpStatus.FORBIDDEN)
		    ));
		    return null;
	    }).when(jwtAccessDeniedHandler).handle(any(), any(), any());

    }

    private void setAuthentication(String email, Role role) {
        UserDetails userDetails = new User(
                email,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()))
        );
        
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("일반 사용자 권한 테스트")
    class RegularUserTests {


        @BeforeEach
        void setUp() {
            setAuthentication(USER_EMAIL, Role.USER);
        }

        @AfterEach
        void tearDown() {
            clearAuthentication();
        }

        @Test
        @DisplayName("일반 사용자는 상품 목록을 조회할 수 있다")
        void getAllProductsTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    get(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.body.data[0].title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("일반 사용자는 상품 상세를 조회할 수 있다")
        void getProductByIdTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    get(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.body.data.title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("일반 사용자는 상품을 생성할 수 없다")
        void createProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest))
            );

            // then
            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("일반 사용자는 상품을 수정할 수 없다")
        void updateProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
            );

            // then
            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("일반 사용자는 상품을 삭제할 수 없다")
        void deleteProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    delete(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("관리자 권한 테스트")
    class AdminUserTests {

        @BeforeEach
        void setUp() {
            setAuthentication(ADMIN_EMAIL, Role.ADMIN);
        }

        @AfterEach
        void tearDown() {
            clearAuthentication();
        }

        @Test
        @DisplayName("관리자는 상품 목록을 조회할 수 있다")
        void getAllProductsTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    get(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.body.data[0].title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("관리자는 상품 상세를 조회할 수 있다")
        void getProductByIdTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    get(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.body.data.title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("관리자는 상품을 생성할 수 있다")
        void createProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest))
            );

            // then
            result.andExpect(status().isCreated())
                  .andExpect(jsonPath("$.body.data.title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("관리자는 상품을 수정할 수 있다")
        void updateProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
            );

            // then
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.body.data.title").value(testProductResponse.getTitle()));
        }

        @Test
        @DisplayName("관리자는 상품을 삭제할 수 있다")
        void deleteProductTest() throws Exception {
            // when
            ResultActions result = mockMvc.perform(
                    delete(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNoContent());
        }
    }
}