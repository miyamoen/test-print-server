package heimdall.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import enkan.data.HttpRequest;
import enkan.data.HttpResponse;

import static enkan.util.BeanBuilder.builder;

public class IndexController {
    public String index() {
        return "Hello enkan & kotowari !!";
    }

    public HttpResponse print(HttpRequest request) {
        System.out.println("-----------------------------------------------------------------");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) request.getBody()))) {
            reader.lines().forEach(str -> System.out.println(str));
        } catch (IOException e) {
            return builder(HttpResponse.of(e.getMessage())).set(HttpResponse::setStatus, 500).build();
        }
        return builder(HttpResponse.of("Print!")).build();
    }

}
