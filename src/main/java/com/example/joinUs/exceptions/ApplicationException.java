package com.example.joinUs.exceptions;

public class ApplicationException extends Exception{

    String message;

    public ApplicationException(String e){
        super(e);
        message=e;
    }
}
