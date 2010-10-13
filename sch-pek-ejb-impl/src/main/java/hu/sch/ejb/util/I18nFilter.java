package hu.sch.ejb.util;

import org.springframework.ldap.filter.CompareFilter;

/**
 * Internacionalizált filter
 *
 * Ez egy OpenDS specifikus egyedi filter, amivel lehetőség van ékezetektől
 * független keresések végrehajtására. Pl. ha 'Gábor'-ra keresel, akkor
 * megtalálod a 'Gabor'-t is és fordítva is. Egyelőre csak a 'sub'-os filter
 * van támogatva, de később könnyűszerrel ki lehet majd egészíteni, hogy mondjuk
 * a sima 'eq'-t is támogassa, stb.
 *
 * <strong>FIGYELEM!</strong>
 * <p>
 * Sajnos ez a filter egy kicsit buta az OpenDS-ben, pl, ha nem teszed be a '*'-okat
 * sub üzemmódban, akkor nem fog matchelni a teljes tartalomra...
 * </p>
 *
 * @see <a href="https://www.opends.org/wiki/page/SearchingUsingInternationalCollationRules">OpenDS dokumentáció</a>
 * @author aldaris
 */
public class I18nFilter extends CompareFilter {

    private static final String HUNGARIAN_COLLATOR = ":1.3.6.1.4.1.42.2.27.9.4.88.1.6";
    private static final String I18N_COMPARE = ":=";

    public I18nFilter(String attribute, String value) {
        super(attribute + HUNGARIAN_COLLATOR, value);
    }

    @Override
    protected String getCompareString() {
        return I18N_COMPARE;
    }
}
