package com.example.nfstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.nfstore.databinding.ActivityBerandaBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BerandaActivity extends AppCompatActivity {

    ActivityBerandaBinding binding;
    private HomeFragment hf;
    private AssetFragment af;
    String user = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        user = getIntent().getStringExtra("User");
        hf = new HomeFragment(user);
        af = new AssetFragment(user);
        binding = ActivityBerandaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(hf);
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(hf);
                    break;
                case R.id.Profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.Asset:
                    replaceFragment(af);
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.constraint_layout,fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hf != null){
            hf.refresh();
        }
    }
}