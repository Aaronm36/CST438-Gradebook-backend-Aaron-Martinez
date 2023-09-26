package com.cst438;


import com.cst438.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;


import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestAssignment {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Test
    public void getAssignmentByIdTest() throws Exception {
        MockHttpServletResponse response;

        response = mvc.perform(MockMvcRequestBuilders.get("/assignment/2").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        assertEquals(result.id(), 2, "correct");
        assertEquals(result.assignmentName(), "requirements", "correct");
    }

    @Test
    public void changeAssignmentNameTest() throws Exception {
        MockHttpServletResponse response;

        response = mvc.perform(MockMvcRequestBuilders.get("/assignment/1").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        assertEquals("db design", result.assignmentName());
        assertEquals("2021-09-01", result.dueDate());

        Assignment test = new Assignment();
        test.setName("CS");
        test.setDueDate(Date.valueOf("2021-09-10"));

        response = mvc
                .perform(MockMvcRequestBuilders.put("/assignment/changeName/1").accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(test)).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        response = mvc.perform(
                        get("/assignment/1").
                                accept(MediaType.APPLICATION_JSON)).
                andReturn().
                getResponse();
        assertEquals(200, response.getStatus());
        AssignmentDTO results = fromJsonString(
                response.getContentAsString(),
                AssignmentDTO.class);
        assertEquals(1, results.id(), "correct");
        assertEquals("CS", results.assignmentName(), "correct");
        assertEquals("2021-09-10", results.dueDate(), "correct");
    }

    @Test
    public void addingAssignmentTest() throws Exception {
        MockHttpServletResponse response;
        Assignment test = new Assignment();
        test.setName("CS");
        test.setDueDate(Date.valueOf("2021-09-10"));
        response = mvc.perform(
                        post("/assignment/31045/new").
                                content(asJsonString(test)).
                                contentType(MediaType.APPLICATION_JSON).
                                accept(MediaType.APPLICATION_JSON)).
                andReturn().
                getResponse();
        assertEquals(200, response.getStatus());
        AssignmentDTO actual_mr = fromJsonString(
                response.getContentAsString(),
                AssignmentDTO.class);
       assertEquals(3, actual_mr.id(), "correct");

        response = mvc.perform(
                        get("/assignment/3").
                                accept(MediaType.APPLICATION_JSON)).
                andReturn().
                getResponse();
        assertEquals(200, response.getStatus());
        AssignmentDTO results = fromJsonString(
                response.getContentAsString(),
                AssignmentDTO.class);
        assertEquals(3, results.id(), "correct");
        assertEquals("CS", results.assignmentName(), "correct");
        assertEquals("2021-09-10", results.dueDate(), "correct");
        assertEquals("CST 363 - Introduction to Database Systems", results.courseTitle(), "correct");
        assertEquals(31045, results.courseId(), "correct");
    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        MockHttpServletResponse response;

        response = mvc.perform(MockMvcRequestBuilders.get("/assignment/2").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        assertEquals(result.id(), 2, "correct");
        assertEquals(result.assignmentName(), "requirements", "correct");

        response = mvc
                .perform(MockMvcRequestBuilders.delete("/assignment/delete/1").accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(result)).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(409, response.getStatus());

        response = mvc
                .perform(MockMvcRequestBuilders.delete("/assignment/delete/1?force=yes").accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(result)).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        response = mvc.perform(MockMvcRequestBuilders.get("/assignment/1").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus(), "Assignment was Deleted");
    }


    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
