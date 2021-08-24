package alexandre.blended.data;

import alexandre.blended.model.Activity;
import alexandre.blended.model.Registration;
import alexandre.blended.repository.ActivityRepository;
import alexandre.blended.repository.RegistrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class LoadDatabase {

    @Bean
    public CommandLineRunner initDatabase(ActivityRepository activityRepository, RegistrationRepository registrationRepository){
        return args -> {
            LocalDateTime today = LocalDateTime.now().withNano(0);
            List<Activity> activities = new ArrayList<>();
            List<Registration> registrations = new ArrayList<>();
            activities.add(Activity.builder()
                    .id("interesting")
                    .name("Interesting")
                    .dates(Collections.singletonList(today.toString()))
                    .build());
            activities.add(Activity.builder()
                .id("multidate")
                .name("Long event")
                .dates(List.of(today.toString(), today.plusDays(1).toString(), today.plusDays(2).toString()))
                .build());
            activityRepository.saveAll(activities);
            registrations.add(Registration.builder()
                .id("registration")
                .activityId("interesting")
                .firstName("Hello")
                .lastName("There")
                .email("Hello@hotmail.com")
                .telephoneNumber("123456")
                    .date(LocalDateTime.now().withNano(0).toString())
            .build());
            registrationRepository.saveAll(registrations);
            System.out.println("Finished saving activities: " + activities.size());
            System.out.println("Finished saving registrations: " + registrations.size());
        };
    }
}
