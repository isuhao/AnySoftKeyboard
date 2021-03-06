package com.anysoftkeyboard.keyboards.views;

import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.anysoftkeyboard.ViewTestUtils;
import com.anysoftkeyboard.keyboards.AnyKeyboard;
import com.anysoftkeyboard.keyboards.Keyboard;
import com.anysoftkeyboard.keyboards.KeyboardFactory;
import com.menny.android.anysoftkeyboard.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class AnyKeyboardViewBaseTest {
    protected OnKeyboardActionListener mMockKeyboardListener;
    private AnyKeyboardViewBase mUnderTest;
    protected AnyKeyboard mEnglishKeyboard;
    private PointerTracker mMockPointerTrack;
    @Before
    public void setUp() throws Exception {
        mMockPointerTrack = Mockito.mock(PointerTracker.class);
        mMockKeyboardListener = Mockito.mock(OnKeyboardActionListener.class);
        AnyKeyboardViewBase view = createViewToTest(RuntimeEnvironment.application);
        setCreatedKeyboardView(view);
        mUnderTest.setOnKeyboardActionListener(mMockKeyboardListener);

        mEnglishKeyboard = KeyboardFactory.getEnabledKeyboards(RuntimeEnvironment.application)
                .get(0)
                .createKeyboard(RuntimeEnvironment.application, Keyboard.KEYBOARD_ROW_MODE_NORMAL);
        mEnglishKeyboard.loadKeyboard(mUnderTest.getThemedKeyboardDimens());

        mUnderTest.setKeyboard(mEnglishKeyboard, 0);
    }

    @CallSuper
    protected void setCreatedKeyboardView(@NonNull AnyKeyboardViewBase view) {
        mUnderTest = view;
    }

    protected AnyKeyboardViewBase createViewToTest(Context context) {
        return new AnyKeyboardViewBase(context, null);
    }

    @Test
    public void testKeyboardViewCreated() {
        Assert.assertNotNull(mUnderTest);
    }

    @Test
    public void testLongPressOutput() {
        AnyKeyboard.AnyKey key = (AnyKeyboard.AnyKey) mEnglishKeyboard.getKeys().get(5);
        key.longPressCode = 'z';
        mUnderTest.onLongPress(mEnglishKeyboard.getKeyboardAddOn(), key, false, mMockPointerTrack);

        Mockito.verify(mMockPointerTrack).onCancelEvent();
        Mockito.verify(mMockKeyboardListener).onKey(Mockito.eq((int)'z'), Mockito.same(key), Mockito.eq(0), Mockito.any(int[].class), Mockito.eq(true));
        Mockito.verify(mMockKeyboardListener, Mockito.never()).onKey(Mockito.eq(key.getPrimaryCode()), Mockito.any(Keyboard.Key.class), Mockito.anyInt(), Mockito.any(int[].class), Mockito.anyBoolean());
    }

    @Test
    public void testLongPressOutputTagsToast() {
        AnyKeyboard.AnyKey key = Mockito.mock(AnyKeyboard.AnyKey.class);
        Mockito.doReturn(Arrays.asList("tag", "tag2")).when(key).getKeyTags();

        mUnderTest.onLongPress(mEnglishKeyboard.getKeyboardAddOn(), key, false, mMockPointerTrack);
        Mockito.verify(mMockPointerTrack, Mockito.never()).onCancelEvent();
        Mockito.verify(mMockKeyboardListener, Mockito.never()).onKey(Mockito.anyInt(), Mockito.any(Keyboard.Key.class), Mockito.anyInt(), Mockito.any(int[].class), Mockito.anyBoolean());
        Assert.assertEquals(":tag, :tag2", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testLongPressKeyPressState() {
        final Keyboard.Key key = mEnglishKeyboard.getKeys().get(17);
        KeyDrawableStateProvider provider = new KeyDrawableStateProvider(R.attr.key_type_function, R.attr.key_type_action, R.attr.action_done, R.attr.action_search, R.attr.action_go);
        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));

        Point keyPoint = ViewTestUtils.getKeyCenterPoint(key);

        ViewTestUtils.navigateFromTo(mUnderTest, keyPoint, keyPoint, 400, true, false);
        Assert.assertArrayEquals(provider.KEY_STATE_PRESSED, key.getCurrentDrawableState(provider));

        mUnderTest.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, keyPoint.x, keyPoint.y, 0));

        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));
    }

    @Test
    public void testRegularPressKeyPressState() {
        final Keyboard.Key key = mEnglishKeyboard.getKeys().get(17);
        KeyDrawableStateProvider provider = new KeyDrawableStateProvider(R.attr.key_type_function, R.attr.key_type_action, R.attr.action_done, R.attr.action_search, R.attr.action_go);
        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));

        Point keyPoint = ViewTestUtils.getKeyCenterPoint(key);

        ViewTestUtils.navigateFromTo(mUnderTest, keyPoint, keyPoint, 60, true, false);
        Assert.assertArrayEquals(provider.KEY_STATE_PRESSED, key.getCurrentDrawableState(provider));

        mUnderTest.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, keyPoint.x, keyPoint.y, 0));

        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));
    }
    @Test
    public void testWithLongPressOutputLongPressKeyPressState() {
        final AnyKeyboard.AnyKey key = (AnyKeyboard.AnyKey) mEnglishKeyboard.getKeys().get(17);
        key.longPressCode = 'z';
        KeyDrawableStateProvider provider = new KeyDrawableStateProvider(R.attr.key_type_function, R.attr.key_type_action, R.attr.action_done, R.attr.action_search, R.attr.action_go);
        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));

        Point keyPoint = ViewTestUtils.getKeyCenterPoint(key);

        ViewTestUtils.navigateFromTo(mUnderTest, keyPoint, keyPoint, 80, true, false);
        Assert.assertArrayEquals(provider.KEY_STATE_PRESSED, key.getCurrentDrawableState(provider));
        ViewTestUtils.navigateFromTo(mUnderTest, keyPoint, keyPoint, 300, false, false);
        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));

        mUnderTest.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, keyPoint.x, keyPoint.y, 0));

        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));
    }

    @Test
    public void testWithLongPressOutputRegularPressKeyPressState() {
        final AnyKeyboard.AnyKey key = (AnyKeyboard.AnyKey) mEnglishKeyboard.getKeys().get(17);
        key.longPressCode = 'z';
        KeyDrawableStateProvider provider = new KeyDrawableStateProvider(R.attr.key_type_function, R.attr.key_type_action, R.attr.action_done, R.attr.action_search, R.attr.action_go);
        Assert.assertEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));

        Point keyPoint = ViewTestUtils.getKeyCenterPoint(key);

        ViewTestUtils.navigateFromTo(mUnderTest, keyPoint, keyPoint, 60, true, false);
        Assert.assertArrayEquals(provider.KEY_STATE_PRESSED, key.getCurrentDrawableState(provider));

        mUnderTest.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, keyPoint.x, keyPoint.y, 0));

        Assert.assertArrayEquals(provider.KEY_STATE_NORMAL, key.getCurrentDrawableState(provider));
    }
}
