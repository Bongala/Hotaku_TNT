package net.hotaku.manga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,	// TODO: Delete when implement authenticate feature
})
public class MangaApplication {
	public static void main(String[] args) {
		SpringApplication.run(MangaApplication.class, args);
	}

}
