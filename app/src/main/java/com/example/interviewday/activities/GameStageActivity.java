package com.example.interviewday.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.interviewday.R;
import com.example.interviewday.controllers.GameSessionManager;
import com.example.interviewday.controllers.GameStageController;
import com.example.interviewday.controllers.GimmickController;
import com.example.interviewday.controllers.LevelController;
import com.example.interviewday.controllers.NotebookController;
import com.example.interviewday.controllers.TimerController;
import com.example.interviewday.dialogs.GimmickDialog;
import com.example.interviewday.dialogs.PauseDialog;
import com.example.interviewday.dialogs.SettingsDialog;
import com.example.interviewday.models.Item;
import com.example.interviewday.models.SecretItem;

import java.util.List;

public class GameStageActivity extends BaseActivity {

    // Controller
    private GameStageController gameStageController;

    // UI Views
    private ImageView imgBackground;
    private ImageButton btnPause;
    private TextView tvTimer;
    private ImageButton btnNext;

    // Quest Item Slot Views (แถบด้านล่าง)
    private ImageView imgQuestItem1;
    private ImageView imgQuestItem2;
    private ImageView imgQuestItem3;

    // Hidden Item Views (ไอเท็มธรรมดาบนฉาก)
    private ImageView imgHiddenItem1;
    private ImageView imgHiddenItem2;
    private ImageView imgHiddenItem3;

    // Secret Item View (ไอเท็มลับบนฉาก)
    private ImageView imgSecretItem;

