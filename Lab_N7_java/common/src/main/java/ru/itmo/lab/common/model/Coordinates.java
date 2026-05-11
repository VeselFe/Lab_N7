package ru.itmo.lab.common.model;

import ru.itmo.lab.common.myExceptions.CreationException;

import java.io.Serializable;

/**
 * Неизменяемая модель координат (X, Y) для геолокации StudyGroup.
 * Содержит координаты в декартовой системе отсчета.
 * Поле X не может быть null.
 *
 * @see StudyGroup
 */
public class Coordinates implements Serializable
{
    private static final long serialVersionUID = 666L;
    /**  Поле не может быть null   */
    private Double x;
    private double y;


    public Coordinates( Double newX, double newY ) throws CreationException
    {
        if ( newX == null ) { throw  new CreationException("Поле X не может быть пустым"); }
        x = newX;
        y = newY;
    }

    /**
     * Возвращает X-координату.
     *
     * @return X-координата (гарантированно не null)
     */
    public Double getX()
    {
        return Double.valueOf(x);
    }
    /**
     * Возвращает Y-координату.
     * Прямой доступ к примитивному значению без потери точности.
     *
     * @return Y-координата (примитив double)
     */
    public double getY()
    {
        return y;
    }
}
