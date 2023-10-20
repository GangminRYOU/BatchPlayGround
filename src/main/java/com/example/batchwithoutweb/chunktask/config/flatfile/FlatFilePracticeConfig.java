package com.example.batchwithoutweb.chunktask.config.flatfile;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.config.flatfile.dto.InboundDemantiaCenterData;
import com.example.batchwithoutweb.chunktask.config.flatfile.fieldsetmapper.DemantiaClinicMapper;
import com.example.batchwithoutweb.chunktask.mapper.DefaultLineMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFilePracticeConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job demantiaCenterBatchJob(){
        return new JobBuilder("DemantiaCenterMigrationBatchJob", jobRepository)
            .start(chunkOrientedDemantiaCenterStep())
            .build();
    }

    @Bean
    public Step chunkOrientedDemantiaCenterStep(){
        return new StepBuilder("step1 - DemantiaCenter Info Migration to DB", jobRepository)
            .<InboundDemantiaCenterData, InboundDemantiaCenterData>chunk(10, transactionManager)
            .reader(flatFileDemantiaCenterReader())
            .writer(new ItemWriter<InboundDemantiaCenterData>() {
                @Override
                public void write(Chunk<? extends InboundDemantiaCenterData> chunk) throws Exception {
                    log.info("------------------------------------------------------------");
                    chunk.forEach(item -> {
                        log.info("Center Info={}", item);
                        log.info("Center Address={}", item.getCenterAddress());
                        log.info("Workers Info={}", item.getWorkersDto());
                        log.info("Clinic Info={}", item.getClinic());
                        log.info("Management Authotiry Info={}", item.getManagement());
                        log.info("Data Issue Authority Info", item.getDataIssueAuthorityDto());
                    });
                }
            })
            .faultTolerant().skip(DateTimeParseException.class)
            .build();
    }

    @Bean
    public ItemReader<? extends InboundDemantiaCenterData> flatFileDemantiaCenterReader(){
        FlatFileItemReader<InboundDemantiaCenterData> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("C:\\Users\\dbrkd\\GitFolder\\PrivateProject\\BatchWithOutWeb\\src\\main\\resources\\전국치매센터표준데이터.csv"));
        DefaultLineMapper<InboundDemantiaCenterData> lineMapper = new DefaultLineMapper<>();
        lineMapper.setTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new DemantiaClinicMapper());
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);
        itemReader.setEncoding("X-WINDOWS-949");
        itemReader.setStrict(false);
        return itemReader;
    }
}
