package com.ecosystem.batchservice;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.management.MXBean;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfing {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobRegistry jobRegistry;

    @Value("classpath:files/persons.csv")
    private Resource inputCsv;

    @Value("classpath:files/output.json")
    private Resource outputJson;

    @Bean
    public ItemReader<Person> itemReader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = {"id", "name", "age", "phone"};
        tokenizer.setNames(tokens);
        tokenizer.setDelimiter(";");
        reader.setResource(inputCsv);
        reader.setLinesToSkip(1);
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<Person, Person> itemProcessor() {
        return new FunctionItemProcessor<Person, Person>(person -> {
            person.setName(person.getName() + "-fatched!");
            return person;
        });
    }

    @Bean
    public JsonObjectMarshaller marshaller() {
        JacksonJsonObjectMarshaller marshaller = new JacksonJsonObjectMarshaller();
        return marshaller;
    }

    @Bean
    public ItemWriter<Person> itemWriter(JsonObjectMarshaller marshaller) {
        JsonFileItemWriter<Person> itemWriter = new JsonFileItemWriter<>(outputJson, marshaller);
        return itemWriter;
    }

    @Bean
    protected Step step1(ItemReader<Person> itemReader, ItemProcessor<Person, Person> itemProcessor, ItemWriter<Person> itemWriter) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(5)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job firstBatchJob(Step step1) {
        return jobBuilderFactory.get("firstBatchJob").start(step1).incrementer(new RunIdIncrementer()).build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
