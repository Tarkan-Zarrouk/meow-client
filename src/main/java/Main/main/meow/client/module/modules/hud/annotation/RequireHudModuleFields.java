package Main.main.meow.client.module.modules.hud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * merely a marker that RequireHudModuleFieldsProcessor processes, this is done because it just helps me or anyone else who ends up contributing to
 * remember to add a Background Color Changer and a Text Color changer lol
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RequireHudModuleFields {

}
