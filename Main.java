package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main extends Application {
	private Timeline locationUpdateTimeline;

	@Override
	public void start(Stage primaryStage) {
		try {
//			WebView view = new WebView();
//			view.setMinSize(500, 400);
//			view.setPrefSize(500, 400);
//			final WebEngine webEngine = new WebEngine(getClass().getResource("googlemap.html").toString());
			final WebView webView = new WebView();
			final WebEngine webEngine = webView.getEngine();
			String url = Main.class.getResource("googlemap.html").toExternalForm();  
			webEngine.load(url);

			final ToggleGroup mapTypeGroup = new ToggleGroup();
	        final ToggleButton road = new ToggleButton("Road");
	        road.setSelected(true);
	        road.setToggleGroup(mapTypeGroup);
	        final ToggleButton satellite = new ToggleButton("Satellite");
	        satellite.setToggleGroup(mapTypeGroup);
	        final ToggleButton hybrid = new ToggleButton("Hybrid");
	        hybrid.setToggleGroup(mapTypeGroup);
	        final ToggleButton terrain = new ToggleButton("Terrain");
	        terrain.setToggleGroup(mapTypeGroup);
	        
	        mapTypeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {			      		        		        		        	
				 public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle toggle1) {
					 if (road.isSelected()) {
						webEngine.executeScript("document.setMapTypeRoad()");
					} else if (satellite.isSelected()) {
						webEngine.executeScript("document.setMapTypeSatellite()");
					} else if (hybrid.isSelected()) {
						webEngine.executeScript("document.setMapTypeHybrid()");
					} else if (terrain.isSelected()) {
						webEngine.executeScript("document.setMapTypeTerrain()");
					}
				 }
			});

	        ToggleGroup mapSourceGroup = new ToggleGroup();
	        final ToggleButton google = new ToggleButton("Google");
	        google.setSelected(true);
	        google.setToggleGroup(mapSourceGroup);
	        final ToggleButton yahoo = new ToggleButton("Yahoo");
	        yahoo.setToggleGroup(mapSourceGroup);
	        final ToggleButton bing = new ToggleButton("Bing");
	        bing.setToggleGroup(mapSourceGroup);
	        // listen to selected source
	        mapSourceGroup.selectedToggleProperty().addListener(
	                            new ChangeListener<Toggle>() {
	            public void changed(
	                    ObservableValue<? extends Toggle> observableValue,
	                    Toggle toggle, Toggle toggle1) {
	                terrain.setDisable(true);
	                if (google.isSelected()) {
	                    terrain.setDisable(false);
	                    webEngine.load(
	                          getClass().getResource("googlemap.html").toString());
	                } else if (yahoo.isSelected()) {
	                    webEngine.load(
	                          getClass().getResource("bingmap.html").toString());
	                } else if (bing.isSelected()) {
	                    webEngine.load(
	                          getClass().getResource("yahoomap.html").toString());
	                }
	                mapTypeGroup.selectToggle(road);
	            }
	        });
	        
	        final TextField searchBox = new TextField("95054");
//	        searchBox.raw
	        searchBox.setPromptText("Search");
	        searchBox.textProperty().addListener(new ChangeListener<String>() {
	        	public void changed(ObservableValue<? extends String> observableValue, String s, String s1) {	     	       
	                // delay location updates to we don't go too fast file typing
	                if (locationUpdateTimeline!=null) locationUpdateTimeline.stop();
	                locationUpdateTimeline = new Timeline();
	                locationUpdateTimeline.getKeyFrames().add(
	                    new KeyFrame(new Duration(400),
	                    		new EventHandler<ActionEvent>() {
	                        		public void handle(ActionEvent actionEvent) {
	                        			webEngine.executeScript("document.goToLocation(\""+
	                        					searchBox.getText()+"\")");
	                        		}
	                    }));	        	
	               locationUpdateTimeline.play();
	        	}
			});
//	                        }
//	                    })
//	                );
//	                locationUpdateTimeline.play();
//	            }
//	        });
	        
	        Button zoomIn = new Button("Zoom In");
	        zoomIn.setOnAction(new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent actionEvent) {
					webEngine.executeScript("document.zoomIn()");
				}
			});
	        
	        Button zoomOut = new Button("Zoom Out");
	        zoomOut.setOnAction(new EventHandler<ActionEvent>() {
	        	public void handle(ActionEvent actionEvent) {
					webEngine.executeScript("document.zoomOut()");
				}
			});
	        
	        ToolBar toolBar = new ToolBar();
	        toolBar.getStyleClass().add("map-toolbar");
	        toolBar.getItems().addAll(road,satellite,hybrid,terrain,createSpacer(),google, yahoo, bing,
	                createSpacer(),new Label("Location:"),searchBox,zoomIn,zoomOut);
	        
			BorderPane root = new BorderPane();
			root.getStyleClass().add("map");
			root.setCenter(webView);
	        root.setTop(toolBar);
	        primaryStage.setTitle("Web Map");
	        Scene scene = new Scene(root,1000,700, Color.web("#666970"));
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (

		Exception e)

		{
			e.printStackTrace();
		}

	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