    // Resource IDs และข้อมูลเก็บจาก Model ผ่าน Controller
    private int colorRes1, colorRes2, colorRes3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stage);

        int stageNumber = getIntent().getIntExtra("STAGE_NUMBER", 1);
        if (stageNumber == 1) {
            GameSessionManager.resetSession();
        }
        gameStageController = new GameStageController(stageNumber);

        initViews();
        renderStageUI();
        setupItemClickListeners();
        setupTimer();
    }

    private void initViews() {
        imgBackground = findViewById(R.id.imgBackground);
        btnPause = findViewById(R.id.btnPause);
        tvTimer = findViewById(R.id.tvTimer);
        btnNext = findViewById(R.id.btnNext);

        imgQuestItem1 = findViewById(R.id.imgQuestItem1);
        imgQuestItem2 = findViewById(R.id.imgQuestItem2);
        imgQuestItem3 = findViewById(R.id.imgQuestItem3);

        imgHiddenItem1 = findViewById(R.id.imgHiddenItem1);
        imgHiddenItem2 = findViewById(R.id.imgHiddenItem2);
        imgHiddenItem3 = findViewById(R.id.imgHiddenItem3);
        imgSecretItem = findViewById(R.id.imgSecretItem);
    }

    // ดึงข้อมูลรูปภาพและพิกัดไอเท็มทั้งหมดมาจาก Model ผ่าน Controller
    private void renderStageUI() {
        imgHiddenItem1.setVisibility(View.VISIBLE);
        imgHiddenItem2.setVisibility(View.VISIBLE);
        imgHiddenItem3.setVisibility(View.VISIBLE);
        imgSecretItem.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);

        // ตั้งค่าภาพพื้นหลัง
        imgBackground.setImageResource(gameStageController.getBackgroundResId());

        // โหลดข้อมูลไอเท็มธรรมดา 3 ชิ้น
        List<Item> normalItems = gameStageController.getNormalItems();
        if (normalItems != null && normalItems.size() >= 3) {
            Item item1 = normalItems.get(0);
            Item item2 = normalItems.get(1);
            Item item3 = normalItems.get(2);

            // แถบไอเทมด้านล่าง (เงาดำ)
            imgQuestItem1.setImageResource(item1.getShadowResId());
            imgQuestItem2.setImageResource(item2.getShadowResId());
            imgQuestItem3.setImageResource(item3.getShadowResId());

            // บันทึกสีสำหรับแสดงเมื่อผู้เล่นเก็บไอเท็มได้
            colorRes1 = item1.getColorResId();
            colorRes2 = item2.getColorResId();
            colorRes3 = item3.getColorResId();

            // ไอเทมสีที่ซ่อนอยู่บนฉาก
            imgHiddenItem1.setImageResource(item1.getColorResId());
            imgHiddenItem2.setImageResource(item2.getColorResId());
            imgHiddenItem3.setImageResource(item3.getColorResId());

            // จัดตำแหน่งไอเท็มบนฉากตามพิกัดที่ระบุใน Model
            setItemPosition(imgHiddenItem1, item1.getBiasX(), item1.getBiasY());
            setItemPosition(imgHiddenItem2, item2.getBiasX(), item2.getBiasY());
            setItemPosition(imgHiddenItem3, item3.getBiasX(), item3.getBiasY());
        }

        // โหลดข้อมูลไอเท็มลับ
        SecretItem secretItem = gameStageController.getSecretItem();
        if (secretItem != null) {
            imgSecretItem.setImageResource(secretItem.getColorResId());
            setItemPosition(imgSecretItem, secretItem.getBiasX(), secretItem.getBiasY());
        }
    }

    private void setupTimer() {
        TimerController timerController = gameStageController.getTimerController();
        if (timerController != null) {
            timerController.setOnTimerTickListener(new TimerController.OnTimerTickListener() {
                @Override
                public void onTick(String formattedTime) {
                    updateTimerText(formattedTime);
                }

                @Override
                public void onFinish() {
                    updateTimerText("00:00");
                    // เวลาหมด เปิดหน้า Failed ending ทันที
                    Intent intent = new Intent(GameStageActivity.this, EndingActivity.class);
                    intent.putExtra("ENDING_TYPE", "FAILED");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
        gameStageController.startStage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameStageController != null) {
            gameStageController.pauseStage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameStageController != null) {
            gameStageController.resumeStage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameStageController != null) {
            gameStageController.stopStage();
        }
    }

    public void pauseGameStage() {
        if (gameStageController != null) {
            gameStageController.pauseStage();
        }
    }

    public void resumeGameStage() {
        if (gameStageController != null && !isFinishing() && !isDestroyed()) {
            gameStageController.resumeStage();
        }
    }

    private void setupItemClickListeners() {
        btnPause.setOnClickListener(v -> {
            pauseGameStage();
            PauseDialog dialog = PauseDialog.newInstance(tvTimer.getText().toString());
            dialog.setListener(new PauseDialog.PauseDialogListener() {
                @Override
                public void onContinueClicked() {
                    resumeGameStage();
                }

                @Override
                public void onOpenSettingsClicked() {
                    SettingsDialog settingsDialog = SettingsDialog.newInstance();
                    settingsDialog.show(getSupportFragmentManager(), "SettingsDialog");
                }

                @Override
                public void onHomeClicked() {
                    Intent intent = new Intent(GameStageActivity.this, MainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onRestartClicked() {
                    recreate();
                }
            });
            dialog.show(getSupportFragmentManager(), "PauseDialog");
        });

        btnNext.setOnClickListener(v -> {
            int currentStage = gameStageController.getCurrentStage();
            TimerController timerController = gameStageController.getTimerController();
            if (timerController != null) {
                GameSessionManager.recordStageTime(currentStage, timerController.getTimeLimit(), timerController.getRemainingTime());
            }

            // ปลดล็อกด่านถัดไปในระบบ
            LevelController.unlockStage(currentStage + 1);

            if (currentStage < 3) {
                Intent intent = new Intent(GameStageActivity.this, StoryActivity.class);
                intent.putExtra("STAGE_NUMBER", currentStage + 1);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(GameStageActivity.this, EndingActivity.class);
                intent.putExtra("ENDING_TYPE", isSecretItemFound() ? "SPECIAL" : "NORMAL");
                intent.putExtra("LEVEL1_TIME", GameSessionManager.getLevel1FormattedTime());
                intent.putExtra("LEVEL2_TIME", GameSessionManager.getLevel2FormattedTime());
                intent.putExtra("LEVEL3_TIME", GameSessionManager.getLevel3FormattedTime());
                intent.putExtra("TOTAL_TIME", GameSessionManager.getTotalFormattedTime());
                startActivity(intent);
                finish();
            }
        });

        // 1. ไอเท็มธรรมดา ชิ้นที่ 1
        imgHiddenItem1.setOnClickListener(v -> {
            imgHiddenItem1.setVisibility(View.GONE);
            imgQuestItem1.setImageResource(colorRes1);
            boolean allFound = gameStageController.collectNormalItem(0);
            List<Item> items = gameStageController.getNormalItems();
            if (items != null && items.size() > 0) {
                NotebookController.addUnlockedDiscoveredItem(items.get(0).getItemName());
            }
            if (allFound) {
                LevelController.unlockStage(gameStageController.getCurrentStage() + 1);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        // 2. ไอเท็มธรรมดา ชิ้นที่ 2
        imgHiddenItem2.setOnClickListener(v -> {
            imgHiddenItem2.setVisibility(View.GONE);
            imgQuestItem2.setImageResource(colorRes2);
            boolean allFound = gameStageController.collectNormalItem(1);
            List<Item> items = gameStageController.getNormalItems();
            if (items != null && items.size() > 1) {
                NotebookController.addUnlockedDiscoveredItem(items.get(1).getItemName());
            }
            if (allFound) {
                LevelController.unlockStage(gameStageController.getCurrentStage() + 1);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        // 3. ไอเท็มธรรมดา ชิ้นที่ 3
        imgHiddenItem3.setOnClickListener(v -> {
            imgHiddenItem3.setVisibility(View.GONE);
            imgQuestItem3.setImageResource(colorRes3);
            boolean allFound = gameStageController.collectNormalItem(2);
            List<Item> items = gameStageController.getNormalItems();
            if (items != null && items.size() > 2) {
                NotebookController.addUnlockedDiscoveredItem(items.get(2).getItemName());
            }
            if (allFound) {
                LevelController.unlockStage(gameStageController.getCurrentStage() + 1);
                btnNext.setVisibility(View.VISIBLE);
            }
        });

        // 4. ไอเท็มลับประจำด่าน (เปิด GimmickDialog เมื่อพบไอเท็มลับ)
        imgSecretItem.setOnClickListener(v -> {
            imgSecretItem.setVisibility(View.GONE);
            String secretName = gameStageController.collectSecretItem();
            Toast.makeText(this, "ปลดล็อกไอเท็มลับ: " + secretName, Toast.LENGTH_LONG).show();

            NotebookController.addUnlockedDiscoveredItem(secretName);

            // ปลดล็อกคำศัพท์ทั้งหมดของด่านนี้ลงสมุดโน้ตทันทีเมื่อพบไอเท็มลับ
            int currentStage = gameStageController.getCurrentStage();
            for (int qIdx = 0; qIdx < 3; qIdx++) {
                GimmickController tempController = new GimmickController(currentStage, qIdx);
                NotebookController.addUnlockedVocabulary(tempController.getVocabularyForCurrentQuestion());
            }

            pauseGameStage();

            // เปิด GimmickDialog สำหรับตอบคำถามไอเท็มลับประจำด่าน
            GimmickDialog dialog = new GimmickDialog();
            dialog.setStageAndQuestion(currentStage, 0);
            dialog.show(getSupportFragmentManager(), "GimmickDialog");
        });
    }

    // กำหนดตำแหน่งไอเท็มอิงตามอัตราส่วนหน้าจอที่ได้รับมาจาก Model
    private void setItemPosition(ImageView item, float biasX, float biasY) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) item.getLayoutParams();
        params.horizontalBias = biasX;
        params.verticalBias = biasY;
        item.setLayoutParams(params);
    }

    public void updateTimerText(String timeFormatted) {
        if (tvTimer != null) {
            tvTimer.setText(timeFormatted);
        }
    }

    public boolean isSecretItemFound() {
        return gameStageController != null && gameStageController.getSecretItem() != null && gameStageController.getSecretItem().isCollected();
    }
}
