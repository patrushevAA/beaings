/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bearings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;

/**
 *
 * @author Александр
 */
public class Bearings extends Application {
    Stage primaryStage;
    Group rootGraph = new Group();
    WebView webView=new WebView();
    WebView aboutWebView=new WebView();
    TabPane tp=new TabPane();
    
    //Создание колекций для хранения элементов Базы Данных
    ArrayList<Bearing> bearingList = new ArrayList<>();
    ArrayList<Bearing> bearingSearched = new ArrayList<>();
    ObservableList<Bearing> bearingObservableList = FXCollections.observableArrayList(bearingList);
    ObservableList<Bearing> bearingObservableListTmp = FXCollections.observableArrayList(bearingList);
    TableView<Bearing> table = new TableView<Bearing>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    
    //Создние графика
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
    XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
    
    //Переменные для провери этапов поиска
    boolean isOpened = false;
    boolean isSaved = false;
    boolean isChecked = false;
    boolean isError = false;
    boolean isTable = false;
    boolean isSearched = false;
    boolean isGraph = false;
    
    //Параметры для поиска подшпников
    String name;
    double minInD;
    double maxInD;
    double minOutD;
    double maxOutD;
    double minHeight;
    double maxHeight;
    ObservableList<String> type;
    ObservableList<String> producer;
    Date minDate;
    Date maxDate;
    int minCount;
    int maxCount;
    
    //Надписи для формы поиска
    Label nameLabel = new Label("Наименование");
    Label typeLabel = new Label("Тип");
    Label producerLabel = new Label("Производитель");
    Label inDLabel = new Label("Внутренний диаметр, мм");
    Label outDLabel = new Label("Внешний диаметр, мм");
    Label heightDLabel = new Label("Ширина, мм");
    Label diameterFromLabel = new Label("от");
    Label diameterToLabel = new Label("до");
    Label dateLabel = new Label("Дата поступления дд.мм.гггг");
    Label countLabel = new Label("Количество, шт");
    TextArea checkError = new TextArea("");
    
    //Надписи ошибок при считывании введенных пользователем параметров
    static Label nameError = new Label("");
    static Label typeError = new Label("");
    static Label producerError = new Label("");
    static Label inDError = new Label("");
    static Label outDError = new Label("");
    static Label hError = new Label("");
    static Label dateError = new Label("");
    static Label countError = new Label("");
    static Label otherError = new Label("");
    //Метод очистки сообщений об ошибке при поиске
    public static void clearErrorLabels(){
        nameError.setText("");
        typeError.setText("");
        producerError.setText("");
        inDError.setText("");
        outDError.setText("");
        hError.setText("");
        dateError.setText("");
        countError.setText("");
    }
    
    //Поля для получения введенных пользователем значений
    static TextField nameTF = new TextField();
    static TextField typeTF = new TextField();
    static TextField producerTF = new TextField();
    static TextField minInDTF = new TextField();
    static TextField maxInDTF = new TextField();
    static TextField minOutDTF = new TextField();
    static TextField maxOutDTF = new TextField();
    static TextField minHeightTF = new TextField();
    static TextField maxHeightTF = new TextField();
    static TextField minDateTF = new TextField();
    static TextField maxDateTF = new TextField();
    static TextField minCountTF = new TextField();
    static TextField maxCountTF = new TextField();
    ListView<String> typeList = new ListView<>();
    ListView<String> producerList = new ListView<>();
    
    //Метод очистки полей наименования ошибок
    public static void clearTextFields(){
        nameTF.setText("");
        typeTF.setText("");
        producerTF.setText("");
        minInDTF.setText("");
        maxInDTF.setText("");
        minOutDTF.setText("");
        maxOutDTF.setText("");
        minHeightTF.setText("");
        maxHeightTF.setText("");
        minDateTF.setText("");
        maxDateTF.setText("");
        minCountTF.setText("");
        maxCountTF.setText("");
    }
    
    Button searchButton = new Button("Поиск");
    Button clearButton = new Button("Очистить");
    
    //Метод обнуления введенных пользователем параметров поиска
    EventHandler<ActionEvent> onClearButton = new EventHandler<ActionEvent>() {
            @Override
           public void handle(ActionEvent e) {
            clearTextFields();
            clearErrorLabels();
            producerList.getSelectionModel().clearSelection();
            producerList.getSelectionModel().select(0);
            typeList.getSelectionModel().clearSelection();
            typeList.getSelectionModel().select(0);
        }
    };
    
