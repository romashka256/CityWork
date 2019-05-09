package com.producticity.utils.commonutils;

import java.util.List;

public class ListUtils {
    public static final <A> A getLastElement(List<A> list) {
        return list != null ? getElement(list, list.size() - 1) : null;
    }

    public static final <A> A getFirstElement(List<A> list) {
        return list != null ? getElement(list, 0) : null;
    }

    private static final <A> A getElement(List<A> list, int pointer) {
        A res = null;
        if (!list.isEmpty()) {
            res = list.get(pointer);
        }
        return res;
    }
}
