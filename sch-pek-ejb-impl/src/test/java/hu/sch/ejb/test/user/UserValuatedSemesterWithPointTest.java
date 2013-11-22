package hu.sch.ejb.test.user;

import hu.sch.domain.Group;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.SemesterPoint;
import hu.sch.domain.Valuation;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.user.User;
import hu.sch.ejb.EjbConstructorArgument;
import hu.sch.ejb.UserManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.GroupBuilder;
import hu.sch.ejb.test.builder.PointRequestBuilder;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.ejb.test.builder.ValuationBuilder;
import hu.sch.services.SystemManagerLocal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 *
 * @author ksisu
 */
public class UserValuatedSemesterWithPointTest extends AbstractDatabaseBackedTest {

    private List<SemesterPoint> semestersPoint;

    private UserManagerBean userManagerBean;

    @Override
    protected void before() {
        User user = new UserBuilder().create(getEm());
        getEm().persist(user);

        createTestPontRequests(user);

        getEm().flush();

        SystemManagerLocal systemManager =  mock(SystemManagerLocal.class);
        when(systemManager.getSzemeszter()).thenReturn(new Semester("201320141"));

        EjbConstructorArgument args = new EjbConstructorArgument();
        args.setEm(getEm());
        args.setSystemManager(systemManager);

        userManagerBean = new UserManagerBean(args);
        semestersPoint = userManagerBean.getAllValuatedSemesterWithPointForUser(user);
    }

    private void createTestPontRequests(User user) {

        Group groupA = new GroupBuilder().build();
        Group groupB = new GroupBuilder().build();
        Group groupC = new GroupBuilder().build();
        getEm().persist(groupA);
        getEm().persist(groupB);
        getEm().persist(groupC);

        // 1999/2000/2 A:0 B:0 C:0 SUM = 0
        addSemesterGroupPointPointState(user, new Semester("199920002"), groupA, 50, ValuationStatus.ELUTASITVA);

        // 2000/2001/1 A:20 B:30 C:0 SUM = sqrt(20^2+30^2) = 36
        addSemesterGroupPointAccepted(user, new Semester("200020011"), groupA, 20);
        addSemesterGroupPointAccepted(user, new Semester("200020011"), groupB, 30);
        addSemesterGroupPointPointState(user, new Semester("200020011"), groupC, 50, ValuationStatus.ELUTASITVA);

        // 2000/2001/2 A:5 B:10 C:15 SUM = sqrt((20+5)^2+(30+10)^2+15^2) = 49
        addSemesterGroupPointAccepted(user, new Semester("200020012"), groupA, 5);
        addSemesterGroupPointAccepted(user, new Semester("200020012"), groupB, 10);
        addSemesterGroupPointAccepted(user, new Semester("200020012"), groupC, 15);

        // 2001/2002/1 A:7 B:22 C:15 SUM = sqrt((5+7)^2+(10+22)^2+(15+15)^2) = 45
        addSemesterGroupPointAccepted(user, new Semester("200120021"), groupA, 7);
        addSemesterGroupPointAccepted(user, new Semester("200120021"), groupB, 22);
        addSemesterGroupPointAccepted(user, new Semester("200120021"), groupC, 15);

        // 2001/2002/2 A:0 B:0 C:0 SUM = sqrt(7^2+22^2+15^2) = 27

        // 2002/2003/1 A:0 B:0 C:0 SUM = 0
    }

    private void addSemesterGroupPointAccepted(User user, Semester s, Group g, Integer p) {
        addSemesterGroupPointPointState(user, s, g, p, ValuationStatus.ELFOGADVA);
    }

    private void addSemesterGroupPointPointState(User user, Semester s, Group g, Integer p, ValuationStatus ps) {
        Valuation tmpV = new ValuationBuilder()
                .withSemester(s)
                .withGroup(g)
                .withPointStatus(ps)
                .build();
        getEm().persist(tmpV);
        PointRequest tmpPR = new PointRequestBuilder()
                .withUser(user)
                .withPoint(p)
                .withValuation(tmpV)
                .build();
        getEm().persist(tmpPR);
    }

    /*
     * Search semester's point from semestersPoint.
     */
    private int getSemesterPoint(Semester s, List<SemesterPoint> semestersPoint) {
        for (SemesterPoint sp : semestersPoint) {
            if (sp.getSemester().equals(s)) {
                return sp.getPoint();
            }
        }
        return 0;
    }

    private int getSemesterPoint(Semester s) {
        return getSemesterPoint(s, semestersPoint);
    }

    @Test
    public void getSemesterPointTest() {
        List<SemesterPoint> semestersPoint = new ArrayList<>();
        semestersPoint.add(new SemesterPoint(new Semester("200120022"), 15));
        semestersPoint.add(new SemesterPoint(new Semester("200220031"), 55));
        semestersPoint.add(new SemesterPoint(new Semester("200220032"), 5));
        Assert.assertEquals(55, getSemesterPoint(new Semester("200220031"), semestersPoint));
    }

    @Test
    public void nonAcceptedPointRequest() {
        Assert.assertEquals(0, getSemesterPoint(new Semester("199920002")));
    }

    @Test
    public void valuatedSemesterAfterNonValuatedSemester() {
        Assert.assertEquals(36, getSemesterPoint(new Semester("200020011")));
    }

    @Test
    public void valuatedSemesterAfterValuatedSemester() {
        Assert.assertEquals(49, getSemesterPoint(new Semester("200020012")));
    }

    @Test
    public void valuatedSemesterAfterTwoValuatedSemesters() {
        Assert.assertEquals(45, getSemesterPoint(new Semester("200120021")));
    }

    @Test
    public void nonValuatedSemesterAfterValuatedSemester() {
        Assert.assertEquals(27, getSemesterPoint(new Semester("200120022")));
    }

    @Test
    public void nonValuatedSemesterAfterNonValuatedSemester() {
        Assert.assertEquals(0, getSemesterPoint(new Semester("200220031")));
    }
    
    @Test
    public void reverseSortedBySemester(){
        SemesterPoint prev = semestersPoint.get(0);
        for (SemesterPoint sp : semestersPoint) {
            if(prev.compareTo(sp) < 0){
                Assert.fail();
            }
        }
    }
}