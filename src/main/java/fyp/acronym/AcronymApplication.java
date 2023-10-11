package fyp.acronym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static fyp.acronym.Acronym.fetchData;

@SpringBootApplication
public class AcronymApplication {

	public static void main(String[] args) throws IOException {

		SpringApplication.run(AcronymApplication.class, args);

		fetchData();
	}

}
