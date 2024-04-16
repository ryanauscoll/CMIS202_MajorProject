// **********************************************************************************
// Title: Major Project Part #1
// Author: Ryan Collingham
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: WordFrequencyAnalyzer
// Description: Counts the number of times words appear in a text file.
// **********************************************************************************

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

public class WordFrequencyAnalyzer extends Application {
    private TextArea textArea;
    private ListView<String> wordListView;
    private ObservableList<String> wordList;

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
        wordListView = new ListView<>();
        wordList = FXCollections.observableArrayList();

        // Set event handlers
        loadFileButton.setOnAction(event -> loadTextFile(primaryStage));
        analyzeButton.setOnAction(event -> analyzeText());

        // Add components to VBox
        vbox.getChildren().addAll(loadFileButton, analyzeButton, textArea);

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
        StringTokenizer tokenizer = new StringTokenizer(text, " \n\t\r\f,.:;?!\"'()-");
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
        }
        List<Map.Entry<String, Integer>> wordFrequencyList = new ArrayList<>(wordFrequencyMap.entrySet());
        wordFrequencyList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        wordList.clear();
        for (Map.Entry<String, Integer> entry : wordFrequencyList) {
            wordList.add(entry.getKey() + " : " + entry.getValue());
        }
        wordListView.setItems(wordList);
    }
}
