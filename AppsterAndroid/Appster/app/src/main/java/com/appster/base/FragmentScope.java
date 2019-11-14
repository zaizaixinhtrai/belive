package com.appster.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by thanhbc on 4/26/18.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentScope { }