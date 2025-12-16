package com.user.userServiceSpringApplication.exception;

import java.util.List;

public class EmailExistException extends RuntimeException{

    private final List<String> errors;
    public  EmailExistException(String msg,List<String> errors){
        super(msg);
        this.errors=errors;
    }
    public List<String> getErrors(){
        return errors;
    }
}
