package com.e_eduspace.sellib;

/**
 * Created by Administrator on 2017/11/2.
 */

public interface TickedInterceptor<E,T> {
    T salt (E... e);
}
