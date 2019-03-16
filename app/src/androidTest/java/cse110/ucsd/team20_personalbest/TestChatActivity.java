package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cse110.ucsd.team20_personalbest.activities.ChatActivity;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestChatActivity {

    ChatActivity myActivity;

    @Rule
    public ActivityTestRule<ChatActivity> mActivityTestRule = new ActivityTestRule<ChatActivity>(ChatActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, ChatActivity.class);
            result.putExtra("FACTORY_KEY", "mock");
            result.putExtra("friend", "user2@team20pb.com");
            result.putExtra("UserName", "user1");
            return result;
        }
    };

    @Test
    public void testChatActivity () {
        myActivity = mActivityTestRule.getActivity();
        assertEquals("chatlogs",myActivity.COLLECTION_KEY);
        assertEquals("user2user1",myActivity.DOCUMENT_KEY);
        assertEquals("messages",myActivity.MESSAGES_KEY);

        myActivity.fb.sendMessage("YOU ARE FOOLED!", null);
    }
}
