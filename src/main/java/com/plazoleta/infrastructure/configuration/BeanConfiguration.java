package com.plazoleta.infrastructure.configuration;

import com.plazoleta.application.port.input.CreateDishUseCase;
import com.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.plazoleta.application.port.input.GetRestaurantUseCase;
import com.plazoleta.application.port.input.ListDishesUseCase;
import com.plazoleta.application.port.input.ListRestaurantsUseCase;
import com.plazoleta.application.port.input.UpdateDishStatusUseCase;
import com.plazoleta.application.port.input.UpdateDishUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.application.service.CreateDishService;
import com.plazoleta.application.service.CreateRestaurantService;
import com.plazoleta.application.service.GetRestaurantService;
import com.plazoleta.application.service.ListDishesService;
import com.plazoleta.application.service.ListRestaurantsService;
import com.plazoleta.application.service.UpdateDishService;
import com.plazoleta.application.service.UpdateDishStatusService;
import com.plazoleta.infrastructure.output.client.UserRestClientAdapter;
import com.plazoleta.infrastructure.output.persistence.adapter.DishPersistenceAdapter;
import com.plazoleta.infrastructure.output.persistence.adapter.RestaurantPersistenceAdapter;
import com.plazoleta.infrastructure.output.persistence.repository.DishRepository;
import com.plazoleta.infrastructure.output.security.AuthenticatedUserAdapter;
import com.plazoleta.infrastructure.output.persistence.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.plazoleta.application.port.input.CreateOrderUseCase;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.application.service.CreateOrderService;
import com.plazoleta.infrastructure.output.persistence.adapter.OrderPersistenceAdapter;
import com.plazoleta.infrastructure.output.persistence.repository.OrderRepository;
import com.plazoleta.application.port.input.ListOrdersUseCase;
import com.plazoleta.application.service.ListOrdersService;
import com.plazoleta.application.port.input.AssignOrderUseCase;
import com.plazoleta.application.service.AssignOrderService;
import com.plazoleta.application.port.input.MarkOrderReadyUseCase;
import com.plazoleta.application.port.output.MessagingServicePort;
import com.plazoleta.application.service.MarkOrderReadyService;
import com.plazoleta.infrastructure.output.client.MessagingRestClientAdapter;
import com.plazoleta.application.port.input.DeliverOrderUseCase;
import com.plazoleta.application.service.DeliverOrderService;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantPersistencePort restaurantPersistencePort(RestaurantRepository restaurantRepository) {
        return new RestaurantPersistenceAdapter(restaurantRepository);
    }

    @Bean
    public UserServicePort userServicePort(@Value("${usuarios.base-url}") String baseUrl) {
        return new UserRestClientAdapter(baseUrl);
    }

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(RestaurantPersistencePort restaurantPersistencePort,
                                                           UserServicePort userServicePort) {
        return new CreateRestaurantService(restaurantPersistencePort, userServicePort);
    }

    @Bean
    public DishPersistencePort dishPersistencePort(DishRepository dishRepository) {
        return new DishPersistenceAdapter(dishRepository);
    }

    @Bean
    public AuthenticatedUserPort authenticatedUserPort() {
        return new AuthenticatedUserAdapter();
    }

    @Bean
    public CreateDishUseCase createDishUseCase(DishPersistencePort dishPersistencePort,
                                               RestaurantPersistencePort restaurantPersistencePort,
                                               AuthenticatedUserPort authenticatedUserPort) {
        return new CreateDishService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
    }

    @Bean
    public UpdateDishUseCase updateDishUseCase(DishPersistencePort dishPersistencePort,
                                               RestaurantPersistencePort restaurantPersistencePort,
                                               AuthenticatedUserPort authenticatedUserPort) {
        return new UpdateDishService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
    }

    @Bean
    public GetRestaurantUseCase getRestaurantUseCase(RestaurantPersistencePort restaurantPersistencePort) {
        return new GetRestaurantService(restaurantPersistencePort);
    }

    @Bean
    public ListRestaurantsUseCase listRestaurantsUseCase(RestaurantPersistencePort restaurantPersistencePort) {
        return new ListRestaurantsService(restaurantPersistencePort);
    }

    @Bean
    public ListDishesUseCase listDishesUseCase(DishPersistencePort dishPersistencePort,
                                               RestaurantPersistencePort restaurantPersistencePort) {
        return new ListDishesService(dishPersistencePort, restaurantPersistencePort);
    }

    @Bean
    public UpdateDishStatusUseCase updateDishStatusUseCase(DishPersistencePort dishPersistencePort,
                                                           RestaurantPersistencePort restaurantPersistencePort,
                                                           AuthenticatedUserPort authenticatedUserPort) {
        return new UpdateDishStatusService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
    }

    @Bean
    public OrderPersistencePort orderPersistencePort(OrderRepository orderRepository) {
        return new OrderPersistenceAdapter(orderRepository);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderPersistencePort orderPersistencePort,
                                                 RestaurantPersistencePort restaurantPersistencePort,
                                                 DishPersistencePort dishPersistencePort,
                                                 AuthenticatedUserPort authenticatedUserPort) {
        return new CreateOrderService(orderPersistencePort, restaurantPersistencePort,
                dishPersistencePort, authenticatedUserPort);
    }

    @Bean
    public ListOrdersUseCase listOrdersUseCase(OrderPersistencePort orderPersistencePort,
                                               AuthenticatedUserPort authenticatedUserPort) {
        return new ListOrdersService(orderPersistencePort, authenticatedUserPort);
    }

    @Bean
    public AssignOrderUseCase assignOrderUseCase(OrderPersistencePort orderPersistencePort,
                                                 AuthenticatedUserPort authenticatedUserPort) {
        return new AssignOrderService(orderPersistencePort, authenticatedUserPort);
    }

    @Bean
    public MessagingServicePort messagingServicePort(
            @Value("${mensajeria.base-url:http://localhost:8083}") String baseUrl) {
        return new MessagingRestClientAdapter(baseUrl);
    }

    @Bean
    public MarkOrderReadyUseCase markOrderReadyUseCase(OrderPersistencePort orderPersistencePort,
                                                       UserServicePort userServicePort,
                                                       MessagingServicePort messagingServicePort,
                                                       AuthenticatedUserPort authenticatedUserPort) {
        return new MarkOrderReadyService(orderPersistencePort, userServicePort,
                messagingServicePort, authenticatedUserPort);
    }

    @Bean
    public DeliverOrderUseCase deliverOrderUseCase(OrderPersistencePort orderPersistencePort,
                                                   AuthenticatedUserPort authenticatedUserPort) {
        return new DeliverOrderService(orderPersistencePort, authenticatedUserPort);
    }
}