    //Действия меню
    //Открытие базы данных
    EventHandler<ActionEvent> onOpen = new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent e) {
                clearErrorLabels();
                System.out.println("Открываю базу данных");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new java.io.File("..\\src\\bearings\\"));
                fileChooser.setTitle("Выбрать файл для загрузки Базы данных");
                fileChooser.getExtensionFilters().addAll(new 
                FileChooser.ExtensionFilter("Текстровый документ", "*.txt"));
                fileChooser.setInitialFileName("arguments.txt");
                java.io.File file = fileChooser.showOpenDialog(primaryStage);
                String fileName = file.getAbsolutePath();
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));     
                    System.out.println("Создал BufferedReader");
                    bearingList.clear();
                    bearingObservableList.clear();
                    bearingObservableListTmp.clear();
                    barChart.getData().clear();
                    String str;
                    while((str = in.readLine()) != null){
                        String [] s = str.split(" ");
                        for(int i = 0; i <s.length; i++){
                            System.out.println(s[i]);
                        }
                        String name = s[0];
                        String type = s[1];
                        String producer = s[2];
                        double inDiameter = Double.parseDouble(s[3]);
                        double outDiameter = Double.parseDouble(s[4]);
                        double height = Double.parseDouble(s[5]);
                        String inDate = s[6];
                        int count = Integer.parseInt(s[7]);
                        System.out.println(name+" "+type+" "+producer+" "+inDiameter+" "+outDiameter+" "+height+" "+inDate+" "+count);
                        Bearing b = new Bearing(name, type, producer, inDiameter, outDiameter, height, inDate,count);
                        bearingList.add(b);
                        for(Bearing bearing : bearingList){
                            System.out.println(bearing.getName());
                        }
                    }
                    
                    isOpened = true;
                    isChecked = false;
                    isError = false;
                    isTable = false;
                    isSearched = false;
                    isGraph = false;
                    if(isOpened) System.out.println("База данных открыта isOpened = true");
                }catch(Exception ex){
                    checkError.setText("Ошибка при чтении из файла " + fileName + "\n");
                    isOpened = false;
                }
        }
    };
    
    EventHandler<ActionEvent> onCheck = new EventHandler<ActionEvent>() {
            @Override
           public void handle(ActionEvent e) {
             clearErrorLabels();
             isError = false;
             
             name = nameTF.getText();
             
             if(!minInDTF.getText().equals("")){
                 try{
                     minInD = Double.parseDouble(minInDTF.getText());
                 }catch(Exception e1){
                    inDError.setText("Ошибка ввода внутреннего диаметра");
                    inDError.setTextFill(Color.web("#f50a0a"));
                    inDError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else minInD = 0;
             
             if(!maxInDTF.getText().equals("")){
                 try{
                     maxInD = Double.parseDouble(maxInDTF.getText());
                 }catch(Exception e1){
                    inDError.setText("Ошибка ввода внутреннего диаметра");
                    inDError.setTextFill(Color.web("#f50a0a"));
                    inDError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else maxInD = 10000;
             
             if(!minOutDTF.getText().equals("")){
                 try{
                    minOutD = Double.parseDouble(minOutDTF.getText());
                 }catch(Exception e1){
                    outDError.setText("Ошибка ввода наружного диаметра");
                    outDError.setTextFill(Color.web("#f50a0a"));
                    outDError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else minOutD = 0;
             
             if(!maxOutDTF.getText().equals("")){
                 try{
                     maxOutD = Double.parseDouble(maxOutDTF.getText());
                 }catch(Exception e1){
                    outDError.setText("Ошибка ввода наружного диаметра");
                    outDError.setTextFill(Color.web("#f50a0a"));
                    outDError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else maxOutD = 10000;
             
             if(!minHeightTF.getText().equals("")){
                 try{
                     minHeight = Double.parseDouble(minHeightTF.getText());
                 }catch(Exception e1){
                    hError.setText("Ошибка ввода ширины");
                    hError.setTextFill(Color.web("#f50a0a"));
                    hError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else minHeight = 0;
             
             if(!maxHeightTF.getText().equals("")){
                 try{
                     maxHeight = Double.parseDouble(maxHeightTF.getText());
                 }catch(Exception e1){
                    hError.setText("Ошибка ввода ширины");
                    hError.setTextFill(Color.web("#f50a0a"));
                    hError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else maxHeight = 10000;
             
             type = typeList.getSelectionModel().getSelectedItems();
             producer = producerList.getSelectionModel().getSelectedItems();
             
             if(!minCountTF.getText().equals("")){
                 try{
                    minCount = Integer.parseInt(minCountTF.getText());
                 }catch(Exception e1){
                    countError.setText("Ошибка ввода количества");
                    countError.setTextFill(Color.web("#f50a0a"));
                    countError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else minCount = 0;
             
             if(!maxCountTF.getText().equals("")){
                 try{
                     maxCount = Integer.parseInt(maxCountTF.getText());
                 }catch(Exception e1){
                    countError.setText("Ошибка ввода количества");
                    countError.setTextFill(Color.web("#f50a0a"));
                    countError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             } else maxCount = 10000;
             
             if(!minDateTF.getText().equals("")){
                 try{
                     minDate = dateFormat.parse(minDateTF.getText());
                     System.out.println("Введена минимальная дата: "+dateFormat.format(minDate));
                 }catch(Exception e1){
                    dateError.setText("Ошибка ввода даты");
                    dateError.setTextFill(Color.web("#f50a0a"));
                    dateError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             }else{
                 System.out.println("Минимальная дата пуста");
                 try {
                     minDate = dateFormat.parse("01.01.2001");
                     System.out.println(dateFormat.format(minDate));
                } catch (ParseException ex) {
                     System.out.println("Не удалось задать минимальную дату");
                }
             }
             
             if(!maxDateTF.getText().equals("")){
                 try{
                     maxDate = dateFormat.parse(maxDateTF.getText());
                     System.out.println("Введена максимальная дата: "+dateFormat.format(maxDate));
                 }catch(Exception e1){
                    dateError.setText("Ошибка ввода даты");
                    dateError.setTextFill(Color.web("#f50a0a"));
                    dateError.setStyle("-fxfont:bold 14pt Verdana;");
                    isError = true;
                 }
             }else{
                 System.out.println("Максимальная дата пуста");
                 try {
                     maxDate = dateFormat.parse("31.12.2099");
                     System.out.println(dateFormat.format(maxDate));
                 } catch (ParseException ex) {
                     System.out.println("Не удалось задать максимальную дату");
                 }
             }
             
               isChecked=true;
           }
    };
    
    EventHandler<ActionEvent> onSave = new EventHandler<ActionEvent>() {
        @Override
          public void handle(ActionEvent e) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new java.io.File("..\\src\\bearings\\"));
            fileChooser.setTitle("Выбрать файл для сохранения параметров");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Текстровый документ", "*.txt"));
            fileChooser.setInitialFileName("arguments.txt");
            java.io.File file = fileChooser.showSaveDialog(primaryStage);
            String fileName = file.getAbsolutePath();  
            try {
              PrintWriter pw = new PrintWriter(fileName, "UTF-8");
              if(!bearingList.isEmpty()){
                 for(int i = 0; i < bearingList.size(); i++){
                  Bearing bearing = bearingList.get(i);
                  String s = bearing.getName() + " " + bearing.getType() + " " + bearing.getProducer() + " " +
                          bearing.getInDiameter() + " " + bearing.getOutDiameter() + " " + bearing.getHeight() 
                          + " " + bearing.inDateProperty().get() + " " + bearing.countProperty().get() + "\n";
                  pw.print(s);
                }
                 pw.close();
              }
            }catch(Exception es){
                checkError.setText("Ошибка при сохранении данных " + fileName + "\n");
                isSaved = false;
            }
        }
    };
    
    EventHandler<ActionEvent> onTable = new EventHandler<ActionEvent>(){
        @Override
          public void handle(ActionEvent e) {
              otherError.setText("");
              if(isOpened){
                  if(isChecked && !isError){
                      //Алгоритм построения таблицы
                      if(isSearched){
                        bearingObservableList.clear();
                        bearingObservableListTmp.clear();
                        for(Bearing bearing : bearingSearched){
                            bearingObservableList.add(bearing);
                        }
                        for(Bearing bearing : bearingObservableList){
                            bearingObservableListTmp.add(new Bearing(bearing.getName(), bearing.getType(),
                            bearing.getProducer(), bearing.getInDiameter(), bearing.getOutDiameter(),
                            bearing.getHeight(), bearing.inDateProperty().get(), bearing.countProperty().get()));
                        }
                        table.setItems(bearingObservableListTmp);
                        isTable = true;
                        tp.getSelectionModel().select(1);
                      } else{
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Внимание!");
                        alert.setHeaderText("Ошибка!");
                        alert.setContentText("Не произведен поиск!");
                        alert.showAndWait();
                      }                                                              
                      
                  } else{
                      Alert alert = new Alert(AlertType.INFORMATION);
                      alert.setTitle("Внимание!");
                      alert.setHeaderText("Ошибка!");
                      alert.setContentText("Неверно введены параметры поиска!");
                      alert.showAndWait();
                  }
                  
              }else {
                  Alert alert = new Alert(AlertType.INFORMATION);
                  alert.setTitle("Внимание!");
                  alert.setHeaderText("Ошибка!");
                  alert.setContentText("Не открыта база данных!");
                  alert.showAndWait();
              }
          }
    };
    
    EventHandler<ActionEvent> onExit = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            Platform.exit();
        }
    };
    
    EventHandler<ActionEvent> onSearch = new EventHandler<ActionEvent>(){
        @Override
       public void handle(ActionEvent e) {
            if(isOpened){
                if(isChecked && !isError){
                    ObservableList<String> typeObservableList = typeList.getSelectionModel().getSelectedItems();
                    ObservableList<String> producerObservableList = producerList.getSelectionModel().getSelectedItems();
                    int searchedCount = 0;
                    bearingSearched = new ArrayList<>();
                    for(Bearing bearing : bearingList){
                        if(bearing.getName().contains(nameTF.getText())){
                            if(typeObservableList.contains(bearing.getType()) || typeList.getSelectionModel().isSelected(0)){
                                if(producerObservableList.contains(bearing.getProducer()) || producerList.getSelectionModel().isSelected(0)){
                                    if(bearing.getInDiameter()>=minInD && bearing.getInDiameter()<=maxInD && 
                                        bearing.getOutDiameter()>=minOutD && bearing.getOutDiameter()<=maxOutD &&
                                        bearing.getHeight()>=minHeight && bearing.getHeight()<=maxHeight){
                                            if(bearing.countProperty().get()>=minCount && bearing.countProperty().get()<=maxCount){
                                                
                                                Date bearingDate = null;
                                                long bearingTime = 0;
                                                long minTime = 0;
                                                long maxTime = 0;
                                                try {
                                                    bearingDate = dateFormat.parse(bearing.inDateProperty().get());
                                                    bearingTime = bearingDate.getTime();
                                                    minTime = minDate.getTime();
                                                    maxTime = maxDate.getTime();
                                                } catch (ParseException ex) {

                                                }
                                                if(bearingTime>=minTime && bearingTime<=maxTime){
                                                    bearingSearched.add(bearing);
                                                    searchedCount++;     
                                                }
                                                 
                                            }     
                                    }
                                }
                            }
                        }
                    }
                    
                    if(searchedCount==0){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Внимание!");
                        alert.setHeaderText("Ошибка!");
                        alert.setContentText("По данным параметрам поиска не найдено результатов!");
                        alert.showAndWait();
                        isSearched = false;
                    } else isSearched = true;
                    
                    for(Bearing bearing : bearingSearched){
                        System.out.println(bearing.getName()+" "+bearing.getType()+" "+bearing.getProducer()+" "+bearing.getInDiameter()+" "+bearing.getOutDiameter()+" "
                                +bearing.getHeight()+" "+bearing.inDateProperty().get()+" "+bearing.countProperty().get());
                    }
                    
                }else{
                  Alert alert = new Alert(AlertType.INFORMATION);
                  alert.setTitle("Внимание!");
                  alert.setHeaderText("Ошибка!");
                  alert.setContentText("Не выполнена проверка!");
                  alert.showAndWait();
                }
            }else {
                  Alert alert = new Alert(AlertType.INFORMATION);
                  alert.setTitle("Внимание!");
                  alert.setHeaderText("Ошибка!");
                  alert.setContentText("Не открыта база данных!");
                  alert.showAndWait();
            }
        }
    };
    
    EventHandler<ActionEvent> onPrint = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            if(isGraph){
                Group rootGr = rootGraph;
                Printer printer = Printer.getDefaultPrinter();
                PageLayout pageLayout = printer.createPageLayout(Paper.A4,
                        PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

                double scaleX = pageLayout.getPrintableWidth()/rootGraph.getBoundsInParent().getWidth();
                double scaleY = pageLayout.getPrintableHeight() / rootGraph.getBoundsInParent().getHeight();
                System.out.println("scaleX: " + scaleX);
                System.out.println("scaleY: " + scaleY);
                rootGraph.getTransforms().add(new Scale(scaleX,scaleY));
                PrinterJob job = PrinterJob.createPrinterJob();
                if(job!=null){
                    boolean success = job.printPage(rootGr);
                    if (success) {
                        job.endJob();
                    }
                }
                rootGraph.getTransforms().add(new Scale(1/scaleX,1/scaleY));
            }else{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Внимание!");
                alert.setHeaderText("Ошибка!");
                alert.setContentText("График не построен!");
                alert.showAndWait();
            }
        }
    };
    
    EventHandler<ActionEvent> onHelp = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            System.out.println("Открывается помощь");
            File file = new File("..\\src\\bearings\\helpMenu.html");
            URL url = null;
            try {
                url= file.toURI().toURL();
            } catch (MalformedURLException ex) {}
            webView.getEngine().load(url.toString());
            tp.getSelectionModel().select(3);
        }
    };
    
    EventHandler<ActionEvent> onGraph = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent e) {
            if(isSearched && isOpened && isChecked){
                dataSeries1 = new XYChart.Series<String, Number>();
                for(Bearing bearing : bearingSearched){
                dataSeries1.getData().add(new XYChart.Data<String, Number>(bearing.getName(), bearing.countProperty().get()));
                isGraph=true;
                }
                barChart.getData().clear();
                barChart.getData().add(dataSeries1);
                tp.getSelectionModel().select(2);
                
            }else{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Внимание!");
                alert.setHeaderText("Ошибка!");
                alert.setContentText("Поиск не произведен!");
                alert.showAndWait();
            }
        }  
    };
    
    
    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 950, 640);
        primaryStage.setTitle("Учет подшипников");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        //Добавление меню
        MenuBar menuBar = new MenuBar();
        menuBar.setLayoutX(0);
        menuBar.setLayoutY(0);
        menuBar.setPrefSize(960, 20);
        menuBar.setCursor(Cursor.CLOSED_HAND);
        Menu menuF = new Menu("Файл");
        MenuItem menuOpen = new MenuItem("Открыть");
        menuOpen.setOnAction(onOpen);
        MenuItem menuSave = new MenuItem("Сохранить");
        menuSave.setOnAction(onSave);
        MenuItem menuClose = new MenuItem("Выход");
        menuClose.setOnAction(onExit);
        menuF.getItems().addAll(menuOpen, menuSave, menuClose);
        Menu menuR = new Menu("Функции");
        MenuItem menuCheck = new MenuItem("Проверить");
        menuCheck.setOnAction(onCheck);
        MenuItem searchMenu = new MenuItem("Поиск");
        searchMenu.setOnAction(onSearch);
        MenuItem menuTable = new MenuItem("Построить таблицу");
        menuTable.setOnAction(onTable);
        MenuItem menuModel = new MenuItem("Построть график");
        menuModel.setOnAction(onGraph);
        MenuItem menuPrint = new MenuItem("Напечатать график");
        menuPrint.setOnAction(onPrint);
        MenuItem menuClear = new MenuItem("Очистить");
        menuClear.setOnAction(onClearButton);
        menuR.getItems().addAll(menuCheck, searchMenu, menuTable, menuModel, menuPrint, menuClear);
        Menu menuS = new Menu("Справка");
        MenuItem menuHelp= new MenuItem("Помощь");
        menuHelp.setOnAction(onHelp);
        MenuItem menuAbout= new MenuItem("О программе");
        menuAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Group secondaryLayout = new Group();
                aboutWebView.setPrefSize(950, 640);
                aboutWebView.setCursor(Cursor.TEXT);
                //Создаем файл с для загрузки в окно "О программе"
                File fileAbout = new File("..\\src\\bearings\\aboutMenu.html");
                URL urlAbout = null;
                try {
                    urlAbout= fileAbout.toURI().toURL();
                } catch (MalformedURLException ex) {}
                aboutWebView.getEngine().load(urlAbout.toString());
                secondaryLayout.getChildren().add(aboutWebView);
                Scene secondScene = new Scene(secondaryLayout, 520, 210);
                //Создаем новое окно (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("О программе");
                newWindow.setScene(secondScene);
                // Уазываем позицию нового она относительно главного
                newWindow.setX(primaryStage.getX() + 200);
                newWindow.setY(primaryStage.getY() + 100);
                newWindow.setResizable(false);
                newWindow.show();
            }
        });
        menuS.getItems().addAll(menuHelp, menuAbout);
        menuBar.getMenus().addAll(menuF, menuR, menuS);
        root.getChildren().add(menuBar);
        
        
        //Добавление вкалдок
        tp.setLayoutX(0);
        tp.setLayoutY(25);
        tp.setPrefSize(960, 625);
        tp.setCursor(Cursor.HAND);
        tp.setStyle("-fx-border-width:1pt;-fx-border-color:grey;");
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setTabMinHeight(20);
        tp.setTabMinWidth(100);
        
        //Панель поиска
        Tab tabInp = new Tab("Поиск");
        Group rootInp = new Group();
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(30));
        gp.setHgap(30);
        gp.setVgap(20);
        nameTF.setCursor(Cursor.TEXT);
        nameTF.setPromptText("Наименование"); 
        typeTF.setCursor(Cursor.TEXT);
        producerTF.setCursor(Cursor.TEXT);
        minInDTF.setCursor(Cursor.TEXT);
        minInDTF.setPromptText("от"); 
        maxInDTF.setCursor(Cursor.TEXT);
        maxInDTF.setPromptText("до"); 
        minOutDTF.setCursor(Cursor.TEXT);
        minOutDTF.setPromptText("от"); 
        maxOutDTF.setCursor(Cursor.TEXT);
        maxOutDTF.setPromptText("до"); 
        minHeightTF.setCursor(Cursor.TEXT);
        minHeightTF.setPromptText("от"); 
        maxHeightTF.setCursor(Cursor.TEXT);
        maxHeightTF.setPromptText("до"); 
        minDateTF.setCursor(Cursor.TEXT);
        minDateTF.setPromptText("от"); 
        maxDateTF.setCursor(Cursor.TEXT);
        maxDateTF.setPromptText("до"); 
        minCountTF.setCursor(Cursor.TEXT);
        minCountTF.setPromptText("от"); 
        maxCountTF.setCursor(Cursor.TEXT);
        maxCountTF.setPromptText("до");
        
        typeList.getItems().add("Любой");
        typeList.getItems().add("Шариковый-радиальный");
        typeList.getItems().add("Шариковый-радиально-упорный");
        typeList.getItems().add("Шариковый-упорный");
        typeList.getItems().add("Шариковый-двухрядный");
        typeList.getItems().add("Роликовый-радиальный");
        typeList.getItems().add("Роликовый-упорный");
        typeList.getItems().add("Роликовый-радиальный(конический)");
        typeList.getItems().add("Игольчатый");
        typeList.getItems().add("Ступичный");
        typeList.getItems().add("Корпусной");
        typeList.getSelectionModel().select(0);
        typeList.setMaxHeight(75);
        typeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        producerList.getItems().add("Любой");
        producerList.getItems().add("АПП");
        producerList.getItems().add("Россия");
        producerList.getItems().add("Китай");
        producerList.getItems().add("FBC");
        producerList.getItems().add("NSK");
        producerList.getItems().add("FAG");
        producerList.getItems().add("ISB");
        producerList.getItems().add("NTN");
        producerList.getItems().add("SKF");
        producerList.getItems().add("Koyo");
        producerList.getItems().add("Др.импорт");
        producerList.getSelectionModel().select(0);
        producerList.setMaxHeight(75);
        producerList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        gp.add(nameLabel, 0, 0);
        gp.add(typeLabel, 0, 1);
        gp.add(producerLabel, 0, 2);
        gp.add(inDLabel, 0, 3);
        gp.add(outDLabel, 0, 4);
        gp.add(heightDLabel, 0, 5);
        gp.add(dateLabel, 0, 6);
        gp.add(countLabel, 0, 7);
        gp.add(nameTF, 1, 0);
        gp.add(typeList, 1, 1);
        gp.add(producerList, 1, 2);
        gp.add(minInDTF, 1, 3);
        gp.add(minOutDTF, 1, 4);
        gp.add(minHeightTF, 1, 5);
        gp.add(minDateTF, 1, 6);
        gp.add(minCountTF, 1, 7);
        gp.add(maxInDTF, 2, 3);
        gp.add(maxOutDTF, 2, 4);
        gp.add(maxHeightTF, 2, 5);
        gp.add(maxDateTF, 2, 6);
        gp.add(maxCountTF, 2, 7);
        checkError.setMaxSize(50, 50);
        gp.add(nameError, 3, 0);
        gp.add(typeError, 3, 1);
        gp.add(producerError, 3, 2);
        gp.add(inDError, 3, 3);
        gp.add(outDError, 3, 4);
        gp.add(hError, 3, 5);
        gp.add(dateError, 3, 6);
        gp.add(countError, 3, 7);
        clearButton.setOnAction(onClearButton);
        gp.add(clearButton, 1, 8);
        searchButton.setOnAction(onSearch);
        gp.add(searchButton, 2, 8);
        gp.add(otherError, 3, 8);
        rootInp.getChildren().add(gp);
        tabInp.setContent(rootInp);
        
        //Владка таблицы
        Tab tabTab = new Tab("Таблица");
        table.setEditable(false);
        table.setTableMenuButtonVisible(true);
        table.setTooltip(new Tooltip("Подшипники"));
        table.setStyle("-fx-font: 8pt Arial;");
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Создаем столбец "Наименование"
        TableColumn<Bearing,String> nameCol = new TableColumn("Наименование");
        nameCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,String>("name"));
        nameCol.setPrefWidth(130);
        nameCol.setSortable(true);
        
        //Создаем столбец "Тип"
        TableColumn<Bearing,String> typeCol = new TableColumn("Тип");
        typeCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,String>("type"));
        typeCol.setPrefWidth(160);
        typeCol.setSortable(true);
        
        //Создаем столбец "Производитель"
        TableColumn<Bearing,String> producerCol = new TableColumn("Производитель");
        producerCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,String>("producer"));
        producerCol.setPrefWidth(110);
        producerCol.setSortable(true);
        
        //Создаем столбец "Внутренний диаметр"
        TableColumn<Bearing,Double> inDiameterCol = new TableColumn("Вн. диаметр, мм");
        inDiameterCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,Double>("inDiameter"));
        inDiameterCol.setPrefWidth(110);
        inDiameterCol.setSortable(true);
        
        //Создаем столбец "Наружный диаметр"
        TableColumn<Bearing,Double> outDiameterCol = new TableColumn("Нар. диаметр, мм");
        outDiameterCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,Double>("outDiameter"));
        outDiameterCol.setPrefWidth(110);
        outDiameterCol.setSortable(true);
        
        //Создаем столбец "Ширина"
        TableColumn<Bearing,Double> heightCol = new TableColumn("Ширина, мм");
        heightCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,Double>("height"));
        heightCol.setPrefWidth(110);
        heightCol.setSortable(true);
        
        //Создаем столбец "Дата поставки"
        TableColumn<Bearing,String> inDateCol = new TableColumn("Дата поставки");
        inDateCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,String>("inDate"));
        inDateCol.setPrefWidth(110);
        inDateCol.setSortable(true);
        
        //Создаем столбец "Количество"
        TableColumn<Bearing,Integer> countCol = new TableColumn("Кол-во");
        countCol.setCellValueFactory(
        new PropertyValueFactory<Bearing,Integer>("count"));
        countCol.setPrefWidth(60);
        countCol.setSortable(true);

        table.getColumns().addAll(nameCol, typeCol, producerCol, inDiameterCol,
                outDiameterCol, heightCol, inDateCol, countCol);
        
        HBox hbox = new HBox();
        TextField dateText = new TextField();
        dateText.setPrefWidth(110);
        TextField countText = new TextField();
        countText.setPrefWidth(60);
        Button commit = new Button("OK");
        commit.setPrefWidth(40);
        //Назначаем действие для кнопки "ОК"
        commit.setOnAction(new EventHandler() {
            @Override
            public void handle(Event evt) {
                Bearing item = table.getSelectionModel().getSelectedItem();
                String tmpDateText = dateText.getText();
                Date tmpDate;
                try{
                    tmpDate = dateFormat.parse(dateText.getText());
                    item.inDateProperty().set(dateText.getText());
                 }catch(Exception e2){
                    dateText.setText(tmpDateText);
                 }
                item.countProperty().set(Integer.valueOf(countText.getText()));
                table.toFront();
            }
        });
        hbox.getChildren().addAll(dateText, countText,commit);
        StackPane stPane = new StackPane(hbox, table);
        //Назначаем действие по двойному щелчку по строке таблицы
        table.setOnMouseClicked(event -> {
                if (event.getClickCount()==2){
                    StackPane.setMargin(hbox, new Insets(event.getSceneY()-55, 0, 0, 730));
                    hbox.toFront();
                }
        });
        //Поучаем данные из выбранной строки в форму ввода
        table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                dateText.setText(String.valueOf(nv.inDateProperty().get()));
                countText.setText(String.valueOf(nv.countProperty().get()));
            }
        });
        //Создаем панель для добавления кнопок управления таблицей
        FlowPane buttonBar = new FlowPane();
        Button clearTable = new Button( "Сброс");
        clearTable.setOnAction(e -> {
            bearingObservableListTmp.clear();
            for(Bearing bearing : bearingObservableList){
                bearingObservableListTmp.add(new Bearing(bearing.getName(), bearing.getType(),
                bearing.getProducer(), bearing.getInDiameter(), bearing.getOutDiameter(),
                bearing.getHeight(), bearing.inDateProperty().get(), bearing.countProperty().get()));
            }
            table.setItems(bearingObservableListTmp);
        });
        Button saveButton = new Button( "Сохранить");
        saveButton.setOnAction(e -> {
            bearingObservableList.clear();
            for(Bearing bearing : bearingObservableListTmp){
                bearingObservableList.add(new Bearing(bearing.getName(), bearing.getType(),
                bearing.getProducer(), bearing.getInDiameter(), bearing.getOutDiameter(),
                bearing.getHeight(), bearing.inDateProperty().get(), bearing.countProperty().get()));
            }
            for(Bearing bearing : bearingList){
                for(Bearing bearingTmp : bearingObservableList){
                    if(bearing.getName().equals(bearingTmp.getName()) &&
                            bearing.getProducer().equals(bearingTmp.getProducer())){
                        bearing.inDateProperty().set(bearingTmp.inDateProperty().get());
                        bearing.countProperty().set(bearingTmp.countProperty().get());
                    }
                }
            }        
            
        });
        buttonBar.getChildren().addAll(clearTable, saveButton);
        //Создаем панель для расположения таблицы и кнопок
        BorderPane bRoot = new BorderPane();
        bRoot.setCenter(stPane);
        bRoot.setBottom(buttonBar);
        tabTab.setContent(bRoot);
        
        //Вкладка графика
        Tab tabGraph = new Tab("График");
        barChart.setPrefWidth(940);
        barChart.setPrefHeight(550);
        xAxis.setLabel("Наменование");
        yAxis.setLabel("Кол-во");
        dataSeries1 = new XYChart.Series<String, Number>();
        barChart.setTitle("Остатки подшипников");
        //Добавление контекстного меню
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clearGaph = new MenuItem("Очистить");
        clearGaph.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                barChart.getData().clear();
                isGraph = false;
            }
        });
        MenuItem printGraph = new MenuItem("Печать");
        printGraph.setOnAction(onPrint);
        contextMenu.getItems().addAll(clearGaph, printGraph);
        barChart.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(barChart, event.getScreenX(), event.getScreenY());
            }
        });
        rootGraph.getChildren().add(barChart);
        tabGraph.setContent(rootGraph);
        
        //Добавление вкладки справка
        Tab tabHlp = new Tab("Справка"); // Название вкладки
        tabHlp.setClosable(true);
        Group rootHlp = new Group();
        tabHlp.setContent(rootHlp);
        webView.setPrefSize(950, 640);
        webView.setCursor(Cursor.TEXT);
        rootHlp.getChildren().add(webView);
        webView.getEngine().loadContent("<HTML><BODY>Встроенная помощь</BODY></HTML>");
        
        root.getChildren().add(tp);
        tp.getTabs().addAll(tabInp, tabTab, tabGraph, tabHlp);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
