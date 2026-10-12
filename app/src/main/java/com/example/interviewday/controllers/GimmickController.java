package com.example.interviewday.controllers;

import android.content.Context;
import com.example.interviewday.R;
import com.example.interviewday.models.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * คลาส GimmickController
 * หน้าที่: เป็นตัวกลางควบคุมลอจิกมินิเกม เชื่อมต่อระหว่าง GimmickDialog (View) กับ Question/Audio/Pronunciation (Model & Service)
 */
public class GimmickController {

    // ===== Attributes =====
    private int currentStage;                          // ด่านปัจจุบัน (1, 2, 3)
    private int currentQuestion;                       // ข้อปัจจุบัน (0, 1, 2)

    private final List<Question> questionList;         // รายการ Model เก็บโจทย์และคำตอบทั้งหมด
    private final AudioController audioController;    // Controller สำหรับจัดการเสียงอ่านโจทย์
    private final PronunciationController pronunciationController; // Controller สำหรับตรวจเสียงพูด

    // ===== Constructors =====
    public GimmickController() {
        this(1, 0);
    }

    public GimmickController(int currentStage, int currentQuestion) {
        this.currentStage = currentStage;
        this.currentQuestion = currentQuestion;
        this.audioController = new AudioController();
        this.pronunciationController = new PronunciationController();
        this.questionList = new ArrayList<>();
        loadQuestions(); // โหลดข้อมูล Model โจทย์
    }

    // โหลดชุดข้อมูลโจทย์ คำตอบ เสียงอ่าน และ UI res ของแต่ละด่านเข้าสู่ Model
    private void loadQuestions() {
        questionList.clear();

        // --- ชุดโจทย์ด่านที่ 1 (OOP Concepts) ---
        questionList.add(new Question(
                1, 1, 0,
                "Level 1: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_1,
                "What is the OOP concept that hides internal details?",
                "Encapsulation",
                "Listening",
                R.raw.level1_1,
                false
        ));
        questionList.add(new Question(
                2, 1, 1,
                "Level 1: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_2,
                "What is the mechanism where a class acquires properties of another class?",
                "Inheritance",
                "Listening",
                R.raw.level1_2,
                false
        ));
        questionList.add(new Question(
                3, 1, 2,
                "Level 1: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_3,
                "What concept allows objects to take many forms?",
                "Polymorphism",
                "Listening",
                R.raw.level1_3,
                false
        ));

        // --- ชุดโจทย์ด่านที่ 2 (Hardware Tools) ---
        questionList.add(new Question(
                4, 2, 0,
                "Level 2: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_1,
                "What hardware component is a compact integrated circuit designed to govern a specific operation in an embedded system?",
                "Microcontroller",
                "Listening",
                R.raw.level2_1,
                false
        ));
        questionList.add(new Question(
                5, 2, 1,
                "Level 2: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_2,
                "What electronic test instrument allows observation of varying signal voltages?",
                "Oscilloscope",
                "Listening",
                R.raw.level2_2,
                false
        ));
        questionList.add(new Question(
                6, 2, 2,
                "Level 2: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_3,
                "What electronic measuring instrument combines several measurement functions in one unit?",
                "Multimeter",
                "Listening",
                R.raw.level2_3,
                false
        ));

        // --- ชุดโจทย์ด่านที่ 3 (Interview Summary) ---
        questionList.add(new Question(
                7, 3, 0,
                "Level 3: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_1,
                "Tell me about yourself and your background.",
                "I am a Computer Engineering graduate passionate about software development and system architecture",
                "Listening",
                R.raw.level3_1,
                true
        ));
        questionList.add(new Question(
                8, 3, 1,
                "Level 3: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_2,
                "What is your greatest strength?",
                "my greatest strength is my problem solving ability and quick adaptability to new technologies",
                "Listening",
                R.raw.level3_2,
                true
        ));
        questionList.add(new Question(
                9, 3, 2,
                "Level 3: Listen to the question, then press the speak button to answer.",
                R.drawable.ic_logo_3,
                "What are your career goals or why do you want to work here?",
                "I want to apply my technical skills to build scalable software solutions and learn from expert engineers",
                "Listening",
                R.raw.level3_3,
                true
        ));
    }

