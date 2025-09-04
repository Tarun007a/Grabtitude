package com.project.grabtitude;

import com.project.grabtitude.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GrabtitudeApplicationTests {

	@Autowired
	private EmailService emailService;

	@Test
	void sendMail() throws IOException {
		emailService.send("bgmiloveeer@gmail.com", "Testing mail", "Ha bhai kaise ho");
	}

}
