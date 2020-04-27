package ca.mobilelive.retailstore.controller;

import ca.mobilelive.retailstore.Application;
import ca.mobilelive.retailstore.exception.ProductNotFoundException;
import ca.mobilelive.retailstore.model.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Noush on 10/25/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class ProductControllerTest {

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testCreateProduct() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/createProduct")
                .param("name", "Cup")
                .param("description", "used for tea and coffee")
                .param("quantity", "10")
                .param("unitPrice", "25$")).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product exists
    public void testGetProductCase1() throws Exception {
        mockMvc.perform(post("/createProduct")
                .param("name", "Cup")
                .param("description", "used for tea and coffee")
                .param("quantity", "10")
                .param("unitPrice", "25$"));

        ResultActions resultActions = mockMvc.perform(get("/getProduct")
                .param("id", "1")).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        System.out.println("********* Product *********");
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product does not exist
    public void testGetProductCase2() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/getProduct")
                .param("id", "0")).andExpect(status().isNotFound());
        MvcResult mvcResult = resultActions.andReturn();
        Assert.assertEquals(Constants.NO_PRODUCT_FOUND_MESSAGE, mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product exists
    public void testUpdateProductCase1() throws Exception {
        mockMvc.perform(post("/createProduct")
                .param("name", "Cup")
                .param("description", "used for tea and coffee")
                .param("quantity", "10")
                .param("unitPrice", "25$"));

        ResultActions resultActions = mockMvc.perform(put("/updateProduct")
                .param("id", "1")
                .param("name", "Cup")
                .param("description", "just for tea")
                .param("quantity", "100")
                .param("unitPrice", "30$")).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        ResultActions list = mockMvc.perform(get("/list"));
        System.out.println("********* Products *********");
        System.out.println(list.andReturn().getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product does not exist
    public void testUpdateProductCase2() throws Exception {
        mockMvc.perform(post("/createProduct")
                .param("name", "Cup")
                .param("description", "used for tea and coffee")
                .param("quantity", "10")
                .param("unitPrice", "25$"));

        ResultActions resultActions = mockMvc.perform(put("/updateProduct")
                .param("id", "100")
                .param("name", "Cup")
                .param("description", "just for tea")
                .param("quantity", "100")
                .param("unitPrice", "30$")).andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        ResultActions list = mockMvc.perform(get("/list"));
        System.out.println("********* Products *********");
        System.out.println(list.andReturn().getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product exists
    public void testDeleteProductOnSuccess() throws Exception {
        mockMvc.perform(post("/createProduct")
                .param("name", "Cup")
                .param("description", "used for tea and coffee")
                .param("quantity", "10")
                .param("unitPrice", "25$"));

        ResultActions resultActions = mockMvc.perform(delete("/deleteProduct").param("id", "1"))
                .andExpect(status().isOk());
        Assert.assertEquals(resultActions.andReturn().getResponse().getContentAsString(), Constants.SUCCESSFUL_DELETE_MESSAGE);
        ResultActions list = mockMvc.perform(get("/list"));
        System.out.println("********* Products *********");
        System.out.println(list.andReturn().getResponse().getContentAsString());
    }

    @Test
    // The test case is when the product does not exist
    public void testDeleteProductNotFoundException() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/deleteProduct").param("id", "1"))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(resultActions.andReturn().getResponse().getContentAsString(),
                Constants.PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
