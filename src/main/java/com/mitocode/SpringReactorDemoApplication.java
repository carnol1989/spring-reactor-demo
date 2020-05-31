package com.mitocode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringReactorDemoApplication implements CommandLineRunner {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(SpringReactorDemoApplication.class);
	
	private static List<String> platos = new ArrayList<>();
	
	public static void main(String[] args) {
		platos.add("Hamburguesa");
		platos.add("Pizza");
		
		SpringApplication.run(SpringReactorDemoApplication.class, args);
	}

	public void crearMono() {
		log.info("crearMono: ");
		Mono<Integer> numero = Mono.just(7);
		numero.subscribe(x -> log.info("Numero: " + x));
	}
	
	public void crearFlux() {
		log.info("crearFlux: ");
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		obsPlatos.subscribe(p -> log.info(p));
		
		//Flux a Mono
		obsPlatos.collectList().subscribe(lista -> log.info(lista.toString()));
	}
	
	//doOnNext: Para depuración
	public void m1doOnNext() {
		log.info("m1doOnNext: ");
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		//obsPlatos.doOnNext(p -> log.info(p)).subscribe();
		obsPlatos.doOnNext(p -> {
			log.info(p);
		}).subscribe();
	}
	
	//map: Lógica de transformación.
	public void m2map() {
		log.info("m2map: ");
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		obsPlatos.map(p -> "Plato: " + p).subscribe(p -> log.info(p));
	}
	
	//flatMap: Devuelve otro flujo de datos
	public void m3flatMap() {
		log.info("m3flatMap: ");
		Mono.just("Jaime").flatMap(x -> Mono.just(29)).subscribe(rpta -> log.info("RPTA: " + rpta));//subscribe el último flujo de datos
		
		/*
		 * return service.listarPorId(id).flatMap(c -> {
			return service.eliminar(c.getId()).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));

		 */
	}
	
	public void m4range() {
		log.info("m4range: ");
		Flux<Integer> obs1 = Flux.range(0, 10);
		obs1 = obs1.map(x -> {
			return x + 1;
		});//.subscribe(x -> log.info("N: " + x));
		//obs1.map(x -> x + 1);
		obs1.subscribe(x -> log.info("N: " + x));
	}
	
	public void m5delayElements() {
		log.info("m5delayElements: ");
		Flux<Integer> obs1 = Flux.range(0, 10)
				.delayElements(Duration.ofSeconds(2))
				.doOnNext(x -> log.info(x.toString()));
		//obs1.subscribe();//no sirve cuando se usa delay
		obs1.blockLast();
	}
	
	@Override
	public void run(String... args) throws Exception {
		//crearMono();
		//crearFlux();
		//m1doOnNext();
		//m2map();
		//m3flatMap();
		//m4range();
		m5delayElements();
	}

}
