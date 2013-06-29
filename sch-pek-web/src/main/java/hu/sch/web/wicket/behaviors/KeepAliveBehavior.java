package hu.sch.web.wicket.behaviors;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.time.Duration;

/**
 * Egy olyan Behavior, ami lehetővé teszi, hogy időnként megpingeljük a servert, így
 * a kritikus helyeken, ahol erre szükség van, nem járhat le a session.
 * 
 * @see <a href="http://chillenious.wordpress.com/2007/06/19/how-to-create-a-text-area-with-a-heart-beat-with-wicket/">How to create a text area with a heart beat with Wicket</a>
 */
public class KeepAliveBehavior extends AbstractAjaxTimerBehavior {

    /**
     * A default, hogy 15 percenként pingelünk.
     */
    public KeepAliveBehavior() {
        // 30 perces a session, így félidőnél hosszabbítsunk
        this(Duration.minutes(15));
    }

    /**
     * Finomhangolhatjuk, hogy milyen időközönként jelezzen a szervernek.
     *
     * @param duration milyen időközönként pingeljen
     */
    public KeepAliveBehavior(Duration duration) {
        super(duration);
    }

    @Override
    protected void onTimer(AjaxRequestTarget target) {
        // prevent wicket changing focus
        target.focusComponent(null);
    }
}
