package com.example.mambabli.Provider;

import com.example.provider.controller.StudentController;
import com.example.provider.utill.GetMethodUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testGetStudent() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("mesh_origin", "test");
        headers.put("Mesh_Provider_Dataid", "12345");

        assertThrows(ArithmeticException.class, () -> {
            studentController.getStudent(1L, headers);
        });
    }

    @Test
    public void testPostEcho() {
        HashMap<String, String> headers = new HashMap<>();

        assertNotNull(studentController.handlePostRequest("{\"test\":\"data\"}", headers));
    }

    @Test
    public void testPutEcho() {
        assertNotNull(studentController.handlePutRequest("data", request));
    }

    @Test
    public void testGetMethodUtil() {
        assertEquals("", GetMethodUtil.safeGetString(null, "name"));
    }
}
