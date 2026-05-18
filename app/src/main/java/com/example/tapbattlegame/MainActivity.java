package com.example.tapbattlegame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvScore, tvTimer;
    private Button btnTap;
    private RelativeLayout gameLayout;

    private int score = 0;
    private boolean gameActive = false;
    private CountDownTimer countDownTimer;
    private final Random random = new Random();

    private static final int GAME_DURATION_MS = 30000;
    private static final int BUTTON_MARGIN = 100; // px buffer from edges

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore    = findViewById(R.id.tvScore);
        tvTimer    = findViewById(R.id.tvTimer);
        btnTap     = findViewById(R.id.btnTap);
        gameLayout = findViewById(R.id.gameLayout);

        btnTap.setOnClickListener(v -> onTapButton());
    }

    // ── Called on every tap ──────────────────────────────────────────────────
    private void onTapButton() {
        if (!gameActive) {
            startGame();
            return;
        }
        score++;
        updateScoreDisplay();
        moveButtonToRandomPosition();
    }

    // ── Game lifecycle ───────────────────────────────────────────────────────
    private void startGame() {
        score = 0;
        gameActive = true;
        btnTap.setText("TAP!");
        updateScoreDisplay();
        moveButtonToRandomPosition();
        startTimer();
    }

    private void endGame() {
        gameActive = false;
        if (countDownTimer != null) countDownTimer.cancel();

        btnTap.setText("Play Again");

        // Move button back to center
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) btnTap.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        btnTap.setLayoutParams(params);

        tvScore.setText("Final Score: " + score);
        tvTimer.setText("Time: 0s");
    }

    // ── Timer ────────────────────────────────────────────────────────────────
    private void startTimer() {
        countDownTimer = new CountDownTimer(GAME_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                tvTimer.setText("Time: " + secondsLeft + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Time: 0s");
                endGame();
            }
        }.start();
    }

    // ── UI helpers ───────────────────────────────────────────────────────────
    private void updateScoreDisplay() {
        tvScore.setText("Score: " + score);
    }

    private void moveButtonToRandomPosition() {
        // Wait until layout has measured itself
        gameLayout.post(() -> {
            int layoutW = gameLayout.getWidth();
            int layoutH = gameLayout.getHeight();
            int btnW    = btnTap.getWidth();
            int btnH    = btnTap.getHeight();

            if (layoutW == 0 || btnW == 0) return; // safety guard

            int maxX = layoutW - btnW - BUTTON_MARGIN;
            int maxY = layoutH - btnH - BUTTON_MARGIN;

            int newX = BUTTON_MARGIN + random.nextInt(Math.max(1, maxX - BUTTON_MARGIN));
            int newY = BUTTON_MARGIN + random.nextInt(Math.max(1, maxY - BUTTON_MARGIN));

            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) btnTap.getLayoutParams();

            // Switch to absolute positioning
            params.removeRule(RelativeLayout.CENTER_IN_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.leftMargin = newX;
            params.topMargin  = newY;

            btnTap.setLayoutParams(params);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
