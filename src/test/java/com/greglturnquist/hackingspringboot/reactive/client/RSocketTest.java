package com.greglturnquist.hackingspringboot.reactive.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
public class RSocketTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	ItemRepository repository;
	
	@Test
	void verifyRemoteOperationsThroughSocketRequestResponse() throws InterruptedException {
		
		this.repository.deleteAll()
			.as(StepVerifier::create)
			.verifyComplete();
		
		this.webTestClient.post().uri("/items/request-response")
			.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
			.exchange()
			.expectStatus().isCreated()
			.expectBody(Item.class)
			.value(item -> {
				assertThat(item.getId()).isNotNull();
				assertThat(item.getName()).isEqualTo("Alf alarm clock");
				assertThat(item.getDescription()).isEqualTo("nothing important");
				assertThat(item.getPrice()).isEqualTo(19.99);
			});
		
		Thread.sleep(500);
		
		this.repository.findAll()
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getId()).isNotNull();
				assertThat(item.getName()).isEqualTo("Alf alarm clock");
				assertThat(item.getDescription()).isEqualTo("nothing important");
				assertThat(item.getPrice()).isEqualTo(19.99);
				return true;
			})
			.verifyComplete();
	}
	
	@Test
	void verifyRemoteOperationsThroughSocketFireAndForget() throws InterruptedException {
		
		this.repository.deleteAll()
			.as(StepVerifier::create)
			.verifyComplete();
		
		this.webTestClient.post().uri("/items/fire-and-forget")
			.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
			.exchange()
			.expectStatus().isCreated()
			.expectBody().isEmpty();
		
		Thread.sleep(500);
		
		this.repository.findAll()
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getId()).isNotNull();
				assertThat(item.getName()).isEqualTo("Alf alarm clock");
				assertThat(item.getDescription()).isEqualTo("nothing important");
				assertThat(item.getPrice()).isEqualTo(19.99);
				return true;
			})
			.verifyComplete();
	}

}
