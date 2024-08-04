package pl.kaczmarek.Recruitment.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RecruitmentTaskApplicationTests {

	@Test
	void contextLoads() {
		final var applicationClass = RecruitmentTaskApplication.class;

		final var context = SpringApplication.run(applicationClass);

		assertThat(context).isNotNull();
	}

}
