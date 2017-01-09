package pv256.fi.muni.cz.moviotk.uco409735.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.azimolabs.conditionwatcher.Instruction;

import pv256.fi.muni.cz.moviotk.uco409735.App;
import pv256.fi.muni.cz.moviotk.uco409735.MainActivity;
import pv256.fi.muni.cz.moviotk.uco409735.MainFragment;
import pv256.fi.muni.cz.moviotk.uco409735.R;

/**
 * Created by Tobias on 1/8/2017.
 */

public class LoadingRecyclerViewInstruction extends Instruction {

    @Override
    public String getDescription() {
        return "Recycler view must be loaded.";
    }

    @Override
    public boolean checkCondition() {

        MainActivity activity = (MainActivity) ((App)InstrumentationRegistry.getTargetContext().getApplicationContext()).getCurrentActivity();
        if (activity == null) return false;

        View inflated = activity.findViewById(R.id.recyclerView_movies_empty_infl);
        if (inflated != null && inflated.getVisibility() == View.VISIBLE) return false;

        RecyclerView res =(RecyclerView) activity.findViewById(R.id.recyclerView_movies);
        if (res == null) return false;
        RecyclerView.Adapter adapter = res.getAdapter();
        if (adapter == null) return false;
        return adapter.getItemCount() > 0;
    }
}
