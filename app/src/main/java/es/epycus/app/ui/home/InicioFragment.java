package es.epycus.app.ui.home;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentInicioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DashboardResponse;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.util.CacheManager;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";
    private FragmentInicioBinding binding;
    private SessionManager sessionManager;
    private CacheManager cacheManager;
    private boolean dashboardDataLoaded = false;
    private boolean progresoDataLoaded = false;
    private boolean animationsStarted = false;
    private int xpTargetProgress = 0;
    private final List<Call<?>> activeCalls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());
        cacheManager = CacheManager.getInstance(requireContext());

        String nombre = sessionManager.getUserName();
        binding.tvBienvenida.setText(getString(R.string.hola_formato, nombre != null ? nombre : "Epycus"));

        binding.swipeRefresh.setOnRefreshListener(() -> {
            animationsStarted = false;
            binding.pbXp.setProgress(0);
            cargarDashboard();
            cargarProgreso();
        });
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        setupXpGradient();
        cargarDashboard();
        cargarProgreso();
        setupClickListeners();

        return view;
    }

    private void setupXpGradient() {
        TypedValue tvAccent = new TypedValue();
        TypedValue tvAccentSecondary = new TypedValue();
        TypedValue tvBorder = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.epAccent, tvAccent, true);
        requireContext().getTheme().resolveAttribute(R.attr.epAccentSecondary, tvAccentSecondary, true);
        requireContext().getTheme().resolveAttribute(R.attr.epSurfaceBorder, tvBorder, true);

        int accentColor = tvAccent.resourceId != 0
                ? ContextCompat.getColor(requireContext(), tvAccent.resourceId) : tvAccent.data;
        int accentSecondaryColor = tvAccentSecondary.resourceId != 0
                ? ContextCompat.getColor(requireContext(), tvAccentSecondary.resourceId) : tvAccentSecondary.data;
        int borderColor = tvBorder.resourceId != 0
                ? ContextCompat.getColor(requireContext(), tvBorder.resourceId) : tvBorder.data;

        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setShape(GradientDrawable.RECTANGLE);
        trackBg.setCornerRadius(7);
        trackBg.setColor(borderColor);

        GradientDrawable progressFg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{accentColor, accentSecondaryColor}
        );
        progressFg.setCornerRadius(7);

        ClipDrawable progressClip = new ClipDrawable(progressFg, Gravity.START, ClipDrawable.HORIZONTAL);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{trackBg, progressClip});

        binding.pbXp.setProgressDrawable(layerDrawable);
        binding.pbXp.setProgress(0);
    }

    private void setupClickListeners() {
        binding.cardAddHabit.setOnClickListener(v -> {
            if (getActivity() instanceof MainContainerActivity) {
                ((MainContainerActivity) getActivity()).seleccionarTab(R.id.nav_habitos);
            }
        });

        binding.cardMision.setOnClickListener(v -> {
            if (getActivity() instanceof MainContainerActivity) {
                ((MainContainerActivity) getActivity()).seleccionarTab(R.id.nav_misiones);
            }
        });

        binding.cardFocus.setOnClickListener(v -> navegarAPomodoro());

        binding.cardHabitosPendientes.setOnClickListener(v -> {
            if (getActivity() instanceof MainContainerActivity) {
                ((MainContainerActivity) getActivity()).seleccionarTab(R.id.nav_habitos);
            }
        });
    }

    private void navegarAPomodoro() {
        if (getActivity() instanceof MainContainerActivity) {
            ((MainContainerActivity) getActivity()).navegarAPomodoro();
        }
    }

    private void cargarDashboard() {
        binding.loadingView.setVisibility(View.VISIBLE);

        Call<RespuestaApi<DashboardResponse>> call = RetrofitClient.getInstance(requireContext()).getApiDashboardService()
                .resumen();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<DashboardResponse>> call,
                                   @NonNull Response<RespuestaApi<DashboardResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {
                    try {
                        DashboardResponse data = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        cacheManager.put("dashboard", json, CacheManager.TTL_DASHBOARD);
                        dashboardDataLoaded = true;

                        DashboardResponse.Kpis kpis = data.getKpis();

                        int habitos = (kpis != null)
                                ? kpis.getHabitosPendientes() : data.getHabitosPendientes();
                        int misiones = (kpis != null)
                                ? kpis.getMisionesPendientes() : data.getMisionesPendientes();

                        binding.tvHabitosPendientes.setText(String.valueOf(habitos));
                        binding.tvMisionesPendientes.setText(String.valueOf(misiones));

                        if (data.getFrase() != null) {
                            binding.tvFrase.setText(data.getFrase().getFrase());
                            binding.tvFraseAutor.setText(getString(R.string.autor_formato, data.getFrase().getAutor()));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing dashboard", e);
                        binding.tvHabitosPendientes.setText(String.valueOf(0));
                        binding.tvMisionesPendientes.setText(String.valueOf(0));
                    }
                }
                verificarCargaCompleta();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<DashboardResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (cargarDashboardDesdeCache()) {
                    dashboardDataLoaded = true;
                }
                verificarCargaCompleta();
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarDashboardDesdeCache() {
        String json = cacheManager.get("dashboard");
        if (json != null) {
            try {
                Gson gson = new Gson();
                DashboardResponse data = gson.fromJson(json, DashboardResponse.class);
                DashboardResponse.Kpis kpis = data.getKpis();

                int habitos = (kpis != null)
                        ? kpis.getHabitosPendientes() : data.getHabitosPendientes();
                int misiones = (kpis != null)
                        ? kpis.getMisionesPendientes() : data.getMisionesPendientes();

                binding.tvHabitosPendientes.setText(String.valueOf(habitos));
                binding.tvMisionesPendientes.setText(String.valueOf(misiones));
                if (data.getFrase() != null) {
                    binding.tvFrase.setText(data.getFrase().getFrase());
                    binding.tvFraseAutor.setText(getString(R.string.autor_formato, data.getFrase().getAutor()));
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached dashboard", e);
            }
        }
        return false;
    }

    private void cargarProgreso() {
        Call<RespuestaApi<GamificacionResponse>> call = RetrofitClient.getInstance(requireContext()).getApiGamificacionService()
                .miProgreso();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<GamificacionResponse>> call,
                                   @NonNull Response<RespuestaApi<GamificacionResponse>> response) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {
                    try {
                        GamificacionResponse data = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        cacheManager.put("progreso", json, CacheManager.TTL_PROGRESO);
                        progresoDataLoaded = true;
                        xpTargetProgress = (int) data.getPorcentajeProgreso();

                        binding.tvRacha.setText(String.valueOf(data.getRachaActual()));
                        binding.tvNivel.setText(getString(R.string.nv_formato, data.getNivel()));
                        binding.tvNivelTitulo.setText(getString(R.string.nivel_formato, data.getNivel())
                                + (data.getTitulo() != null ? " - " + data.getTitulo() : ""));
                        binding.tvXpText.setText(getString(R.string.xp_formato, data.getXpTotal()));
                        binding.tvXpPorcentaje.setText(getString(R.string.porcentaje_nivel, (int) data.getPorcentajeProgreso()));

                        int xpRestante = data.getXpParaSiguienteNivel();
                        if (xpRestante > 0) {
                            binding.tvSiguienteHito.setText(getString(R.string.siguiente_nivel_xp, xpRestante));
                            binding.tvSiguienteHito.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvSiguienteHito.setVisibility(View.GONE);
                        }

                        if (data.getImagenPersonaje() != null && !data.getImagenPersonaje().isEmpty()) {
                            Glide.with(InicioFragment.this)
                                    .load(data.getImagenPersonaje())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .error(R.drawable.ic_profile)
                                    .into(binding.ivPersonaje);
                        } else {
                            binding.ivPersonaje.setImageResource(R.drawable.ic_profile);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing progreso", e);
                    }
                }
                verificarCargaCompleta();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<GamificacionResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (cargarProgresoDesdeCache()) {
                    progresoDataLoaded = true;
                }
                verificarCargaCompleta();
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarProgresoDesdeCache() {
        String json = cacheManager.get("progreso");
        if (json != null) {
            try {
                Gson gson = new Gson();
                GamificacionResponse data = gson.fromJson(json, GamificacionResponse.class);
                progresoDataLoaded = true;
                xpTargetProgress = (int) data.getPorcentajeProgreso();

                binding.tvRacha.setText(String.valueOf(data.getRachaActual()));
                binding.tvNivel.setText(getString(R.string.nv_formato, data.getNivel()));
                binding.tvNivelTitulo.setText(getString(R.string.nivel_formato, data.getNivel())
                        + (data.getTitulo() != null ? " - " + data.getTitulo() : ""));
                binding.tvXpText.setText(getString(R.string.xp_formato, data.getXpTotal()));
                binding.tvXpPorcentaje.setText(getString(R.string.porcentaje_nivel, (int) data.getPorcentajeProgreso()));

                int xpRestante = data.getXpParaSiguienteNivel();
                if (xpRestante > 0) {
                    binding.tvSiguienteHito.setText(getString(R.string.siguiente_nivel_xp, xpRestante));
                    binding.tvSiguienteHito.setVisibility(View.VISIBLE);
                } else {
                    binding.tvSiguienteHito.setVisibility(View.GONE);
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached progreso", e);
            }
        }
        return false;
    }

    private void animarEntrada() {
        if (!isAdded() || animationsStarted) return;
        animationsStarted = true;

        binding.pbXp.animate().cancel();
        ObjectAnimator xpAnimator = ObjectAnimator.ofInt(binding.pbXp, "progress", 0, xpTargetProgress);
        xpAnimator.setDuration(500);
        xpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        xpAnimator.start();

        View[] cards = {
                binding.cardHabitosPendientes,
                binding.cardRacha,
                binding.cardNivel,
                binding.cardMisionesPendientes,
                (View) binding.pbXp.getParent(),
                (View) binding.tvFrase.getParent(),
                binding.tvHabilidadesLabel,
                binding.cardAddHabit,
                binding.cardMision,
                binding.cardFocus,
        };

        for (int i = 0; i < cards.length; i++) {
            View card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(30f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(i * 80)
                    .start();
        }
    }

    private void verificarCargaCompleta() {
        if (!dashboardDataLoaded && !progresoDataLoaded) {
            String cachedDashboard = cacheManager.get("dashboard");
            String cachedProgreso = cacheManager.get("progreso");
            if (cachedDashboard == null && cachedProgreso == null) {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            binding.emptyView.setVisibility(View.GONE);
            animarEntrada();
        }
    }

    private void mostrarErrorRed(Throwable t) {
        Snackbar.make(requireView(),
                getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        super.onDestroyView();
        binding = null;
    }
}
