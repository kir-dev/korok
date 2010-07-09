/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ezzel kell megjelölni azokat a getter metódusokat, melyeknek a visszatérési
 * értékük alapján rendezni szeretnénk egy listát az {@link hu.sch.web.wicket.util.AutoSorter AutoSorter}
 * segítségével. Értékként azt adjuk meg, hogy melyik property esetén kell az
 * {@link hu.sch.web.wicket.util.AutoSorter AutoSorter}nek rendezni.
 *
 * @author  messo
 * @since   2.3.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SortProperty {

    String value();
}
