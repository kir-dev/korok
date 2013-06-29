package hu.sch.web.wicket.components.tables;

/**
 * Ha egy táblázatban valamelyik bejegyzést ki akarunk tudni jelölni, akkor
 * implementálja ezt az interfészt. Ha egy táblázathoz akarunk jelölhető (ha admin
 * jogosultsága van) és csak listázható (sima user) entitásokat és ezeket egy
 * táblázatban akarjuk kezelni, akkor ez egy jó módszer lehet.
 *
 * @author  messo
 * @since   2.3.1
 */
public interface SelectableEntry {

    public boolean getSelected();

    public void setSelected(boolean isSelected);
}
