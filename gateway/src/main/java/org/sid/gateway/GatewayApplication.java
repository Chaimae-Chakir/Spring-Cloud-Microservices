package org.sid.gateway;

import com.netflix.discovery.DiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

    //@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder){
		//autre maniére de confugirer si vous n'avez pas utiliser le fichier app.yml
		/* on utilise ce configuration quand on a des urls fixes
		return builder.routes()
				.route("r1",r-> r.path("/customers/**").uri("http://localhost:8081/"))
				.route("r2",(r)->r.path("/products/**").uri("http://localhost:8082/"))
				.build();*/

		return builder.routes()
				.route("r1",r-> r.path("/customers/**").uri("lb://CUSTOMER-SERVICE"))
				.route("r2",(r)->r.path("/products/**").uri("lb://PRODUCT-SERVICE"))
				.build();
	}
	@Bean
	DiscoveryClientRouteDefinitionLocator definitionLocator(
			ReactiveDiscoveryClient rdc,
			DiscoveryLocatorProperties properties){
		return new DiscoveryClientRouteDefinitionLocator(rdc, properties);
	} // http://localhost:8888/PRODUCT-SERVICE/products
}
