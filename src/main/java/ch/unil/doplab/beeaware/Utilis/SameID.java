package ch.unil.doplab.beeaware.Utilis;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface SameID {
}
