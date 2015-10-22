package com.example.recipe.data.graph;


public class UnderflowException extends RuntimeException
{
    /**
     * Construct this exception object.
     * @param message the error message.
     */
    public UnderflowException( String message )
    {
        super( message );
    }
}