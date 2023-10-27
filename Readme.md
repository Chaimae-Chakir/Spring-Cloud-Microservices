## Architectures Micro-services avec Spring cloud

**Architecture**

**Première partie :**

<ul>
  <li>Customer-Service</li>
  <li>Inventory-Service</li>
  <li>Spring Cloud Gateway</li>
  <li>Eureka Discovery</li>
</ul>
 
**Deuxième Partie :**

<ul>
  <li>Billing Service avec Open Feign Rest Client</li>
</ul>

## Architecture

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23000.jpg" width="70%">
</div>

## Première partie

> **Customer-Service**

<ul>
  <li>Entité Customer</li>
</ul>

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
```

<ul>
  <li>CustomerRepository basée sur Spring Data</li>
</ul>

```java
@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
```

<ul>
  <li>Test</li>
</ul>

```java
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration restConfiguration) {
		restConfiguration.exposeIdsFor(Customer.class);
		return args -> {
			customerRepository.save(new Customer(null, "Mohamed", "mohamed@gmail.com"));
			customerRepository.save(new Customer(null, "chakir", "chakir@gmail.com"));
			customerRepository.save(new Customer(null, "Jinan", "jinan@gmail.com"));
			customerRepository.save(new Customer(null, "Aicha", "aicha@gmail.com"));
			customerRepository.findAll().forEach(System.out::println);
		};
	}
}
```

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23002.PNG" width="70%">
</div>

> **Inventory-Service**

<ul>
  <li>Entité Product</li>
</ul>

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private double quantity;
}
```

<ul>
  <li>ProductRepository basée sur Spring Data</li>
</ul>

