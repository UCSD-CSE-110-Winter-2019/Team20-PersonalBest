package cse110.ucsd.team20_personalbest;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Espresso {
    private static final String TEST_SERVICE = "MOCK_FIT";
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, MainActivity.class);
            result.putExtra("service_key", TEST_SERVICE);
            return result;
        }
    };

    @Test
    public void startupChangeGoal() throws InterruptedException {
        ViewInteraction editText = onView(
                allOf(withId(R.id.height_feet),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        editText.perform(click());
        Thread.sleep(1500);
        ViewInteraction editText2 = onView(
                allOf(withId(R.id.height_feet),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        editText2.perform(replaceText("5"), closeSoftKeyboard());
        Thread.sleep(1500);
        ViewInteraction editText3 = onView(
                allOf(withId(R.id.height_inches),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText3.perform(click());
        Thread.sleep(1500);
        ViewInteraction editText4 = onView(
                allOf(withId(R.id.height_inches),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText4.perform(replaceText("10"), closeSoftKeyboard());
        Thread.sleep(1500);
        ViewInteraction button = onView(
                allOf(withId(R.id.initial_next_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        button.perform(click());
        Thread.sleep(1500);

        onView(withId(R.id.textViewSteps)).check(matches(withText("300")));
        onView(withId(R.id.textViewGoal)).check(matches(withText("5000")));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_stats), withContentDescription("Stats"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
        Thread.sleep(1500);
        ViewInteraction view = onView(
                allOf(withId(R.id.chart),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.statFrag),
                                        0),
                                1),
                        isDisplayed()));
        view.check(matches(isDisplayed()));
        Thread.sleep(1500);
        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.changeFeet)).check(matches(withText("5")));
        onView(withId(R.id.changeInches)).check(matches(withText("10")));
        Thread.sleep(1500);

        onView(withId(R.id.changeSteps)).perform(replaceText("8000"));

        Thread.sleep(1500);
        onView(withText("Apply Changes")).perform(click());
        Thread.sleep(1500);
        onView(withText("Updates Applied")).inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        Thread.sleep(1500);
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.navigation_dashboard), withContentDescription("Dashboard"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        Thread.sleep(1500);
        onView(withId(R.id.textViewGoal)).check(matches(withText("8000")));

        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.navigation_friend), withContentDescription("Friends"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.floatBtn)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.EmailText)).check(matches(withText("Enter their email")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}