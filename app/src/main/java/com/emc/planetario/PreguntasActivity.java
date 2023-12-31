package com.emc.planetario;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreguntasActivity extends AppCompatActivity {
    private static final int MAX_INCORRECT_COUNT = 5; // Máximo número de respuestas incorrectas permitidas
    private TextView questionTextView, answer1TextView, answer2TextView, answer3TextView, puntajeTxt, vidasTxt;
    private ImageView correctImageView, incorrectImageView;
    private List<Question> questionList;
    private int currentQuestionIndex;
    private int incorrectAnswerCount; // Nueva variable para contar respuestas incorrectas consecutivas
    private int puntaje = 0, highscore = 0, vidas = MAX_INCORRECT_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preguntas);

        questionTextView = findViewById(R.id.questionTextView);
        answer1TextView = findViewById(R.id.answer1TextView);
        answer2TextView = findViewById(R.id.answer2TextView);
        answer3TextView = findViewById(R.id.answer3TextView);
        correctImageView = findViewById(R.id.correctImageView);
        incorrectImageView = findViewById(R.id.incorrectImageView);
        puntajeTxt = findViewById(R.id.puntaje);
        vidasTxt = findViewById(R.id.vidas);
        // Cargar preguntas y respuestas desde el archivo
        questionList = loadQuestionsFromRaw();

        // Mostrar la primera pregunta
        showNextQuestion();

        // Manejar clics en respuestas
        answer1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(answer1TextView.getText().toString());
            }
        });

        answer2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(answer2TextView.getText().toString());
            }
        });

        answer3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(answer3TextView.getText().toString());
            }
        });
    }

    private void showNextQuestion() {
        correctImageView.setVisibility(View.INVISIBLE);
        incorrectImageView.setVisibility(View.INVISIBLE);

        if (currentQuestionIndex < questionList.size() && incorrectAnswerCount < MAX_INCORRECT_COUNT) {
            Question question = questionList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestion());
            List<String> answers = question.getShuffledAnswers();
            answer1TextView.setText(answers.get(0));
            answer2TextView.setText(answers.get(1));
            answer3TextView.setText(answers.get(2));
            currentQuestionIndex++;
        } else {
            // Has completado todas las preguntas o alcanzado el límite de respuestas incorrectas, puedes manejarlo aquí
            if (incorrectAnswerCount == MAX_INCORRECT_COUNT) {
                if (isFirstTime()) {
                    savePuntaje(puntaje);
                    goToHighScorePage(puntaje);
                } else {
                    highscore = loadPuntajeFromFile();
                    if (puntaje > highscore) {
                        savePuntaje(puntaje);
                        goToHighScorePage(puntaje);
                    } else {
                        returnToScreen2();
                    }
                }
            }
        }
    }

    private void goToHighScorePage(int puntaje) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        intent.putExtra("puntaje", puntaje);
        startActivity(intent);
        finish(); // Esto cierra la actividad actual
    }

    private void returnToScreen2() {
        Intent intent = new Intent(this, EleccionActivity.class);
        startActivity(intent);
        finish(); // Esto cierra la actividad actual
    }

    private void checkAnswer(String selectedAnswer) {
        Question currentQuestion = questionList.get(currentQuestionIndex - 1);
        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            puntaje += 100;
            puntajeTxt.setText(Integer.toString(puntaje));
            correctImageView.setVisibility(View.VISIBLE); // Mostrar la imagen correcta
        } else {
            vidas -= 1;
            vidasTxt.setText(Integer.toString(vidas));
            incorrectAnswerCount++;
            incorrectImageView.setVisibility(View.VISIBLE); // Mostrar la imagen incorrecta
        }
        // Mostrar la siguiente pregunta después de un breve retraso (puedes ajustar la duración)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextQuestion();
            }
        }, 1000); // Retraso de 1 segundo
    }

    private List<Question> loadQuestionsFromRaw() {
        List<Question> questions = new ArrayList<>();
        try {
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String questionText = parts[0];
                    String correctAnswer = parts[1];
                    String incorrectAnswer1 = parts[2];
                    String incorrectAnswer2 = parts[3];

                    Question question = new Question(questionText, correctAnswer, incorrectAnswer1, incorrectAnswer2);
                    questions.add(question);
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(questions);
        return questions;
    }

    private void savePuntaje(int puntaje) {
        try (FileOutputStream fos = openFileOutput("puntaje.txt", Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeInt(puntaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadPuntajeFromFile() {
        int loadedPuntaje = 0;
        try (FileInputStream fis = openFileInput("puntaje.txt");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            loadedPuntaje = ois.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPuntaje;
    }

    //Checa si el file existe. Si existe, regresa falso.
    private boolean isFirstTime() {
        File file = new File(getFilesDir(), "puntaje.txt");
        return !file.exists() || file.length() == 0;
    }
}
