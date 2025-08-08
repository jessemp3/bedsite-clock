package com.nativo.bedsideclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nativo.bedsideclock.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Locale;

// 1 - bateria

public class MainActivity extends AppCompatActivity implements View

        .OnClickListener {

    private ActivityMainBinding binding;
    private final Handler handler = new Handler();
    private Runnable runnable;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            binding.textViewBattery.setText(getString(R.string.label_battery, level));
        }
        // $d - inteiro
        // $s - string
        // $f - float/ double
        // $b - boolean
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); // se eu registro um reveiver, preciso desregistrar depois


        setFlags();
        setListeners();
        hideOptions();
        startBedsideClock();
    }

    private void startBedsideClock() {

        runnable = new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);


                String hoursMinuteFormat = String.format(Locale.getDefault(), "%02d:%02d", hours, minute);
                String secondFormat = String.format(Locale.getDefault(), "%02d", second);

                binding.textViewHourMinute.setText(hoursMinuteFormat);
                binding.textViewSecond.setText(secondFormat);

                long now = SystemClock.uptimeMillis();
                handler.postAtTime(runnable , now + (1000 - (now % 1000))); // 1 segundo
            }
        };
        runnable.run();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkBox_battery) {
            toggleBatteryLevel();
        } else if (v.getId() == R.id.imageView_close) {
            hideOptions();
        } else if (v.getId() == R.id.imageView_settings) {
            showOptions();
        }
    }

    private void showOptions() {
        int duration = 400; // duração da animaçã
        binding.checkBoxBattery.animate().translationY(0).setDuration(duration);
        binding.imageViewClose.animate().translationY(0).setDuration(duration);
    }

    private void hideOptions() {
        int duration = 400; // duração da animação

        binding.checkBoxBattery.post(() -> {
            int heighCheckBox = binding.checkBoxBattery.getHeight();
            binding.checkBoxBattery.animate().translationY(heighCheckBox).setDuration(duration);
        });

        binding.imageViewClose.post(() -> {
            int heighImageViewClose = binding.imageViewClose.getHeight();
            binding.imageViewClose.animate().translationY(heighImageViewClose).setDuration(duration);
        });
    }

    private void toggleBatteryLevel() {
        boolean isVisible = binding.textViewBattery.getVisibility() == View.VISIBLE;
        binding.textViewBattery.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    private void setListeners() {
        binding.checkBoxBattery.setOnClickListener(this);
        binding.imageViewClose.setOnClickListener(this);
        binding.imageViewSettings.setOnClickListener(this);

    }

    private void setFlags() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // flag de manter a tela ligada
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // essa flag toma a tela inteira e some com a barra de status

    }

    //broadcast


}