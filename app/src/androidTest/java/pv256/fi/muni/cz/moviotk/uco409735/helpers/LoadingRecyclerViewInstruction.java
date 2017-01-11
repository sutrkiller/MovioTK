package pv256.fi.muni.cz.moviotk.uco409735.helpers;

import android.support.test.InstrumentationRegistry;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.azimolabs.conditionwatcher.Instruction;

import pv256.fi.muni.cz.moviotk.uco409735.App;
import pv256.fi.muni.cz.moviotk.uco409735.MainActivity;
import pv256.fi.muni.cz.moviotk.uco409735.R;

/**
 * Instruction to check whether RecylerView has finished loading.
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
        //noinspection SimplifiableIfStatement
        if (adapter == null) return false;
        return adapter.getItemCount() > 0;
    }
}
