package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    DoctorService doctorService;


}
