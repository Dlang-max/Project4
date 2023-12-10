import javafx.application.Application;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import javafx.stage.Stage;

public class GraphingCalculator extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	protected static final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 500;
	protected static final double MIN_X = -10, MAX_X = +10, DELTA_X = 0.01;
	protected static final double MIN_Y = -10, MAX_Y = +10;
	protected static final double GRID_INTERVAL = 5;
	protected static final String EXAMPLE_EXPRESSION = "2*x+5*x*x";
	protected final ExpressionParser expressionParser = new SimpleExpressionParser();

	private boolean HAS_STARTED = false;
	private double START_DIFF_X = 0;
	private double START_DIFF_y = 0;
	private int GRAPH_CENTER_X = 263;
	private int GRAPH_CENTER_Y = 213;
	private int GRAPH_WIDTH = 445;
	private int GRAPH_HEIGHT = 345;
	private double SCALING_FACTOR = 0.8;
	private int MINIMUM_SCROLL_DELTA = 10; 

	
	private void graph (LineChart<Number, Number> chart, Expression expression, boolean clear) {
		final XYChart.Series series = new XYChart.Series();
		for (double x = MIN_X; x <= MAX_X; x += DELTA_X) {
			final double y = expression.evaluate(x);
			series.getData().add(new XYChart.Data(x, y));
		}
		if (clear) {
			chart.getData().clear();
		}
		chart.getData().addAll(series);
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Graphing Calculator");

		final Pane queryPane = new HBox();
		final Label label = new Label("y=");
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button graphButton = new Button("Graph");
		final CheckBox diffBox = new CheckBox("Show Derivative");
		queryPane.getChildren().add(label);
		queryPane.getChildren().add(textField);

		final Pane graphPane = new Pane();
		final LineChart<Number, Number>  chart = new LineChart<Number, Number>(new NumberAxis(MIN_X, MAX_X, GRID_INTERVAL), new NumberAxis(MIN_Y, MAX_Y, GRID_INTERVAL));
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		chart.getYAxis().setTickLabelRotation(-90);
		graphPane.getChildren().add(chart);
		graphButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			//When the button is clicked:
			public void handle (MouseEvent e) {
				try {
					final Expression expression = expressionParser.parse(textField.getText());
					graph(chart, expression, true);
					System.out.println(expression.convertToString(0));
					if (diffBox.isSelected()) {
						final Expression derivative = expression.differentiate();
						graph(chart, derivative, false);
					}
				} catch (ExpressionParseException epe) {
					textField.setStyle("-fx-text-fill: red");
				} catch (UnsupportedOperationException epe) {
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});

		
		//Handles panning the graph with the mouse.
		graphPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			//When the mouse is dragged:
			public void handle (MouseEvent e) {
				NumberAxis xAxis = (NumberAxis) chart.getXAxis();
				NumberAxis yAxis = (NumberAxis) chart.getYAxis();
				
				Double width = xAxis.getUpperBound() - xAxis.getLowerBound();
				Double height = yAxis.getUpperBound() - yAxis.getLowerBound();
				
				Double x = -1 * (e.getSceneX() - GRAPH_CENTER_X) * width / GRAPH_WIDTH;
				Double y = (e.getSceneY() - GRAPH_CENTER_Y) * height / GRAPH_HEIGHT;
				
				if(!HAS_STARTED){
					START_DIFF_X = x - (xAxis.getUpperBound() + xAxis.getLowerBound())/2;
					START_DIFF_y = y - (yAxis.getUpperBound() + yAxis.getLowerBound())/2;
					HAS_STARTED = true;
				}
				
				x = x - START_DIFF_X;
				y = y - START_DIFF_y;

				Double minX = x - width/2;
				Double maxX = x + width/2;
				Double minY = y - height/2;
				Double maxY = y + height/2;

				// Update axis ranges based on mouse drag movement
				xAxis.setLowerBound(minX);
				xAxis.setUpperBound(maxX);

				yAxis.setLowerBound(minY);
				yAxis.setUpperBound(maxY);
				
			}
				
		});

		graphPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e){
				HAS_STARTED = false;
				START_DIFF_X = 0;
				START_DIFF_y = 0;
			}
		});

		//Handles zooming in and out of the graph with the mouse.
		graphPane.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent e){
				
				NumberAxis xAxis = (NumberAxis) chart.getXAxis();
				NumberAxis yAxis = (NumberAxis) chart.getYAxis();

				if(e.getDeltaY() > MINIMUM_SCROLL_DELTA){
					xAxis.setLowerBound(xAxis.getLowerBound() * SCALING_FACTOR);
					xAxis.setUpperBound(xAxis.getUpperBound() * SCALING_FACTOR);

					yAxis.setLowerBound(yAxis.getLowerBound() * SCALING_FACTOR);
					yAxis.setUpperBound(yAxis.getUpperBound() * SCALING_FACTOR);
				}

				if(e.getDeltaY() < MINIMUM_SCROLL_DELTA){
					xAxis.setLowerBound(xAxis.getLowerBound() / SCALING_FACTOR);
					xAxis.setUpperBound(xAxis.getUpperBound() / SCALING_FACTOR);

					yAxis.setLowerBound(yAxis.getLowerBound() / SCALING_FACTOR);
					yAxis.setUpperBound(yAxis.getUpperBound() / SCALING_FACTOR);
				}
			}
		});
		
		queryPane.getChildren().add(graphButton);
		queryPane.getChildren().add(diffBox);

		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(graphPane);

		final Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
