package hu.sch.web.common;

import java.io.Serializable;

/**
 *
 * @author messo
 */
public class HeaderLink implements Serializable {

    private Class<? extends PekPage> clazz;
    private String text;

    public HeaderLink(Class<? extends PekPage> clazz, String text) {
        this.clazz = clazz;
        this.text = text;
    }

    public Class<? extends PekPage> getPageClass() {
        return clazz;
    }

    public String getText() {
        return text;
    }
}
