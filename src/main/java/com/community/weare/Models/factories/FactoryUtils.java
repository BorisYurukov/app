package com.community.weare.Models.factories;

public class FactoryUtils {

    public static  <T> T getNotNull(T n, T l) {

        if (n==null || n.hashCode()==0 ){
            return l;
        }
        if (n!=null || n.hashCode()!=0 ){
            return n;
        }

        return n != null && l != null && !n.equals(l) ? n : l;

    }

}