    // ค้นหาและดึง Question Model ประจำด่านและข้อปัจจุบัน
    public Question getCurrentQuestionModel() {
        for (Question q : questionList) {
            if (q.getStageNumber() == currentStage && q.getQuestionIndex() == currentQuestion) {
                return q;
            }
        }
        return new Question(0, currentStage, currentQuestion,
                "คำถามด่านที่ " + currentStage, R.drawable.ic_logo_1,
                "Question", "Encapsulation", "Speaking", 0, false);
    }

    // Getter ส่งข้อมูลคำสั่งสอนให้ View
    public String getInstructionText() {
        return getCurrentQuestionModel().getInstructionText();
    }

    // Getter ส่งรูปภาพ Badge ให้ View
    public int getBadgeResId() {
        return getCurrentQuestionModel().getBadgeResId();
    }

    // Getter ส่งคำตอบเฉลยภาษาอังกฤษให้ View
    public String getTargetSentence() {
        return getCurrentQuestionModel().getCorrectAnswer();
    }

    // ดึงข้อความคำศัพท์พร้อมคำแปลภาษาไทยประจำข้อคำถามปัจจุบันสำหรับบันทึกลงสมุดโน้ต
    public String getVocabularyForCurrentQuestion() {
        Question q = getCurrentQuestionModel();
        if (q == null) return "";
        String answer = q.getCorrectAnswer();
        if ("Encapsulation".equalsIgnoreCase(answer)) {
            return "1. Encapsulation - การซ่อนรายละเอียดภายใน";
        } else if ("Inheritance".equalsIgnoreCase(answer)) {
            return "2. Inheritance - การสืบทอดคุณสมบัติ";
        } else if ("Polymorphism".equalsIgnoreCase(answer)) {
            return "3. Polymorphism - การมีได้หลายรูปแบบ";
        } else if ("Microcontroller".equalsIgnoreCase(answer)) {
            return "4. Microcontroller - ไมโครคอนโทรลเลอร์";
        } else if ("Oscilloscope".equalsIgnoreCase(answer)) {
            return "5. Oscilloscope - ออสซิลโลสโคป";
        } else if ("Multimeter".equalsIgnoreCase(answer)) {
            return "6. Multimeter - มัลติมิเตอร์";
        } else if (answer.startsWith("I am a Computer Engineering graduate")) {
            return "7. Self Introduction - " + answer;
        } else if (answer.startsWith("My greatest strength")) {
            return "8. Greatest Strength - " + answer;
        } else if (answer.startsWith("I want to apply my technical skills")) {
            return "9. Career Goals - " + answer;
        }
        return (q.getQuestionIndex() + 1) + ". " + answer;
    }

    // Getter เช็กสถานะการมองเห็นกระดาษโน้ต
    public boolean isNotepadVisible() {
        return getCurrentQuestionModel().isNotepadVisible();
    }

    // สั่งการ AudioController ให้เล่นเสียงโจทย์ประจำข้อ
    public void playQuestionAudio(Context context) {
        int resId = getCurrentQuestionModel().getAudioResId();
        if (resId != 0 && audioController != null) {
            audioController.playSentenceAudio(context, resId);
        }
    }

    // ส่งข้อความที่พูดเข้า PronunciationController เพื่อตรวจเทียบคำตอบ
    public boolean checkAnswer(String spokenText) {
        String targetWord = getTargetSentence();
        pronunciationController.setTargetSentence(targetWord);
        pronunciationController.recognizeSpeech(spokenText);
        boolean isCorrect = pronunciationController.compareSentence();
        if (isCorrect) {
            NotebookController.addUnlockedVocabulary(getVocabularyForCurrentQuestion());
        }
        return isCorrect;
    }

    // เปลี่ยนข้อคำถาม ถัดไป (0 -> 1 -> 2)
    public boolean nextQuestion() {
        if (currentQuestion < 2) {
            currentQuestion++;
            return true;
        } else {
            return false; // เล่นครบคำถามประจำด่านนี้แล้ว
        }
    }

    // ===== Getters & Setters =====
    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public void setStageAndQuestion(int stage, int question) {
        this.currentStage = stage;
        this.currentQuestion = question;
    }

    public AudioController getAudioController() {
        return audioController;
    }

    public PronunciationController getPronunciationController() {
        return pronunciationController;
    }
}
