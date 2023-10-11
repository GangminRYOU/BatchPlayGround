package com.example.batchwithoutweb.chunktask.config.xml;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.CustomerV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class XMLConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v4XMLBatchJob(){
        return new JobBuilder("v4XMLBatchJob", jobRepository)
            .start(v4XMLStep1())
            .build();
    }

    @Bean
    public Step v4XMLStep1(){
        return new StepBuilder("v4XMLStep1", jobRepository)
            .<CustomerV2, CustomerV2>chunk(3, transactionManager)
            .reader(XMLItemReader())
            .writer(new ItemWriter<CustomerV2>() {
                @Override
                public void write(Chunk<? extends CustomerV2> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            }).build();
    }

    @Bean
    public ItemReader<? extends CustomerV2> XMLItemReader(){
        return new StaxEventItemReaderBuilder<CustomerV2>()
            .name("XMLItemReader")
            .resource(new ClassPathResource("/customer.xml"))
            .addFragmentRootElements("customer")
            .unmarshaller(XMLItemUnmarshaller())
            .build();
    }


    @Bean
    public Unmarshaller XMLItemUnmarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", CustomerV2.class);
        aliases.put("id", Long.class);
        aliases.put("name", String.class);
        aliases.put("age", Integer.class);
        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);
        //XStream Version변경 이후로 class type에 대한 security 설정이 추가되었다.
        xStreamMarshaller.getXStream().allowTypes(new Class[]{CustomerV2.class});
        return xStreamMarshaller;
    }
}
