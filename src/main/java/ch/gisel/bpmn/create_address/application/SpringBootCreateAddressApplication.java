package ch.gisel.bpmn.create_address.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ch.gisel.bpmn.create_address")
public class SpringBootCreateAddressApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCreateAddressApplication.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonJaxbJsonProvider jsonProvider(ObjectMapper objectMapper) {
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        return provider;
    }
}
