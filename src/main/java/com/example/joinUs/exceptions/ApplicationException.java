package com.example.joinUs.exceptions;

public class ApplicationException extends Exception{

    String message;

    public ApplicationException(Exception e){
        message=e.getMessage();
    }
}
