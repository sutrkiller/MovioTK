package pv256.fi.muni.cz.moviotk.uco409735;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static pv256.fi.muni.cz.moviotk.uco409735.CustomMatchers.withCompoundDrawable;
import static pv256.fi.muni.cz.moviotk.uco409735.CustomMatchers.withFabDrawable;
import static pv256.fi.muni.cz.moviotk.uco409735.CustomMatchers.withImageDrawable;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailViewTest {

    private MainActivity mainActivity;

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init() {
        mainActivity = rule.getActivity();
    }

    @Test
    public void testDisplaysDetail() throws Exception {
        ViewInteraction detailFragment = onView(withId(R.id.fab_detail));

        detailFragment.check(ViewAssertions.doesNotExist());

        goToDetail();
        detailFragment.check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void fabClickChangesDrawable() throws Exception {
        goToDetail();

        ViewInteraction fab = onView(withId(R.id.fab_detail));

        fab.check(ViewAssertions.matches(isDisplayed()));

        boolean addVis = false;
        try {
            fab.check(ViewAssertions.matches(withFabDrawable(R.drawable.ic_add_black_24dp)));
            addVis = true;
        } catch (AssertionError e) {
        }
        try {
            fab.check(ViewAssertions.matches(withFabDrawable(R.drawable.ic_grade_black_24dp)));
        } catch (AssertionError e) {
            if (!addVis) throw e;
        }

        fab.perform(click());
        Thread.sleep(1000);
        fab.check(ViewAssertions.matches(withFabDrawable(addVis ? R.drawable.ic_grade_black_24dp : R.drawable.ic_add_black_24dp)));
    }

    private void goToDetail() throws Exception {
        Thread.sleep(1000);
        onView(withId(R.id.recyclerView_movies)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Thread.sleep(1000);
    }
}
