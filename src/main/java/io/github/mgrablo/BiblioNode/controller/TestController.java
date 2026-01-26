package io.github.mgrablo.BiblioNode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/test")
class TestController {
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello World!";
	}
}
