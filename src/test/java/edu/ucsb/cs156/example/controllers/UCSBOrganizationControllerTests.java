package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;

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


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase{
    
    @MockBean
    UCSBOrganizationRepository ucsbOrgRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/ucsborganizations/all"))
            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception{
        mockMvc.perform(get("/api/ucsborganizations/all"))
            .andExpect(status().is(200));
    }

    @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations?orgCode=ABC"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

    @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403));
        }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsborganizations/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {


            UCSBOrganization ucsbOrg = UCSBOrganization.builder()
                            .orgCode("ABC")
                            .orgTranslation("Aleph Bet Cee")
                            .orgTranslationShort("Aleph Bet")
                            .inactive(false)
                            .build();

            when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.of(ucsbOrg));

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=ABC"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbOrgRepository, times(1)).findById(eq("ABC"));
            String expectedJson = mapper.writeValueAsString(ucsbOrg);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=ABC"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(ucsbOrgRepository, times(1)).findById(eq("ABC"));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("UCSBOrganization with id ABC not found", json.get("message"));
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsborgs() throws Exception {

        UCSBOrganization ucsbOrg1 = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee")
            .orgTranslationShort("Aleph Bet")
            .inactive(false)
            .build();

        UCSBOrganization ucsbOrg2 = UCSBOrganization.builder()
            .orgCode("RCK")
            .orgTranslation("UCSB Rock Climbing Club")
            .orgTranslationShort("Rock Climbing Club")
            .inactive(false)
            .build();

        ArrayList<UCSBOrganization> expectedOrgs = new ArrayList<>();
        expectedOrgs.addAll(Arrays.asList(ucsbOrg1, ucsbOrg2));

        when(ucsbOrgRepository.findAll()).thenReturn(expectedOrgs);

        // act
        MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                        .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbOrgRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedOrgs);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_ucsborg() throws Exception {
        // arrange

        UCSBOrganization ucsbOrg1 = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee")
            .orgTranslationShort("Aleph Bet")
            .inactive(true)
            .build();

        when(ucsbOrgRepository.save(eq(ucsbOrg1))).thenReturn(ucsbOrg1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/ucsborganizations/post?inactive=true&orgCode=ABC&orgTranslation=Aleph Bet Cee&orgTranslationShort=Aleph Bet")
                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbOrgRepository, times(1)).save(ucsbOrg1);
        String expectedJson = mapper.writeValueAsString(ucsbOrg1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_org() throws Exception {
            // arrange

        UCSBOrganization ucsbOrg1 = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee")
            .orgTranslationShort("Aleph Bet")
            .inactive(false)
            .build();

            when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.of(ucsbOrg1));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/ucsborganizations?orgCode=ABC")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbOrgRepository, times(1)).findById("ABC");
            verify(ucsbOrgRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBOrganization with id ABC deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_ucsborg_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/ucsborganizations?orgCode=ABC")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbOrgRepository, times(1)).findById("ABC");
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBOrganization with id ABC not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_ucsborg() throws Exception {
            
        UCSBOrganization ucsbOrgOrig = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee")
            .orgTranslationShort("Aleph Bet")
            .inactive(false)
            .build();

        UCSBOrganization ucsbOrgEdited = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee Delta")
            .orgTranslationShort("Aleph Bet Cee")
            .inactive(true)
            .build();

        String requestBody = mapper.writeValueAsString(ucsbOrgEdited);

        when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.of(ucsbOrgOrig));

        // act
        MvcResult response = mockMvc.perform(
            put("/api/ucsborganizations?orgCode=ABC")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("utf-8")
                            .content(requestBody)
                            .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbOrgRepository, times(1)).findById("ABC");
        verify(ucsbOrgRepository, times(1)).save(ucsbOrgEdited); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
        // arrange

        UCSBOrganization ucsbOrgEdited = UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("Aleph Bet Cee Delta")
            .orgTranslationShort("Aleph Bet Cee")
            .inactive(true)
            .build();

        String requestBody = mapper.writeValueAsString(ucsbOrgEdited);

        when(ucsbOrgRepository.findById(eq("ABC"))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/ucsborganizations?orgCode=ABC")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(ucsbOrgRepository, times(1)).findById("ABC");
        Map<String, Object> json = responseToJson(response);
        assertEquals("UCSBOrganization with id ABC not found", json.get("message"));

    }
}
