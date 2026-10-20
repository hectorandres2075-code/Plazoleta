package com.plazoleta.infrastructure.configuration;

import com.plazoleta.application.port.input.CreateDishUseCase;
import com.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.plazoleta.application.port.input.GetRestaurantUseCase;
import com.plazoleta.application.port.input.ListDishesUseCase;
import com.plazoleta.application.port.input.ListRestaurantsUseCase;
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
import com.plazoleta.infrastructure.output.client.UserRestClientAdapter;
import com.plazoleta.infrastructure.output.persistence.adapter.DishPersistenceAdapter;
import com.plazoleta.infrastructure.output.persistence.adapter.RestaurantPersistenceAdapter;
import com.plazoleta.infrastructure.output.persistence.repository.DishRepository;
import com.plazoleta.infrastructure.output.security.AuthenticatedUserAdapter;
import com.plazoleta.infrastructure.output.persistence.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
