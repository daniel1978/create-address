package ch.gisel.bpmn.create_address.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "ch.gisel.bpmn.create_address")
@EntityScan(basePackages = "ch.gisel.bpmn.create_address.entity")
@EnableJpaRepositories(basePackages = "ch.gisel.bpmn.create_address.repository")
@ImportResource("classpath:beans.xml")
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

//    @Bean(name="entityManagerFactory")
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        return sessionFactory;
//    }
}
