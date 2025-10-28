package at.spengergasse.ehif_dbi;

import org.springframework.boot.SpringApplication;

public class TestEhifDbiApplication {

	public static void main(String[] args) {
		SpringApplication.from(EhifDbiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
