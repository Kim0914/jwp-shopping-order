package cart.ui;

import static cart.helper.RestDocsHelper.prettyDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cart.application.CartItemService;
import cart.dao.MemberDao;
import cart.domain.CartItem;
import cart.domain.Member;
import cart.domain.Product;
import cart.dto.request.CartItemQuantityUpdateRequest;
import cart.dto.request.CartItemRequest;
import cart.dto.response.CartItemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CartItemApiController.class)
@AutoConfigureRestDocs
class CartItemApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CartItemService cartItemService;

    @MockBean
    MemberDao memberDao;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원의 모든 장바구니 품목을 조회한다.")
    void showCartItems() throws Exception {
        Product product = new Product(1L, "곰돌이", 10000, "http:localhost:8080");
        Member member = new Member(1L, "aa@aaa.com", "1234", 1000);
        CartItem cartItem = new CartItem(1L, 3, product, member);
        CartItemResponse cartItemResponse = CartItemResponse.of(cartItem);
        given(cartItemService.findAllByMember(any(Member.class))).willReturn(List.of(cartItemResponse));
        given(memberDao.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(get("/cart-items")
                        .header("Authorization", "basic " + "YUBhLmNvbToxMjM0"))
                .andExpect(status().isOk())
                .andDo(prettyDocument(
                        "cart-items/showCartItems"
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("회원의 장바구니에 품목을 등록한다.")
    void addCartItems() throws Exception {
        Member member = new Member(1L, "aaa@google.com", "1234", 1000);
        CartItemRequest cartItemRequest = new CartItemRequest(1L);
        given(cartItemService.add(any(Member.class), any(CartItemRequest.class))).willReturn(1L);
        given(memberDao.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/cart-items")
                        .header("Authorization", "basic " + "YUBhLmNvbToxMjM0")
                        .content(objectMapper.writeValueAsString(cartItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(prettyDocument(
                        "cart-items/addCartItems"
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("회원의 장바구니 수량을 변경한다.")
    void updateCartItemQuantity() throws Exception {
        CartItemQuantityUpdateRequest updateRequest = new CartItemQuantityUpdateRequest(10);
        Member member = new Member(1L, "aaa@google.com", "1234", 1000);
        willDoNothing().given(cartItemService)
                .updateQuantity(any(Member.class), anyLong(), any(CartItemQuantityUpdateRequest.class));
        given(memberDao.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/cart-items/{id}", 1L)
                        .header("Authorization", "basic " + "YUBhLmNvbToxMjM0")
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(prettyDocument(
                        "cart-items/updateCartItems",
                        pathParameters(
                                parameterWithName("id").description("장바구니 ID")
                        ),
                        requestFields(
                                fieldWithPath("quantity").description("상품 수량")
                        )
                ))
                .andDo(print());
    }

    @Test
    @DisplayName("회원의 장바구니 품목을 삭제한다.")
    void deleteCartItemQuantity() throws Exception {
        Member member = new Member(1L, "aaa@google.com", "1234", 1000);
        willDoNothing().given(cartItemService)
                .remove(any(Member.class), anyLong());
        given(memberDao.findByEmail(any())).willReturn(Optional.of(member));

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/cart-items/{id}", 1L)
                        .header("Authorization", "basic " + "YUBhLmNvbToxMjM0"))
                .andExpect(status().isOk())
                .andDo(prettyDocument(
                        "cart-items/deleteCartItems",
                        pathParameters(
                                parameterWithName("id").description("장바구니 ID")
                        )
                ))
                .andDo(print());
    }
}
