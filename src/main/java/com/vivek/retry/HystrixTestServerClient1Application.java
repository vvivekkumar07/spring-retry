package com.vivek.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableRetry
public class HystrixTestServerClient1Application {

	public static void main(String[] args) {
		SpringApplication.run(HystrixTestServerClient1Application.class, args);
	}
	
	@RestController
	class ShakyController{
		
		private final ShakyService  shakyService;
		
		@Autowired
		public ShakyController(ShakyService shakyService) {
			this.shakyService=shakyService;
		}
		
		@GetMapping("boom")
		public int boom(@RequestParam("value") int value) throws Exception{
			return this.shakyService.deviceNumber(value);
			
		}
		
	}
	
	class BoomException extends RuntimeException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		BoomException(String message){
			super(message);
		}
	}
	
	@Service
	public class ShakyService {
		
		@Recover
		public int fallBack() {
			return 2;
		}
		
		@Retryable
		public int deviceNumber(int value) throws InterruptedException{
			System.out.println("value"+value);
			if(value<5) {
				System.out.println("deviceNumber error");
				Thread.sleep(1000*3);
				throw new BoomException("BOOMMMM!!!!");
			}
			return 1;
		}

	}
	
}
