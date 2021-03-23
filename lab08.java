import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.Group;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class lab08 extends Application {

    public File currentFile = null;
    public Stage primaryStage;
    public class StudentRecord
	{
		private String id;
		private float assignments;
		private float midterm;
		private float finalExam;
		private float finalMark;
		private String letterGrade;
		
		public StudentRecord(String id, float assignment, float midterm, float finalExam)
		{
			this.id = id;
			this.assignments = assignment;
			this.midterm = midterm;
			this.finalExam = finalExam;
			
			this.finalMark = (this.assignments * 0.2f) + (this.midterm * 0.3f) + (this.finalExam * 0.5f);
			
			if (this.finalMark >= 80)
			{
				this.letterGrade = "A";
			}
			else if (this.finalMark >= 70)
			{
				this.letterGrade = "B";
			}
			else if (this.finalMark >= 60)
			{
				this.letterGrade = "C";
			}
			else if (this.finalMark >= 50)
			{
				this.letterGrade = "D";
			}
			else
			{
				this.letterGrade = "F";
			}
		}
		
		public String getId() {
			return id;
		}
		
		public float getAssignments() {
			return assignments;
		}
		
		public float getMidterm() {
			return midterm;
		}
		
		public float getFinalExam() {
			return finalExam;
		}
		
		public float getFinalMark() {
			return finalMark;
		}
		
		public String getLetterGrade() {
			return letterGrade;
		}
	}

    public class DataSource {
        public ObservableList<StudentRecord> loadMarks(File sourceFile)
        {
            ObservableList<StudentRecord> marks = FXCollections.observableArrayList();
            Scanner scanner = null;
            try
            {
                scanner = new Scanner(sourceFile);
                scanner.nextLine(); //skip title line
            }
            catch (Exception e)
            {
                System.out.println("The file does not exist!");
            }
            while (scanner.hasNextLine())
            {
                List<String> data = new ArrayList<String>();
                try (Scanner rowScanner = new Scanner(scanner.nextLine()))
                {
                    rowScanner.useDelimiter(",");
                    while (rowScanner.hasNext())
                    {
                        data.add(rowScanner.next());
                    }
                }
                marks.add(new StudentRecord(data.get(0), Float.parseFloat(data.get(1)), Float.parseFloat(data.get(2)), Float.parseFloat(data.get(3))));
            }
            return marks;
        }

        public void saveMarks(File childFile, ObservableList<StudentRecord> records)
        {
            try
            {
                FileWriter writer = new FileWriter(childFile);
                writer.write("SID,Assignments,Midterm,Exam\n");

                for (StudentRecord r : records)
                {
                    String rawRecord = r.getId() + "," + r.getAssignments() + "," + r.getMidterm() + "," + r.getFinalExam() + "\n";
                    writer.write(rawRecord);
                }
                writer.close();
            }
            catch (IOException e)
            {
                System.out.println("Failed To Save File!");
            }
        }
    }

    @Override
    public void start(Stage myStage) throws Exception {
        DataSource source = new DataSource();
        primaryStage = myStage;

        primaryStage.setTitle("Lab 08");

        GridPane grid = new GridPane();
        TableView view = new TableView();

        TableColumn<StudentRecord, String> column1 = new TableColumn<>("Student ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StudentRecord, String> column2 = new TableColumn<>("Assignment Grade");
        column2.setCellValueFactory(new PropertyValueFactory<>("assignments"));

        TableColumn<StudentRecord, String> column3 = new TableColumn<>("Midterm Grade");
        column3.setCellValueFactory(new PropertyValueFactory<>("midterm"));

        TableColumn<StudentRecord, String> column4 = new TableColumn<>("Final Exam Grade");
        column4.setCellValueFactory(new PropertyValueFactory<>("finalExam"));

        TableColumn<StudentRecord, String> column5 = new TableColumn<>("Final Mark");
        column5.setCellValueFactory(new PropertyValueFactory<>("finalMark"));

        TableColumn<StudentRecord, String> column6 = new TableColumn<>("Letter Grade");
        column6.setCellValueFactory(new PropertyValueFactory<>("letterGrade"));

        view.getColumns().add(column1);
        view.getColumns().add(column2);
        view.getColumns().add(column3);
        view.getColumns().add(column4);
        view.getColumns().add(column5);
        view.getColumns().add(column6);

        Menu menu = new Menu("File");
        MenuItem menuNew = new MenuItem("New");
        MenuItem menuOpen = new MenuItem("Open");
        MenuItem menuSave = new MenuItem("Save");
        MenuItem menuSaveAs = new MenuItem("Save As");
        MenuItem menuExit = new MenuItem("Exit");

        menu.getItems().add(menuNew);
        menu.getItems().add(menuOpen);
        menu.getItems().add(menuSave);
        menu.getItems().add(menuSaveAs);
        menu.getItems().add(menuExit);

        menuNew.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            currentFile = selectedFile;
            view.getItems().clear();
        });

        menuOpen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            currentFile = selectedFile;
            view.getItems().clear();
            ObservableList<StudentRecord> records = source.loadMarks(currentFile);
            for (StudentRecord r : records)
            {
                view.getItems().add(r);
            }
        });

        menuSave.setOnAction(e -> {
            if (currentFile == null)
            {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                currentFile = selectedFile;
            }
            source.saveMarks(currentFile, view.getItems());
        });

        menuSaveAs.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            currentFile = selectedFile;
            source.saveMarks(currentFile, view.getItems());
        });

        menuExit.setOnAction(e -> {
            primaryStage.close();
        });

        MenuBar bar = new MenuBar();
        bar.getMenus().add(menu);
        VBox vbox = new VBox(0);
        vbox.getChildren().add(bar);
        vbox.getChildren().add(view);
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}