package com.github.vlastikcz.ciconsumer.web.controller;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.github.vlastikcz.Application;
import com.github.vlastikcz.ciconsumer.IntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Category(IntegrationTest.class)
public class ReleaseDetailControllerTest {
    private static final MediaType CONTENT_TYPE = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void givenPost_whenEmptyRequestReceived_thenReturnError() throws Exception {
        final String request = "";
        mockMvc.perform(post(ReleaseController.RELEASE_ENDPOINT_PATH)
                .content(request)
                .contentType(CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPost_whenEmptyValuesReceived_thenReturnError() throws Exception {
        final String request = "{\"number_of_release\": \"\", \"datetime\": \"\"}";
        mockMvc.perform(post(ReleaseController.RELEASE_ENDPOINT_PATH)
                .content(request)
                .contentType(CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPost_whenEmptyEntityReceived_thenReturnError() throws Exception {
        final String request = "{\"number_of_release\": null, \"datetime\": null}";
        mockMvc.perform(post(ReleaseController.RELEASE_ENDPOINT_PATH)
                .content(request)
                .contentType(CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPost_whenValidEntityReceived_thenReturnStatusCreated() throws Exception {
        final String request = "{\"number_of_release\": \"2016.20\", \"datetime\": \"2016-01-01 23:59:30\"}";

        mockMvc.perform(post(ReleaseController.RELEASE_ENDPOINT_PATH)
                .content(request)
                .contentType(CONTENT_TYPE))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenPost_whenInvalidDateTimeReceived_thenReturnError() throws Exception {
        final String request = "{\"number_of_release\": \"1\", \"datetime\": \"invalidDateTime\"}";

        mockMvc.perform(post(ReleaseController.RELEASE_ENDPOINT_PATH)
                .content(request)
                .contentType(CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }


}