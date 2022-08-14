package co.topper.domain.controller;

import co.topper.AbstractionIntegrationTests;
import co.topper.domain.data.entity.Role;
import co.topper.domain.data.entity.UserEntity;
import co.topper.domain.data.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

class RewardsControllerTests extends AbstractionIntegrationTests {

    static final String ROOT_PATH = "/topper/v1/rewards";

    final UserEntity user = new UserEntity(
            "test-id-123@mail.com",
            "username-123",
            new BCryptPasswordEncoder().encode("pass"),
            Collections.emptySet(),
            Collections.emptySet(), Collections.emptyMap(),
            1000L, Instant.now(), Set.of(Role.USER)
    );

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    String token;

    @BeforeEach
    void setup() throws Exception {
        teardown();
        userRepository.save(user);

        token = getToken();
    }

    @AfterEach
    void teardown() {
        userRepository.deleteAll();
    }

    @Test
    void testIncrementVotes() throws Exception {
        final Long votes = 1000L;

        final String rewardsPath = ROOT_PATH + "?rewardedVotes=" + votes;
        Headers headers = new Headers(new Header("Authorization", "Bearer " + token));

        post(rewardsPath, headers, null, okStatus());

        UserEntity test = userRepository.findById(user.getEmailId())
                        .orElseThrow(() -> new RuntimeException("Error fetching User"));

        Assertions.assertEquals(user.getAvailableVotes() + votes, test.getAvailableVotes());
    }

}
