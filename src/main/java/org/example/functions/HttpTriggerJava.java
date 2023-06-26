package org.example.functions;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.sun.corba.se.impl.orbutil.ObjectUtility;
import com.sun.xml.internal.ws.util.StringUtils;
import jdk.nashorn.internal.ir.RuntimeNode;

/**
 * Azure Functions with HTTP Trigger.
 */

public class HttpTriggerJava {
    /**
     * This function listens at endpoint "/api/HttpTriggerJava". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTriggerJava
     * 2. curl {your host}/api/HttpTriggerJava?name=HTTP%20Query
     */
    private static final String errorMessage = "Name cannot be null or empty";
    @FunctionName("HttpTriggerJava")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        // Parse query parameter
        ResponseDto response = new ResponseDto();
        RequestDto dto;
        Gson gson = new Gson();
        try {

            dto = gson.fromJson(request.getBody().get(), RequestDto.class);
            if(dto.getName() == null || dto.getName().isEmpty()){
                throw new JsonParseException(errorMessage);
            }

        } catch (JsonParseException e) {
            response.setMessage("Please include the JSON data in the request body. "+errorMessage);
            return buildResponse(request, response, HttpStatus.BAD_REQUEST, gson);

        } catch (Exception e){
            response.setMessage("Exception: " +e.getMessage());
            return buildResponse(request, response, HttpStatus.BAD_REQUEST, gson);
        }
        response.setMessage("Hello, "+dto.getName());
        return buildResponse(request, response, HttpStatus.OK, gson);

    }
    public HttpResponseMessage buildResponse(HttpRequestMessage request, ResponseDto response, HttpStatus status, Gson gson){
        return request.createResponseBuilder(status)
                .header("Content-Type", "application/json")
                .body(gson.toJson(response)).build();
    }

}

class RequestDto {
    private String name;

    public RequestDto() {
    }

    public RequestDto(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class ResponseDto {
    private String message;
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}