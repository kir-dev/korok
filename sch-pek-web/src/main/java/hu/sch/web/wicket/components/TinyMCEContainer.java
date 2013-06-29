package hu.sch.web.wicket.components;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 *
 * @author aldaris
 */
public class TinyMCEContainer extends Panel {

    public TinyMCEContainer(String id, IModel<String> model, boolean required) {
        super(id);

        TinyMCESettings settings = new TinyMCESettings(TinyMCESettings.Theme.advanced);

        settings.setToolbarAlign(TinyMCESettings.Align.left);
        settings.setToolbarLocation(TinyMCESettings.Location.top);
        settings.setStatusbarLocation(TinyMCESettings.Location.bottom);
        settings.setResizing(true);
        settings.setHorizontalResizing(false);

        settings.addCustomSetting("plugins : \"paste,preview\"");
        settings.addCustomSetting("theme_advanced_buttons1 : \"bold,italic,underline,strikethrough,|,formatselect,|,bullist,numlist,|,outdent,indent,|,undo,redo,|,cut,copy,paste,pastetext,pasteword,|,cleanup,code,preview\"");
        settings.addCustomSetting("theme_advanced_buttons2 : \"\"");
        TextArea<String> ta = new TextArea<String>("textarea", model);
        ta.add(new TinyMceBehavior(settings));
        ta.setRequired(required);
        add(ta);
    }
}
