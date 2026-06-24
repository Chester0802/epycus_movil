package es.epycus.app.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import es.epycus.app.ui.diario.DiarioFragment;
import es.epycus.app.ui.habitos.HabitosFragment;
import es.epycus.app.ui.home.InicioFragment;
import es.epycus.app.ui.misiones.MisionesFragment;
import es.epycus.app.ui.perfil.PerfilFragment;

public class MainFragmentAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 5;

    public MainFragmentAdapter(@NonNull MainContainerActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new InicioFragment();
            case 1: return new HabitosFragment();
            case 2: return new MisionesFragment();
            case 3: return new DiarioFragment();
            case 4: return new PerfilFragment();
            default: return new InicioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
