package com.leonidov.rest.exception;

//import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

//@XmlRootElement(name = "error")
public record ErrorResponse(String message, List<String> details) {
}
