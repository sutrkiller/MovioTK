package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Launching activity of the program.
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 */
public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private static final String PREF_THEME = "PREF_THEME";
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getPreferences(MODE_PRIVATE);
        currentTheme = prefs.getInt(PREF_THEME, R.style.Theme);
        setTheme(currentTheme);
        setContentView(R.layout.activity_main);


    }

    public void button_onClick(View view) {
        int newTheme = currentTheme == R.style.Theme ? R.style.Theme_ThemeSecondary : R.style.Theme;
        prefs.edit().putInt(PREF_THEME, newTheme).apply();

        recreate();
    }
}
