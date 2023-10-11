package com.example.batchwithoutweb.chunktask.config.flatfile.reader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.Customer;
import com.example.batchwithoutweb.chunktask.mapper.CustomerFieldSetMapper;
import com.example.batchwithoutweb.chunktask.mapper.DefaultLineMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FlatFileConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job v1FlatFileBatchJob(){
        return new JobBuilder("v1FlatFileBatchJob", jobRepository)
            .start(v1FlatFileStep1())
            .next(v1FlatFileStep2())
            .build();
    }

    @Bean
    public ItemReader v1FlatFileItemReader(){
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/customer.csv"));
        //Line을 객체와 매핑해주는 총괄 클래스
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        //Line 토큰화
        lineMapper.setTokenizer(new DelimitedLineTokenizer());
        //LineTokenizer가 준 FieldSet을 객체로 변환하는 클래스
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
        flatFileItemReader.setLineMapper(lineMapper);
        flatFileItemReader.setLinesToSkip(1);
        return flatFileItemReader;
    }

    @Bean
    public Step v1FlatFileStep1(){
        return new StepBuilder("v1FlatFileStep1", jobRepository)
            .<String, Customer>chunk(5, transactionManager)
            .reader(v1FlatFileItemReader())
            .writer(new ItemWriter<Customer>() {
                @Override
                public void write(Chunk<? extends Customer> chunk) throws Exception {
                    log.info("items={}", chunk.getItems());
                }
            })
            .build();
    }

    @Bean
    public Step v1FlatFileStep2(){
        return new StepBuilder("v1FlatFileStep2", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    log.info("-----------------------------------------------------------");
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName={}", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
}
