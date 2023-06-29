package com.bizzan.bitrade.core;


/**
 * @description: Parameter
 * @author: Hevin  E-mail:bizzanhevin@gmail.com
 * @create: 2021/07/04 14:41
 */
public class Parameter {
    public int type;
    public ParameterDirection direction;
    public Object value;

    public Parameter(int type, ParameterDirection direction, Object value) {
        this.type = type;
        this.direction = direction;
        this.value = value;
    }
}
