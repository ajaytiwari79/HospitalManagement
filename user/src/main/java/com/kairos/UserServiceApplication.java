package com.kairos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.config.LocalDateDeserializer;
import com.kairos.config.LocalDateSerializer;
import com.kairos.dto.QueueDTO;
import com.kairos.kafka.listener.DefaultKafkaMessageListener;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepositoryImpl;
import com.kairos.util.userContext.UserContextInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Application Start Point
 */
@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement(proxyTargetClass=true)
@EnableResourceServer
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNeo4jRepositories(basePackages = {"com.kairos.persistence.repository"},repositoryBaseClass = Neo4jBaseRepositoryImpl.class)
@EnableCircuitBreaker
@EnableKafka
public class UserServiceApplication extends WebMvcConfigurerAdapter{

	public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");
	//public static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);


	static{
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");
	}


	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(javaTimeModule);
		return objectMapper;
	}
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setObjectMapper(serializingObjectMapper());
		mappingJackson2HttpMessageConverter.setPrettyPrint(true);
		return mappingJackson2HttpMessageConverter;
	}
	@Bean
	public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter() {
		MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter = new MappingJackson2XmlHttpMessageConverter();
		mappingJackson2XmlHttpMessageConverter.setPrettyPrint(true);
		return mappingJackson2XmlHttpMessageConverter;
	}
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		converters.add(mappingJackson2HttpMessageConverter());
		converters.add(mappingJackson2XmlHttpMessageConverter());

	}
    @Profile("!local")
    @LoadBalanced
	@Primary
	@Bean
	public RestTemplate getCustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate template =restTemplateBuilder
				.interceptors(new UserContextInterceptor())
				.messageConverters(mappingJackson2HttpMessageConverter())
				.build();
		return template;
	}
    @Profile("!local")
	@LoadBalanced
	@Bean(name ="schedulerRestTemplate")
	public RestTemplate getCustomRestTemplateWithoutAuthorization(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate template =restTemplateBuilder
				.messageConverters(mappingJackson2HttpMessageConverter())
				.build();
		return template;
	}
    @Profile("local")
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }
    @Profile("local")
    @Bean(name ="schedulerRestTemplate")
    public RestTemplate getCustomRestTemplateWithoutAuthorizationLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

/*	private Map<String, Object> consumerProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		return props;
	}


	private Map<String, Object> senderProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}*/

	/*@Bean
	private KafkaTemplate<Integer, String> kafkaTemplate() {
		Map<String, Object> senderProps = senderProps();
		ProducerFactory<Integer, String> pf =
				new DefaultKafkaProducerFactory<Integer, String>(senderProps);
		KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
		return template;
	}*/

	/*@Bean
	ConcurrentKafkaListenerContainerFactory<Integer, String>
	kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
*/
/*		@Bean
	private KafkaMessageListenerContainer<Integer, String> kafkaContainer() {
		ContainerProperties containerProps = new ContainerProperties("userSchedulerQueue");
		containerProps.setMessageListener( new DefaultKafkaMessageListener());

		Map<String, Object> props = consumerProps();
		DefaultKafkaConsumerFactory<Integer, String> cf =
				new DefaultKafkaConsumerFactory<Integer, String>(props);
		KafkaMessageListenerContainer<Integer, String> container =
				new KafkaMessageListenerContainer<>(cf, containerProps);
		container.start();

		return container;
	}*/
	/*@Bean
	public ConsumerFactory<Integer, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		//props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());

		return props;
	}

	*//*@Bean
	public Listener listener() {
		return new Listener();
	}
*//*
	@Bean
	public ProducerFactory<Integer, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		//props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
		return props;
	}

	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate() {
		return new KafkaTemplate<Integer, String>(producerFactory());
	}*/


}

