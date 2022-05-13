package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.repositories.ArticleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = ArticleController.class)
@Import(TestConfig.class)
public class ArticleControllerTests extends ControllerTestCase{

        @MockBean
        ArticleRepository articleRepository;

        @MockBean
        UserRepository userRepository;


        // Authorization tests for /api/article/admin/all


        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/article/all"))
                        .andExpect(status().is(403)); // logged out users cannot get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/article/all"))
                        .andExpect(status().is(200)); // logged in 
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/article?id=7"))
                        .andExpect(status().is(403)); // logged out users cannot get by id 
        }


        // Authorization tests for /api/article/post


        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/article/post"))
                                .andExpect(status().is(403)); // logged out users cannot post 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/article/post"))
                                .andExpect(status().is(403)); // only admins can post
        }


        @Test
        public void logged_out_users_cannot_put() throws Exception {
                mockMvc.perform(post("/api/article/put"))
                                .andExpect(status().is(403)); // logged out users cannot put 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_can_put() throws Exception {
                mockMvc.perform(post("/api/article/put"))
                                .andExpect(status().is(403)); // logged in users cannot put
        }  


        // Authorization tests for /api/article/delete


        @Test
        public void logged_out_users_cannot_delete() throws Exception {
                mockMvc.perform(post("/api/article/delete"))
                                .andExpect(status().is(403)); // logged out users cannot delete 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_can_delete() throws Exception {
                mockMvc.perform(post("/api/article/delete"))
                                .andExpect(status().is(403)); // logged in users cannot delete
        }
        
        
        // Tests with mocks for database actions


        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDate ld = LocalDate.parse("2022-01-03");

                Article article = Article.builder()
                                .title("Some Article")
                                .url("urlforarticles.com")
                                .explanation("This is an article.")
                                .email("someemail@email.com")
                                .dateAdded(ld)
                                .build();

                when(articleRepository.findById(eq(7L))).thenReturn(Optional.of(article));

                // act
                MvcResult response = mockMvc.perform(get("/api/article?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(articleRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(article);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(articleRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/article?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(articleRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Article with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_articles() throws Exception {

                // arrange
                LocalDate ld1 = LocalDate.parse("2022-01-03");

                Article article1 = Article.builder()
                                .title("This is Article 1")
                                .url("urlforarticle1.com")
                                .explanation("This is article 1.")
                                .email("emailone@email.com")
                                .dateAdded(ld1)
                                .build();

                LocalDate ld2 = LocalDate.parse("2022-03-11");

                Article article2 = Article.builder()
                                .title("Some Article 2")
                                .url("urlforarticles2.com")
                                .explanation("This is article 2.")
                                .email("emailtwo@email.com")
                                .dateAdded(ld2)
                                .build();

                ArrayList<Article> expectedArticles = new ArrayList<>();
                expectedArticles.addAll(Arrays.asList(article1, article2));

                when(articleRepository.findAll()).thenReturn(expectedArticles);

                // act
                MvcResult response = mockMvc.perform(get("/api/article/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(articleRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedArticles);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_article() throws Exception {
                // arrange

                LocalDate ld1 = LocalDate.parse("2022-01-03");

                Article article1 = Article.builder()
                                .title("This is Article 1")
                                .url("urlforarticle1.com")
                                .explanation("This is article 1.")
                                .email("emailone@email.com")
                                .dateAdded(ld1)
                                .build();

                when(articleRepository.save(eq(article1))).thenReturn(article1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/article/post?title=This is Article 1&url=urlforarticle1.com&explanation=This is article 1.&email=emailone@email.com&dateAdded=2022-01-03")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).save(article1);
                String expectedJson = mapper.writeValueAsString(article1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_an_article() throws Exception {
                // arrange

                LocalDate ld1 = LocalDate.parse("2022-01-03");

                Article article1 = Article.builder()
                                .title("This is Article 1")
                                .url("urlforarticle1.com")
                                .explanation("This is article 1.")
                                .email("emailone@email.com")
                                .dateAdded(ld1)
                                .build();

                when(articleRepository.findById(eq(15L))).thenReturn(Optional.of(article1));


                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/article?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(15L);
                verify(articleRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Article with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_article_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(articleRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/article?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Article with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_article() throws Exception {
                // arrange
                LocalDate ld1 = LocalDate.parse("2022-01-03");

                Article articleOriginal = Article.builder()
                                .title("This is Article 1")
                                .url("urlforarticle1.com")
                                .explanation("This is article 1.")
                                .email("emailone@email.com")
                                .dateAdded(ld1)
                                .build();

                LocalDate ld2 = LocalDate.parse("2022-03-11");

                Article articleEdited = Article.builder()
                                .title("Some Article 2")
                                .url("urlforarticles2.com")
                                .explanation("This is article 2.")
                                .email("emailtwo@email.com")
                                .dateAdded(ld2)
                                .build();

                String requestBody = mapper.writeValueAsString(articleEdited);

                when(articleRepository.findById(eq(67L))).thenReturn(Optional.of(articleOriginal));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/article?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(67L);
                verify(articleRepository, times(1)).save(articleEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_article_that_does_not_exist() throws Exception {
                // arrange

                LocalDate ld1 = LocalDate.parse("2022-01-03");

                Article articleEdited = Article.builder()
                                .title("This is Article 1")
                                .url("urlforarticle1.com")
                                .explanation("This is article 1.")
                                .email("emailone@email.com")
                                .dateAdded(ld1)
                                .build();

                String requestBody = mapper.writeValueAsString(articleEdited);

                when(articleRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/article?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(articleRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Article with id 67 not found", json.get("message"));

        }


}
