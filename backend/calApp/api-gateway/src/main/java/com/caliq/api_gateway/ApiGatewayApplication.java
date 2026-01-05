package com.caliq.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("auth_route", r -> r.path("/auth/**")
//						.uri("http://localhost:8091")
//				)
//				.route("ledger_route", r -> r.path("/calapp/**")
//						.uri("http://localhost:8092")
//				)
//				.route("open_food_fact_api", r-> r.path("/product/**")
//						.uri("http:/localhost:8093"))
//				.build();
//
//	}

}
