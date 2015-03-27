package com.HideoKuzeGits.Callback.crud;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcludeFromCrudOperations {
}
