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
	private static List<String> clientes = new ArrayList<>();
	
	public static void main(String[] args) {
		String vacio = "";
		platos.add("Hamburguesa");
		platos.add("Pizza");
		platos.add("Pollo a la Brasa");
		platos.add(vacio.trim());
		
		clientes.add("Carlos");
		clientes.add("Wilson");
		clientes.add("Elvia");
		
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
	
	public void m6zipWith() {
		log.info("m6zipWith: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		Flux<String> obsClientes = Flux.fromIterable(clientes);
		
		obsPlatos.zipWith(obsClientes, (p, c) -> String.format("Flux1: %s, Flux2: %s", p, c)).subscribe(x -> log.info(x));
		
		//return Mono.zip(cliente, plato, PlatoClienteDTO::new)
	}
	
	public void m7merge() {
		log.info("m7merge: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		Flux<String> obsClientes = Flux.fromIterable(clientes);
		
		Flux.merge(obsPlatos, obsClientes).subscribe(x -> log.info(x));
	}
	
	public void m8filter() {
		log.info("m8filter: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		
		obsPlatos.filter(p -> {
			return p.startsWith("H");
		}).subscribe(x -> log.info(x));
	}
	
	//takeLast: Toma los últimos elementos "n"
	public void m9takeLast() {
		log.info("m9takeLast: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		obsPlatos.takeLast(1).subscribe(x -> log.info(x));
	}
	
	//take: Toma los primeros elementos "n"
	public void m10take() {
		log.info("m10take: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		obsPlatos.take(2).subscribe(x -> log.info(x));
	}
	
	//defaultIfEmpty: Si encuentra un vacío debe devolverse una instancia del tipo de flux.
	public void m11DefaultIfEmpty() {
		log.info("m11DefaultIfEmpty: ");
		
		platos = new ArrayList<>();
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		obsPlatos.defaultIfEmpty("VACIO").subscribe(x -> log.info(x));
		
		//Forzar un flux vacío.
		//Flux.empty()
	}
	
	public void m12onErrorReturn() {
		log.info("m12onErrorReturn: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		
		obsPlatos.doOnNext(p -> {
			throw new ArithmeticException("MAL CALCULO (1)");
		})//.onErrorMap(ex -> new ArithmeticException("MAL CALCULO (2)"))////lanza el error en la traza
		.onErrorReturn("ERROR (3)")
		.subscribe(x -> log.info(x));
	}
	
	public void m13retry() {
		log.info("m13retry: ");
		
		Flux<String> obsPlatos = Flux.fromIterable(platos);
		
		obsPlatos.doOnNext(p -> {
			log.info("intentando...");
			throw new ArithmeticException("MAL CALCULO (1)");
		}).retry(3)//reintenta el número de veces indicado
		.onErrorReturn("ERROR (2)")
		.subscribe(x -> log.info(x));
	}
	
	@Override
	public void run(String... args) throws Exception {
		//crearMono();
		//crearFlux();
		//m1doOnNext();
		//m2map();
		//m3flatMap();
		//m4range();
		//m5delayElements();
		//m6zipWith();
		//m7merge();
		//m8filter();
		//m9takeLast();
		//m10take();
		//m11DefaultIfEmpty();
		//m12onErrorReturn();
		m13retry();
	}

}
