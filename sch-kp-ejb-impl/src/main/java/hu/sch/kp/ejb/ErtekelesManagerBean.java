/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.ejb;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.BelepoTipus;
import hu.sch.domain.ElbiraltErtekeles;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesStatusz;
import hu.sch.domain.ErtekelesUzenet;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.Szemeszter;
import hu.sch.domain.Csoport;
import hu.sch.domain.ElfogadottBelepo;
import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.domain.ErtekelesStatisztika;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hege
 */
@Stateless
public class ErtekelesManagerBean implements ErtekelesManagerLocal {

    private static final String defaultSortColumnForErtekelesLista = "csoportNev";
    private static final Map<String, String> sortMapForErtekelesLista;
    private static final String statisztikaQuery = "SELECT new hu.sch.domain.ErtekelesStatisztika(e, " + "(SELECT avg(p.pont) FROM PontIgeny p WHERE p.ertekeles = e AND p.pont > 0) as atlagpont, " + "(SELECT count(*) as numKDO FROM BelepoIgeny as b WHERE b.ertekeles = e AND b.belepotipus=\'KDO\') as igenyeltkdo, " + "(SELECT count(*) as numKB FROM BelepoIgeny as b WHERE b.ertekeles = e AND b.belepotipus=\'KB\') as igenyeltkb, " + "(SELECT count(*) as numAB FROM BelepoIgeny as b WHERE b.ertekeles = e AND b.belepotipus=\'AB\') as igenyeltab" + ") FROM Ertekeles e ";
    @PersistenceContext
    EntityManager em;
    @EJB
    UserManagerLocal userManager;
    @EJB
    SystemManagerLocal systemManager;


    static {
        /* Hibernate BUG:
         * http://opensource.atlassian.com/projects/hibernate/browse/HHH-1902
         */
        sortMapForErtekelesLista = new HashMap<String, String>();
        sortMapForErtekelesLista.put("csoportNev", "e.csoport.nev ASC");
        sortMapForErtekelesLista.put("atlagPont", "col_1_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottKDO", "col_2_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottKB", "col_3_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottAB", "col_4_0_ DESC");
        sortMapForErtekelesLista.put("pontStatusz", "e.pontStatusz DESC");
        sortMapForErtekelesLista.put("belepoStatusz", "e.belepoStatusz DESC");
    }

    public void createErtekeles(Ertekeles ertekeles) {
        em.persist(ertekeles);
        em.flush();
    }

