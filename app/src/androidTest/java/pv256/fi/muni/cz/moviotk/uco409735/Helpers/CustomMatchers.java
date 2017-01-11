package pv256.fi.muni.cz.moviotk.uco409735.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Helps with comparing drawables in android tests
 */

@SuppressWarnings("unused")
public class CustomMatchers {
    public static Matcher<View> withBackground(final int resourceId) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return sameBitmap(view.getContext(), view.getBackground(), resourceId);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has background resource " + resourceId);
            }
        };
    }

    public static Matcher<View> withCompoundDrawable(final int resourceId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has compound drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                for (Drawable drawable : textView.getCompoundDrawables()) {
                    if (sameBitmap(textView.getContext(), drawable, resourceId)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Matcher<View> withImageDrawable(final int resourceId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has image drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                return sameBitmap(imageView.getContext(), imageView.getDrawable(), resourceId);
            }
        };
    }

    public static Matcher<View> withFabDrawable(final int resourceId) {
        return new BoundedMatcher<View, FloatingActionButton>(FloatingActionButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has button drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(FloatingActionButton fab) {
                return sameBitmap(fab.getContext(), fab.getDrawable(), resourceId);
            }
        };
    }

    private static boolean sameBitmap(Context context, Drawable drawable, int resourceId) {
        Drawable otherDrawable = AppCompatDrawableManager.get().getDrawable(context, resourceId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            otherDrawable = (DrawableCompat.wrap(otherDrawable)).mutate();
        }
        if (drawable == null || otherDrawable == null) {
            return false;
        }
        if (drawable instanceof StateListDrawable && otherDrawable instanceof StateListDrawable) {
            drawable = drawable.getCurrent();
            otherDrawable = otherDrawable.getCurrent();
        }
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }
        //noinspection SimplifiableIfStatement
        if (drawable instanceof VectorDrawable) {
            return !(drawable.getConstantState() == null || otherDrawable.getConstantState() == null) && drawable.getConstantState().equals(otherDrawable.getConstantState());
        }
        return false;
    }

}
