package org.mesibo.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mesibo.calls.MesiboAudioCallFragment;

public class AudioOutgoingFragment extends MesiboAudioCallFragment implements MesiboAudioCallFragment.MesiboAudioCallEvents {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outgoing_fragment_new, container, false);
        initAudioCall(this);
        return view;
    }

    @Override
    public void onAudiDeviceState(int i, boolean b) {

    }
}