    public Ertekeles findErtekeles(Csoport csoport, Szemeszter szemeszter) {
        Query q = em.createNamedQuery(Ertekeles.findBySzemeszterAndCsoport);
        q.setParameter("szemeszter", szemeszter);
        q.setParameter("csoport", csoport);

        try {
            return (Ertekeles) q.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<ErtekelesStatisztika> getStatisztikaForErtekelesek(List<Long> ertekelesId) {
        String ids = Arrays.toString(ertekelesId.toArray());
        ids = ids.substring(1, ids.length() - 1);
        Query q = em.createQuery(statisztikaQuery + "WHERE e.id in (" + ids + ")");

        return (List<ErtekelesStatisztika>) q.getResultList();
    }

    public List<ErtekelesStatisztika> findErtekelesStatisztikaForSzemeszter(Szemeszter szemeszter) {
        return findErtekelesStatisztikaForSzemeszter(szemeszter, defaultSortColumnForErtekelesLista);
    }

    @SuppressWarnings({"unchecked"})
    public List<ErtekelesStatisztika> findErtekelesStatisztikaForSzemeszter(Szemeszter szemeszter, String sortColumn) {
        String sc = sortMapForErtekelesLista.get(sortColumn);
        if (sc == null) {
            throw new RuntimeException("Az eredményt nem lehet a megadott attribútum alapján rendezni");
        }
        Query q = em.createQuery(statisztikaQuery + "WHERE e.szemeszter=:szemeszter ORDER BY " + sc);
        q.setParameter("szemeszter", systemManager.getSzemeszter());

        return (List<ErtekelesStatisztika>) q.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<ErtekelesStatisztika> findElbiralatlanErtekelesStatisztika() {
        Query q = em.createQuery(statisztikaQuery + "WHERE e.szemeszter=:szemeszter " +
                "AND (e.pontStatusz=:pontStatusz OR e.belepoStatusz=:belepoStatusz)");

        q.setParameter("szemeszter", systemManager.getSzemeszter());
        q.setParameter("pontStatusz", ErtekelesStatusz.ELBIRALATLAN);
        q.setParameter("belepoStatusz", ErtekelesStatusz.ELBIRALATLAN);

        return (List<ErtekelesStatisztika>) q.getResultList();
    }

    protected Ertekeles elbiralastElokeszit(Ertekeles ertekeles, Felhasznalo elbiralo) {
        Ertekeles e = findErtekeles(ertekeles.getCsoport(), ertekeles.getSzemeszter());
        e.setElbiralo(elbiralo);
        e.setUtolsoElbiralas(new Date());

        return e;
    }

    private void PontIgenyElbiral(Ertekeles ertekeles, Felhasznalo elbiralo, boolean elfogad) {
        Ertekeles e = elbiralastElokeszit(ertekeles, elbiralo);
        if (elfogad) {
            e.setPontStatusz(ErtekelesStatusz.ELFOGADVA);
        } else {
            e.setPontStatusz(ErtekelesStatusz.ELUTASITVA);
        }

    }

    private void BelepoIgenyElbiral(Ertekeles ertekeles, Felhasznalo elbiralo, boolean elfogad) {
        Ertekeles e = elbiralastElokeszit(ertekeles, elbiralo);
        if (elfogad) {
            e.setBelepoStatusz(ErtekelesStatusz.ELFOGADVA);
        } else {
            e.setBelepoStatusz(ErtekelesStatusz.ELUTASITVA);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean ErtekeleseketElbiral(Collection<ElbiraltErtekeles> elbiralas, Felhasznalo felhasznalo) {
        for (ElbiraltErtekeles ee : elbiralas) {

            if ((ee.getPontStatusz().equals(ErtekelesStatusz.ELUTASITVA) ||
                    ee.getBelepoStatusz().equals(ErtekelesStatusz.ELUTASITVA)) &&
                    ee.getIndoklas() == null) {
                return false;
            }

            if (ee.getPontStatusz().equals(ErtekelesStatusz.ELFOGADVA) || ee.getPontStatusz().equals(ErtekelesStatusz.ELUTASITVA)) {
                PontIgenyElbiral(ee.getErtekeles(), felhasznalo, ee.getPontStatusz().equals(ErtekelesStatusz.ELFOGADVA));
            }
            if (ee.getBelepoStatusz().equals(ErtekelesStatusz.ELFOGADVA) || ee.getBelepoStatusz().equals(ErtekelesStatusz.ELUTASITVA)) {
                BelepoIgenyElbiral(ee.getErtekeles(), felhasznalo, ee.getBelepoStatusz().equals(ErtekelesStatusz.ELFOGADVA));
            }
            if (ee.getIndoklas() != null) {
                Uzen(ee.getErtekeles().getId(), felhasznalo, ee.getIndoklas());
            }
        }
        return true;
    }

    public void Uzen(Long ertekelesId, Felhasznalo uzeno, String uzenetStr) {
        Ertekeles ertekeles = em.find(Ertekeles.class, ertekelesId);

        ErtekelesUzenet uzenet = new ErtekelesUzenet();
        uzenet.setUzenet(uzenetStr);
        uzenet.setFelado(uzeno);
        uzenet.setDatum(new Date());
        uzenet.setErtekeles(ertekeles);
        ertekeles.getUzenetek().add(uzenet);
        ertekeles.setUtolsoModositas(new Date());

        em.persist(uzenet);
        em.merge(ertekeles);
    }

    public Ertekeles getErtekelesWithUzenetek(Long ertekelesId) {
        Query q = em.createNamedQuery(Ertekeles.findByIdUzenetJoined);
        q.setParameter("id", ertekelesId);

        return (Ertekeles) q.getSingleResult();
    }

    private void add(Ertekeles ertekeles, ErtekelesUzenet ertekelesUzenet) {
        //em.refresh(ertekeles);
        ertekeles.getUzenetek().add(ertekelesUzenet);
        ertekelesUzenet.setErtekeles(ertekeles);
        ertekeles.setUtolsoModositas(new Date());

        em.persist(ertekelesUzenet);
    }

    private void add(Ertekeles ertekeles, BelepoIgeny belepoIgeny) {
        //em.refresh(ertekeles);
        ertekeles.getBelepoIgenyek().add(belepoIgeny);
        belepoIgeny.setErtekeles(ertekeles);
        ertekeles.setUtolsoModositas(new Date());

        em.persist(belepoIgeny);
    }

    private void add(Ertekeles ertekeles, PontIgeny pontIgeny) {
        //em.refresh(ertekeles);
        ertekeles.getPontIgenyek().add(pontIgeny);
        pontIgeny.setErtekeles(ertekeles);
        ertekeles.setUtolsoModositas(new Date());

        em.persist(pontIgeny);
    }

    @SuppressWarnings({"unchecked"})
    public List<Ertekeles> findErtekeles(Csoport csoport) {
        Query q = em.createNamedQuery(Ertekeles.findByCsoport);
        q.setParameter("csoport", csoport);

        return (List<Ertekeles>) q.getResultList();
    }

    public void ujErtekeles(Csoport csoport, Felhasznalo felado, String szovegesErtekeles) {
        Ertekeles e = new Ertekeles();
        e.setFelado(felado);
        try {
            e.setSzemeszter(systemManager.getSzemeszter());
        } catch (NoSuchAttributeException ex) {
            throw new RuntimeException("Fatális hiba: szemeszter nincs beállítva?!", ex);
        }

        e.setCsoport(csoport);
        e.setSzovegesErtekeles(szovegesErtekeles);

        //TODO csoport flag alapján van-e joga rá?!

        em.persist(e);
    }

    public boolean isErtekelesLeadhato(Csoport csoport) {
        try {
            if (systemManager.getErtekelesIdoszak() !=
                    ErtekelesIdoszak.ERTEKELESLEADAS) {

                return false;
            }

            // leadási időszakban akkor adhat le, ha még nem adott le
            Ertekeles e = findErtekeles(csoport, systemManager.getSzemeszter());
            if (e == null) {
                return true;
            }
        } catch (NoSuchAttributeException ex) {
        }

        return false;
    }

    public void ujErtekelesUzenet(Long ertekelesId, Felhasznalo felado, String uzenet) {
        Ertekeles e = findErtekelesById(ertekelesId);
        ErtekelesUzenet uz = new ErtekelesUzenet();

        uz.setFelado(felado);
        uz.setUzenet(uzenet);
        add(e, uz);
    }

    public void pontIgenyekLeadasa(Long ertekelesId, List<PontIgeny> igenyek) {
        Ertekeles ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getPontStatusz().equals(ErtekelesStatusz.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (PontIgeny igeny : igenyek) {
            if (igeny.getErtekeles() == null) { //Új
                if (igeny.getPont() > 0) {
                    add(ertekeles, igeny);
                }
            } else { //módosítás
                PontIgeny ig = em.find(PontIgeny.class, igeny.getId());
                if (igeny.getPont().equals(0)) {
                    em.remove(ig);
                } else {
                    ig.setPont(igeny.getPont());
                }
            }
        }
        ertekeles.setPontStatusz(ErtekelesStatusz.ELBIRALATLAN);
        ertekeles.setUtolsoModositas(new Date());
    }

    public boolean belepoIgenyekLeadasa(Long ertekelesId, List<BelepoIgeny> igenyek) {
        Ertekeles ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getBelepoStatusz().equals(ErtekelesStatusz.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (BelepoIgeny igeny : igenyek) {
            if ((igeny.getBelepotipus().equals(BelepoTipus.AB) ||
                    igeny.getBelepotipus().equals(BelepoTipus.KB)) &&
                    (igeny.getSzovegesErtekeles() == null)) {
                return false;
            }
        }
        for (BelepoIgeny igeny : igenyek) {
            if (igeny.getErtekeles() == null) { //új
                add(ertekeles, igeny);
            } else { //módosítás
                BelepoIgeny ig = em.find(BelepoIgeny.class, igeny.getId());
                ig.setBelepotipus(igeny.getBelepotipus());
                if (ig.getBelepotipus() != BelepoTipus.KDO) {
                    ig.setSzovegesErtekeles(igeny.getSzovegesErtekeles());
                } else {
                    ig.setSzovegesErtekeles(null);
                }
            }
        }
        ertekeles.setBelepoStatusz(ErtekelesStatusz.ELBIRALATLAN);
        ertekeles.setUtolsoModositas(new Date());
        return true;
    }

    public Ertekeles findErtekelesById(Long ertekelesId) {
        return em.find(Ertekeles.class, ertekelesId);
    }

    @SuppressWarnings({"unchecked"})
    public List<BelepoIgeny> findBelepoIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT i FROM BelepoIgeny i JOIN FETCH i.felhasznalo " +
                "JOIN i.ertekeles WHERE i.ertekeles.id=:ertekelesId " +
                "ORDER BY i.felhasznalo.vezeteknev ASC, i.felhasznalo.keresztnev ASC");
        q.setParameter("ertekelesId", ertekelesId);

        return q.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<PontIgeny> findPontIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT i FROM PontIgeny i JOIN FETCH i.felhasznalo " +
                "JOIN i.ertekeles WHERE i.ertekeles.id=:ertekelesId " +
                "ORDER BY i.felhasznalo.vezeteknev ASC, i.felhasznalo.keresztnev ASC");
        q.setParameter("ertekelesId", ertekelesId);

        return q.getResultList();
    }

    public List<ElfogadottBelepo> findElfogadottBelepoIgenyekForSzemeszter(Szemeszter szemeszter) {
        Query q = em.createQuery("SELECT new hu.sch.domain.ElfogadottBelepo(i.felhasznalo.neptunkod," +
                "i.belepotipus) FROM BelepoIgeny i " +
                "WHERE i.ertekeles.szemeszter = :szemeszter AND i.ertekeles.belepoStatusz=:statusz");

        q.setParameter("statusz", ErtekelesStatusz.ELBIRALATLAN);
        q.setParameter("szemeszter", szemeszter);

        return q.getResultList();
    }
}