```java
@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

<ul>
  <li>Test</li>
</ul>

```java
@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
    @Bean
	CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration restConfiguration){
		restConfiguration.exposeIdsFor(Product.class);
		return args -> {
			productRepository.save(new Product(null, "Ordinateur",788,12));
			productRepository.save(new Product(null, "Imprimante",88,129));
			productRepository.save(new Product(null, "Smartphone",1288,112));
			productRepository.findAll().forEach(p->{
				System.out.println(p.getName());
			});
		};
	}
}
```

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23003.PNG" width="70%">
</div>

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23004.PNG" width="70%">
</div>

> **Spring Cloud Gateway**

**Création la Gateway service en utilisant Spring Cloud Gateway**

<ul>
  <li>Teste de la Service proxy en utilisant une configuration Statique basée sur le fichier application.yml</li>
</ul>

```bash
spring:
  cloud:
    gateway:
      routes:
        - id: r1
          uri: http://localhost:8081/
          predicates:
                - Path=/customers/**
        - id: r2
          uri: http://localhost:8082/
          predicates:
            - Path=/products/**
```

Si il n'y a pas de fichier application.yml on peut utiliser cette configuration

```java
RouteLocator routeLocator(RouteLocatorBuilder builder){

		return builder.routes()
				.route("r1",r-> r.path("/customers/**").uri("http://localhost:8081/"))
				.route("r2",(r)->r.path("/products/**").uri("http://localhost:8082/"))
				.build();
	     }
```

<ul>
  <li>Teste de la Service proxy en utilisant une configuration Statique basée une configuration Java</li>
</ul>

```java
RouteLocator routeLocator(RouteLocatorBuilder builder){

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
	}
```

> **Eureka Discovery**

**Création de l’annuaire Registry Service basé sur NetFlix Eureka Server**

```java
@SpringBootApplication
@EnableEurekaServer  //pour activer le serveur eureka
public class EurekaDiscoveryApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaDiscoveryApplication.class, args);
	}
}
```

**Les dépendances utilsées**

```bash
<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
</dependencies>
```

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23005.PNG" width="70%">
</div>

## Deuxième Partie

<ul>
  <li>Billing Service avec Open Feign Rest Client</li>
</ul>

**Création du service Billing-Service en utilisant Open Feign pour communiquer avec les services Customer-service et Inventory-service**

**Les dépendances utilsées**

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23006.PNG" width="70%">
</div>

**Diagramme de classes**

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23007.PNG" width="70%">
</div>

**Structure du projet**

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23008.PNG" width="50%">
</div>

<ul>
  <li>Entité Bill</li>
</ul>

```java
@Entity
@Data @NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    @OneToMany(mappedBy = "bill")
    private Collection<ProductItem> productItems;
    private Long customerId;
    @Transient
    private Customer customer;
}
```

<ul>
  <li>Entité ProductItem</li>
</ul>

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double quantity;
    private double price;
    private Long productId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private Bill bill;
    @Transient
    private Product product;
    @Transient
    private  String productName;
}
```

<ul>
  <li>Model Customer</li>
</ul>

```java
@Data
public class Customer {
    private Long id;
    private String name;
    private String email;

}
```

<ul>
  <li>Model Product</li>
</ul>

```java
@Data
public class Product {
    private Long id;
    private String name;
    private double price;
    private double quantity;
}
```

<ul>
  <li>Bill Repository</li>
</ul>

```java
@RepositoryRestResource
public interface BillRepository extends JpaRepository<Bill, Long> {
}
```

<ul>
  <li>Product Item Repository</li>
</ul>

```java
@RepositoryRestResource
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    Collection<ProductItem> findByBillId(Long id);
}
```

<ul>
  <li>Customer Rest Client</li>
</ul>

```java
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerRestClient {
     @GetMapping(path = "/customers/{id}")
     Customer getCustomerById(@PathVariable(name = "id") Long id);
}
```

<ul>
  <li>Product Rest Client</li>
</ul>

```java
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductItemRestClient {
    @GetMapping(path = "/products")
    //PagedModel<Product> pageProducts(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size);
    PagedModel<Product> pageProducts();

    @GetMapping(path = "/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
```

<ul>
  <li>Billing Rest Controller</li>
</ul>

```java
@RestController
public class BillingRestController {
    private BillRepository billRepository;
    private ProductItemRepository productItemRepository;
    private CustomerRestClient customerRestClient;
    private ProductItemRestClient productItemRestClient;

    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productItemRestClient = productItemRestClient;
    }


    @GetMapping(path = "/fullBill/{id}")
    public Bill getBill(@PathVariable(name = "id") Long id){
        Bill bill=billRepository.findById(id).get();
        Customer customer=customerRestClient.getCustomerById(bill.getCustomerId());
        bill.setCustomer(customer);
        bill.getProductItems().forEach(pi->{
           // pi.setProduct(productItemRestClient.getProductById(pi.getProductId()));
             pi.setProductName(productItemRestClient.getProductById(pi.getProductId()).getName());
        });
        return bill;

    }
}
```

<ul>
  <li>Test</li>
</ul>

```java
@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(
            BillRepository billRepository,
            ProductItemRepository productItemRepository,
            CustomerRestClient customerRestClient,
            ProductItemRestClient productItemRestClient
    ) {

        return args -> {
            Customer c1 = customerRestClient.getCustomerById(1L);
            Bill bill = billRepository.save(new Bill(null, new Date(), null, c1.getId(), null));
            System.out.println("***********************");
            System.out.println("ID = " + c1.getId());
            System.out.println("Name = " + c1.getName());
            System.out.println("Email = " + c1.getEmail());
            PagedModel<Product> productPagedModel=productItemRestClient.pageProducts();
            productPagedModel.forEach(p->{
                ProductItem productItem=new ProductItem();
                productItem.setPrice(p.getPrice());
                productItem.setQuantity(1+ (int)Math.random()*100);
                productItem.setBill(bill);
                productItem.setProductId(p.getId());
                productItemRepository.save(productItem);
            });

                    };

    }
}
```

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23009.PNG" width="70%">
</div>

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23010.PNG" width="70%">
</div>

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23011.PNG" width="70%">
</div>

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23012.PNG" width="70%">
</div>

<div align="center">
<img src="https://github.com/Chaimae-Chakir/Spring-Cloud-Microservices/blob/master/captures/%23013.PNG" width="70%">
</div>
