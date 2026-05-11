package ru.itmo.lab.common.terminal;

abstract public class AbstractConsoleHandler
{
    public AbstractConsoleHandler() {}

    final protected void print( String message ) { System.out.println(message); }
    final protected void printCurLine( String message ) { System.out.print(message); }
    protected void error( String messege ) { print("Ошибка: " + messege); }
}
