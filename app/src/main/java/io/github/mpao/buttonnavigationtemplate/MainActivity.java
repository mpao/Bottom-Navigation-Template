package io.github.mpao.buttonnavigationtemplate;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String FRAGMENT_NORMAL = "FRAGMENT_NORMAL";
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFragment( new HomeFragment());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewFragment(new HomeFragment());
                        return true;
                    case R.id.navigation_dashboard:
                        viewFragment(new DashboardFragment(), FRAGMENT_NORMAL);
                        return true;
                    case R.id.navigation_notifications:
                        viewFragment(new NotificationsFragment(), FRAGMENT_NORMAL);
                        return true;
                }
                return false;
            }

        });

    }

    //todo: interpolator dell'animazione
    //todo: onFragmentInteraction ?
    //todo: se clicco mentre animazione è in corso ? inibire bottone ?

    @Override
    public void onFragmentInteraction(Uri uri){
        // vedi commenti interfaccia OnFragmentInteractionListener
        // e http://developer.android.com/training/basics/fragments/communicating.html
    }

    private void viewFragment(Fragment fragment, String name){

        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        // gestisco overload del metodo, navigazione normale:
        // faccio il pop perchè se faccio start->fragment->home voglio che in ogni caso
        // dalla scheda home si esca, altrimenti sarebbe un doppio click su back
        if(name == null){
            fragmentManager.popBackStack(FRAGMENT_NORMAL, POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.commit();
            return;
        }
        // Navigatione tasto back: le linee guida per il Material Design riguardante la Bottom navigation
        // https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior
        // recitano : "On Android, the Back button does not navigate between bottom navigation bar views."
        // quindi il popBack non deve avvenire. Siccome a Google piace mettere delle linee guida,
        // ma poi trova soluzioni migliori e le applica all'interno delle sue applicazioni, anche io
        // utilizzo una navigazione simile a quella che avviene in Google+ : se sono sul fragment home esco,
        // altrimenti vado prima ad home, ed al successivo back esco.
        final int count = fragmentManager.getBackStackEntryCount();
        if( name.equals( FRAGMENT_NORMAL) ) {
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if( fragmentManager.getBackStackEntryCount() <= count){
                    // sto diminuendo lo stack, ovvero ho premuto back:
                    // https://developer.android.com/reference/android/app/FragmentManager.html#POP_BACK_STACK_INCLUSIVE
                    // elimino tutti i fragment con label FRAGMENT_NORMAL, quindi mi ritrovo
                    // solo con il fragment HOME, e ne seleziono il pulsante sulla BottomBar
                    fragmentManager.popBackStack(FRAGMENT_NORMAL, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    navigation.getMenu().getItem(0).setChecked(true);
                }
            }
        });

    }

    @SuppressWarnings("unused")
    private void viewFragment(Fragment fragment){
        // navigazione tasto back normale: clicco back ed esco
        viewFragment( fragment, null );

    }

}
