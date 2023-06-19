package com.bhola.livevideochat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomePage_fragment();
            case 1:
                return new Messenger_fragment();
            case 2:
                return new DesiGirlsTeam_fragment();
            default:
                return new User_Profile_fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
