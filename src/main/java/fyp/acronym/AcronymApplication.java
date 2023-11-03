package fyp.acronym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static fyp.acronym.Acronym.fetchData;
import static fyp.acronym.multithread_subsequence.construct_trie;

@SpringBootApplication
public class AcronymApplication {

	public static void main(String[] args) throws IOException {
		System.out.println("website at http://localhost/spring/web/index.html");
		fetchData();
		construct_trie();
		SpringApplication.run(AcronymApplication.class, args);


	}


}
