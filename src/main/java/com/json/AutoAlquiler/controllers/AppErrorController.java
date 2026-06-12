package com.json.AutoAlquiler.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        try{
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            System.out.println("Estado del error STATUS: "+status);
            if (status == null) {
                return "errors/error";
            }
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == 400){
                return "errors/400";
            }
            if (statusCode == 403){
                return "errors/403";
            }
            if (statusCode == 404) {
                return "errors/404";
            }

            return "errors/error";
        }catch(Exception e){
            System.out.println("[🚨 *** ALERTA *** 🚨] Ocurrio un Error general Causado por: "+ e);
            return "errors/error";
        }
    }
}