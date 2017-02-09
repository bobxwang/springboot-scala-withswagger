package com.bob.java.webapi.handler;

import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by bob on 17/2/9.
 */
public class MdcPropagatingOnScheduleAction implements Func1<Action0, Action0> {

    @Override
    public Action0 call(final Action0 action0) {
        return new MdcPropagatingAction0(action0);
    }
}