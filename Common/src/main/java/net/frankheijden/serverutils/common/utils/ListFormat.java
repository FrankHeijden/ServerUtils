package net.frankheijden.serverutils.common.utils;

public interface ListFormat<T> {

    ListFormat<String> stringFormat = String::toString;

    String format(T t);

}
