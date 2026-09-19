package com.example.medicalclinicproxy.medicalClinicFeignClientConfig;

import com.example.medicalclinicproxy.customErrorDecoder.MedicalClinicClientErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class FeignMedicalClinicConfiguration {
    @Bean
    public ErrorDecoder medicalClinicErrorDecoder(){
        return new MedicalClinicClientErrorDecoder();
    }
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(2L),3);
    }
}
