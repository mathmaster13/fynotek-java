package io.github.mathmaster13.aspenlangs.fynotek;

import java.lang.annotation.*;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

@Nonnull
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
@TypeQualifierDefault({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface NonNullPackage {}