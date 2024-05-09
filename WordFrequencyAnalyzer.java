// **********************************************************************************
// Title: Major Project Part #1
// Author: Ryan Collingham
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: WordFrequencyAnalyzer
// Description: Counts the number of times words appear in a text file.
// **********************************************************************************

/*
 * To use this GUI, type/load in text and then click "analyze"
 * "Start" and "Stop" buttons are used to calculate the user's WPM
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class WordFrequencyAnalyzer extends Application {
    private TextArea textArea;
    private ListView<String> wordListView;
    private ObservableList<String> wordList;
    private Label wordCountLabel;
    private long startTime;
    private boolean isTimerRunning;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CMIS202 Word Frequency Analyzer");

        // Create UI components
        BorderPane root = new BorderPane();
        VBox vbox = new VBox();
        textArea = new TextArea();
        Button loadFileButton = new Button("Load Text File");
        Button analyzeButton = new Button("Analyze");
        Button startTimerButton = new Button("Start Timer");
        Button stopTimerButton = new Button("Stop Timer");
        wordListView = new ListView<>();
        wordList = FXCollections.observableArrayList();
        wordCountLabel = new Label("Word Count: 0");
        wordCountLabel.getStyleClass().add("word-count-label"); // Apply CSS class
        isTimerRunning = false;

        // Set event handlers
        loadFileButton.setOnAction(event -> loadTextFile(primaryStage));
        analyzeButton.setOnAction(event -> analyzeText());
        startTimerButton.setOnAction(event -> startTimer());
        stopTimerButton.setOnAction(event -> stopTimer());
        // Add components to VBox
        vbox.getChildren().addAll(loadFileButton, analyzeButton, textArea, wordCountLabel, startTimerButton, stopTimerButton);

        // Add components to BorderPane
        root.setTop(vbox);
        root.setCenter(wordListView);

        // Set padding and spacing
        vbox.setSpacing(10);
        BorderPane.setMargin(vbox, new Insets(10));

        // Set the scene
        Scene scene = new Scene(root, 800, 600);
        
        // Apply CSS styling from external file
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadTextFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        /*
         * This code uses the Collections.sort() method to sort the word frequencies based on their frequency in descending order. 
         * This method internally uses a variation of the mergesort algorithm called TimSort, which is the default sorting algorithm 
         * used in Java's Collections.sort() method for objects that implement the Comparable interface. TimSort is a hybrid sorting algorithm 
         * derived from merge sort and insertion sort, optimized for sorting arrays with partially ordered elements, like the word 
         * frequencies in this case. This approach is similar to the Merge Sort algorithm, which divides the input list into 
         * smaller sublists, sorts each sublist independently, and then merges the sorted sublists to produce the final sorted list.
         */

        private void analyzeText() {
            String text = textArea.getText().toLowerCase();
            ExecutorService executorService = Executors.newSingleThreadExecutor();

                    Future<Map<String, Integer>> future = executorService.submit(() -> {

            StringTokenizer tokenizer = new StringTokenizer(text, " \n\t\r\f,.:;?!\"'()-");
    
            // ArrayList to store word frequencies
            List<String> words = new ArrayList<>();
    
            // LinkedList to store word frequency map entries
            LinkedList<Map.Entry<String, Integer>> wordFrequencyList = new LinkedList<>();
    
            // HashSet to keep track of unique words
            Set<String> uniqueWords = new HashSet<>();
    
            // Queue to store words in the order they appear
            Queue<String> wordQueue = new LinkedList<>();
    
            Map<String, Integer> wordFrequencyMap = new HashMap<>();
    
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
                wordQueue.offer(word);
                uniqueWords.add(word);
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
            }
           

           
            // Sort words alphabetically
            words.addAll(uniqueWords);
            Collections.sort(words);
    
            // Sort word frequencies based on frequency in descending order
            wordFrequencyList.addAll(wordFrequencyMap.entrySet());
            wordFrequencyList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
            
            return wordFrequencyMap;
        });

        try {
            Map<String, Integer> wordFrequencyMap = future.get();

            // Update word count label
            int wordCount = wordFrequencyMap.values().stream().mapToInt(Integer::intValue).sum();
            wordCountLabel.setText("Word Count: " + wordCount);

            // Clear wordList
            wordList.clear();

            // Add sorted word frequencies to wordList
            for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .toList()) {
                wordList.add(entry.getKey() + " : " + entry.getValue());
            }

            // Set the items in the ListView
            wordListView.setItems(wordList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
           

        private void startTimer() {
            if (!isTimerRunning) {
                startTime = System.currentTimeMillis();
                isTimerRunning = true;
                wordCountLabel.setText("Word Count: 0 (Timer started)");
            } else {
                showAlert(Alert.AlertType.ERROR, "Timer is already running.");
            }
        }
    
        // Calculates WPM by dividing word count by time (in seconds)
        private void stopTimer() {
            if (isTimerRunning) {
                isTimerRunning = false;
                long endTime = System.currentTimeMillis();
                long elapsedTimeInMillis = endTime - startTime;
                double elapsedTimeInSeconds = elapsedTimeInMillis / 1000.0;
                int wordCount = textArea.getText().split("\\s+").length;
                int wordsPerMinute = (int) (wordCount / (elapsedTimeInSeconds / 60));
                showAlert(Alert.AlertType.INFORMATION, "Words Per Minute (WPM): " + wordsPerMinute);
            } else {
                showAlert(Alert.AlertType.ERROR, "Timer is not running.");
            }
        }
    
    
        private void showAlert(Alert.AlertType alertType, String content) {
            Alert alert = new Alert(alertType);
            alert.setTitle("Word Frequency Analyzer");
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }
    